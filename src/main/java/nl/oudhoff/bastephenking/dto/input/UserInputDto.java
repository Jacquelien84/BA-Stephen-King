package nl.oudhoff.bastephenking.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class UserInputDto {
    @NotBlank
    private String username;
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 6, message = "Het wachtwoord moet tussen de 6 en 25 tekens lang zijn")
    private String password;
    private Role role;
}

