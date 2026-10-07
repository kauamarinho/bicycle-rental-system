package org.example.client;

import org.example.domain.model.Address;
import org.example.domain.vo.Cep;

import java.util.Optional;

/** Looks up the address of a CEP in an external service. */
public interface CepClient {

    Optional<Address> findByCep(Cep cep);
}
