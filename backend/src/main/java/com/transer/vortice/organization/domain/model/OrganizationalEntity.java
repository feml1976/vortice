package com.transer.vortice.organization.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Clase base para entidades organizacionales con UUID como PK y soporte para soft delete.
 * Todas las entidades organizacionales (Office, Warehouse, etc.) extienden de esta clase.
 *
 * @author Vórtice Development Team
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class OrganizationalEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Auditoría de creación
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private Long createdBy;

    // Auditoría de actualización
    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

    // Soft delete
    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    // Optimistic locking
    @Version
    @Column(name = "version")
    private Long version;

    // =====================================================
    // Métodos de ciclo de vida
    // =====================================================

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // =====================================================
    // Métodos de negocio
    // =====================================================

    /**
     * Marca la entidad como eliminada (soft delete)
     *
     * @param deletedBy ID del usuario que elimina la entidad
     */
    public void markAsDeleted(Long deletedBy) {
        this.deletedAt = Instant.now();
        this.deletedBy = deletedBy;
        this.isActive = false;
    }

    /**
     * Verifica si la entidad está eliminada
     *
     * @return true si deletedAt no es null
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    /**
     * Activa la entidad
     */
    public void activate() {
        if (isDeleted()) {
            throw new IllegalStateException("No se puede activar una entidad eliminada");
        }
        this.isActive = true;
    }

    /**
     * Desactiva la entidad temporalmente
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * Verifica si la entidad es nueva (no persistida)
     *
     * @return true si id es null
     */
    public boolean isNew() {
        return this.id == null;
    }

    // =====================================================
    // equals() y hashCode()
    // =====================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganizationalEntity that = (OrganizationalEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
