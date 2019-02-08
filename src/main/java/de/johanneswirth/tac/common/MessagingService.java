package de.johanneswirth.tac.common;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import java.util.List;

import static de.johanneswirth.tac.common.ServiceUtils.client;
import static de.johanneswirth.tac.common.ServiceUtils.messaging;

public class MessagingService {

    public static Status sendMessage(Message message) throws Exception {
        WebTarget target = client.target(messaging.getInstance().buildUriSpec());
        return target.request(MediaType.APPLICATION_JSON).post(Entity.entity(message, MediaType.APPLICATION_JSON), Status.class);
    }

    public static Status sendMessage(String service, String resource, List<String> recipients, String payload) throws Exception {
        Message message = new Message(service, resource, System.currentTimeMillis(), recipients, payload);
        return sendMessage(message);
    }
}
