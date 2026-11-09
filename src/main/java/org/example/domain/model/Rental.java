package org.example.domain.model;

import jakarta.persistence.*;
import org.example.domain.enums.BicycleStatus;
import org.example.domain.enums.RentalStatus;

import java.time.LocalDate;

@Entity
@Table(name = "rentals")
public class Rental {

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
    private LocalDate pickupDate;

    private LocalDate returnDate;
    private int hoursUsed;
    private double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatus status;

    protected Rental() {
        // required by JPA
    }

    public Rental(Customer customer, Bicycle bicycle, LocalDate pickupDate) {
        this.customer = customer;
        this.bicycle = bicycle;
        this.pickupDate = pickupDate;
        this.status = RentalStatus.IN_PROGRESS;
        this.bicycle.changeStatus(BicycleStatus.RENTED);
    }

    public void finishRental(LocalDate returnDate, int hoursUsed) {
        this.returnDate = returnDate;
        this.hoursUsed = hoursUsed;
        this.totalAmount = hoursUsed * bicycle.getHourlyRate();
        this.status = RentalStatus.FINISHED;
        this.bicycle.changeStatus(BicycleStatus.AVAILABLE);
    }

    public String displayData() {
        return "Rental ID: " + id
                + " | Customer: " + customer.getName()
                + " | Bicycle: " + bicycle.getModel()
                + " | Pickup: " + pickupDate
                + " | Status: " + status;
    }

    public Integer getId() { return id; }
    public Customer getCustomer() { return customer; }
    public Bicycle getBicycle() { return bicycle; }
    public LocalDate getPickupDate() { return pickupDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public int getHoursUsed() { return hoursUsed; }
    public double getTotalAmount() { return totalAmount; }
    public RentalStatus getStatus() { return status; }
}
