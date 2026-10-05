package org.example.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicInteger;
import org.example.domain.model.Customer;
import org.example.domain.vo.Cpf;
import org.example.domain.vo.Email;
import org.example.repository.CustomerRepository;

import java.util.List;

@Service
public class CustomerService {

    private CustomerRepository customerRepository;
    private final AtomicInteger nextId = new AtomicInteger(1);

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer registerCustomer(String name, String cpf, String email) {

        Customer customer = new Customer(
                nextId.getAndIncrement(),
                name,
                new Cpf(cpf),
                new Email(email)
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
