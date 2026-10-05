package org.example.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicInteger;
import org.example.domain.exception.RentalException;
import org.example.domain.model.Bicycle;
import org.example.domain.model.Customer;
import org.example.domain.model.Rental;
import org.example.domain.enums.BicycleStatus;
import org.example.domain.enums.RentalStatus;
import org.example.repository.RentalRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class RentalService {

    private RentalRepository rentalRepository;
    private final AtomicInteger nextId = new AtomicInteger(1);

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public Rental rentBicycle(Customer customer, Bicycle bicycle, LocalDate pickupDate) {

        if (bicycle.getStatus() == BicycleStatus.RENTED) {
            throw new RentalException("Bicycle is already rented.");
        }

        if (bicycle.getStatus() == BicycleStatus.REMOVED) {
            throw new RentalException("Bicycle has been removed from the system.");
        }

        Rental rental = new Rental(
                nextId.getAndIncrement(),
                customer,
                bicycle,
                pickupDate
        );

        rentalRepository.save(rental);

        return rental;
    }

    public void returnBicycle(Rental rental, LocalDate returnDate, int hoursUsed) {

        if (rental.getStatus() != RentalStatus.IN_PROGRESS) {
            throw new RentalException("This rental is not in progress.");
        }

        if (hoursUsed <= 0) {
            throw new RentalException("Hours used must be greater than zero.");
        }

        rental.finishRental(returnDate, hoursUsed);
    }

    public Rental findById(int id) {
        return rentalRepository.findById(id).orElse(null);
    }

    public List<Rental> findAll() {
        return rentalRepository.findAll();
    }
}
