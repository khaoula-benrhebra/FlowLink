package org.supplychain.supplychain.dto;
import lombok.Data;
import org.supplychain.supplychain.enums.Role;
@Data
public class UserResponse {
    private Long idUser;       
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}