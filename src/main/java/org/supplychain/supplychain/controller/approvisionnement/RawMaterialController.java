package org.supplychain.supplychain.controller.approvisionnement;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.supplychain.supplychain.dto.Approvisionnement.RawMaterialDTO;
import org.supplychain.supplychain.response.SuccessResponse;
import org.supplychain.supplychain.service.approvisionnement.RawMaterialService;

import java.util.List;


@RestController
@RequestMapping("/api/raw-materials")
@RequiredArgsConstructor
@Tag(name = "Matières Premières", description = "API de gestion des matières premières")
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;

    @Operation(summary = "Créer une matière première", description = "Ajoute une nouvelle matière première au système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Matière première créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    @PreAuthorize("hasRole('GESTIONNAIRE_APPROVISIONNEMENT')")
    public ResponseEntity<SuccessResponse<RawMaterialDTO>> createRawMaterial(
            @Parameter(description = "Données de la matière première à créer", required = true)
            @Valid @RequestBody RawMaterialDTO rawMaterialDTO,
            HttpServletRequest request) {

        RawMaterialDTO createdMaterial = rawMaterialService.createRawMaterial(rawMaterialDTO);

        SuccessResponse<RawMaterialDTO> response = SuccessResponse.of(
                HttpStatus.CREATED,
                "Matière première créée avec succès",
                createdMaterial,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Modifier une matière première", description = "Met à jour les informations d'une matière première existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matière première modifiée avec succès"),
            @ApiResponse(responseCode = "404", description = "Matière première non trouvée"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTIONNAIRE_APPROVISIONNEMENT')")
    public ResponseEntity<SuccessResponse<RawMaterialDTO>> updateRawMaterial(
            @Parameter(description = "ID de la matière première", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données de la matière première", required = true)
            @Valid @RequestBody RawMaterialDTO rawMaterialDTO,
            HttpServletRequest request) {

        RawMaterialDTO updatedMaterial = rawMaterialService.updateRawMaterial(id, rawMaterialDTO);

        SuccessResponse<RawMaterialDTO> response = SuccessResponse.of(
                HttpStatus.OK,
                "Matière première modifiée avec succès",
                updatedMaterial,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Supprimer une matière première", description = "Supprime une matière première si elle n'est pas utilisée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matière première supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Matière première non trouvée"),
            @ApiResponse(responseCode = "409", description = "Matière première utilisée, suppression impossible")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTIONNAIRE_APPROVISIONNEMENT')")
    public ResponseEntity<SuccessResponse<Void>> deleteRawMaterial(
            @Parameter(description = "ID de la matière première à supprimer", required = true)
            @PathVariable Long id,
            HttpServletRequest request) {

        rawMaterialService.deleteRawMaterial(id);

        SuccessResponse<Void> response = SuccessResponse.of(
                HttpStatus.OK,
                "Matière première supprimée avec succès",
                null,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lister toutes les matières premières", description = "Récupère la liste paginée de toutes les matières premières")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @GetMapping
    @PreAuthorize("hasRole('SUPERVISEUR_LOGISTIQUE')")
    public ResponseEntity<SuccessResponse<Page<RawMaterialDTO>>> getAllRawMaterials(
            @Parameter(description = "Numéro de page (commence à 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Direction du tri (asc ou desc)") @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<RawMaterialDTO> materials = rawMaterialService.getAllRawMaterials(pageable);

        SuccessResponse<Page<RawMaterialDTO>> response = SuccessResponse.of(
                HttpStatus.OK,
                "Liste des matières premières récupérée avec succès",
                materials,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Matières premières en rupture", description = "Récupère les matières premières dont le stock est inférieur au seuil minimum")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des matières en rupture récupérée avec succès")
    })
    @GetMapping("/below-min-stock")
    @PreAuthorize("hasRole('SUPERVISEUR_LOGISTIQUE')")
    public ResponseEntity<SuccessResponse<List<RawMaterialDTO>>> getMaterialsBelowMinStock(
            HttpServletRequest request) {

        List<RawMaterialDTO> materials = rawMaterialService.getMaterialsBelowMinStock();

        SuccessResponse<List<RawMaterialDTO>> response = SuccessResponse.of(
                HttpStatus.OK,
                "Matières premières en dessous du seuil minimum récupérées avec succès",
                materials,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Récupérer une matière première", description = "Récupère les détails d'une matière première par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matière première trouvée"),
            @ApiResponse(responseCode = "404", description = "Matière première non trouvée")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE_APPROVISIONNEMENT', 'SUPERVISEUR_LOGISTIQUE')")
    public ResponseEntity<SuccessResponse<RawMaterialDTO>> getRawMaterialById(
            @Parameter(description = "ID de la matière première", required = true)
            @PathVariable Long id,
            HttpServletRequest request) {

        RawMaterialDTO material = rawMaterialService.getRawMaterialById(id);

        SuccessResponse<RawMaterialDTO> response = SuccessResponse.of(
                HttpStatus.OK,
                "Matière première récupérée avec succès",
                material,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }
}