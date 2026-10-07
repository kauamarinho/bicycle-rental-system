package org.example.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicInteger;
import org.example.domain.exception.RentalException;
import org.example.domain.model.Bicycle;
import org.example.domain.model.Customer;
import org.example.domain.model.Reservation;
import org.example.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final AtomicInteger nextId = new AtomicInteger(1);

    public Reservation makeReservation(Customer customer, Bicycle bicycle, LocalDate reservationDate) {
        if (!bicycle.isAvailable()) {
            throw new RentalException("Bicycle is not available for reservation.");
        }
        Reservation reservation = new Reservation(nextId.getAndIncrement(), customer, bicycle, reservationDate);
        customer.addReservation(reservation);
        reservationRepository.save(reservation);
        return reservation;
    }

    public void cancelReservation(int reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RentalException("Reservation not found."));
        reservation.cancel();
    }

    public Reservation findById(int id) {
        return reservationRepository.findById(id).orElse(null);
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }
}
