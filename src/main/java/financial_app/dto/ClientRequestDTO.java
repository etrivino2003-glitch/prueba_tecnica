package financial_app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class ClientRequestDTO {

    @NotBlank(message = "El tipo de identificación es obligatorio")
    private String identificationType;

    @NotBlank(message = "El número de identificación es obligatorio")
    private String identificationNumber;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 2, message = "Los nombres deben tener mínimo 2 caracteres")
    private String names;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, message = "El apellido debe tener mínimo 2 caracteres")
    private String lastName;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo electrónico no tiene un formato válido")
    private String email;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate birthDate;

    public String getIdentificationType() {
        return identificationType;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public String getNames() {
        return names;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setIdentificationType(String identificationType) {
        this.identificationType = identificationType;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}