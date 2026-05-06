package financial_app.service;

import financial_app.dto.ClientRequestDTO;
import financial_app.entity.Client;
import financial_app.exception.BusinessException;
import financial_app.exception.ResourceNotFoundException;
import financial_app.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public Client createClient(ClientRequestDTO request) {
        validateAdult(request.getBirthDate());

        if (clientRepository.existsByIdentificationNumber(request.getIdentificationNumber())) {
            throw new BusinessException("Ya existe un cliente con ese número de identificación");
        }

        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Ya existe un cliente con ese correo electrónico");
        }

        Client client = Client.builder()
                .identificationType(request.getIdentificationType())
                .identificationNumber(request.getIdentificationNumber())
                .names(request.getNames())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .birthDate(request.getBirthDate())
                .build();

        return clientRepository.save(client);
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
    }

    public Client updateClient(Long id, ClientRequestDTO request) {
        Client client = getClientById(id);

        validateAdult(request.getBirthDate());

        client.setIdentificationType(request.getIdentificationType());
        client.setIdentificationNumber(request.getIdentificationNumber());
        client.setNames(request.getNames());
        client.setLastName(request.getLastName());
        client.setEmail(request.getEmail());
        client.setBirthDate(request.getBirthDate());

        return clientRepository.save(client);
    }

    public void deleteClient(Long id) {
        Client client = getClientById(id);
        clientRepository.delete(client);
    }

    private void validateAdult(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        if (age < 18) {
            throw new BusinessException("El cliente no puede ser menor de edad");
        }
    }
}

