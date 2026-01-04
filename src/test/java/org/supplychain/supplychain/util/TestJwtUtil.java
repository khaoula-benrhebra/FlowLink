package org.supplychain.supplychain.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.supplychain.supplychain.enums.Role;
import org.supplychain.supplychain.model.AppUser;
import org.supplychain.supplychain.repository.UserRepository;
import org.supplychain.supplychain.security.JwtService;

@Component
public class TestJwtUtil {
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public String generateAdminToken() {
        AppUser admin = createOrGetUser("admin@test.com", "admin123", Role.ADMIN);
        return jwtService.generateToken(admin);
    }
    
    public String generateUserToken() {
        AppUser user = createOrGetUser("user@test.com", "user123", Role.GESTIONNAIRE_COMMERCIAL);
        return jwtService.generateToken(user);
    }
    
    public String generateExpiredToken() {
        return "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MDk0NTkyMDB9.invalid";
    }
    
    public AppUser createOrGetUser(String email, String password, Role role) {
        return userRepository.findByEmail(email)
            .orElseGet(() -> {
                AppUser user = new AppUser();
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode(password));
                user.setFirstName(role.name());
                user.setLastName("Test");
                user.setRole(role);
                return userRepository.save(user);
            });
    }
}
