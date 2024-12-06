package nl.oudhoff.bastephenking.dto.output;

import lombok.Data;

@Data
public class UserOutputDto { String username;
    private String email;
    private String password;
    private Role role;
}
