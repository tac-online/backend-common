package de.johanneswirth.tac.common;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationRequired implements DynamicFeature {

    private String publicKey;

    public AuthenticationRequired(String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        if (resourceInfo.getResourceMethod().getAnnotation(Secured.class) != null) {
            context.register(new AuthenticationFilter(publicKey));
        }
    }
}
