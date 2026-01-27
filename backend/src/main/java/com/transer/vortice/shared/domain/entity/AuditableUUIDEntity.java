package com.transer.vortice.shared.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Clase base para entidades UUID que requieren auditoría y soft delete
 * Incluye campos de creación, actualización y eliminación con timestamps y usuario
 *
 * @author Vórtice Development Team
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableUUIDEntity extends BaseUUIDEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

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
    // Métodos de negocio para soft delete
    // =====================================================

    /**
     * Verifica si la entidad ha sido eliminada (soft delete)
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * Marca la entidad como eliminada (soft delete)
     */
    public void markAsDeleted(Long deletedByUserId) {
        this.deletedAt = Instant.now();
        this.deletedBy = deletedByUserId;
    }

    /**
     * Revierte la eliminación (soft delete)
     */
    public void undelete() {
        this.deletedAt = null;
        this.deletedBy = null;
    }
}
