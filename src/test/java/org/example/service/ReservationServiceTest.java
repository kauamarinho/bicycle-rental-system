package org.example.service;

import org.example.domain.enums.BicycleStatus;
import org.example.domain.enums.ReservationStatus;
import org.example.domain.exception.NotFoundException;
import org.example.domain.exception.RentalException;
import org.example.domain.model.Address;
import org.example.domain.model.Bicycle;
import org.example.domain.model.Customer;
import org.example.domain.model.Reservation;
import org.example.domain.vo.Cpf;
import org.example.domain.vo.Email;
import org.example.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    private static final LocalDate DAY = LocalDate.of(2026, 8, 12);

    @Mock private ReservationRepository reservationRepository;
    @Mock private CustomerService customerService;
    @Mock private BicycleService bicycleService;

    private ReservationService reservationService;
    private Customer customer;
    private Bicycle bicycle;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(reservationRepository, customerService, bicycleService);
        customer = new Customer("Ana", new Cpf("12345678901"), new Email("ana@email.com"),
                new Address("01001000", "Praça da Sé", "Sé", "São Paulo", "SP"));
        bicycle = new Bicycle("Aro 29", BicycleStatus.AVAILABLE, 5.0);
    }

    @Test
    void makeReservation_withAvailableBicycle_shouldCreateReservationAndChangeBicycleStatus() {
        when(customerService.findById(1)).thenReturn(customer);
        when(bicycleService.findById(1)).thenReturn(bicycle);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reservation reservation = reservationService.makeReservation(1, 1, DAY);

        assertEquals(BicycleStatus.RESERVED, bicycle.getStatus());
        assertEquals(ReservationStatus.ACTIVE, reservation.getStatus());
        assertEquals(1, customer.getReservations().size());
        verify(reservationRepository).save(reservation);
    }

    @ParameterizedTest
    @EnumSource(value = BicycleStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "AVAILABLE")
    void makeReservation_withUnavailableBicycle_shouldThrowRentalException(BicycleStatus status) {
        when(customerService.findById(1)).thenReturn(customer);
        when(bicycleService.findById(1)).thenReturn(bicycle);
        bicycle.changeStatus(status);

        assertThrows(RentalException.class, () -> reservationService.makeReservation(1, 1, DAY));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void cancelReservation_withExistingReservation_shouldChangeStatusToCancelledAndFreeBicycle() {
        Reservation reservation = new Reservation(customer, bicycle, DAY);
        when(reservationRepository.findById(1)).thenReturn(Optional.of(reservation));

        reservationService.cancelReservation(1);

        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        assertEquals(BicycleStatus.AVAILABLE, bicycle.getStatus());
    }

    @Test
    void cancelReservation_withNonExistentId_shouldThrowNotFoundException() {
        when(reservationRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reservationService.cancelReservation(999));
    }
}
