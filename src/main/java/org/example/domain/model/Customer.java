package org.example.domain.model;

import jakarta.persistence.*;
import org.example.domain.vo.Cpf;
import org.example.domain.vo.Email;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer implements Registrable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    // stored through CpfConverter / EmailConverter (autoApply)
    @Column(nullable = false, unique = true, length = 11)
    private Cpf cpf;

    @Column(nullable = false)
    private Email email;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "customer")
    private List<Reservation> reservations = new ArrayList<>();

    protected Customer() {
        // required by JPA
    }

    public Customer(String name, Cpf cpf, Email email, Address address) {
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.address = address;
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
    public Integer getId() {
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
