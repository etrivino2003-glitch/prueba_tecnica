package financial_app.repository;

import financial_app.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByIdentificationNumber(String identificationNumber);

    Optional<Client> findByEmail(String email);

    boolean existsByIdentificationNumber(String identificationNumber);

    boolean existsByEmail(String email);
}
