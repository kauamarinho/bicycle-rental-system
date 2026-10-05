package org.example.controller;

import org.example.service.BicycleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds the in-memory repositories with the bicycles available at startup.
 */
@Component
public class InitialDataLoader implements CommandLineRunner {

    private final BicycleService bicycleService;

    public InitialDataLoader(BicycleService bicycleService) {
        this.bicycleService = bicycleService;
    }

    @Override
    public void run(String... args) {
        bicycleService.registerBicycle("Caloi Elite",   15.0);
        bicycleService.registerBicycle("Monark Urbana", 12.0);
        bicycleService.registerBicycle("Sense Bike",    18.0);
        bicycleService.registerBicycle("Caloi Speed",   20.0);
        bicycleService.registerBicycle("Houston Bike",  10.0);
    }
}
