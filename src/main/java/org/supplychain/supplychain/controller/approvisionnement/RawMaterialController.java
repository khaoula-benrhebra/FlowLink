package org.supplychain.supplychain.controller.approvisionnement;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supplychain.supplychain.dto.Approvisionnement.RawMaterialDTO;
import org.supplychain.supplychain.response.SuccessResponse;
import org.supplychain.supplychain.service.approvisionnement.RawMaterialService;

import java.util.List;

@RestController
@RequestMapping("/api/raw-materials")
@RequiredArgsConstructor
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;

    //Ajouter une matière première

    @PostMapping
    public ResponseEntity<SuccessResponse<RawMaterialDTO>> createRawMaterial(
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

    // Modifier une matière première

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<RawMaterialDTO>> updateRawMaterial(
            @PathVariable Long id,
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

    // Supprimer une matière première si elle n'est pas utilisée

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteRawMaterial(
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

    // Consulter la liste complète des matières premières avec pagination

    @GetMapping
    public ResponseEntity<SuccessResponse<Page<RawMaterialDTO>>> getAllRawMaterials(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
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

    //Consulter les matières dont le stock est inférieur au seuil critique

    @GetMapping("/below-min-stock")
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

    // MéthodeRécupérer une matière première par ID

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<RawMaterialDTO>> getRawMaterialById(
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