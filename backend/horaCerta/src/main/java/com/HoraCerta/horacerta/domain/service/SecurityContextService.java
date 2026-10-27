package com.HoraCerta.horacerta.domain.service;

import com.HoraCerta.horacerta.domain.entity.User;
import com.HoraCerta.horacerta.domain.exception.ResourceNotFoundException;
import com.HoraCerta.horacerta.domain.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SecurityContextService {
    private final UserRepository userRepository;

    public SecurityContextService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Transactional(readOnly = true)
    public User getAuthenticatedUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new AuthenticationCredentialsNotFoundException(
                    "No authenticated user found."
            );
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found."
                ));
    }

    @Transactional(readOnly = true)
    public UUID getAuthenticatedUserId() {
        return getAuthenticatedUser().getId();
    }
}
