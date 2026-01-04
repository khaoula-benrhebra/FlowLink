package org.supplychain.supplychain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;              // Access Token (JWT)
    private String refreshToken;       // Refresh Token (UUID)
    private Long expiresIn;            // Expiration du access token (en secondes)
    private String tokenType;          // Type: "Bearer"
    private String email;
    private String role;
    private String firstName;
    private String lastName;
}
