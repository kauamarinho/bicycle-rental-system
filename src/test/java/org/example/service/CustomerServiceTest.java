package org.example.service;

import org.example.domain.exception.DuplicateResourceException;
import org.example.domain.exception.InvalidCepException;
import org.example.domain.exception.InvalidCpfException;
import org.example.domain.exception.InvalidEmailException;
import org.example.domain.exception.NotFoundException;
import org.example.domain.model.Address;
import org.example.domain.model.Customer;
import org.example.domain.vo.Cpf;
import org.example.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private static final Address SE = new Address("01001000", "Praça da Sé", "Sé", "São Paulo", "SP");

    @Mock
    private CustomerRepository customerRepository;

    private CustomerService customerService;
    private final List<String> lookedUpCeps = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // fake CepClient: only "01001000" exists
        AddressService addressService = new AddressService(cep -> {
            lookedUpCeps.add(cep.getValue());
            return cep.getValue().equals(SE.cep()) ? Optional.of(SE) : Optional.empty();
        });
        customerService = new CustomerService(customerRepository, addressService);
    }

    @Test
    void registerCustomer_withValidData_shouldPersistNormalizedCustomer() {
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer customer = customerService.registerCustomer("Ana", "123.456.789-01", "ana@email.com", "01001-000");

        assertEquals("Ana", customer.getName());
        assertEquals("12345678901", customer.getCpf());
        assertEquals("ana@email.com", customer.getEmail());
        assertEquals(SE, customer.getAddress());
        verify(customerRepository).save(customer);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123",                  // invalid length
            "1234567890123",        // invalid length
            "abc.def.ghi-01",       // contains letters
            "1234567890a"           // contains a letter among the digits
    })
    void registerCustomer_withInvalidCpf_shouldThrowWithoutLookupOrPersisting(String invalidCpf) {
        assertThrows(InvalidCpfException.class,
                () -> customerService.registerCustomer("Ana", invalidCpf, "ana@email.com", "01001-000"));

        assertTrue(lookedUpCeps.isEmpty());
        verifyNoInteractions(customerRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ana-email.com",  // no @
            "ana@",           // no domain
            "@email.com",     // no user
            "ana@email"       // no TLD
    })
    void registerCustomer_withInvalidEmail_shouldThrowInvalidEmailException(String invalidEmail) {
        assertThrows(InvalidEmailException.class,
                () -> customerService.registerCustomer("Ana", "12345678901", invalidEmail, "01001-000"));

        verifyNoInteractions(customerRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "010010000", "0100100a"})
    void registerCustomer_withInvalidCep_shouldThrowWithoutCallingLookup(String invalidCep) {
        assertThrows(InvalidCepException.class,
                () -> customerService.registerCustomer("Ana", "12345678901", "ana@email.com", invalidCep));

        assertTrue(lookedUpCeps.isEmpty());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void registerCustomer_withUnknownCep_shouldThrowNotFoundAndNotPersist() {
        assertThrows(NotFoundException.class,
                () -> customerService.registerCustomer("Ana", "12345678901", "ana@email.com", "99999999"));

        verify(customerRepository, never()).save(any());
    }

    @Test
    void registerCustomer_withDuplicateCpf_shouldThrowWithoutLookupOrPersisting() {
        when(customerRepository.existsByCpf(new Cpf("12345678901"))).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> customerService.registerCustomer("Ana", "123.456.789-01", "ana@email.com", "01001-000"));

        assertTrue(lookedUpCeps.isEmpty());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void findById_withUnknownId_shouldThrowNotFound() {
        when(customerRepository.findById(42)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customerService.findById(42));
    }
}
