package com.HoraCerta.horacerta.domain.entity;

import com.HoraCerta.horacerta.domain.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100, message = "Name must be up to 100 characters")
    @Column(name = "username",nullable = false, length = 100)
    private String username;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email must be up to 150 characters")
    @Column(name = "email",nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotNull(message = "Role is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name = "role",nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.active == null) {
            this.active = true;
        }
    }
}
