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

    @Incoming("temperatures")
    @Outgoing("temps")
    public Record<String, TemperatureMeasurement> fromMqttToKafka(byte[] temperature) {
        // LIVE CODE THIS
        JsonObject input = Buffer.buffer(temperature).toJsonObject();
        var location = repository.getLocationForDevice(input.getString("device"));
        TemperatureMeasurement outcome = new TemperatureMeasurement(location, input.getDouble("value"));
        System.out.println("Writing " + outcome);
        return Record.of(location, outcome);
    }

    record TemperatureMeasurement(String location, double temperature) {
    }


}
