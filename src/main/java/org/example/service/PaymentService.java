package org.example.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicInteger;
import org.example.domain.exception.RentalException;
import org.example.domain.enums.PaymentMethod;
import org.example.domain.model.Rental;
import org.example.domain.model.Payment;
import org.example.domain.enums.RentalStatus;

@Service
public class PaymentService {

    private final AtomicInteger nextId = new AtomicInteger(1);

    public Payment makePayment(Rental rental, PaymentMethod paymentMethod) {
        if (rental.getStatus() != RentalStatus.FINISHED) {
            throw new RentalException("The rental must be finished before a payment can be made.");
        }
        Payment payment = new Payment(nextId.getAndIncrement(), rental, paymentMethod);
        return payment;
    }
}
