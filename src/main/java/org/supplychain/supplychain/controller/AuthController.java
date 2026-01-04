package org.supplychain.supplychain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.supplychain.supplychain.dto.AuthResponse;
import org.supplychain.supplychain.dto.LoginRequest;
import org.supplychain.supplychain.dto.RefreshTokenRequest;
import org.supplychain.supplychain.model.AppUser;
import org.supplychain.supplychain.model.RefreshToken;
import org.supplychain.supplychain.repository.UserRepository;
import org.supplychain.supplychain.response.SuccessResponse;
import org.supplychain.supplychain.security.JwtService;
import org.supplychain.supplychain.security.RefreshTokenService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Endpoints d'authentification JWT avec refresh token")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    /**
     * Endpoint de connexion - Retourne access token et refresh token
     */
    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur", description = "Authentifie un utilisateur et retourne access token + refresh token")
    public ResponseEntity<SuccessResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUser user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Génère l'access token JWT
        String accessToken = jwtService.generateToken(user);
        
        // Crée un refresh token en base de données
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(accessToken);
        authResponse.setRefreshToken(refreshToken.getToken());
        authResponse.setTokenType("Bearer");
        authResponse.setExpiresIn(jwtService.getTokenExpiration());
        authResponse.setEmail(user.getEmail());
        authResponse.setRole(user.getRole().toString());
        authResponse.setFirstName(user.getFirstName());
        authResponse.setLastName(user.getLastName());

        SuccessResponse<AuthResponse> response = SuccessResponse.of(
                HttpStatus.OK,
                "Authentification réussie",
                authResponse,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de rafraîchissement - Utilise refresh token pour obtenir un nouveau access token
     */
    @PostMapping("/refresh")
    @Operation(summary = "Rafraîchir le token", description = "Utilise le refresh token pour obtenir un nouveau access token")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<SuccessResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest,
            HttpServletRequest request) {
        
        try {
            // Cherche le refresh token en base de données
            RefreshToken refreshToken = refreshTokenService.getRefreshToken(refreshTokenRequest.getRefreshToken())
                    .orElseThrow(() -> new RuntimeException("Refresh token non trouvé"));

            // Valide le refresh token
            if (!refreshTokenService.validateRefreshToken(refreshToken)) {
                throw new RuntimeException("Refresh token expiré ou révoqué");
            }

            // Récupère l'utilisateur associé (force le chargement dans la transaction)
            AppUser user = refreshToken.getUser();
            if (user != null) {
                user.getIdUser(); // Force l'initialisation du proxy
            }

            // Génère un nouveau access token
            String newAccessToken = jwtService.generateToken(user);

            // Optionnel: Renouvelle aussi le refresh token
            refreshTokenService.revokeRefreshToken(refreshTokenRequest.getRefreshToken());
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(newAccessToken);
            authResponse.setRefreshToken(newRefreshToken.getToken());
            authResponse.setTokenType("Bearer");
            authResponse.setExpiresIn(jwtService.getTokenExpiration());
            authResponse.setEmail(user.getEmail());
            authResponse.setRole(user.getRole().toString());
            authResponse.setFirstName(user.getFirstName());
            authResponse.setLastName(user.getLastName());

            SuccessResponse<AuthResponse> response = SuccessResponse.of(
                    HttpStatus.OK,
                    "Token rafraîchi avec succès",
                    authResponse,
                    request.getRequestURI()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            SuccessResponse<AuthResponse> errorResponse = SuccessResponse.of(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token invalide: " + e.getMessage(),
                    null,
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Endpoint de vérification du token
     */
    @GetMapping("/verify")
    @Operation(summary = "Vérifier le token", description = "Valide le token JWT courant")
    public ResponseEntity<SuccessResponse<String>> verifyToken(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        
        boolean isValid = token != null && jwtService.validateToken(token);
        
        SuccessResponse<String> response = SuccessResponse.of(
                HttpStatus.OK,
                isValid ? "Token valide" : "Token invalide",
                isValid ? "OK" : "INVALID",
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de déconnexion - Révoque le refresh token
     */
    @PostMapping("/logout")
    @Operation(summary = "Déconnexion", description = "Révoque le refresh token et déconnecte l'utilisateur")
    public ResponseEntity<SuccessResponse<String>> logout(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest,
            HttpServletRequest request) {
        
        try {
            refreshTokenService.revokeRefreshToken(refreshTokenRequest.getRefreshToken());
            SecurityContextHolder.clearContext();

            SuccessResponse<String> response = SuccessResponse.of(
                    HttpStatus.OK,
                    "Déconnexion réussie",
                    "OK",
                    request.getRequestURI()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            SuccessResponse<String> errorResponse = SuccessResponse.of(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur lors de la déconnexion",
                    null,
                    request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
