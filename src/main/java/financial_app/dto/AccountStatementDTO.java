package  financial_app.dto;

import  financial_app.entity.Transaction;
import  financial_app.enums.AccountStatus;
import  financial_app.enums.AccountType;

import java.math.BigDecimal;
import java.util.List;

public class AccountStatementDTO {

    private Long accountId;
    private String accountNumber;
    private AccountType accountType;
    private AccountStatus status;
    private BigDecimal balance;
    private List<Transaction> transactions;

    public AccountStatementDTO(
            Long accountId,
            String accountNumber,
            AccountType accountType,
            AccountStatus status,
            BigDecimal balance,
            List<Transaction> transactions
    ) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.status = status;
        this.balance = balance;
        this.transactions = transactions;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}