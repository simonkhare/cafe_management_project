package com.cafe.cafe_management.service;

import com.cafe.cafe_management.entity.User;
import com.cafe.cafe_management.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @PostConstruct
    public void seedDatabase() {
        log.info("Checking and verifying default system accounts inside MySQL...");

        // 1. Verify and seed Employee if missing
        if (userRepository.findByUsername("chef_emma").isEmpty()) {
            userRepository.save(User.builder().username("chef_emma").password("password123").role("EMPLOYEE").build());
            log.info("Default employee account generated.");
        }

        // 2. Verify and seed Customer if missing
        if (userRepository.findByUsername("alex_customer").isEmpty()) {
            userRepository.save(User.builder().username("alex_customer").password("password123").role("CUSTOMER").build());
            log.info("Default customer account generated.");
        }

        // 3. Independent Check: Verify and seed Admin if missing
        if (userRepository.findByUsername("owner_boss").isEmpty()) {
            log.info("Admin account missing. Seeding 'owner_boss' into MySQL configuration...");
            userRepository.save(User.builder().username("owner_boss").password("boss99").role("ADMIN").build());
        }
    }

    public Optional<User> authenticate(String username, String password, String expectedRole) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password) && user.getRole().equalsIgnoreCase(expectedRole));
    }

    public boolean registerCustomer(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) return false;
        userRepository.save(User.builder().username(username).password(password).role("CUSTOMER").build());
        return true;
    }

    // NEW: Administrative logic to onboard staff profiles directly from the panel
    public boolean registerEmployee(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) return false;
        userRepository.save(User.builder().username(username).password(password).role("EMPLOYEE").build());
        log.info("Admin successfully created new EMPLOYEE profile: [{}]", username);
        return true;
    }

    // NEW: Retrieve every registration record stored in the system
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // NEW: Delete a customer or employee account permanently from MySQL
    public void removeUser(Long id) {
        log.warn("Admin executing permanent deletion of User Account ID: {}", id);
        userRepository.deleteById(id);
    }
}