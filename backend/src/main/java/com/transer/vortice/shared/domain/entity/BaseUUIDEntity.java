package com.transer.vortice.shared.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Clase base para entidades que usan UUID como identificador
 * Proporciona ID de tipo UUID y métodos equals/hashCode basados en ID
 *
 * @author Vórtice Development Team
 */
@Getter
@Setter
@MappedSuperclass
public abstract class BaseUUIDEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Version
    @Column(name = "version")
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseUUIDEntity that = (BaseUUIDEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public boolean isNew() {
        return this.id == null;
    }
}
