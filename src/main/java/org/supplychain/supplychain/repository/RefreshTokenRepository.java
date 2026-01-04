package org.supplychain.supplychain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.supplychain.supplychain.model.RefreshToken;
import org.supplychain.supplychain.model.AppUser;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Cherche un refresh token par sa valeur
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Cherche un refresh token par sa valeur avec eager loading de l'user
     */
    @Query("SELECT rt FROM RefreshToken rt JOIN FETCH rt.user WHERE rt.token = ?1")
    Optional<RefreshToken> findByTokenWithUser(String token);

    /**
     * Cherche tous les tokens non révoqués d'un utilisateur
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = ?1 AND rt.revoked = false")
    java.util.List<RefreshToken> findActiveTokensByUser(AppUser user);

    /**
     * Supprime tous les tokens expiré d'un utilisateur
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = ?1 AND rt.expiryDate <= ?2")
    void deleteExpiredTokensByUser(AppUser user, Instant now);

    /**
     * Révoque tous les tokens d'un utilisateur
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = ?2 WHERE rt.user = ?1 AND rt.revoked = false")
    int revokeAllTokensByUser(AppUser user, Instant now);

    /**
     * Compte les tokens valides d'un utilisateur
     */
    @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.user = ?1 AND rt.revoked = false AND rt.expiryDate > ?2")
    long countActiveTokensByUser(AppUser user, Instant now);
}
