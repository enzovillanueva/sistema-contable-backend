package com.sistemacontable.usuarios.entity;

import com.sistemacontable.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "roles")
public class Rol extends BaseEntity {
    @Column(name = "name", length = 20, nullable = false, unique = true)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;

    public Rol(String name, String description) {
        this.name = Objects.requireNonNull(name, "name");
        this.description = Objects.requireNonNull(description, "description");
    }

    public void rename(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public void changeDescription(String description) {
        this.description = Objects.requireNonNull(description, "description");
    }
}
