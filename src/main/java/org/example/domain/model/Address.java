package org.example.domain.model;

import jakarta.persistence.Embeddable;

@Embeddable
public record Address(String cep, String street, String neighborhood, String city, String state) {
}
