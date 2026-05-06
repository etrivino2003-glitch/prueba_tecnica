package financial_app.service;

import financial_app.dto.AccountRequestDTO;
import financial_app.entity.Account;
import  financial_app.entity.Client;
import  financial_app.enums.AccountStatus;
import  financial_app.enums.AccountType;
import  financial_app.exception.BusinessException;
import  financial_app.exception.ResourceNotFoundException;
import  financial_app.repository.AccountRepository;
import  financial_app.repository.ClientRepository;
import financial_app.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import financial_app.dto.AccountStatementDTO;
import financial_app.repository.TransactionRepository;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final TransactionRepository transactionRepository;

    private final SecureRandom random = new SecureRandom();

    public Account createAccount(AccountRequestDTO request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        BigDecimal initialBalance = request.getBalance() != null
                ? request.getBalance()
                : BigDecimal.ZERO;

        if (request.getAccountType() == AccountType.SAVINGS &&
                initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("La cuenta de ahorros no puede tener saldo menor a cero");
        }

        Account account = Account.builder()
                .accountType(request.getAccountType())
                .accountNumber(generateAccountNumber(request.getAccountType()))
                .status(AccountStatus.ACTIVE)
                .balance(initialBalance)
                .gmfExempt(request.getGmfExempt() != null ? request.getGmfExempt() : false)
                .client(client)
                .build();

        return accountRepository.save(account);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + id));
    }

    public Account updateAccountStatus(Long id, AccountStatus status) {
        Account account = getAccountById(id);

        if (status == AccountStatus.CANCELLED &&
                account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessException("Solo se pueden cancelar cuentas con saldo igual a cero");
        }

        account.setStatus(status);
        return accountRepository.save(account);
    }

    public void deleteAccount(Long id) {
        Account account = getAccountById(id);

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessException("No se puede eliminar una cuenta con saldo diferente de cero");
        }

        accountRepository.delete(account);
    }

    private String generateAccountNumber(AccountType accountType) {
        String prefix = accountType == AccountType.SAVINGS ? "53" : "33";

        String accountNumber;

        do {
            StringBuilder number = new StringBuilder(prefix);

            for (int i = 0; i < 8; i++) {
                number.append(random.nextInt(10));
            }

            accountNumber = number.toString();

        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }
    public AccountStatementDTO getAccountStatement(Long accountId) {
    Account account = getAccountById(accountId);

    return new AccountStatementDTO(
            account.getId(),
            account.getAccountNumber(),
            account.getAccountType(),
            account.getStatus(),
            account.getBalance(),
            transactionRepository.findBySourceAccountIdOrTargetAccountId(accountId, accountId)
    );
}
}