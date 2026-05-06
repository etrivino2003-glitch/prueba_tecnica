package  financial_app.service;

import  financial_app.dto.AccountRequestDTO;
import  financial_app.entity.Account;
import  financial_app.entity.Client;
import  financial_app.enums.AccountStatus;
import  financial_app.enums.AccountType;
import  financial_app.exception.BusinessException;
import  financial_app.exception.ResourceNotFoundException;
import  financial_app.repository.AccountRepository;
import  financial_app.repository.ClientRepository;
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
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void shouldCreateSavingsAccountSuccessfully() {
        Client client = Client.builder()
                .id(1L)
                .names("Carlos")
                .build();

        AccountRequestDTO request = new AccountRequestDTO();
        request.setAccountType(AccountType.SAVINGS);
        request.setBalance(BigDecimal.valueOf(100000));
        request.setGmfExempt(false);
        request.setClientId(1L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = accountService.createAccount(request);

        assertNotNull(result);
        assertEquals(AccountType.SAVINGS, result.getAccountType());
        assertEquals(AccountStatus.ACTIVE, result.getStatus());
        assertEquals(BigDecimal.valueOf(100000), result.getBalance());
        assertTrue(result.getAccountNumber().startsWith("53"));
        assertEquals(10, result.getAccountNumber().length());

        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void shouldCreateCheckingAccountSuccessfully() {
        Client client = Client.builder()
                .id(1L)
                .names("Carlos")
                .build();

        AccountRequestDTO request = new AccountRequestDTO();
        request.setAccountType(AccountType.CHECKING);
        request.setBalance(BigDecimal.ZERO);
        request.setGmfExempt(true);
        request.setClientId(1L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = accountService.createAccount(request);

        assertNotNull(result);
        assertEquals(AccountType.CHECKING, result.getAccountType());
        assertTrue(result.getAccountNumber().startsWith("33"));
        assertEquals(10, result.getAccountNumber().length());
    }

    @Test
    void shouldThrowExceptionWhenClientDoesNotExist() {
        AccountRequestDTO request = new AccountRequestDTO();
        request.setAccountType(AccountType.SAVINGS);
        request.setBalance(BigDecimal.ZERO);
        request.setClientId(99L);

        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> accountService.createAccount(request)
        );

        assertEquals("Cliente no encontrado", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSavingsAccountHasNegativeBalance() {
        Client client = Client.builder()
                .id(1L)
                .names("Carlos")
                .build();

        AccountRequestDTO request = new AccountRequestDTO();
        request.setAccountType(AccountType.SAVINGS);
        request.setBalance(BigDecimal.valueOf(-1000));
        request.setClientId(1L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> accountService.createAccount(request)
        );

        assertEquals("La cuenta de ahorros no puede tener saldo menor a cero", exception.getMessage());

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void shouldCancelAccountWhenBalanceIsZero() {
        Account account = Account.builder()
                .id(1L)
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .build();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = accountService.updateAccountStatus(1L, AccountStatus.CANCELLED);

        assertEquals(AccountStatus.CANCELLED, result.getStatus());
    }

    @Test
    void shouldNotCancelAccountWhenBalanceIsNotZero() {
        Account account = Account.builder()
                .id(1L)
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(50000))
                .build();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> accountService.updateAccountStatus(1L, AccountStatus.CANCELLED)
        );

        assertEquals("Solo se pueden cancelar cuentas con saldo igual a cero", exception.getMessage());
    }
}