package  financial_app.service;

import  financial_app.dto.TransactionRequestDTO;
import  financial_app.entity.Account;
import  financial_app.entity.Transaction;
import  financial_app.enums.AccountStatus;
import  financial_app.enums.AccountType;
import  financial_app.enums.TransactionType;
import  financial_app.exception.BusinessException;
import  financial_app.exception.ResourceNotFoundException;
import  financial_app.repository.AccountRepository;
import  financial_app.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Transaction createTransaction(TransactionRequestDTO request) {
        return switch (request.getTransactionType()) {
            case DEPOSIT -> deposit(request);
            case WITHDRAWAL -> withdrawal(request);
            case TRANSFER -> transfer(request);
        };
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada con id: " + id));
    }

    public List<Transaction> getTransactionsByAccount(Long accountId) {
        return transactionRepository.findBySourceAccountIdOrTargetAccountId(accountId, accountId);
    }

    private Transaction deposit(TransactionRequestDTO request) {
        if (request.getTargetAccountId() == null) {
            throw new BusinessException("Para una consignación debe indicar la cuenta destino");
        }

        Account targetAccount = getAccount(request.getTargetAccountId());
        validateActiveAccount(targetAccount);

        targetAccount.setBalance(targetAccount.getBalance().add(request.getAmount()));
        accountRepository.save(targetAccount);

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .targetAccount(targetAccount)
                .description(request.getDescription())
                .build();

        return transactionRepository.save(transaction);
    }

    private Transaction withdrawal(TransactionRequestDTO request) {
        if (request.getSourceAccountId() == null) {
            throw new BusinessException("Para un retiro debe indicar la cuenta origen");
        }

        Account sourceAccount = getAccount(request.getSourceAccountId());
        validateActiveAccount(sourceAccount);
        validateAvailableBalance(sourceAccount, request.getAmount());

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        accountRepository.save(sourceAccount);

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .sourceAccount(sourceAccount)
                .description(request.getDescription())
                .build();

        return transactionRepository.save(transaction);
    }

    private Transaction transfer(TransactionRequestDTO request) {
        if (request.getSourceAccountId() == null || request.getTargetAccountId() == null) {
            throw new BusinessException("Para una transferencia debe indicar cuenta origen y cuenta destino");
        }

        if (request.getSourceAccountId().equals(request.getTargetAccountId())) {
            throw new BusinessException("No se puede transferir a la misma cuenta");
        }

        Account sourceAccount = getAccount(request.getSourceAccountId());
        Account targetAccount = getAccount(request.getTargetAccountId());

        validateActiveAccount(sourceAccount);
        validateActiveAccount(targetAccount);
        validateAvailableBalance(sourceAccount, request.getAmount());

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        targetAccount.setBalance(targetAccount.getBalance().add(request.getAmount()));

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.TRANSFER)
                .amount(request.getAmount())
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .description(request.getDescription())
                .build();

        return transactionRepository.save(transaction);
    }

    private Account getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + accountId));
    }

    private void validateActiveAccount(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException("La cuenta debe estar activa para realizar transacciones");
        }
    }

    private void validateAvailableBalance(Account account, BigDecimal amount) {
        BigDecimal newBalance = account.getBalance().subtract(amount);

        if (account.getAccountType() == AccountType.SAVINGS && newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("La cuenta de ahorros no puede quedar con saldo negativo");
        }
    }
}
