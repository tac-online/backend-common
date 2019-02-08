package de.johanneswirth.tac.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import static java.util.logging.Logger.getGlobal;

/**
 * @author Johannes Wirth
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private String publicKey;

    public AuthenticationFilter(String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        // get token from Http Authorization header
        String ticket = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (ticket == null) {
            getGlobal().log(Level.WARNING, "Authorization header not set");
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        getGlobal().log(Level.INFO, "Trying to authorize with ticket " + ticket);
        RSAPublicKey publicKey = null;
        try {
            publicKey = Utils.loadPublic(this.publicKey);
        } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
            getGlobal().log(Level.SEVERE, "Uncaught Exception", e);
        }
        try {

            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("tac-server")
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(ticket);
            final boolean sec = requestContext.getSecurityContext().isSecure();
            final String auth = requestContext.getSecurityContext().getAuthenticationScheme();
            requestContext.setSecurityContext(new SecurityContext() {

                @Override
                public boolean isUserInRole(String role) {
                    return true;
                }

                @Override
                public boolean isSecure() {
                    return sec;
                }

                @Override
                public Principal getUserPrincipal() {
                    return jwt::getSubject;
                }

                @Override
                public String getAuthenticationScheme() {
                    return auth;
                }

            });
        } catch (JWTVerificationException exception){
            requestContext.abortWith(Response.status(401).header("access-control-allow-origin", "*").header("access-control-allow-headers", "access-control-allow-origin,content-type,authorization").build());
        }

    }

}
