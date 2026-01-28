package com.transer.vortice.organization.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para la solicitud de creación de una ubicación.
 *
 * @author Vórtice Development Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWarehouseLocationRequest {

    @NotBlank(message = "El código de la ubicación es obligatorio")
    @Size(min = 1, max = 10, message = "El código debe tener entre 1 y 10 caracteres")
    @Pattern(regexp = "^[A-Z0-9\\-]{1,10}$", message = "El código debe contener solo letras mayúsculas, números y guiones")
    private String code;

    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String name;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;

    @NotNull(message = "El almacén es obligatorio")
    private UUID warehouseId;
}
