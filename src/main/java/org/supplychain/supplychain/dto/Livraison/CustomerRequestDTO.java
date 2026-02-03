package org.supplychain.supplychain.dto.Livraison;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDTO {
    @NotBlank(message = "Le nom du client est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    private String name;
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Le numéro de téléphone doit être valide")
    private String phone;
    @NotBlank(message = "L'adresse est obligatoire")
    @Size(min = 10, max = 255, message = "L'adresse doit contenir entre 10 et 255 caractères")
    private String address;
    @NotBlank(message = "La ville est obligatoire")
    @Size(min = 2, max = 50, message = "La ville doit contenir entre 2 et 50 caractères")
    private String city;
}