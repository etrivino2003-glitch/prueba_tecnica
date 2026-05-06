package financial_app.controller;

import financial_app.dto.ClientRequestDTO;
import financial_app.entity.Client;
import financial_app.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Client createClient(@Valid @RequestBody ClientRequestDTO request) {
        return clientService.createClient(request);
    }

    @GetMapping
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/{id}")
    public Client getClientById(@PathVariable Long id) {
        return clientService.getClientById(id);
    }

    @PutMapping("/{id}")
    public Client updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequestDTO request
    ) {
        return clientService.updateClient(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
    }
}