package de.johanneswirth.tac.common;

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
            throw new NotAuthorizedException("Authorization header must be provided");
        }
        Optional<String> subject = Utils.validateJWT(ticket);
        if (subject.isPresent()) {


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
                    return subject::get;
                }

                @Override
                public String getAuthenticationScheme() {
                    return auth;
                }

            });
        } else {
            requestContext.abortWith(Response.status(401).header("access-control-allow-origin", "*").header("access-control-allow-headers", "access-control-allow-origin,content-type,authorization").build());
        }

    }

}
