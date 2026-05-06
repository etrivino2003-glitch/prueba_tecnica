package  financial_app.service;

import  financial_app.dto.TransactionRequestDTO;
import  financial_app.entity.Account;
import  financial_app.entity.Transaction;
import  financial_app.enums.AccountStatus;
import  financial_app.enums.AccountType;
import  financial_app.enums.TransactionType;
import  financial_app.exception.BusinessException;
import  financial_app.repository.AccountRepository;
import  financial_app.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void shouldDepositSuccessfully() {
        Account targetAccount = Account.builder()
                .id(1L)
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(100000))
                .build();

        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setTransactionType(TransactionType.DEPOSIT);
        request.setAmount(BigDecimal.valueOf(50000));
        request.setTargetAccountId(1L);
        request.setDescription("Consignación inicial");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(targetAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.createTransaction(request);

        assertNotNull(result);
        assertEquals(TransactionType.DEPOSIT, result.getTransactionType());
        assertEquals(BigDecimal.valueOf(150000), targetAccount.getBalance());

        verify(accountRepository).save(targetAccount);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldWithdrawSuccessfully() {
        Account sourceAccount = Account.builder()
                .id(1L)
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(100000))
                .build();

        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setTransactionType(TransactionType.WITHDRAWAL);
        request.setAmount(BigDecimal.valueOf(30000));
        request.setSourceAccountId(1L);
        request.setDescription("Retiro en oficina");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.createTransaction(request);

        assertNotNull(result);
        assertEquals(TransactionType.WITHDRAWAL, result.getTransactionType());
        assertEquals(BigDecimal.valueOf(70000), sourceAccount.getBalance());

        verify(accountRepository).save(sourceAccount);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldTransferSuccessfully() {
        Account sourceAccount = Account.builder()
                .id(1L)
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(100000))
                .build();

        Account targetAccount = Account.builder()
                .id(2L)
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(20000))
                .build();

        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setTransactionType(TransactionType.TRANSFER);
        request.setAmount(BigDecimal.valueOf(40000));
        request.setSourceAccountId(1L);
        request.setTargetAccountId(2L);
        request.setDescription("Transferencia entre cuentas");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(targetAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.createTransaction(request);

        assertNotNull(result);
        assertEquals(TransactionType.TRANSFER, result.getTransactionType());
        assertEquals(BigDecimal.valueOf(60000), sourceAccount.getBalance());
        assertEquals(BigDecimal.valueOf(60000), targetAccount.getBalance());

        verify(accountRepository).save(sourceAccount);
        verify(accountRepository).save(targetAccount);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldNotWithdrawWhenSavingsAccountHasInsufficientBalance() {
        Account sourceAccount = Account.builder()
                .id(1L)
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(10000))
                .build();

        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setTransactionType(TransactionType.WITHDRAWAL);
        request.setAmount(BigDecimal.valueOf(50000));
        request.setSourceAccountId(1L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> transactionService.createTransaction(request)
        );

        assertEquals("La cuenta de ahorros no puede quedar con saldo negativo", exception.getMessage());

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void shouldNotTransferToSameAccount() {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setTransactionType(TransactionType.TRANSFER);
        request.setAmount(BigDecimal.valueOf(10000));
        request.setSourceAccountId(1L);
        request.setTargetAccountId(1L);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> transactionService.createTransaction(request)
        );

        assertEquals("No se puede transferir a la misma cuenta", exception.getMessage());
    }

    @Test
    void shouldNotMakeTransactionWhenAccountIsInactive() {
        Account account = Account.builder()
                .id(1L)
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.INACTIVE)
                .balance(BigDecimal.valueOf(100000))
                .build();

        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setTransactionType(TransactionType.DEPOSIT);
        request.setAmount(BigDecimal.valueOf(50000));
        request.setTargetAccountId(1L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> transactionService.createTransaction(request)
        );

        assertEquals("La cuenta debe estar activa para realizar transacciones", exception.getMessage());
    }
}