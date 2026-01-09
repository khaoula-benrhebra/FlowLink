package org.supplychain.supplychain.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestion personnalisée des erreurs d'accès refusé (403 Forbidden)
 * 
 * Déclenché quand un utilisateur authentifié tente d'accéder à une ressource
 * pour laquelle il n'a pas les permissions nécessaires (rôle insuffisant)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                      AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.error("🔒 Accès refusé: {} - Path: {} - User: {}", 
                  accessDeniedException.getMessage(), 
                  request.getRequestURI(),
                  request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "unknown");

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpServletResponse.SC_FORBIDDEN);
        body.put("error", "Accès Refusé");
        body.put("message", "Vous n'avez pas les permissions nécessaires pour accéder à cette ressource. Vérifiez que votre rôle autorise cette action.");
        body.put("path", request.getRequestURI());

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
