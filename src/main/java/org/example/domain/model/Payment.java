package org.example.domain.model;

import jakarta.persistence.*;
import org.example.domain.enums.PaymentMethod;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rental_id", nullable = false, unique = true)
    private Rental rental;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private String status;

    protected Payment() {
        // required by JPA
    }

    public Payment(Rental rental, PaymentMethod paymentMethod) {
        this.rental = rental;
        this.amount = rental.getTotalAmount();
        this.paymentMethod = paymentMethod;
        this.status = "Confirmed";
    }

    public String generateReceipt() {
        return "RECEIPT"
                + "\nCustomer: " + rental.getCustomer().getName()
                + "\nBicycle: " + rental.getBicycle().getModel()
                + "\nPayment method: " + paymentMethod
                + "\nAmount paid: $" + amount
                + "\nStatus: " + status;
    }

    public Integer getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getStatus() {
        return status;
    }
}
