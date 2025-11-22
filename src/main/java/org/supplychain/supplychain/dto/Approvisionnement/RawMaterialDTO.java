package org.supplychain.supplychain.dto.Approvisionnement;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Données d'une matière première")
public class RawMaterialDTO {

    @Schema(description = "Nom de la matière première", example = "Acier inoxydable")
    @NotBlank(message = "Le nom de la matière première est obligatoire")
    private String name;


    @Schema(description = "Quantité en stock", example = "150")
    @NotNull(message = "Le stock est obligatoire")
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    private Integer stock;

    @Schema(description = "Seuil minimum de stock", example = "20")
    @NotNull(message = "Le stock minimum est obligatoire")
    @Min(value = 0, message = "Le stock minimum ne peut pas être négatif")
    private Integer stockMin;

    @Schema(description = "Unité de mesure", example = "kg")
    @NotBlank(message = "L'unité est obligatoire")
    private String unit;
}