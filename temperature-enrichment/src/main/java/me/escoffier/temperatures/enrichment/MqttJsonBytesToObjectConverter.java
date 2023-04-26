package me.escoffier.temperatures.enrichment;

import io.smallrye.reactive.messaging.MessageConverter;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.lang.reflect.Type;

@ApplicationScoped
public class MqttJsonBytesToObjectConverter implements MessageConverter {
    @Override
    public boolean canConvert(Message<?> message, Type type) {
        if (message.getPayload() instanceof byte[]  && type instanceof Class<?>) {
            try {
                Buffer.buffer((byte[]) message.getPayload()).toJsonObject();
            } catch (Exception e) {
                // Not json
                return false;
            }
        }
        return true;
    }

    @Override
    public Message<?> convert(Message<?> message, Type type) {
        JsonObject json = Buffer.buffer((byte[]) message.getPayload()).toJsonObject();
        if (type == JsonObject.class) {
            return message.withPayload(json);
        } else {
            return message.withPayload(json.mapTo((Class<?>) type));
        }
    }
}
