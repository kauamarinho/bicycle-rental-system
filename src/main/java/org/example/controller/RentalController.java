package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.ApiDtos.PaymentRequest;
import org.example.dto.ApiDtos.PaymentResponse;
import org.example.dto.ApiDtos.RentalRequest;
import org.example.dto.ApiDtos.RentalResponse;
import org.example.dto.ApiDtos.ReturnRequest;
import org.example.service.PaymentService;
import org.example.service.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;
    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RentalResponse rent(@RequestBody RentalRequest request) {
        return RentalResponse.from(
                rentalService.rentBicycle(request.customerId(), request.bicycleId(), request.pickupDate()));
    }

    @PostMapping("/{id}/return")
    public RentalResponse giveBack(@PathVariable int id, @RequestBody ReturnRequest request) {
        return RentalResponse.from(rentalService.returnBicycle(id, request.returnDate(), request.hoursUsed()));
    }

    @PostMapping("/{id}/payment")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse pay(@PathVariable int id, @RequestBody PaymentRequest request) {
        return PaymentResponse.from(paymentService.makePayment(id, request.paymentMethod()));
    }

    @GetMapping
    public List<RentalResponse> list() {
        return rentalService.findAll().stream().map(RentalResponse::from).toList();
    }

    @GetMapping("/{id}")
    public RentalResponse get(@PathVariable int id) {
        return RentalResponse.from(rentalService.findById(id));
    }
}
