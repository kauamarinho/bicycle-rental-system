package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.ApiDtos.ReservationRequest;
import org.example.dto.ApiDtos.ReservationResponse;
import org.example.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse reserve(@RequestBody ReservationRequest request) {
        return ReservationResponse.from(reservationService.makeReservation(
                request.customerId(), request.bicycleId(), request.reservationDate()));
    }

    @PostMapping("/{id}/cancel")
    public ReservationResponse cancel(@PathVariable int id) {
        return ReservationResponse.from(reservationService.cancelReservation(id));
    }

    @GetMapping
    public List<ReservationResponse> list() {
        return reservationService.findAll().stream().map(ReservationResponse::from).toList();
    }
}
