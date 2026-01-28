package com.transer.vortice.organization.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de actualización de un almacén.
 * El código y la oficina NO se pueden actualizar.
 *
 * @author Vórtice Development Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWarehouseRequest {

    @NotBlank(message = "El nombre del almacén es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String name;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;
}
