package org.example.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicInteger;
import org.example.domain.model.Bicycle;
import org.example.domain.enums.BicycleStatus;
import org.example.repository.BicycleRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BicycleService {

    private final BicycleRepository bicycleRepository;
    private final AtomicInteger nextId = new AtomicInteger(1);

    public Bicycle registerBicycle(String model, double hourlyRate) {
        Bicycle bicycle = new Bicycle(nextId.getAndIncrement(), model, BicycleStatus.AVAILABLE, hourlyRate);
        bicycleRepository.save(bicycle);
        return bicycle;
    }

    public List<Bicycle> findAll() {
        return bicycleRepository.findAll();
    }

    public Bicycle findById(int id) {
        return bicycleRepository.findById(id).orElse(null);
    }
}
