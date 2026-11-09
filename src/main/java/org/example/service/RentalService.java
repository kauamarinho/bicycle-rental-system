package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.enums.BicycleStatus;
import org.example.domain.enums.RentalStatus;
import org.example.domain.exception.NotFoundException;
import org.example.domain.exception.RentalException;
import org.example.domain.model.Bicycle;
import org.example.domain.model.Customer;
import org.example.domain.model.Rental;
import org.example.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final CustomerService customerService;
    private final BicycleService bicycleService;

    @Transactional
    public Rental rentBicycle(int customerId, int bicycleId, LocalDate pickupDate) {
        Customer customer = customerService.findById(customerId);
        Bicycle bicycle = bicycleService.findById(bicycleId);

        if (bicycle.getStatus() == BicycleStatus.RENTED) {
            throw new RentalException("Bicycle is already rented.");
        }

        if (bicycle.getStatus() == BicycleStatus.RESERVED) {
            throw new RentalException("Bicycle is reserved.");
        }

        if (bicycle.getStatus() == BicycleStatus.REMOVED) {
            throw new RentalException("Bicycle has been removed from the system.");
        }

        // the bicycle status change is persisted by dirty checking at commit
        return rentalRepository.save(new Rental(customer, bicycle, pickupDate));
    }

    @Transactional
    public Rental returnBicycle(int rentalId, LocalDate returnDate, int hoursUsed) {
        Rental rental = findById(rentalId);

        if (rental.getStatus() != RentalStatus.IN_PROGRESS) {
            throw new RentalException("This rental is not in progress.");
        }

        if (hoursUsed <= 0) {
            throw new RentalException("Hours used must be greater than zero.");
        }

        rental.finishRental(returnDate, hoursUsed);
        return rental;
    }

    @Transactional(readOnly = true)
    public Rental findById(int id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rental not found."));
    }

    @Transactional(readOnly = true)
    public List<Rental> findAll() {
        return rentalRepository.findAll();
    }
}
