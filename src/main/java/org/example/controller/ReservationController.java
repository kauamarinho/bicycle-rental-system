package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.ApiDtos.ReservationRequest;
import org.example.dto.ApiDtos.ReservationResponse;
import org.example.domain.exception.NotFoundException;
import org.example.domain.model.Bicycle;
import org.example.domain.model.Customer;
import org.example.service.BicycleService;
import org.example.service.CustomerService;
import org.example.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final CustomerService customerService;
    private final BicycleService bicycleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse reserve(@RequestBody ReservationRequest request) {
        Customer customer = customerService.findById(request.customerId());
        if (customer == null) {
            throw new NotFoundException("Customer not found.");
        }
        Bicycle bicycle = bicycleService.findById(request.bicycleId());
        if (bicycle == null) {
            throw new NotFoundException("Bicycle not found.");
        }
        return ReservationResponse.from(
                reservationService.makeReservation(customer, bicycle, request.reservationDate()));
    }

    @PostMapping("/{id}/cancel")
    public ReservationResponse cancel(@PathVariable int id) {
        if (reservationService.findById(id) == null) {
            throw new NotFoundException("Reservation not found.");
        }
        reservationService.cancelReservation(id);
        return ReservationResponse.from(reservationService.findById(id));
    }

    @GetMapping
    public List<ReservationResponse> list() {
        return reservationService.findAll().stream().map(ReservationResponse::from).toList();
    }
}
