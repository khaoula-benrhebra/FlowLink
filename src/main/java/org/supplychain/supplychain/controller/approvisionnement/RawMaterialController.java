package org.supplychain.supplychain.controller.approvisionnement;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.supplychain.supplychain.dto.Approvisionnement.MaterialRequest;
import org.supplychain.supplychain.dto.Approvisionnement.MaterialResponse;
import org.supplychain.supplychain.response.SuccessResponse;
import org.supplychain.supplychain.service.approvisionnement.RawMaterialService;
import java.util.List;

@RestController
@RequestMapping("/api/raw-materials")
@RequiredArgsConstructor
@Tag(name = "Matières Premières")
public class RawMaterialController {
    private final RawMaterialService rawMaterialService;

    @PostMapping
    @PreAuthorize("hasRole('GESTIONNAIRE_APPROVISIONNEMENT')")
    public ResponseEntity<SuccessResponse<MaterialResponse>> create(@Valid @RequestBody MaterialRequest request,
            HttpServletRequest req) {
        MaterialResponse res = rawMaterialService.createRawMaterial(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of(HttpStatus.CREATED, "Matière créée", res, req.getRequestURI()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTIONNAIRE_APPROVISIONNEMENT')")
    public ResponseEntity<SuccessResponse<MaterialResponse>> update(@PathVariable Long id,
            @Valid @RequestBody MaterialRequest request, HttpServletRequest req) {
        MaterialResponse res = rawMaterialService.updateRawMaterial(id, request);
        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK, "Matière modifiée", res, req.getRequestURI()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTIONNAIRE_APPROVISIONNEMENT')")
    public ResponseEntity<SuccessResponse<Void>> delete(@PathVariable Long id, HttpServletRequest req) {
        rawMaterialService.deleteRawMaterial(id);
        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK, "Matière supprimée", null, req.getRequestURI()));
    }

    @GetMapping
    @PreAuthorize("hasRole('GESTIONNAIRE_APPROVISIONNEMENT') or hasRole('SUPERVISEUR_LOGISTIQUE') or hasRole('CHEF_PRODUCTION') or hasRole('SUPERVISEUR_PRODUCTION')")
    public ResponseEntity<SuccessResponse<Page<MaterialResponse>>> getAll(Pageable pageable, HttpServletRequest req) {
        Page<MaterialResponse> res = rawMaterialService.getAllRawMaterials(pageable);
        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK, "Liste récupérée", res, req.getRequestURI()));
    }

    @GetMapping("/below-min-stock")
    @PreAuthorize("hasRole('GESTIONNAIRE_APPROVISIONNEMENT') or hasRole('SUPERVISEUR_LOGISTIQUE')")
    public ResponseEntity<SuccessResponse<List<MaterialResponse>>> getBelowStock(HttpServletRequest req) {
        List<MaterialResponse> res = rawMaterialService.getMaterialsBelowMinStock();
        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK, "Alerte stocks bas", res, req.getRequestURI()));
    }
}