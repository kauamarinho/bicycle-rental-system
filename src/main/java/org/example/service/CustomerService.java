package org.example.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicInteger;
import org.example.domain.model.Address;
import org.example.domain.model.Customer;
import org.example.domain.vo.Cpf;
import org.example.domain.vo.Email;
import org.example.repository.CustomerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AddressService addressService;
    private final AtomicInteger nextId = new AtomicInteger(1);

    public Customer registerCustomer(String name, String cpf, String email, String cep) {

        // validate local data first so the external lookup is only called for valid input
        Cpf validCpf = new Cpf(cpf);
        Email validEmail = new Email(email);
        Address address = addressService.findByCep(cep);

        Customer customer = new Customer(
                nextId.getAndIncrement(),
                name,
                validCpf,
                validEmail,
                address
        );

        customerRepository.save(customer);

        return customer;
    }

    public Customer findById(int id) {
        return customerRepository.findById(id).orElse(null);
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
}
