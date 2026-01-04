package org.supplychain.supplychain.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.model.RefreshToken;
import org.supplychain.supplychain.model.AppUser;
import org.supplychain.supplychain.repository.RefreshTokenRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh.expiration:604800000}")  // 7 jours en millisecondes
    private long refreshTokenExpiration;

    /**
     * Crée un nouveau refresh token pour un utilisateur
     */
    @Override
    @Transactional
    public RefreshToken createRefreshToken(AppUser user) {
        // Nettoye les vieux tokens expirés
        cleanExpiredTokens(user);

        // Génère un UUID unique pour le token
        String tokenValue = UUID.randomUUID().toString();

        // Crée le refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenValue)
                .user(user)
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .revoked(false)
                .createdAt(Instant.now())
                .build();

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        log.info("Refresh token créé pour l'utilisateur: {}", user.getEmail());

        return savedToken;
    }

    /**
     * Valide et récupère un refresh token avec l'utilisateur associé
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> getRefreshToken(String token) {
        return refreshTokenRepository.findByTokenWithUser(token);
    }

    /**
     * Vérifie si un refresh token est valide
     */
    @Override
    @Transactional(readOnly = true)
    public boolean validateRefreshToken(RefreshToken token) {
        if (token == null) {
            log.warn("Refresh token est null");
            return false;
        }

        // Force le chargement de l'utilisateur dans la transaction
        if (token.getUser() != null) {
            token.getUser().getIdUser();
        }

        // Vérifie si le token est expiré
        if (token.isExpired()) {
            log.warn("Refresh token expiré pour l'utilisateur: {}", token.getUser().getEmail());
            return false;
        }

        // Vérifie si le token a été révoqué
        if (token.getRevoked()) {
            log.warn("Refresh token révoqué pour l'utilisateur: {}", token.getUser().getEmail());
            return false;
        }

        return true;
    }

    /**
     * Révoque un refresh token spécifique
     */
    @Override
    @Transactional
    public void revokeRefreshToken(String tokenValue) {
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(tokenValue);
        if (tokenOpt.isPresent()) {
            RefreshToken token = tokenOpt.get();
            token.setRevoked(true);
            token.setRevokedAt(Instant.now());
            refreshTokenRepository.save(token);
            log.info("Refresh token révoqué pour l'utilisateur: {}", token.getUser().getEmail());
        }
    }

    /**
     * Révoque tous les tokens d'un utilisateur
     */
    @Override
    @Transactional
    public void revokeAllUserTokens(AppUser user) {
        int revokedCount = refreshTokenRepository.revokeAllTokensByUser(user, Instant.now());
        log.info("Tous les refresh tokens de l'utilisateur {} ont été révoqués ({})", 
                 user.getEmail(), revokedCount);
    }

    /**
     * Nettoie les tokens expiré d'un utilisateur
     */
    @Override
    @Transactional
    public void cleanExpiredTokens(AppUser user) {
        refreshTokenRepository.deleteExpiredTokensByUser(user, Instant.now());
        log.debug("Tokens expiré nettoyés pour l'utilisateur: {}", user.getEmail());
    }

    /**
     * Supprime un refresh token
     */
    @Override
    @Transactional
    public void deleteRefreshToken(Long id) {
        refreshTokenRepository.deleteById(id);
        log.info("Refresh token supprimé: {}", id);
    }
}
