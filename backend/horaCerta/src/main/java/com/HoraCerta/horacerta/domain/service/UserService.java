package com.HoraCerta.horacerta.domain.service;

import com.HoraCerta.horacerta.adapter.dto.request.user.UserRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.request.user.UserRoleUpdateRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.request.user.UserUpdateRequestDTO;
import com.HoraCerta.horacerta.adapter.dto.response.UserResponseDTO;
import com.HoraCerta.horacerta.domain.entity.User;
import com.HoraCerta.horacerta.domain.enums.Role;
import com.HoraCerta.horacerta.domain.exception.EmailAlreadyExistsException;
import com.HoraCerta.horacerta.domain.exception.ResourceNotFoundException;
import com.HoraCerta.horacerta.domain.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO register(UserRequestDTO dto) {
        String email = normalizeEmail(dto.email());

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(
                    "Email is already registered"
            );
        }
        User user = new User();
        user.setUsername(dto.username().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(dto.password()));
        user.setRole(Role.OPERATOR);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        return toResponseDTO(savedUser);
    }
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));

        return toResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO update(UUID id, UserUpdateRequestDTO dto) {
        User user = getUserOrThrow(id);
        String email = normalizeEmail(dto.email());

        if (!user.getEmail().equals(email)
                && userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(
                    "Email is already registered"
            );
        }

        user.setUsername(dto.username().trim());
        user.setEmail(email);

        User updatedUser = userRepository.save(user);

        return toResponseDTO(updatedUser);
    }

    @Transactional
    public void delete(UUID id) {
        User user = getUserOrThrow(id);

        userRepository.delete(user);
    }

    @Transactional
    public UserResponseDTO updateRole(UUID id, UserRoleUpdateRequestDTO dto) {
        User user = getUserOrThrow(id);

        user.setRole(dto.role());

        User updatedUser = userRepository.save(user);
        
        return toResponseDTO(updatedUser);
    }


    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getActive(),
                user.getCreatedAt()
        );
    }

    private User getUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );
    }
    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
