package me.escoffier;

import io.smallrye.reactive.messaging.Table;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

/**
 * Consume the temperature / location / min and the beer order / location / min to create a report.
 * That report will be consumed by the Price analyzer
 */
@ApplicationScoped
public class PriceReportCreator {

    @Channel("temperature-aggregate")
    Table<String, Double> temperatures;

    record Report(String location, double temperature, int numberOfOrder) {

    }

    @Incoming("order-aggregate")
    @Outgoing("reports")
    public Report emitReport(Orders orderCountPerLocationPerTimePeriod) {
        // LIVE CODE THIS
        System.out.println("Computing report from " + orderCountPerLocationPerTimePeriod);
        var location = orderCountPerLocationPerTimePeriod.location();
        var count = orderCountPerLocationPerTimePeriod.numberOfOrders();
        var temperature = temperatures.get(location);
        if (temperature != null) {
            return new Report(location, temperature, count);
        } else {
            System.out.println("no report - no temperature for " + location + " in " + temperatures.toMap());
            return null;
        }
    }

}
