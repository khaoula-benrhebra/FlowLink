package org.supplychain.supplychain.security;

import org.supplychain.supplychain.model.RefreshToken;
import org.supplychain.supplychain.model.AppUser;

import java.util.Optional;

/**
 * Service pour gérer les refresh tokens
 */
public interface RefreshTokenService {

    /**
     * Crée un nouveau refresh token pour un utilisateur
     */
    RefreshToken createRefreshToken(AppUser user);

    /**
     * Valide et récupère un refresh token
     */
    Optional<RefreshToken> getRefreshToken(String token);

    /**
     * Vérifie si un refresh token est valide (existe, pas expiré, pas révoqué)
     */
    boolean validateRefreshToken(RefreshToken token);

    /**
     * Révoque un refresh token spécifique
     */
    void revokeRefreshToken(String token);

    /**
     * Révoque tous les tokens d'un utilisateur
     */
    void revokeAllUserTokens(AppUser user);

    /**
     * Nettoie les tokens expiré d'un utilisateur
     */
    void cleanExpiredTokens(AppUser user);

    /**
     * Supprime un refresh token
     */
    void deleteRefreshToken(Long id);
}
