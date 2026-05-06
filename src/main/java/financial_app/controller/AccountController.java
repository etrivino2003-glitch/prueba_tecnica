package  financial_app.controller;

import  financial_app.dto.AccountRequestDTO;
import  financial_app.entity.Account;
import  financial_app.enums.AccountStatus;
import  financial_app.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import financial_app.dto.AccountStatementDTO;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account createAccount(@Valid @RequestBody AccountRequestDTO request) {
        return accountService.createAccount(request);
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public Account getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @PatchMapping("/{id}/status")
    public Account updateAccountStatus(
            @PathVariable Long id,
            @RequestParam AccountStatus status
    ) {
        return accountService.updateAccountStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
    }
    @GetMapping("/{id}/statement")
public AccountStatementDTO getAccountStatement(@PathVariable Long id) {
    return accountService.getAccountStatement(id);
}
    
}