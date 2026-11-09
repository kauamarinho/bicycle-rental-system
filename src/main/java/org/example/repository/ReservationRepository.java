package org.example.repository;

import org.example.domain.model.Reservation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    @Override
    @EntityGraph(attributePaths = {"customer", "bicycle"})
    Optional<Reservation> findById(Integer id);

    @Override
    @EntityGraph(attributePaths = {"customer", "bicycle"})
    List<Reservation> findAll();
}
