package org.supplychain.supplychain.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Skip pour les endpoints publics
        if (isPublicEndpoint(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String token = extractTokenFromRequest(request);
            
            // Si pas de token sur un endpoint protégé
            if (token == null) {
                sendErrorResponse(response, "Token JWT manquant", "Authentification requise");
                return;
            }
            
            // Si token invalide ou expiré
            if (!jwtService.validateToken(token)) {
                sendErrorResponse(response, "Token JWT invalide ou expiré", "Authentification échouée");
                return;
            }
            
            String username = jwtService.extractUsername(token);
            var userDetails = userDetailsService.loadUserByUsername(username);
            
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
                );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
        } catch (Exception e) {
            log.error("❌ Erreur JWT: {}", e.getMessage());
            sendErrorResponse(response, e.getMessage(), "Authentification échouée");
            return;
        }
        
        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String error, String message) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", error);
        body.put("message", message);
        body.put("path", "");

        objectMapper.writeValue(response.getOutputStream(), body);
    }

    private boolean isPublicEndpoint(String uri) {
        return uri.startsWith("/api/auth/") || 
               uri.startsWith("/swagger-ui/") || 
               uri.startsWith("/v3/api-docs") ||
               uri.startsWith("/actuator/");
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
