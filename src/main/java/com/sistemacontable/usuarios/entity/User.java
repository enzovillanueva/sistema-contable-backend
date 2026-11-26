package com.sistemacontable.usuarios.entity;

import com.sistemacontable.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "username", length = 30, nullable = false, unique = true)
    private String username;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    @Column(name = "email", length = 40, nullable = false, unique = true)
    private String email;
    @Column(name = "is_active", nullable = false)
    private boolean isActive;
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    public User(String username, String passwordHash, String email) {
        this.username = Objects.requireNonNull(username, "username");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
        this.email = Objects.requireNonNull(email, "email");
    }

    public void changeUsername(String username) {
        this.username = Objects.requireNonNull(username, "username");
    }

    public void changeEmail(String email) {
        this.email = Objects.requireNonNull(email, "email");
    }

    public void changePasswordHash(String passwordHash) {
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }
}
