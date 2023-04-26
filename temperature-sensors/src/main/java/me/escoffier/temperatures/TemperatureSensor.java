package me.escoffier.temperatures;

import io.smallrye.reactive.messaging.MutinyEmitter;
import io.vertx.core.json.JsonObject;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.util.Random;

public class TemperatureSensor implements Runnable {


    private final String id;
    private final double current;
    private final Duration frequency;
    private final Random random;
    private final MutinyEmitter<JsonObject> emitter;
    private volatile boolean done;

    private static final Logger LOGGER = Logger.getLogger("sensor");

    public TemperatureSensor(String id, double base, Duration frequency, MutinyEmitter<JsonObject> emitter) {
        this.id = id;
        this.current = base;
        this.frequency = frequency;
        this.random = new Random();
        this.emitter = emitter;
    }

    public void start() {
        this.done = false;
        Thread.startVirtualThread(this);
    }

    public void stop() {
        this.done = true;
    }

    @Override
    public void run() {
        while (!done) {
            var value = JsonObject.of(
                    "device", id, "value", current + (random.nextBoolean() ? -1 : 1) * random.nextInt(3)
            );
            LOGGER.infof("Sensor %s sending %s", id, value);
            emitter.sendAndAwait(value);
            try {
                Thread.sleep(frequency);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
