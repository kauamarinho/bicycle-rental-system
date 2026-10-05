package org.example.controller;

import org.example.controller.dto.ApiDtos.CustomerRequest;
import org.example.controller.dto.ApiDtos.CustomerResponse;
import org.example.domain.exception.NotFoundException;
import org.example.domain.model.Customer;
import org.example.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse register(@RequestBody CustomerRequest request) {
        Customer customer = customerService.registerCustomer(request.name(), request.cpf(), request.email());
        return CustomerResponse.from(customer);
    }

    @GetMapping
    public List<CustomerResponse> list() {
        return customerService.findAll().stream().map(CustomerResponse::from).toList();
    }

    @GetMapping("/{id}")
    public CustomerResponse get(@PathVariable int id) {
        Customer customer = customerService.findById(id);
        if (customer == null) {
            throw new NotFoundException("Customer not found.");
        }
        return CustomerResponse.from(customer);
    }
}
