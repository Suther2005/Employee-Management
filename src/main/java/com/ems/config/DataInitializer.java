package com.ems.config;

import com.ems.entity.Role;
import com.ems.entity.User;
import com.ems.repository.RoleRepository;
import com.ems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data Initializer — runs on startup to seed default roles and admin user
 * if they don't already exist. This ensures the app works even on a fresh database
 * without running data.sql manually.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Seed roles
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role("ADMIN"));
            roleRepository.save(new Role("EMPLOYEE"));
            log.info("✅ Roles seeded: ADMIN, EMPLOYEE");
        }

        // Seed default admin
        if (!userRepository.existsByUsername("admin@ems.com")) {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

            User admin = User.builder()
                    .username("admin@ems.com")
                    .email("admin@ems.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(adminRole)
                    .enabled(true)
                    .build();

            userRepository.save(admin);
            log.info("✅ Default admin created: admin@ems.com / Admin@123");
        }
    }
}
