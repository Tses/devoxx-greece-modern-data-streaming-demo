package me.escoffier.temperatures;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;

@QuarkusMain
@ApplicationScoped
public class SensorApplication implements QuarkusApplication {

    @Inject SensorConfiguration sensors;

    @Channel("temperatures")
    MutinyEmitter<JsonObject> emitter;
    @Override
    public int run(String... args) throws Exception {
        var list = sensors.sensors().stream()
                .map(sensor -> new TemperatureSensor(sensor.id(), sensor.base(), sensor.frequency(), emitter))
                .toList();

        list.forEach(TemperatureSensor::start);

        Quarkus.waitForExit();

        list.forEach(TemperatureSensor::stop);

        return 0;
    }

    public static void main(String[] args) {
        Quarkus.run(SensorApplication.class, args);
    }
}
