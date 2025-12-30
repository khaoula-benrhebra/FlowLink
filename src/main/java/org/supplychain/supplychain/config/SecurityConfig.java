package org.supplychain.supplychain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("!test")
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
            User.builder().username("admin").password(passwordEncoder().encode("admin123")).roles("ADMIN").build(),
            User.builder().username("gestionnaire_appro").password(passwordEncoder().encode("appro123")).roles("GESTIONNAIRE_APPROVISIONNEMENT").build(),
            User.builder().username("responsable_achats").password(passwordEncoder().encode("achats123")).roles("RESPONSABLE_ACHATS").build(),
            User.builder().username("superviseur_logistique").password(passwordEncoder().encode("logis123")).roles("SUPERVISEUR_LOGISTIQUE").build(),
            User.builder().username("chef_production").password(passwordEncoder().encode("prod123")).roles("CHEF_PRODUCTION").build(),
            User.builder().username("planificateur").password(passwordEncoder().encode("plan123")).roles("PLANIFICATEUR").build(),
            User.builder().username("superviseur_production").password(passwordEncoder().encode("superprod123")).roles("SUPERVISEUR_PRODUCTION").build(),
            User.builder().username("gestionnaire_commercial").password(passwordEncoder().encode("comm123")).roles("GESTIONNAIRE_COMMERCIAL").build(),
            User.builder().username("responsable_logistique").password(passwordEncoder().encode("resplogis123")).roles("RESPONSABLE_LOGISTIQUE").build(),
            User.builder().username("superviseur_livraisons").password(passwordEncoder().encode("livr123")).roles("SUPERVISEUR_LIVRAISONS").build()
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                
                // US1-US2: Gestion utilisateurs (ADMIN uniquement)
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                
                // US3-US5: Fournisseurs - GESTIONNAIRE_APPROVISIONNEMENT
                .requestMatchers("POST", "/api/suppliers").hasRole("GESTIONNAIRE_APPROVISIONNEMENT")
                .requestMatchers("PUT", "/api/suppliers/**").hasRole("GESTIONNAIRE_APPROVISIONNEMENT")
                .requestMatchers("DELETE", "/api/suppliers/**").hasRole("GESTIONNAIRE_APPROVISIONNEMENT")
                
                // US6: Consulter fournisseurs - SUPERVISEUR_LOGISTIQUE
                .requestMatchers("GET", "/api/suppliers").hasRole("SUPERVISEUR_LOGISTIQUE")
                
                // US7: Rechercher fournisseur - RESPONSABLE_ACHATS
                .requestMatchers("GET", "/api/suppliers/search").hasRole("RESPONSABLE_ACHATS")
                
                // US8-US10: Matières Premières - GESTIONNAIRE_APPROVISIONNEMENT
                .requestMatchers("POST", "/api/raw-materials").hasRole("GESTIONNAIRE_APPROVISIONNEMENT")
                .requestMatchers("PUT", "/api/raw-materials/**").hasRole("GESTIONNAIRE_APPROVISIONNEMENT")
                .requestMatchers("DELETE", "/api/raw-materials/**").hasRole("GESTIONNAIRE_APPROVISIONNEMENT")
                
                // US11-US12: Consulter matières - SUPERVISEUR_LOGISTIQUE
                .requestMatchers("GET", "/api/raw-materials").hasRole("SUPERVISEUR_LOGISTIQUE")
                .requestMatchers("GET", "/api/raw-materials/low-stock").hasRole("SUPERVISEUR_LOGISTIQUE")
                
                // US13-US15: Commandes approvisionnement - RESPONSABLE_ACHATS
                .requestMatchers("POST", "/api/supply-orders").hasRole("RESPONSABLE_ACHATS")
                .requestMatchers("PUT", "/api/supply-orders/**").hasRole("RESPONSABLE_ACHATS")
                .requestMatchers("DELETE", "/api/supply-orders/**").hasRole("RESPONSABLE_ACHATS")
                
                // US16-US17: Consulter commandes - SUPERVISEUR_LOGISTIQUE
                .requestMatchers("GET", "/api/supply-orders").hasRole("SUPERVISEUR_LOGISTIQUE")
                .requestMatchers("GET", "/api/supply-orders/status/**").hasRole("SUPERVISEUR_LOGISTIQUE")
                
                // US18-US20: Produits finis - CHEF_PRODUCTION
                .requestMatchers("POST", "/api/products").hasRole("CHEF_PRODUCTION")
                .requestMatchers("PUT", "/api/products/**").hasRole("CHEF_PRODUCTION")
                .requestMatchers("DELETE", "/api/products/**").hasRole("CHEF_PRODUCTION")
                
                // US21-US22: Consulter produits - SUPERVISEUR_PRODUCTION
                .requestMatchers("GET", "/api/products").hasRole("SUPERVISEUR_PRODUCTION")
                .requestMatchers("GET", "/api/products/search").hasRole("SUPERVISEUR_PRODUCTION")
                
                // US23-US25: Ordres production - CHEF_PRODUCTION
                .requestMatchers("POST", "/api/production-orders").hasRole("CHEF_PRODUCTION")
                .requestMatchers("PUT", "/api/production-orders/**").hasRole("CHEF_PRODUCTION")
                .requestMatchers("DELETE", "/api/production-orders/**").hasRole("CHEF_PRODUCTION")
                
                // US26-US27: Consulter ordres - SUPERVISEUR_PRODUCTION
                .requestMatchers("GET", "/api/production-orders").hasRole("SUPERVISEUR_PRODUCTION")
                .requestMatchers("GET", "/api/production-orders/status/**").hasRole("SUPERVISEUR_PRODUCTION")
                
                // US28-US29: Planification - PLANIFICATEUR
                .requestMatchers("GET", "/api/planning/**").hasRole("PLANIFICATEUR")
                
                // US30-US34: Clients - GESTIONNAIRE_COMMERCIAL
                .requestMatchers("POST", "/api/customers").hasRole("GESTIONNAIRE_COMMERCIAL")
                .requestMatchers("PUT", "/api/customers/**").hasRole("GESTIONNAIRE_COMMERCIAL")
                .requestMatchers("DELETE", "/api/customers/**").hasRole("GESTIONNAIRE_COMMERCIAL")
                .requestMatchers("GET", "/api/customers").hasRole("GESTIONNAIRE_COMMERCIAL")
                .requestMatchers("GET", "/api/customers/search").hasRole("GESTIONNAIRE_COMMERCIAL")
                
                // US35-US37: Commandes clients - GESTIONNAIRE_COMMERCIAL
                .requestMatchers("POST", "/api/orders").hasRole("GESTIONNAIRE_COMMERCIAL")
                .requestMatchers("PUT", "/api/orders/**").hasRole("GESTIONNAIRE_COMMERCIAL")
                .requestMatchers("DELETE", "/api/orders/**").hasRole("GESTIONNAIRE_COMMERCIAL")
                
                // US38-US39: Consulter commandes clients - SUPERVISEUR_LIVRAISONS
                .requestMatchers("GET", "/api/orders").hasRole("SUPERVISEUR_LIVRAISONS")
                .requestMatchers("GET", "/api/orders/status/**").hasRole("SUPERVISEUR_LIVRAISONS")
                
                // US40: Livraisons - RESPONSABLE_LOGISTIQUE
                .requestMatchers("/api/deliveries/**").hasRole("RESPONSABLE_LOGISTIQUE")
                
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {})
            .build();
    }
}
