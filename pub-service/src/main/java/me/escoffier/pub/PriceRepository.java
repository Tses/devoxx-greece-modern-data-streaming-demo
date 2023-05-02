package me.escoffier.pub;

import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.Table;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PriceRepository {

    @Channel("prices")
    Table<String, Double> prices;
    
    @ConfigProperty(name = "location")
    String location;

    private double price = 5.00;

    public double getCurrentPrice() {
        return Optional.ofNullable(prices.get(location))
            .orElse(price);
    }

    public Multi<Double> getPriceStream() {
        return prices.filterKey(k -> location.equalsIgnoreCase(k))
            .map(t -> t.getItem2());
    }
}
