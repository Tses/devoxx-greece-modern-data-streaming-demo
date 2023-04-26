package me.escoffier.pub;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.resteasy.reactive.RestStreamElementType;

@Path("/")
public class PubResource {

    @ConfigProperty(name = "location")
    String location;

    @Channel("orders")
    MutinyEmitter<Record<String, Double>> emitter;

    @Inject
    PriceRepository repository;

    @POST
    @Path("/order")
    public void order() {
        System.out.println("Sending order for " + location);
        emitter.sendAndAwait(Record.of(location, repository.getCurrentPrice()));
    }

    @GET
    @Path("/price")
    public double getCurrentPrice() {
        return repository.getCurrentPrice();
    }

    @GET
    @Path("/stream")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<Double> getStream() {
        return repository.getPriceStream();
    }
}
