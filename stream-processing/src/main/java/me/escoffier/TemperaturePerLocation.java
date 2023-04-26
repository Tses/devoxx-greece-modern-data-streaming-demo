package me.escoffier;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.subscription.Cancellable;
import io.smallrye.mutiny.tuples.Tuple2;
import io.smallrye.reactive.messaging.Table;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.reactive.messaging.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class TemperaturePerLocation
{

    @Channel("temperature-aggregate")
    Table<String, Tuple2<String, Double>> temperatures;

    private Map<String, Double> temperaturePerLocation = new ConcurrentHashMap<>();
    private volatile Cancellable cancellable;

    public void subscribe(@Observes StartupEvent ev) {
        cancellable = temperatures.subscribe().with(s -> {
            temperaturePerLocation.put(s.getItem1(), s.getItem2().getItem2());
        });
    }

    public void unsubscribe(@Observes ShutdownEvent ev) {
        if (cancellable != null) {
            cancellable.cancel();
        }
    }

    public Double get(String location) {
        return temperaturePerLocation.get(location);
    }


    public String getAll() {
        return temperaturePerLocation.toString();
    }
}
