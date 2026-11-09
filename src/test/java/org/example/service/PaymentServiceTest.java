package org.example.service;

import org.example.domain.enums.BicycleStatus;
import org.example.domain.enums.PaymentMethod;
import org.example.domain.exception.RentalException;
import org.example.domain.model.*;
import org.example.domain.vo.Cpf;
import org.example.domain.vo.Email;
import org.example.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private static final LocalDate DAY = LocalDate.of(2026, 8, 12);

    @Mock private PaymentRepository paymentRepository;
    @Mock private RentalService rentalService;

    private PaymentService paymentService;
    private Rental rental;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentRepository, rentalService);
        Customer customer = new Customer("Ana", new Cpf("12345678901"), new Email("ana@email.com"),
                new Address("01001000", "Praça da Sé", "Sé", "São Paulo", "SP"));
        rental = new Rental(customer, new Bicycle("Aro 29", BicycleStatus.AVAILABLE, 5.0), DAY);
        when(rentalService.findById(1)).thenReturn(rental);
    }

    @Test
    void makePayment_withRentalInProgress_shouldThrowRentalException() {
        assertThrows(RentalException.class, () -> paymentService.makePayment(1, PaymentMethod.PIX));
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void makePayment_withFinishedRental_shouldSaveConfirmedPaymentWithRentalAmount() {
        rental.finishRental(DAY, 3);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment payment = paymentService.makePayment(1, PaymentMethod.CREDIT_CARD);

        assertEquals("Confirmed", payment.getStatus());
        assertEquals(15.0, payment.getAmount());
        assertTrue(payment.generateReceipt().contains("CREDIT_CARD"));
        verify(paymentRepository).save(payment);
    }

    @Test
    void makePayment_withAlreadyPaidRental_shouldThrowRentalException() {
        rental.finishRental(DAY, 3);
        when(paymentRepository.existsByRentalId(any())).thenReturn(true);

        assertThrows(RentalException.class, () -> paymentService.makePayment(1, PaymentMethod.PIX));
        verify(paymentRepository, never()).save(any());
    }
}
