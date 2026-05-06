package  financial_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import  financial_app.dto.AccountRequestDTO;
import  financial_app.entity.Account;
import  financial_app.enums.AccountStatus;
import  financial_app.enums.AccountType;
import  financial_app.service.AccountService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAccount() throws Exception {
        AccountRequestDTO request = new AccountRequestDTO();
        request.setAccountType(AccountType.SAVINGS);
        request.setBalance(BigDecimal.valueOf(100000));
        request.setGmfExempt(false);
        request.setClientId(1L);

        Account account = Account.builder()
                .id(1L)
                .accountType(AccountType.SAVINGS)
                .accountNumber("5312345678")
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(100000))
                .gmfExempt(false)
                .build();

        Mockito.when(accountService.createAccount(any(AccountRequestDTO.class))).thenReturn(account);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountType").value("SAVINGS"))
                .andExpect(jsonPath("$.accountNumber").value("5312345678"));
    }

    @Test
    void shouldGetAllAccounts() throws Exception {
        Account account = Account.builder()
                .id(1L)
                .accountType(AccountType.SAVINGS)
                .accountNumber("5312345678")
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(100000))
                .build();

        Mockito.when(accountService.getAllAccounts()).thenReturn(List.of(account));

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].accountNumber").value("5312345678"));
    }

    @Test
    void shouldUpdateAccountStatus() throws Exception {
        Account account = Account.builder()
                .id(1L)
                .accountType(AccountType.SAVINGS)
                .accountNumber("5312345678")
                .status(AccountStatus.INACTIVE)
                .balance(BigDecimal.ZERO)
                .build();

        Mockito.when(accountService.updateAccountStatus(1L, AccountStatus.INACTIVE)).thenReturn(account);

        mockMvc.perform(patch("/api/accounts/1/status")
                        .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void shouldDeleteAccount() throws Exception {
        mockMvc.perform(delete("/api/accounts/1"))
                .andExpect(status().isNoContent());
    }
}