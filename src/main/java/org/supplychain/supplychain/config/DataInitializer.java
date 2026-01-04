package org.supplychain.supplychain.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.supplychain.supplychain.enums.Role;
import org.supplychain.supplychain.model.AppUser;
import org.supplychain.supplychain.repository.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("Base de données déjà initialisée, pas besoin de créer les utilisateurs");
            return;
        }

        log.info("Initialisation de la base de données avec les données par défaut...");

        // Créer l'utilisateur ADMIN
        createUserIfNotExists(
                "admin@example.com",
                "admin123",
                "Admin",
                "User",
                Role.ADMIN
        );

        // Créer les autres rôles
        createUserIfNotExists(
                "gestionnaire@example.com",
                "password123",
                "Gestionnaire",
                "Approvisionnement",
                Role.GESTIONNAIRE_APPROVISIONNEMENT
        );

        createUserIfNotExists(
                "commercial@example.com",
                "password123",
                "Gestionnaire",
                "Commercial",
                Role.GESTIONNAIRE_COMMERCIAL
        );

        createUserIfNotExists(
                "production@example.com",
                "password123",
                "Chef",
                "Production",
                Role.CHEF_PRODUCTION
        );

        createUserIfNotExists(
                "logistique@example.com",
                "password123",
                "Responsable",
                "Logistique",
                Role.RESPONSABLE_LOGISTIQUE
        );

        createUserIfNotExists(
                "achats@example.com",
                "password123",
                "Responsable",
                "Achats",
                Role.RESPONSABLE_ACHATS
        );

        createUserIfNotExists(
                "planificateur@example.com",
                "password123",
                "Planificateur",
                "Production",
                Role.PLANIFICATEUR
        );

        createUserIfNotExists(
                "superviseur_logistique@example.com",
                "password123",
                "Superviseur",
                "Logistique",
                Role.SUPERVISEUR_LOGISTIQUE
        );

        createUserIfNotExists(
                "superviseur_production@example.com",
                "password123",
                "Superviseur",
                "Production",
                Role.SUPERVISEUR_PRODUCTION
        );

        createUserIfNotExists(
                "superviseur_livraisons@example.com",
                "password123",
                "Superviseur",
                "Livraisons",
                Role.SUPERVISEUR_LIVRAISONS
        );

        log.info("Initialisation de la base de données terminée avec succès!");
    }

    /**
     * Crée un utilisateur s'il n'existe pas déjà
     */
    private void createUserIfNotExists(String email, String password, String firstName, 
                                      String lastName, Role role) {
        if (userRepository.findByEmail(email).isEmpty()) {
            AppUser user = new AppUser();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setRole(role);

            userRepository.save(user);
            log.info(" Utilisateur créé: {} ({})", email, role);
        } else {
            log.info(" Utilisateur déjà existant: {} ({})", email, role);
        }
    }
}
