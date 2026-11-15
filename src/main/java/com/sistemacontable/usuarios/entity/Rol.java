package com.sistemacontable.usuarios.entity;

import com.sistemacontable.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "roles")
public class Rol extends BaseEntity {
    @Column(name = "name", length = 20)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
}
