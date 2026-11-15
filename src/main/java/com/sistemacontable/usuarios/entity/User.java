package com.sistemacontable.usuarios.entity;

import com.sistemacontable.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
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
}
