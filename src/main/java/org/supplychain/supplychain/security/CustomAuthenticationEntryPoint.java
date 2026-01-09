package org.supplychain.supplychain.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestion personnalisée des erreurs d'authentification (401 Unauthorized)
 * 
 * Fournit des messages d'erreur détaillés selon le type de problème:
 * - Token expiré
 * - Token invalide (signature, format)
 * - Token manquant
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {

        String errorMessage = determineErrorMessage(authException);
        String errorType = determineErrorType(authException);
        
        log.error("🔓 Authentification échouée [{}]: {} - Path: {}", 
                  errorType, authException.getMessage(), request.getRequestURI());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", errorType);
        body.put("message", errorMessage);
        body.put("path", request.getRequestURI());

        objectMapper.writeValue(response.getOutputStream(), body);
    }

    /**
     * Détermine le message d'erreur approprié selon le type d'exception
     */
    private String determineErrorMessage(AuthenticationException authException) {
        String exceptionMessage = authException.getMessage() != null ? authException.getMessage().toLowerCase() : "";
        
        // Token expiré
        if (exceptionMessage.contains("expired") || exceptionMessage.contains("exp claim")) {
            return "Le token JWT a expiré. Veuillez vous reconnecter pour obtenir un nouveau token.";
        }
        
        // Token avec signature invalide
        if (exceptionMessage.contains("signature") || exceptionMessage.contains("signed")) {
            return "La signature du token JWT est invalide. Le token a peut-être été altéré.";
        }
        
        // Token malformé
        if (exceptionMessage.contains("malformed") || exceptionMessage.contains("invalid token") 
            || exceptionMessage.contains("jwt strings must contain exactly 2 period")) {
            return "Le format du token JWT est invalide. Vérifiez que vous envoyez un token valide.";
        }
        
        // Issuer invalide
        if (exceptionMessage.contains("issuer") || exceptionMessage.contains("iss claim")) {
            return "L'émetteur (issuer) du token ne correspond pas au serveur d'authentification attendu.";
        }
        
        // Token manquant
        if (authException instanceof InvalidBearerTokenException) {
            return "Token Bearer manquant ou invalide dans l'en-tête Authorization.";
        }
        
        // Message par défaut
        return "Authentification requise. Veuillez fournir un token JWT valide dans l'en-tête Authorization.";
    }

    /**
     * Détermine le type d'erreur pour la réponse
     */
    private String determineErrorType(AuthenticationException authException) {
        String exceptionMessage = authException.getMessage() != null ? authException.getMessage().toLowerCase() : "";
        
        if (exceptionMessage.contains("expired")) {
            return "Token Expiré";
        }
        if (exceptionMessage.contains("signature")) {
            return "Signature Invalide";
        }
        if (exceptionMessage.contains("malformed") || exceptionMessage.contains("invalid token")) {
            return "Token Malformé";
        }
        if (exceptionMessage.contains("issuer")) {
            return "Issuer Invalide";
        }
        
        return "Authentification Requise";
    }
}
