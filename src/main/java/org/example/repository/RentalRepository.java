package org.example.repository;

import org.example.domain.model.Rental;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Integer> {

    // customer and bicycle are fetched together so the API can map responses
    // outside the transaction (open-in-view is disabled) without N+1 queries
    @Override
    @EntityGraph(attributePaths = {"customer", "bicycle"})
    Optional<Rental> findById(Integer id);

    @Override
    @EntityGraph(attributePaths = {"customer", "bicycle"})
    List<Rental> findAll();
}
