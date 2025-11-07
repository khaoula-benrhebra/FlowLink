package org.supplychain.supplychain.dto.Approvisionnement;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDTO {

    @NotBlank(message = "Le nom du fournisseur est obligatoire")
    private String name;

    @NotBlank(message = "Le contact est obligatoire")
    private String contact;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Le numéro de téléphone doit être valide")
    private String phone;

    @Min(value = 0, message = "La note ne peut pas être négative")
    @Max(value = 5, message = "La note ne peut pas dépasser 5")
    private Double rating;

    @Min(value = 0, message = "Le délai de livraison ne peut pas être négatif")
    private Integer leadTime;
}