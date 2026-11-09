package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.enums.PaymentMethod;
import org.example.domain.enums.RentalStatus;
import org.example.domain.exception.RentalException;
import org.example.domain.model.Payment;
import org.example.domain.model.Rental;
import org.example.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RentalService rentalService;

    @Transactional
    public Payment makePayment(int rentalId, PaymentMethod paymentMethod) {
        Rental rental = rentalService.findById(rentalId);

        if (rental.getStatus() != RentalStatus.FINISHED) {
            throw new RentalException("The rental must be finished before a payment can be made.");
        }

        if (paymentRepository.existsByRentalId(rental.getId())) {
            throw new RentalException("This rental has already been paid.");
        }

        return paymentRepository.save(new Payment(rental, paymentMethod));
    }
}
