package  financial_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import  financial_app.dto.ClientRequestDTO;
import  financial_app.entity.Client;
import  financial_app.service.ClientService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateClient() throws Exception {
        ClientRequestDTO request = new ClientRequestDTO();
        request.setIdentificationType("CC");
        request.setIdentificationNumber("1075289632");
        request.setNames("Carlos");
        request.setLastName("Villamil");
        request.setEmail("carlos@example.com");
        request.setBirthDate(LocalDate.of(2000, 5, 15));

        Client client = Client.builder()
                .id(1L)
                .identificationType("CC")
                .identificationNumber("1075289632")
                .names("Carlos")
                .lastName("Villamil")
                .email("carlos@example.com")
                .birthDate(LocalDate.of(2000, 5, 15))
                .build();

        Mockito.when(clientService.createClient(any(ClientRequestDTO.class))).thenReturn(client);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.names").value("Carlos"))
                .andExpect(jsonPath("$.email").value("carlos@example.com"));
    }

    @Test
    void shouldGetAllClients() throws Exception {
        Client client = Client.builder()
                .id(1L)
                .names("Carlos")
                .lastName("Villamil")
                .email("carlos@example.com")
                .build();

        Mockito.when(clientService.getAllClients()).thenReturn(List.of(client));

        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].names").value("Carlos"));
    }

    @Test
    void shouldGetClientById() throws Exception {
        Client client = Client.builder()
                .id(1L)
                .names("Carlos")
                .lastName("Villamil")
                .email("carlos@example.com")
                .build();

        Mockito.when(clientService.getClientById(1L)).thenReturn(client);

        mockMvc.perform(get("/api/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.names").value("Carlos"));
    }

    @Test
    void shouldDeleteClient() throws Exception {
        mockMvc.perform(delete("/api/clients/1"))
                .andExpect(status().isNoContent());
    }
}