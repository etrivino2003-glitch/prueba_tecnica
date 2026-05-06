package financial_app.repository;

import financial_app.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySourceAccountIdOrTargetAccountId(Long sourceAccountId, Long targetAccountId);
}