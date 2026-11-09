package org.example.security;

import lombok.RequiredArgsConstructor;
import org.example.domain.exception.DuplicateResourceException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Loads users for Spring Security and manages their creation (passwords are stored as BCrypt hashes). */
@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {

    private static final int MIN_PASSWORD_LENGTH = 6;

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        return User.withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .roles(user.getRole().name())
                .build();
    }

    @Transactional
    public AppUser createUser(String username, String rawPassword, Role role) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (rawPassword == null || rawPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must have at least " + MIN_PASSWORD_LENGTH + " characters.");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role is required.");
        }
        if (appUserRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username already in use.");
        }

        return appUserRepository.save(new AppUser(username, passwordEncoder.encode(rawPassword), role));
    }

    /** Creates the user only when no user with that username exists yet (used to seed the first admin). */
    @Transactional
    public void createIfMissing(String username, String rawPassword, Role role) {
        if (!appUserRepository.existsByUsername(username)) {
            createUser(username, rawPassword, role);
        }
    }
}
