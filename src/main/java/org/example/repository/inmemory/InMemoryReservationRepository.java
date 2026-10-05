package org.example.repository.inmemory;

import org.springframework.stereotype.Repository;
import org.example.domain.model.Reservation;
import org.example.repository.ReservationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryReservationRepository implements ReservationRepository {

    private List<Reservation> reservations = new ArrayList<>();

    @Override
    public void save(Reservation reservation) {
        reservations.add(reservation);
    }

    @Override
    public List<Reservation> findAll() {
        return List.copyOf(reservations);
    }

    @Override
    public Optional<Reservation> findById(int id) {
        for (Reservation r : reservations) {
            if (r.getId() == id) return Optional.of(r);
        }
        return Optional.empty();
    }
}
