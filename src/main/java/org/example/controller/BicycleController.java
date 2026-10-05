package org.example.controller;

import org.example.controller.dto.ApiDtos.BicycleResponse;
import org.example.domain.exception.NotFoundException;
import org.example.domain.model.Bicycle;
import org.example.service.BicycleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bicycles")
public class BicycleController {

    private final BicycleService bicycleService;

    public BicycleController(BicycleService bicycleService) {
        this.bicycleService = bicycleService;
    }

    @GetMapping
    public List<BicycleResponse> list() {
        return bicycleService.findAll().stream().map(BicycleResponse::from).toList();
    }

    @GetMapping("/{id}")
    public BicycleResponse get(@PathVariable int id) {
        Bicycle bicycle = bicycleService.findById(id);
        if (bicycle == null) {
            throw new NotFoundException("Bicycle not found.");
        }
        return BicycleResponse.from(bicycle);
    }
}
