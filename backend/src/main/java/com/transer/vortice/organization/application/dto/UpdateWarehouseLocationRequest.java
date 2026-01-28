package com.transer.vortice.organization.application.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de actualización de una ubicación.
 * El código y el almacén NO se pueden actualizar.
 *
 * @author Vórtice Development Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWarehouseLocationRequest {

    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String name;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;
}
