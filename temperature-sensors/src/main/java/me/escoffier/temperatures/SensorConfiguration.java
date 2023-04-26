package me.escoffier.temperatures;

import io.smallrye.config.ConfigMapping;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@ConfigMapping(prefix = "app")
public interface SensorConfiguration {

    List<Sensor> sensors();

    interface Sensor {
        Duration frequency();
        double base();
        String id();

    }

}
