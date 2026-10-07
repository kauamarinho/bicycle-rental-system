package org.example.client.viacep;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.RequiredArgsConstructor;
import org.example.client.CepClient;
import org.example.domain.exception.AddressLookupException;
import org.example.domain.model.Address;
import org.example.domain.vo.Cep;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

/** {@link CepClient} backed by the public ViaCEP API (https://viacep.com.br). */
@Component
@RequiredArgsConstructor
public class ViaCepClient implements CepClient {

    private final RestClient restClient;

    @Override
    public Optional<Address> findByCep(Cep cep) {
        ViaCepResponse response;
        try {
            response = restClient.get()
                    .uri("/{cep}/json/", cep.getValue())
                    .retrieve()
                    .body(ViaCepResponse.class);
        } catch (RestClientException e) {
            throw new AddressLookupException("Could not reach the CEP lookup service.");
        }

        // ViaCEP answers 200 with {"erro": "true"} when the CEP does not exist
        if (response == null || Boolean.parseBoolean(response.erro())) {
            return Optional.empty();
        }

        return Optional.of(new Address(
                cep.getValue(),
                response.logradouro(),
                response.bairro(),
                response.localidade(),
                response.uf()));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ViaCepResponse(String cep, String logradouro, String bairro,
                          String localidade, String uf, String erro) {}
}
