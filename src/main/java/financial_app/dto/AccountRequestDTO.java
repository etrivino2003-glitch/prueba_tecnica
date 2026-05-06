package financial_app.dto;

import financial_app.enums.AccountType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class AccountRequestDTO {

    @NotNull(message = "El tipo de cuenta es obligatorio")
    private AccountType accountType;

    private BigDecimal balance;

    private Boolean gmfExempt;

    @NotNull(message = "El id del cliente es obligatorio")
    private Long clientId;

    public AccountType getAccountType() {
        return accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Boolean getGmfExempt() {
        return gmfExempt;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setGmfExempt(Boolean gmfExempt) {
        this.gmfExempt = gmfExempt;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}