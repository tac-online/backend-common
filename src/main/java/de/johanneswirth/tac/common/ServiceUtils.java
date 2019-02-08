package de.johanneswirth.tac.common;

import io.dropwizard.discovery.DiscoveryBundle;
import io.dropwizard.discovery.client.DiscoveryClient;
import io.dropwizard.discovery.client.DiscoveryClientManager;
import io.dropwizard.setup.Environment;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

public class ServiceUtils {

    static Client client;
    static DiscoveryClient messaging;

    public static void init(DiscoveryBundle discoveryBundle, Client client, Environment environment) {
        ServiceUtils.client = client;
        messaging = discoveryBundle.newDiscoveryClient("other-service");
        environment.lifecycle().manage(new DiscoveryClientManager(messaging));
    }

    public static Response getResponse(Status status, String allowedMethods) {
        return Response.ok(status)
                .header("access-control-allow-origin", "*")
                .header("access-control-allow-headers", "access-control-allow-origin,content-type,authorization")
                .header("access-control-allow-methods", allowedMethods)
                .build();
    }
}
