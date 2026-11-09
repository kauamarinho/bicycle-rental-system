package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.exception.DuplicateResourceException;
import org.example.domain.exception.NotFoundException;
import org.example.domain.model.Address;
import org.example.domain.model.Customer;
import org.example.domain.vo.Cpf;
import org.example.domain.vo.Email;
import org.example.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AddressService addressService;

    @Transactional
    public Customer registerCustomer(String name, String cpf, String email, String cep) {

        // validate local data first so the external lookup is only called for valid input
        Cpf validCpf = new Cpf(cpf);
        Email validEmail = new Email(email);

        if (customerRepository.existsByCpf(validCpf)) {
            throw new DuplicateResourceException("A customer with this CPF already exists.");
        }

        Address address = addressService.findByCep(cep);

        return customerRepository.save(new Customer(name, validCpf, validEmail, address));
    }

    @Transactional(readOnly = true)
    public Customer findById(int id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found."));
    }

    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
}
