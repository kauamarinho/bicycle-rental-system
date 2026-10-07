package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.client.CepClient;
import org.example.domain.exception.NotFoundException;
import org.example.domain.model.Address;
import org.example.domain.vo.Cep;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final CepClient cepClient;

    public Address findByCep(String cep) {
        return cepClient.findByCep(new Cep(cep))
                .orElseThrow(() -> new NotFoundException("CEP not found."));
    }
}
