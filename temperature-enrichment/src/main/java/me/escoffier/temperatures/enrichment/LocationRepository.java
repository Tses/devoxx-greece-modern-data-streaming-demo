package me.escoffier.temperatures.enrichment;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;

@ApplicationScoped
public class LocationRepository {

    static Map<String, String> LOCATIONS = Map.of(
            "4c3d7f1a-3b6e-4f4a-9d5c-2e8b7d8e0c0f", "Athens",
            "5b2d6a7b-8e9f-4a1c-bc5d-1a2b3c4d5e6f", "Valence",
            "6a7b8c9d-e0f1-2b3c-4d5e-6f7a8b9c0d1e", "Paris"
    );

    public String getLocationForDevice(String device) {
        return LOCATIONS.getOrDefault(device, "unknown");
    }

}
