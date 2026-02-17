package com.medmuncii.medapp.auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create roles if they don't exist
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setName("ROLE_ADMIN");
            return roleRepository.save(role);
        });

        Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            Role role = new Role();
            role.setName("ROLE_USER");
            return roleRepository.save(role);
        });

        // Create or update a default admin user
        User adminUser = userRepository.findByUsername("admin").orElse(new User());
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("admin")); // Ensure password is always encoded
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminRoles.add(userRole); // Admin also has user roles
        adminUser.setRoles(adminRoles);
        userRepository.save(adminUser);

        // Create a default regular user if not exists
        if (userRepository.findByUsername("user").isEmpty()) {
            User regularUser = new User();
            regularUser.setUsername("user");
            regularUser.setPassword(passwordEncoder.encode("user")); // Encoded password
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(userRole);
            regularUser.setRoles(userRoles);
            userRepository.save(regularUser);
        }
    }
}
