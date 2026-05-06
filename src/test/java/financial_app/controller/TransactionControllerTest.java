package  financial_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import  financial_app.dto.TransactionRequestDTO;
import  financial_app.entity.Transaction;
import  financial_app.enums.TransactionType;
import  financial_app.service.TransactionService;
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

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateDepositTransaction() throws Exception {
        TransactionRequestDTO request = new TransactionRequestDTO();
        request.setTransactionType(TransactionType.DEPOSIT);
        request.setAmount(BigDecimal.valueOf(50000));
        request.setTargetAccountId(1L);
        request.setDescription("Consignación inicial");

        Transaction transaction = Transaction.builder()
                .id(1L)
                .transactionType(TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(50000))
                .description("Consignación inicial")
                .build();

        Mockito.when(transactionService.createTransaction(any(TransactionRequestDTO.class))).thenReturn(transaction);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.transactionType").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(50000));
    }

    @Test
    void shouldGetAllTransactions() throws Exception {
        Transaction transaction = Transaction.builder()
                .id(1L)
                .transactionType(TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(50000))
                .description("Consignación inicial")
                .build();

        Mockito.when(transactionService.getAllTransactions()).thenReturn(List.of(transaction));

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].transactionType").value("DEPOSIT"));
    }

    @Test
    void shouldGetTransactionById() throws Exception {
        Transaction transaction = Transaction.builder()
                .id(1L)
                .transactionType(TransactionType.WITHDRAWAL)
                .amount(BigDecimal.valueOf(20000))
                .description("Retiro")
                .build();

        Mockito.when(transactionService.getTransactionById(1L)).thenReturn(transaction);

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.transactionType").value("WITHDRAWAL"));
    }

    @Test
    void shouldGetTransactionsByAccount() throws Exception {
        Transaction transaction = Transaction.builder()
                .id(1L)
                .transactionType(TransactionType.TRANSFER)
                .amount(BigDecimal.valueOf(10000))
                .description("Transferencia")
                .build();

        Mockito.when(transactionService.getTransactionsByAccount(1L)).thenReturn(List.of(transaction));

        mockMvc.perform(get("/api/transactions/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionType").value("TRANSFER"));
    }
}