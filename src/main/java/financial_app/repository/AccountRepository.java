package financial_app.repository;

import financial_app.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByClientId(Long clientId);

    Optional<Account> findByAccountNumber(String accountNumber);
}
