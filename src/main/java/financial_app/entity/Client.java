package financial_app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identification_type", nullable = false)
    private String identificationType;

    @Column(name = "identification_number", nullable = false, unique = true)
    private String identificationNumber;

    @Size(min = 2, message = "El nombre debe tener mínimo 2 caracteres")
    @Column(nullable = false)
    private String names;

    @Size(min = 2, message = "El apellido debe tener mínimo 2 caracteres")
    @Column(nullable = false)
    private String lastName;

    @Email(message = "El correo electrónico no tiene un formato válido")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
