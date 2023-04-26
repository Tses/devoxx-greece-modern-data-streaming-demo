package me.escoffier.analyzer;

import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class PriceAnalyzer {

    private static final double BASE_PRICE = 5.00;
    private final Map<String, Double> prices = new HashMap<>();

    record Report(String location, double temperature, int numberOfOrder) {

    }


    @Incoming("reports")
    @Outgoing("prices")
    public Record<String, Double> analyze(Report report) {
        System.out.println("analyzing... " + report);
        var current = prices.getOrDefault(report.location, BASE_PRICE);
        if (report.temperature >= 22) {
            if (report.numberOfOrder > 5) {
                // Big increase
                prices.put(report.location, current + 1);
                return Record.of(report.location , current + 1);
            } else {
                // Small increase
                prices.put(report.location, current + 0.5);
                return Record.of(report.location, current + 0.5);
            }
        } else {
            if (report.numberOfOrder > 5) {
                prices.put(report.location, current + 0.5);
                return Record.of(report.location, current + 0.5);
            } else {
                var newPrice = Math.min(BASE_PRICE, current - 0.5);
                prices.put(report.location, newPrice);
                return Record.of(report.location, newPrice);
            }
        }
    }
}
