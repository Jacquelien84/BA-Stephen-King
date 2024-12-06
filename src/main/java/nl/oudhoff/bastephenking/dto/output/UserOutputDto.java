package nl.oudhoff.bastephenking.dto.output;

import lombok.Data;
import nl.oudhoff.bastephenking.model.Role;

@Data
public class UserOutputDto { String username;
    private String email;
    private String password;
    private Role role;
}
