package me.escoffier;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.tuples.Tuple2;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import java.time.Duration;

/**
 * Compute the average temperature / location / minute
 */
@ApplicationScoped
public class TemperatureAggregator {

    record TemperatureMeasurement(String location, double temperature) {
    }

    @Incoming("temps")
    @Outgoing("temperature-aggregate")
    public Multi<Tuple2<String, Double>> aggregate(Multi<TemperatureMeasurement> temperatures) {
        return temperatures
                .group().by(t -> t.location) // Group by key
                .flatMap(grouped -> grouped.group().intoLists().every(Duration.ofSeconds(10)).map(l -> Record.of(grouped.key(), l))) // Time window
                .flatMap(rec -> {
                    // Compute the average
                    var avg = rec.value().stream().mapToDouble(r -> r.temperature).average().orElse(0.0);
                    if (avg == 0.0) {
                        // Skip
                        return Multi.createFrom().empty();
                    } else {
                        return Multi.createFrom().item(Tuple2.of(rec.key(), avg));
                    }
                });
    }
}
