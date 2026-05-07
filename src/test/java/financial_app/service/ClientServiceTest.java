package  financial_app.service;

import  financial_app.dto.ClientRequestDTO;
import  financial_app.entity.Client;
import  financial_app.exception.BusinessException;
import  financial_app.exception.ResourceNotFoundException;
import  financial_app.repository.AccountRepository;
import  financial_app.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    void shouldCreateClientSuccessfully() {
        ClientRequestDTO request = new ClientRequestDTO();
        request.setIdentificationType("CC");
        request.setIdentificationNumber("1075289632");
        request.setNames("martin");
        request.setLastName("ortiz");
        request.setEmail("martin@example.com");
        request.setBirthDate(LocalDate.of(2000, 5, 15));

        when(clientRepository.existsByIdentificationNumber("1075289632")).thenReturn(false);
        when(clientRepository.existsByEmail("martin@example.com")).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Client result = clientService.createClient(request);

        assertNotNull(result);
        assertEquals("martin", result.getNames());
        assertEquals("ortiz", result.getLastName());
        assertEquals("1075289632", result.getIdentificationNumber());

        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void shouldThrowExceptionWhenClientIsMinor() {
        ClientRequestDTO request = new ClientRequestDTO();
        request.setIdentificationType("CC");
        request.setIdentificationNumber("123");
        request.setNames("Ana");
        request.setLastName("Ruiz");
        request.setEmail("ana@example.com");
        request.setBirthDate(LocalDate.now().minusYears(15));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> clientService.createClient(request)
        );

        assertEquals("El cliente no puede ser menor de edad", exception.getMessage());

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void shouldThrowExceptionWhenIdentificationAlreadyExists() {
        ClientRequestDTO request = new ClientRequestDTO();
        request.setIdentificationType("CC");
        request.setIdentificationNumber("1075289632");
        request.setNames("Carlos");
        request.setLastName("Villamil");
        request.setEmail("carlos@example.com");
        request.setBirthDate(LocalDate.of(2000, 5, 15));

        when(clientRepository.existsByIdentificationNumber("1075289632")).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> clientService.createClient(request)
        );

        assertEquals("Ya existe un cliente con ese número de identificación", exception.getMessage());
    }

    @Test
    void shouldGetClientByIdSuccessfully() {
        Client client = Client.builder()
                .id(1L)
                .names("Carlos")
                .lastName("Villamil")
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        Client result = clientService.getClientById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Carlos", result.getNames());
    }

    @Test
    void shouldThrowExceptionWhenClientNotFound() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> clientService.getClientById(99L)
        );

        assertEquals("Cliente no encontrado con id: 99", exception.getMessage());
    }

    @Test
    void shouldNotDeleteClientWhenHasAccounts() {
        Client client = Client.builder()
                .id(1L)
                .names("Carlos")
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(accountRepository.existsByClientId(1L)).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> clientService.deleteClient(1L)
        );

        assertEquals("No se puede eliminar el cliente porque tiene productos vinculados", exception.getMessage());

        verify(clientRepository, never()).delete(any(Client.class));
    }
}