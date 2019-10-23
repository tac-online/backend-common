package de.johanneswirth.tac.common;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.Optional;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import static de.johanneswirth.tac.common.ErrorStatus.TOKEN_EXPIRED;
import static de.johanneswirth.tac.common.ErrorStatus.UNAUTHORIZED;

/**
 * @author Johannes Wirth
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    public AuthenticationFilter() {}

    @Override
    public void filter(ContainerRequestContext requestContext) {
        // get token from Http Authorization header
        String ticket = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (ticket == null) {
            LOGGER.warn("Authorization header not set");
            requestContext.abortWith(Response.ok(UNAUTHORIZED).build());
        }
        try {
            DecodedJWT jwt = Utils.validateJWT(ticket);
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
        } catch (TokenExpiredException ex) {
            requestContext.abortWith(Response.ok(TOKEN_EXPIRED).build());
        } catch (JWTVerificationException ex) {
            requestContext.abortWith(Response.ok(UNAUTHORIZED).build());
        }

    }

}
