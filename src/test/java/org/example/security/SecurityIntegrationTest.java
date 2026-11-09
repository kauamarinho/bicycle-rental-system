package org.example.security;

import com.jayway.jsonpath.JsonPath;
import org.example.client.CepClient;
import org.example.domain.model.Address;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** End-to-end checks of the JWT security rules (admin user and bicycles are seeded by InitialDataLoader). */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private CepClient cepClient;

    @Test
    void publicEndpoints_shouldNotRequireToken() throws Exception {
        mockMvc.perform(get("/v1/bicycles")).andExpect(status().isOk());
    }

    @Test
    void protectedEndpoint_withoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/v1/customers")).andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_withInvalidToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/v1/customers").header(HttpHeaders.AUTHORIZATION, "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_withWrongPassword_shouldReturn401() throws Exception {
        mockMvc.perform(post("/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password."));
    }

    @Test
    void admin_shouldAccessProtectedEndpointsWithToken() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(get("/v1/customers").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void employee_canRegisterCustomersButCannotCreateUsers() throws Exception {
        String adminToken = login("admin", "admin123");
        mockMvc.perform(post("/v1/auth/users").header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"joao\",\"password\":\"secret123\",\"role\":\"EMPLOYEE\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("EMPLOYEE"));

        String employeeToken = login("joao", "secret123");

        mockMvc.perform(post("/v1/auth/users").header(HttpHeaders.AUTHORIZATION, "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"maria\",\"password\":\"secret123\",\"role\":\"ADMIN\"}"))
                .andExpect(status().isForbidden());

        when(cepClient.findByCep(any())).thenReturn(
                Optional.of(new Address("01310100", "Avenida Paulista", "Bela Vista", "São Paulo", "SP")));

        mockMvc.perform(post("/v1/customers").header(HttpHeaders.AUTHORIZATION, "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Ana\",\"cpf\":\"123.456.789-01\",\"email\":\"ana@email.com\",\"cep\":\"01310-100\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andExpect(jsonPath("$.address.city").value("São Paulo"));
    }

    private String login(String username, String password) throws Exception {
        String body = mockMvc.perform(post("/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.token");
    }
}
