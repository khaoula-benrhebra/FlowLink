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
import org.supplychain.supplychain.dto.Approvisionnement.SupplierDTO;
import org.supplychain.supplychain.response.SuccessResponse;
import org.supplychain.supplychain.service.approvisionnement.SupplierService;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;


    @PostMapping
    public ResponseEntity<SuccessResponse<SupplierDTO>> createSupplier(
            @Valid @RequestBody SupplierDTO supplierDTO,
            HttpServletRequest request) {

        SupplierDTO createdSupplier = supplierService.createSupplier(supplierDTO);

        SuccessResponse<SupplierDTO> response = SuccessResponse.of(
                HttpStatus.CREATED,
                "Fournisseur créé avec succès",
                createdSupplier,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<SupplierDTO>> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierDTO supplierDTO,
            HttpServletRequest request) {

        SupplierDTO updatedSupplier = supplierService.updateSupplier(id, supplierDTO);

        SuccessResponse<SupplierDTO> response = SuccessResponse.of(
                HttpStatus.OK,
                "Fournisseur modifié avec succès",
                updatedSupplier,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteSupplier(
            @PathVariable Long id,
            HttpServletRequest request) {

        supplierService.deleteSupplier(id);

        SuccessResponse<Void> response = SuccessResponse.of(
                HttpStatus.OK,
                "Fournisseur supprimé avec succès",
                null,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<SuccessResponse<Page<SupplierDTO>>> getAllSuppliers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<SupplierDTO> suppliers = supplierService.getAllSuppliers(pageable);

        SuccessResponse<Page<SupplierDTO>> response = SuccessResponse.of(
                HttpStatus.OK,
                "Liste des fournisseurs récupérée avec succès",
                suppliers,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/search")
    public ResponseEntity<SuccessResponse<Page<SupplierDTO>>> searchSuppliersByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<SupplierDTO> suppliers = supplierService.searchSuppliersByName(name, pageable);

        SuccessResponse<Page<SupplierDTO>> response = SuccessResponse.of(
                HttpStatus.OK,
                "Recherche de fournisseurs effectuée avec succès",
                suppliers,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<SupplierDTO>> getSupplierById(
            @PathVariable Long id,
            HttpServletRequest request) {

        SupplierDTO supplier = supplierService.getSupplierById(id);

        SuccessResponse<SupplierDTO> response = SuccessResponse.of(
                HttpStatus.OK,
                "Fournisseur récupéré avec succès",
                supplier,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }
}