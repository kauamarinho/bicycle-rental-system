package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.exception.NotFoundException;
import org.example.domain.exception.RentalException;
import org.example.domain.model.Bicycle;
import org.example.domain.model.Customer;
import org.example.domain.model.Reservation;
import org.example.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomerService customerService;
    private final BicycleService bicycleService;

    @Transactional
    public Reservation makeReservation(int customerId, int bicycleId, LocalDate reservationDate) {
        Customer customer = customerService.findById(customerId);
        Bicycle bicycle = bicycleService.findById(bicycleId);

        if (!bicycle.isAvailable()) {
            throw new RentalException("Bicycle is not available for reservation.");
        }

        Reservation reservation = new Reservation(customer, bicycle, reservationDate);
        customer.addReservation(reservation);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation cancelReservation(int reservationId) {
        Reservation reservation = findById(reservationId);
        reservation.cancel();
        return reservation;
    }

    @Transactional(readOnly = true)
    public Reservation findById(int id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found."));
    }

    @Transactional(readOnly = true)
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }
}
