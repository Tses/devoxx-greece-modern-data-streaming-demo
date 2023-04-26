package me.escoffier;

import io.smallrye.mutiny.tuples.Tuple2;
import io.smallrye.reactive.messaging.MessageKeyExtractor;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.lang.reflect.Type;

@ApplicationScoped
public class MyKeyExtractor implements MessageKeyExtractor {

    // FIXME: Ozan - deconstruction of value
    @Override
    public boolean canExtract(Message<?> in, Type target) {
        return in.getPayload() instanceof Tuple2;
    }

    @Override
    public Object extractKey(Message<?> in, Type target) {
        return ((Tuple2)in.getPayload()).getItem1();
    }
}
