package org.example.service;

import org.example.domain.enums.BicycleStatus;
import org.example.domain.enums.RentalStatus;
import org.example.domain.exception.RentalException;
import org.example.domain.model.Address;
import org.example.domain.model.Bicycle;
import org.example.domain.model.Customer;
import org.example.domain.model.Rental;
import org.example.domain.vo.Cpf;
import org.example.domain.vo.Email;
import org.example.repository.RentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    private static final LocalDate DAY = LocalDate.of(2026, 8, 12);

    @Mock private RentalRepository rentalRepository;
    @Mock private CustomerService customerService;
    @Mock private BicycleService bicycleService;

    private RentalService rentalService;
    private Customer customer;
    private Bicycle bicycle;

    @BeforeEach
    void setUp() {
        rentalService = new RentalService(rentalRepository, customerService, bicycleService);
        customer = new Customer("Ana", new Cpf("12345678901"), new Email("ana@email.com"),
                new Address("01001000", "Praça da Sé", "Sé", "São Paulo", "SP"));
        bicycle = new Bicycle("Aro 29", BicycleStatus.AVAILABLE, 5.0);
    }

    private void givenCustomerAndBicycle() {
        when(customerService.findById(1)).thenReturn(customer);
        when(bicycleService.findById(1)).thenReturn(bicycle);
    }

    @Test
    void rentBicycle_withAvailableBicycle_shouldCreateRentalAndChangeBicycleStatus() {
        givenCustomerAndBicycle();
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rental rental = rentalService.rentBicycle(1, 1, DAY);

        assertEquals(RentalStatus.IN_PROGRESS, rental.getStatus());
        assertEquals(BicycleStatus.RENTED, bicycle.getStatus());
        verify(rentalRepository).save(rental);
    }

    @ParameterizedTest
    @EnumSource(value = BicycleStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "AVAILABLE")
    void rentBicycle_withUnavailableBicycle_shouldThrowRentalException(BicycleStatus status) {
        givenCustomerAndBicycle();
        bicycle.changeStatus(status);

        assertThrows(RentalException.class, () -> rentalService.rentBicycle(1, 1, DAY));
        verify(rentalRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void returnBicycle_withHoursLessThanOrEqualToZero_shouldThrowRentalException(int hours) {
        when(rentalRepository.findById(1)).thenReturn(Optional.of(new Rental(customer, bicycle, DAY)));

        assertThrows(RentalException.class, () -> rentalService.returnBicycle(1, DAY, hours));
    }

    @Test
    void returnBicycle_withAlreadyFinishedRental_shouldThrowRentalException() {
        Rental rental = new Rental(customer, bicycle, DAY);
        rental.finishRental(DAY, 3);
        when(rentalRepository.findById(1)).thenReturn(Optional.of(rental));

        assertThrows(RentalException.class, () -> rentalService.returnBicycle(1, DAY.plusDays(1), 2));
    }

    @Test
    void returnBicycle_valid_shouldCalculateTotalAmountAndMakeBicycleAvailable() {
        when(rentalRepository.findById(1)).thenReturn(Optional.of(new Rental(customer, bicycle, DAY)));

        Rental rental = rentalService.returnBicycle(1, DAY, 4);

        assertEquals(20.0, rental.getTotalAmount());
        assertEquals(RentalStatus.FINISHED, rental.getStatus());
        assertEquals(BicycleStatus.AVAILABLE, bicycle.getStatus());
    }
}
