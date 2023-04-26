package me.escoffier;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.tuples.Tuple2;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import java.time.Duration;

/**
 * Compute the number of order / location / minute.
 */
public class OrderAggregator {

    @Incoming("orders")
    @Outgoing("order-aggregate")
    public Multi<Record<String, Integer>> aggregate(Multi<Record<String, Double>> orders) {
        return orders
                .log("orders")
                .group().by(Record::key) // Group by location
                .flatMap(streamPerLocation -> streamPerLocation
                        .group().intoLists().every(Duration.ofSeconds(10))
                        .log("list")
                        .map(list -> Record.of(streamPerLocation.key(), list.size()))
                        .ifNoItem().after(Duration.ofSeconds(10)).recoverWithMulti(Multi.createFrom().item(Record.of(streamPerLocation.key(), 0)))
                )
                .log("order-aggregate");
    }
}
