package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.domain.enums.BicycleStatus;
import org.example.domain.enums.PaymentMethod;
import org.example.domain.enums.RentalStatus;
import org.example.domain.model.*;
import org.example.domain.vo.Cpf;
import org.example.domain.vo.Email;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Verifies the JPA mappings (converters, embeddable, relationships) against a real database (H2). */
@DataJpaTest
class RepositoryMappingTest {

    private static final Address SE = new Address("01001000", "Praça da Sé", "Sé", "São Paulo", "SP");

    @Autowired private CustomerRepository customerRepository;
    @Autowired private BicycleRepository bicycleRepository;
    @Autowired private RentalRepository rentalRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private EntityManager entityManager;

    @Test
    void customer_shouldRoundTripValueObjectsAndEmbeddedAddress() {
        Customer saved = customerRepository.save(
                new Customer("Ana", new Cpf("123.456.789-01"), new Email("ana@email.com"), SE));
        entityManager.flush();
        entityManager.clear();

        Customer loaded = customerRepository.findById(saved.getId()).orElseThrow();

        assertNotNull(loaded.getId());
        assertEquals("12345678901", loaded.getCpf());
        assertEquals("ana@email.com", loaded.getEmail());
        assertEquals(SE, loaded.getAddress());
        assertTrue(customerRepository.existsByCpf(new Cpf("12345678901")));
    }

    @Test
    void customer_withDuplicateCpf_shouldViolateUniqueConstraint() {
        customerRepository.saveAndFlush(new Customer("Ana", new Cpf("12345678901"), new Email("ana@email.com"), SE));

        assertThrows(DataIntegrityViolationException.class, () -> customerRepository.saveAndFlush(
                new Customer("Bia", new Cpf("12345678901"), new Email("bia@email.com"), SE)));
    }

    @Test
    void rental_shouldPersistRelationshipsStatusAndPayment() {
        Customer customer = customerRepository.save(
                new Customer("Ana", new Cpf("12345678901"), new Email("ana@email.com"), SE));
        Bicycle bicycle = bicycleRepository.save(new Bicycle("Caloi", BicycleStatus.AVAILABLE, 10.0));
        Rental rental = rentalRepository.save(new Rental(customer, bicycle, LocalDate.of(2026, 8, 12)));
        rental.finishRental(LocalDate.of(2026, 8, 12), 2);
        paymentRepository.save(new Payment(rental, PaymentMethod.PIX));
        entityManager.flush();
        entityManager.clear();

        List<Rental> rentals = rentalRepository.findAll();
        entityManager.clear(); // detach: associations must already be loaded by the entity graph

        Rental loaded = rentals.getFirst();
        assertEquals(RentalStatus.FINISHED, loaded.getStatus());
        assertEquals(20.0, loaded.getTotalAmount());
        assertEquals("Ana", loaded.getCustomer().getName());
        assertEquals(BicycleStatus.AVAILABLE, loaded.getBicycle().getStatus());
        assertTrue(paymentRepository.existsByRentalId(loaded.getId()));
    }
}
