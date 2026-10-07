package org.example.service;

import org.example.domain.exception.RentalException;
import org.example.domain.model.*;
import org.example.domain.vo.*;
import org.example.domain.enums.*;
import org.example.repository.inmemory.InMemoryRentalRepository;
import org.example.repository.RentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RentalServiceTest {

    private RentalService rentalService;
    private Customer customer;
    private Bicycle bicycle;

    @BeforeEach
    void setUp() {
        RentalRepository rentalRepository = new InMemoryRentalRepository();
        rentalService = new RentalService(rentalRepository);
        customer = new Customer(1, "Ana", new Cpf("12345678901"), new Email("ana@email.com"),
                new Address("01001000", "Praça da Sé", "Sé", "São Paulo", "SP"));
        bicycle = new Bicycle(1, "Aro 29", BicycleStatus.AVAILABLE, 5.0);
    }

    @Test
    void rentBicycle_withAvailableBicycle_shouldCreateRentalAndChangeBicycleStatus() {
        Rental rental = rentalService.rentBicycle(customer, bicycle, LocalDate.of(2026, 8, 12));

        assertEquals(RentalStatus.IN_PROGRESS, rental.getStatus());
        assertEquals(BicycleStatus.RENTED, bicycle.getStatus());
        assertEquals(1, rentalService.findAll().size());
    }

    @Test
    void rentBicycle_withAlreadyRentedBicycle_shouldThrowRentalException() {
        bicycle.changeStatus(BicycleStatus.RENTED);

        assertThrows(RentalException.class,
                () -> rentalService.rentBicycle(customer, bicycle, LocalDate.of(2026, 8, 12)));
    }

    @Test
    void rentBicycle_withRemovedBicycle_shouldThrowRentalException() {
        bicycle.changeStatus(BicycleStatus.REMOVED);

        assertThrows(RentalException.class,
                () -> rentalService.rentBicycle(customer, bicycle, LocalDate.of(2026, 8, 12)));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void returnBicycle_withHoursLessThanOrEqualToZero_shouldThrowRentalException(int hours) {
        Rental rental = rentalService.rentBicycle(customer, bicycle, LocalDate.of(2026, 8, 12));

        assertThrows(RentalException.class,
                () -> rentalService.returnBicycle(rental, LocalDate.of(2026, 8, 12), hours));
    }

    @Test
    void returnBicycle_withAlreadyFinishedRental_shouldThrowRentalException() {
        Rental rental = rentalService.rentBicycle(customer, bicycle, LocalDate.of(2026, 8, 12));
        rentalService.returnBicycle(rental, LocalDate.of(2026, 8, 12), 3);

        assertThrows(RentalException.class,
                () -> rentalService.returnBicycle(rental, LocalDate.of(2026, 8, 13), 2));
    }

    @Test
    void returnBicycle_valid_shouldCalculateTotalAmountAndMakeBicycleAvailable() {
        Rental rental = rentalService.rentBicycle(customer, bicycle, LocalDate.of(2026, 8, 12));

        rentalService.returnBicycle(rental, LocalDate.of(2026, 8, 12), 4);

        assertEquals(20.0, rental.getTotalAmount());
        assertEquals(RentalStatus.FINISHED, rental.getStatus());
        assertEquals(BicycleStatus.AVAILABLE, bicycle.getStatus());
    }
}
