package org.example.service;

import org.example.domain.exception.RentalException;
import org.example.domain.model.*;
import org.example.domain.vo.*;
import org.example.domain.enums.*;
import org.example.repository.inmemory.InMemoryReservationRepository;
import org.example.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {

    private ReservationService reservationService;
    private Customer customer;
    private Bicycle bicycle;

    @BeforeEach
    void setUp() {
        ReservationRepository reservationRepository = new InMemoryReservationRepository();
        reservationService = new ReservationService(reservationRepository);
        customer = new Customer(1, "Ana", new Cpf("12345678901"), new Email("ana@email.com"),
                new Address("01001000", "Praça da Sé", "Sé", "São Paulo", "SP"));
        bicycle = new Bicycle(1, "Aro 29", BicycleStatus.AVAILABLE, 5.0);
    }

    @Test
    void makeReservation_withAvailableBicycle_shouldCreateReservationAndChangeBicycleStatus() {
        Reservation reservation = reservationService.makeReservation(customer, bicycle, LocalDate.of(2026, 8, 12));

        assertEquals(BicycleStatus.RESERVED, bicycle.getStatus());
        assertEquals(ReservationStatus.ACTIVE, reservation.getStatus());
        assertEquals(1, customer.getReservations().size());
        assertEquals(1, reservationService.findAll().size());
    }

    @Test
    void makeReservation_withAlreadyRentedBicycle_shouldThrowRentalException() {
        bicycle.changeStatus(BicycleStatus.RENTED);

        assertThrows(RentalException.class,
                () -> reservationService.makeReservation(customer, bicycle, LocalDate.of(2026, 8, 12)));
    }

    @Test
    void makeReservation_withAlreadyReservedBicycle_shouldThrowRentalException() {
        bicycle.changeStatus(BicycleStatus.RESERVED);

        assertThrows(RentalException.class,
                () -> reservationService.makeReservation(customer, bicycle, LocalDate.of(2026, 8, 12)));
    }

    @Test
    void makeReservation_withRemovedBicycle_shouldThrowRentalException() {
        bicycle.changeStatus(BicycleStatus.REMOVED);

        assertThrows(RentalException.class,
                () -> reservationService.makeReservation(customer, bicycle, LocalDate.of(2026, 8, 12)));
    }

    @Test
    void cancelReservation_withExistingReservation_shouldChangeStatusToCancelledAndFreeBicycle() {
        Reservation reservation = reservationService.makeReservation(customer, bicycle, LocalDate.of(2026, 8, 12));

        reservationService.cancelReservation(reservation.getId());

        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        assertEquals(BicycleStatus.AVAILABLE, bicycle.getStatus());
    }

    @Test
    void cancelReservation_withNonExistentId_shouldThrowRentalException() {
        assertThrows(RentalException.class, () -> reservationService.cancelReservation(999));
    }
}
