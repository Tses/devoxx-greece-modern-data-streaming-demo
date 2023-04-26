package me.escoffier.pub;

import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import io.smallrye.mutiny.operators.multi.processors.UnicastProcessor;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class PriceRepository {
    @ConfigProperty(name = "location")
    String location;


    private double price = 5.00;
    private final UnicastProcessor<Double> processor = UnicastProcessor.create();

    private final Multi<Double> stream = Multi.createBy()
            .replaying().upTo(1).ofMulti(processor)
            .broadcast().toAllSubscribers();

    @Incoming("prices")
    public void updatePrice(Record<String, Double> price) {
        if (price.key().equalsIgnoreCase(location)) {
            this.price = price.value();
            this.processor.onNext(this.price);
        }

    }

    public double getCurrentPrice() {
        return price;
    }

    public Multi<Double> getPriceStream() {
        processor.onNext(getCurrentPrice());
        return stream;
    }
}
