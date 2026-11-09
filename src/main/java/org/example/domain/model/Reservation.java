package org.example.domain.model;

import jakarta.persistence.*;
import org.example.domain.enums.BicycleStatus;
import org.example.domain.enums.ReservationStatus;

import java.time.LocalDate;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bicycle_id", nullable = false)
    private Bicycle bicycle;

    @Column(nullable = false)
    private LocalDate reservationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    protected Reservation() {
        // required by JPA
    }

    public Reservation(Customer customer, Bicycle bicycle, LocalDate reservationDate) {
        this.customer = customer;
        this.bicycle = bicycle;
        this.reservationDate = reservationDate;
        this.status = ReservationStatus.ACTIVE;
        this.bicycle.changeStatus(BicycleStatus.RESERVED);
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
        this.bicycle.changeStatus(BicycleStatus.AVAILABLE);
    }

    public String displayData() {
        return "Reservation ID: " + id
                + " | Customer: " + customer.getName()
                + " | Bicycle: " + bicycle.getModel()
                + " | Date: " + reservationDate
                + " | Status: " + status;
    }

    public Integer getId() { return id; }
    public Customer getCustomer() { return customer; }
    public Bicycle getBicycle() { return bicycle; }
    public LocalDate getReservationDate() { return reservationDate; }
    public ReservationStatus getStatus() { return status; }
}
