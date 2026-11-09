package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.enums.BicycleStatus;
import org.example.domain.exception.NotFoundException;
import org.example.domain.model.Bicycle;
import org.example.repository.BicycleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BicycleService {

    private final BicycleRepository bicycleRepository;

    @Transactional
    public Bicycle registerBicycle(String model, double hourlyRate) {
        return bicycleRepository.save(new Bicycle(model, BicycleStatus.AVAILABLE, hourlyRate));
    }

    @Transactional(readOnly = true)
    public List<Bicycle> findAll() {
        return bicycleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Bicycle findById(int id) {
        return bicycleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bicycle not found."));
    }
}
