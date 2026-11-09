package org.example.config;

import lombok.RequiredArgsConstructor;
import org.example.repository.BicycleRepository;
import org.example.security.AppUserService;
import org.example.security.Role;
import org.example.service.BicycleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds the database on first startup: the initial bicycles and the admin user
 * (credentials from {@code security.admin.*}). Existing data is never touched.
 */
@Component
@RequiredArgsConstructor
public class InitialDataLoader implements CommandLineRunner {

    private final BicycleService bicycleService;
    private final BicycleRepository bicycleRepository;
    private final AppUserService appUserService;

    @Value("${security.admin.username}")
    private String adminUsername;

    @Value("${security.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        appUserService.createIfMissing(adminUsername, adminPassword, Role.ADMIN);

        if (bicycleRepository.count() == 0) {
            bicycleService.registerBicycle("Caloi Elite",   15.0);
            bicycleService.registerBicycle("Monark Urbana", 12.0);
            bicycleService.registerBicycle("Sense Bike",    18.0);
            bicycleService.registerBicycle("Caloi Speed",   20.0);
            bicycleService.registerBicycle("Houston Bike",  10.0);
        }
    }
}
