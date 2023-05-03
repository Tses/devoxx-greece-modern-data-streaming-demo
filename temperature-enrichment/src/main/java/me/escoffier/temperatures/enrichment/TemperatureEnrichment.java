package me.escoffier.temperatures.enrichment;


import io.smallrye.reactive.messaging.kafka.Record;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class TemperatureEnrichment {

    @Inject
    LocationRepository repository;


    @Incoming("raw-temperatures")
    @Outgoing("temperatures")
    public Record<String, TemperatureMeasurement> fromMqttToKafka(JsonObject raw) {
        var location = repository
                .getLocationForDevice(raw.getString("device"));
        TemperatureMeasurement outcome = new TemperatureMeasurement(location,
                raw.getDouble("value"));
        System.out.println("Writing " + outcome);
        return Record.of(location, outcome);
    }

    record TemperatureMeasurement(String location, double temperature) {
    }


}
