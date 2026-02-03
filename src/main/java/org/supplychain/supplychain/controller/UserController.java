package org.supplychain.supplychain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.supplychain.supplychain.dto.UserResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.supplychain.supplychain.dto.UserDTO;
import org.supplychain.supplychain.response.SuccessResponse;
import org.supplychain.supplychain.service.user.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "API de gestion des utilisateurs")
public class UserController {

        private final UserService userService;

        @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Créer un nouvel utilisateur", description = "Ajoute un nouvel utilisateur au système (Réservé aux ADMIN)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès"),
                        @ApiResponse(responseCode = "400", description = "Données invalides"),
                        @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle ADMIN requis")
        })
        public ResponseEntity<SuccessResponse<UserDTO>> createUser(
                        @Parameter(description = "Données de l'utilisateur à créer", required = true) @Valid @RequestBody UserDTO userDTO,
                        HttpServletRequest request) {

                UserDTO createdUser = userService.createUser(userDTO);

                SuccessResponse<UserDTO> response = SuccessResponse.of(
                                HttpStatus.CREATED,
                                "Utilisateur créé avec succès",
                                createdUser,
                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Modifier un utilisateur", description = "Met à jour les informations d'un utilisateur existant (Réservé aux ADMIN)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Utilisateur modifié avec succès"),
                        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
                        @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle ADMIN requis")
        })
        public ResponseEntity<SuccessResponse<UserDTO>> updateUser(
                        @Parameter(description = "ID de l'utilisateur", required = true) @PathVariable Long id,
                        @Parameter(description = "Nouvelles données de l'utilisateur", required = true) @Valid @RequestBody UserDTO userDTO,
                        HttpServletRequest request) {

                UserDTO updatedUser = userService.updateUser(id, userDTO);

                SuccessResponse<UserDTO> response = SuccessResponse.of(
                                HttpStatus.OK,
                                "Utilisateur modifié avec succès",
                                updatedUser,
                                request.getRequestURI());

                return ResponseEntity.ok(response);
        }

        @GetMapping("/search")
        @Operation(summary = "Rechercher un utilisateur par email", description = "Trouve un utilisateur par son adresse email")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
                        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
        })
        public ResponseEntity<SuccessResponse<UserDTO>> getUserByEmail(
                        @Parameter(description = "Adresse email de l'utilisateur", required = true) @RequestParam String email,
                        HttpServletRequest request) {

                UserDTO user = userService.getUserByEmail(email);

                SuccessResponse<UserDTO> response = SuccessResponse.of(
                                HttpStatus.OK,
                                "Utilisateur trouvé avec succès",
                                user,
                                request.getRequestURI());

                return ResponseEntity.ok(response);
        }

        @GetMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<SuccessResponse<List<UserResponse>>> getAllUsers(HttpServletRequest request) {
                return ResponseEntity.ok(SuccessResponse.of(
                                HttpStatus.OK,
                                "Liste des utilisateurs récupérée",
                                userService.getAllUsers(),
                                request.getRequestURI()));
        }
}