package org.example.domain.model;

import org.example.domain.enums.BicycleStatus;

public class Administrator implements Registrable {

    private int id;
    private String name;

    public Administrator(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String changePrice(Bicycle bicycle, double newPrice) {
        bicycle.changeHourlyRate(newPrice);

        return "Administrator " + name +
                " changed the price to $" + newPrice + ".";
    }

    public String removeBicycle(Bicycle bicycle) {
        bicycle.changeStatus(BicycleStatus.REMOVED);

        return "Administrator " + name +
                " removed the bicycle.";
    }

    public String viewBicycles() {
        return "Administrator " + name +
                " viewed the bicycles.";
    }

    @Override
    public String displayData() {
        return "Admin ID: " + id + " | Name: " + name;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
