package org.example.client.viacep;

import org.example.domain.exception.AddressLookupException;
import org.example.domain.model.Address;
import org.example.domain.vo.Cep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ViaCepClientTest {

    private MockRestServiceServer server;
    private ViaCepClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://viacep.com.br/ws");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new ViaCepClient(builder.build());
    }

    @Test
    void findByCep_withExistingCep_shouldMapAddress() {
        server.expect(requestTo("https://viacep.com.br/ws/01001000/json/"))
                .andRespond(withSuccess("""
                        {"cep": "01001-000", "logradouro": "Praça da Sé", "complemento": "lado ímpar",
                         "bairro": "Sé", "localidade": "São Paulo", "uf": "SP", "ibge": "3550308"}
                        """, MediaType.APPLICATION_JSON));

        Optional<Address> address = client.findByCep(new Cep("01001-000"));

        assertEquals(Optional.of(new Address("01001000", "Praça da Sé", "Sé", "São Paulo", "SP")), address);
        server.verify();
    }

    @Test
    void findByCep_whenViaCepAnswersErro_shouldReturnEmpty() {
        server.expect(requestTo("https://viacep.com.br/ws/99999999/json/"))
                .andRespond(withSuccess("{\"erro\": \"true\"}", MediaType.APPLICATION_JSON));

        assertTrue(client.findByCep(new Cep("99999999")).isEmpty());
    }

    @Test
    void findByCep_whenServiceFails_shouldThrowAddressLookupException() {
        server.expect(requestTo("https://viacep.com.br/ws/01001000/json/"))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertThrows(AddressLookupException.class, () -> client.findByCep(new Cep("01001000")));
    }
}
