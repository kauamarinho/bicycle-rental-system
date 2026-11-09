package org.example.config;

import lombok.RequiredArgsConstructor;
import org.example.repository.BicycleRepository;
import org.example.service.BicycleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds the database with the initial bicycles on first startup. Existing data is never touched.
 */
@Component
@RequiredArgsConstructor
public class InitialDataLoader implements CommandLineRunner {

    private final BicycleService bicycleService;
    private final BicycleRepository bicycleRepository;

    @Override
    public void run(String... args) {
        if (bicycleRepository.count() == 0) {
            bicycleService.registerBicycle("Caloi Elite",   15.0);
            bicycleService.registerBicycle("Monark Urbana", 12.0);
            bicycleService.registerBicycle("Sense Bike",    18.0);
            bicycleService.registerBicycle("Caloi Speed",   20.0);
            bicycleService.registerBicycle("Houston Bike",  10.0);
        }
    }
}
