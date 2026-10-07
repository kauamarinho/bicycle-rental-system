package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.ApiDtos.PaymentRequest;
import org.example.dto.ApiDtos.PaymentResponse;
import org.example.dto.ApiDtos.RentalRequest;
import org.example.dto.ApiDtos.RentalResponse;
import org.example.dto.ApiDtos.ReturnRequest;
import org.example.domain.exception.NotFoundException;
import org.example.domain.model.Bicycle;
import org.example.domain.model.Customer;
import org.example.domain.model.Rental;
import org.example.service.BicycleService;
import org.example.service.CustomerService;
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
    private final CustomerService customerService;
    private final BicycleService bicycleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RentalResponse rent(@RequestBody RentalRequest request) {
        Customer customer = customerService.findById(request.customerId());
        if (customer == null) {
            throw new NotFoundException("Customer not found.");
        }
        Bicycle bicycle = bicycleService.findById(request.bicycleId());
        if (bicycle == null) {
            throw new NotFoundException("Bicycle not found.");
        }
        return RentalResponse.from(rentalService.rentBicycle(customer, bicycle, request.pickupDate()));
    }

    @PostMapping("/{id}/return")
    public RentalResponse giveBack(@PathVariable int id, @RequestBody ReturnRequest request) {
        Rental rental = findRental(id);
        rentalService.returnBicycle(rental, request.returnDate(), request.hoursUsed());
        return RentalResponse.from(rental);
    }

    @PostMapping("/{id}/payment")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse pay(@PathVariable int id, @RequestBody PaymentRequest request) {
        return PaymentResponse.from(paymentService.makePayment(findRental(id), request.paymentMethod()));
    }

    @GetMapping
    public List<RentalResponse> list() {
        return rentalService.findAll().stream().map(RentalResponse::from).toList();
    }

    @GetMapping("/{id}")
    public RentalResponse get(@PathVariable int id) {
        return RentalResponse.from(findRental(id));
    }

    private Rental findRental(int id) {
        Rental rental = rentalService.findById(id);
        if (rental == null) {
            throw new NotFoundException("Rental not found.");
        }
        return rental;
    }
}
