package org.example.domain.model;

public class Employee implements Registrable {

    private int id;
    private String name;

    public Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String registerRental() {
        return "Rental registered by employee " + name + ".";
    }

    public String registerReturn() {
        return "Return registered by employee " + name + ".";
    }

    public String viewRentals() {
        return "Rental lookup performed by employee " + name + ".";
    }

    @Override
    public String displayData() {
        return "Employee ID: " + id + " | Name: " + name;
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
