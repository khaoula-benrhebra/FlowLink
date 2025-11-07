package org.supplychain.supplychain.dto.Livraison;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

    @NotBlank(message = "Le nom du client est obligatoire")
    private String name;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Le numéro de téléphone doit être valide")
    private String phone;

    @NotBlank(message = "L'adresse est obligatoire")
    private String address;
}
