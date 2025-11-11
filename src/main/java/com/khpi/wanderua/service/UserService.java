package com.khpi.wanderua.service;

import com.khpi.wanderua.entity.Role;
import com.khpi.wanderua.entity.RoleConstants;
import com.khpi.wanderua.entity.User;
import com.khpi.wanderua.repository.RoleRepository;
import com.khpi.wanderua.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    @Override // login by email
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        log.debug("Loading user: {} with roles: {}", email, user.getRoles());
        return user;
    }
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public List<User> allUsers() {
        return userRepository.findAll();
    }
    public User save(User user){ return userRepository.save(user);}

    // UPDATED VARIATION
    public boolean saveUser(User user) {
        Optional<User> userFromDBByUsername = userRepository.findByUsername(user.getUsername());
        if (userFromDBByUsername.isPresent()) {
            log.warn("User with username {} already exists", user.getUsername());
            return false;
        }
        User userFromDBByEmail = userRepository.findByEmail(user.getEmail());
        if (userFromDBByEmail != null) {
            log.warn("User with email {} already exists", user.getEmail());
            return false;
        }

        try {
            // Set roles: USER
            Set<Role> roles = new HashSet<>();

            // Add base role USER
            Role userRole = roleRepository.findByName(RoleConstants.ROLE_USER)
                    .orElseGet(() -> createDefaultRole(RoleConstants.ROLE_USER));
            roles.add(userRole);

            /*init roles if not already init
            Role businessRole = roleRepository.findByName(RoleConstants.ROLE_BUSINESS)
                    .orElseGet(() -> createDefaultRole(RoleConstants.ROLE_BUSINESS));
            Role adminRole = roleRepository.findByName(RoleConstants.ROLE_ADMIN)
                    .orElseGet(() -> createDefaultRole(RoleConstants.ROLE_ADMIN));*/

            /* Add role BUSINESS (for test)
            roles.add(businessRole); */

            user.setRoles(roles);
            user.setBusinessRepresentVerify(false);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEnabled(true);

            userRepository.save(user);
            log.info("User {} registered successfully as default user", user.getUsername());
            return true;

        } catch (Exception e) {
            log.error("Error saving user: ", e);
            return false;
        }
    }

    /*public boolean saveRegularUser(User user) {
        Optional<User> userFromDBByUsername = userRepository.findByUsername(user.getUsername());
        if (userFromDBByUsername.isPresent()) {
            return false;
        }

        User userFromDBByEmail = userRepository.findByEmail(user.getEmail());
        if (userFromDBByEmail != null) {
            return false;
        }

        try {
            Role userRole = roleRepository.findByName(RoleConstants.ROLE_USER)
                    .orElseGet(() -> createDefaultRole(RoleConstants.ROLE_USER, "Registered User"));

            user.setRoles(Collections.singleton(userRole));
            user.setBusinessRepresentVerify(false);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEnabled(true);

            userRepository.save(user);
            log.info("Regular user {} registered successfully", user.getUsername());
            return true;

        } catch (Exception e) {
            log.error("Error saving regular user: ", e);
            return false;
        }
    } */
    public boolean verifyBusinessUser(Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return false;
            }

            User user = userOpt.get();

            if (!user.hasRole(RoleConstants.ROLE_BUSINESS)) {
                Role businessRole = roleRepository.findByName(RoleConstants.ROLE_BUSINESS)
                        .orElseGet(() -> createDefaultRole(RoleConstants.ROLE_BUSINESS));
                user.addRole(businessRole);
            }

            user.setBusinessRepresentVerify(true);
            userRepository.save(user);

            log.info("User {} verified as business representative", user.getUsername());
            return true;

        } catch (Exception e) {
            log.error("Error verifying business user: ", e);
            return false;
        }
    }

    private Role createDefaultRole(String roleName) {
        Role role = Role.builder()
                .name(roleName)
                .build();
        return roleRepository.save(role);
    }

    public boolean deleteUser(Long userId) {
        try {
            if (userRepository.findById(userId).isPresent()) {
                userRepository.deleteById(userId);
                log.info("User with ID {} deleted", userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error deleting user: ", e);
            return false;
        }
    }

    private void createRoleIfNotExists(String roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = Role.builder()
                    .name(roleName)
                    .build();
            roleRepository.save(role);
            log.info("Created role: {}", roleName);
        }
    }

    public boolean canCreateAdvertisement(User user) {
        return user != null && user.isBusinessVerified();
    }

    public User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Authentication is null or not authenticated");
            return null;
        }

        String identifier = authentication.getName();
        log.debug("Looking for user with identifier: {}", identifier);

        Object principal = authentication.getPrincipal();
        log.debug("Principal type: {}", principal != null ? principal.getClass().getName() : "null");

        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            log.debug("UserDetails username: {}", userDetails.getUsername());
            identifier = userDetails.getUsername();
        }
        // try to fing by email
        User user = findUserByEmail(identifier);

        if (user == null) {
            log.warn("User not found by email: {}, trying by username", identifier);
            // Try to find by username
            user = findUserByUsername(identifier).orElse(null);
        }

        if (user == null) {
            log.error("User not found by email OR username: {}", identifier);

            // FOR TEST: Дополнительная отладка - выводим все пользователей в БД
            List<User> allUsers = allUsers();
            log.debug("Total users in database: {}", allUsers.size());
            for (User u : allUsers) {
                log.debug("DB User: id={}, username={}, email={}", u.getId(), u.getUsername(), u.getEmail());
            }
        } else {
            log.debug("User found: {}", user.getUsername());
        }

        return user;
    }

    @PostConstruct
    public void initRoles() {
        try {
            createRoleIfNotExists(RoleConstants.ROLE_USER);
            createRoleIfNotExists(RoleConstants.ROLE_BUSINESS);
            createRoleIfNotExists(RoleConstants.ROLE_ADMIN);

            //initAdminUser();

            log.info("Roles initialization completed");
        } catch (Exception e) {
            log.error("Error initializing roles: ", e);
        }
    }

    @PostConstruct
    public void initAdminUser() {
        try {
            String adminEmail = "admin@wanderua.com";
            User existingAdmin = findUserByEmail(adminEmail);

            if (existingAdmin == null) {
                log.info("Creating default admin user...");

                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("admin123")); // Хэшируем пароль
                admin.setFullName("Системний адміністратор");
                admin.setEnabled(true);
                admin.setAccountNonLocked(true);
                admin.setBusinessRepresentVerify(false);

                // Добавляем роли
                Set<Role> roles = new HashSet<>();

                Role adminRole = roleRepository.findByName(RoleConstants.ROLE_ADMIN)
                        .orElseGet(() -> createDefaultRole(RoleConstants.ROLE_ADMIN));
                roles.add(adminRole);

                Role userRole = roleRepository.findByName(RoleConstants.ROLE_USER)
                        .orElseGet(() -> createDefaultRole(RoleConstants.ROLE_USER));
                roles.add(userRole);

                admin.setRoles(roles);

                userRepository.save(admin);
                log.info("Default admin user created successfully with email: {}", adminEmail);
                log.info("⚠️  Admin credentials - Email: {}, Password: admin123", adminEmail);
            } else {
                log.debug("Admin user already exists");
            }

        } catch (Exception e) {
            log.error("Error creating admin user: ", e);
        }
    }

}