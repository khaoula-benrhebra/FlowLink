package org.supplychain.supplychain.controller.Production;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.supplychain.supplychain.dto.Production.ProductDTO;
import org.supplychain.supplychain.response.SuccessResponse;
import org.supplychain.supplychain.service.Production.Product.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Produits", description = "API de gestion des produits finis")
public class ProductController {

    private final ProductService productService;

        @PostMapping
        @PreAuthorize("hasRole('CHEF_PRODUCTION')")
    @Operation(summary = "Ajouter un produit fini", description = "Crée un nouveau produit dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produit créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
        public ResponseEntity<SuccessResponse<ProductDTO>> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
            HttpServletRequest request) {

        ProductDTO createdProduct = productService.createProduct(productDTO);
        SuccessResponse<ProductDTO> response = SuccessResponse.of(
                HttpStatus.CREATED,
                "Produit créé avec succès",
                createdProduct,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

        @PutMapping("/{id}")
        @PreAuthorize("hasRole('CHEF_PRODUCTION')")
    @Operation(summary = "Modifier les informations d'un produit", description = "Met à jour un produit existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
        public ResponseEntity<SuccessResponse<ProductDTO>> updateProduct(
            @Parameter(description = "ID du produit") @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO,
            HttpServletRequest request) {

        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        SuccessResponse<ProductDTO> response = SuccessResponse.of(
                HttpStatus.OK,
                "Produit mis à jour avec succès",
                updatedProduct,
                request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('CHEF_PRODUCTION')")
    @Operation(summary = "Supprimer un produit",
            description = "Supprime un produit s'il n'existe aucun ordre de production associé")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
            @ApiResponse(responseCode = "409", description = "Produit associé à des ordres de production")
    })
        public ResponseEntity<SuccessResponse<Void>> deleteProduct(
            @Parameter(description = "ID du produit") @PathVariable Long id,
            HttpServletRequest request) {

        productService.deleteProduct(id);
        SuccessResponse<Void> response = SuccessResponse.of(
                HttpStatus.OK,
                "Produit supprimé avec succès",
                null,
                request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un produit", description = "Récupère un produit par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produit récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    public ResponseEntity<SuccessResponse<ProductDTO>> getProductById(
            @Parameter(description = "ID du produit") @PathVariable Long id,
            HttpServletRequest request) {

        ProductDTO product = productService.getProductById(id);
        SuccessResponse<ProductDTO> response = SuccessResponse.of(
                HttpStatus.OK,
                "Produit récupéré avec succès",
                product,
                request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

        @GetMapping
        @PreAuthorize("hasRole('SUPERVISEUR_PRODUCTION')")
    @Operation(summary = "Consulter la liste complète des produits",
            description = "Récupère tous les produits avec pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
        public ResponseEntity<SuccessResponse<Page<ProductDTO>>> getAllProducts(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Direction du tri") @RequestParam(defaultValue = "asc") String sortDirection,
            HttpServletRequest request) {

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductDTO> products = productService.getAllProducts(pageable);

        SuccessResponse<Page<ProductDTO>> response = SuccessResponse.of(
                HttpStatus.OK,
                "Liste des produits récupérée avec succès",
                products,
                request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

        @GetMapping("/search")
        @PreAuthorize("hasRole('SUPERVISEUR_PRODUCTION')")
    @Operation(summary = "Rechercher un produit par nom",
            description = "Recherche des produits par nom avec pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recherche effectuée avec succès")
    })
        public ResponseEntity<SuccessResponse<Page<ProductDTO>>> searchProductsByName(
            @Parameter(description = "Nom du produit à rechercher") @RequestParam String name,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Direction du tri") @RequestParam(defaultValue = "asc") String sortDirection,
            HttpServletRequest request) {

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductDTO> products = productService.searchProductsByName(name, pageable);

        SuccessResponse<Page<ProductDTO>> response = SuccessResponse.of(
                HttpStatus.OK,
                "Recherche effectuée avec succès",
                products,
                request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }
}