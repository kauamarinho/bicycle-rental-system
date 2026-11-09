package org.example.domain.model;

import jakarta.persistence.*;
import org.example.domain.enums.BicycleStatus;

@Entity
@Table(name = "bicycles")
public class Bicycle implements Registrable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BicycleStatus status;

    @Column(nullable = false)
    private double hourlyRate;

    protected Bicycle() {
        // required by JPA
    }

    public Bicycle(String model, BicycleStatus status, double hourlyRate) {
        this.model = model;
        this.status = status;
        this.hourlyRate = hourlyRate;
    }

    public boolean isAvailable() {
        return status == BicycleStatus.AVAILABLE;
    }

    public void changeStatus(BicycleStatus status) {
        this.status = status;
    }

    public void changeHourlyRate(double newHourlyRate) {
        this.hourlyRate = newHourlyRate;
    }

    @Override
    public String displayData() {
        return "ID: " + id + " | Model: " + model + " | Status: " + status + " | Hourly rate: $" + hourlyRate;
    }

    @Override
    public Integer getId() { return id; }

    @Override
    public String getName() { return model; }

    public String getModel() { return model; }
    public BicycleStatus getStatus() { return status; }
    public double getHourlyRate() { return hourlyRate; }
}
