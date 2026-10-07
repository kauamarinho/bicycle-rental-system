package org.example.domain.model;

import org.example.domain.vo.Cpf;
import org.example.domain.vo.Email;

import java.util.ArrayList;
import java.util.List;

public class Customer implements Registrable {

    private int id;
    private String name;
    private Cpf cpf;
    private Email email;
    private Address address;
    private List<Reservation> reservations;

    public Customer(int id, String name, Cpf cpf, Email email, Address address) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.address = address;
        this.reservations = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    @Override
    public String displayData() {
        return "ID: " + id
                + " | Name: " + name
                + " | CPF: " + cpf
                + " | Email: " + email
                + " | CEP: " + address.cep();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getCpf() {
        return cpf.getValue();
    }

    public String getEmail() {
        return email.getValue();
    }

    public Address getAddress() {
        return address;
    }
}
