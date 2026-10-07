package org.example.service;

import org.example.domain.exception.RentalException;
import org.example.domain.model.*;
import org.example.domain.vo.*;
import org.example.domain.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    private PaymentService paymentService;
    private Customer customer;
    private Bicycle bicycle;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService();
        customer = new Customer(1, "Ana", new Cpf("12345678901"), new Email("ana@email.com"),
                new Address("01001000", "Praça da Sé", "Sé", "São Paulo", "SP"));
        bicycle = new Bicycle(1, "Aro 29", BicycleStatus.AVAILABLE, 5.0);
    }

    @Test
    void makePayment_withRentalInProgress_shouldThrowRentalException() {
        Rental rental = new Rental(1, customer, bicycle, LocalDate.of(2026, 8, 12));

        assertThrows(RentalException.class,
                () -> paymentService.makePayment(rental, PaymentMethod.PIX));
    }

    @Test
    void makePayment_withFinishedRental_shouldGenerateConfirmedPaymentWithRentalAmount() {
        Rental rental = new Rental(1, customer, bicycle, LocalDate.of(2026, 8, 12));
        rental.finishRental(LocalDate.of(2026, 8, 12), 3);

        Payment payment = paymentService.makePayment(rental, PaymentMethod.PIX);

        assertEquals("Confirmed", payment.getStatus());
        assertEquals(15.0, payment.getAmount());
    }

    @Test
    void makePayment_shouldGenerateReceiptWithGivenPaymentMethod() {
        Rental rental = new Rental(1, customer, bicycle, LocalDate.of(2026, 8, 12));
        rental.finishRental(LocalDate.of(2026, 8, 12), 2);

        Payment payment = paymentService.makePayment(rental, PaymentMethod.CREDIT_CARD);

        assertTrue(payment.generateReceipt().contains("CREDIT_CARD"));
    }
}
