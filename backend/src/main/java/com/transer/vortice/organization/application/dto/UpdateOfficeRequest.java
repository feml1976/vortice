package com.transer.vortice.organization.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de actualización de una oficina.
 * El código NO se puede actualizar.
 *
 * @author Vórtice Development Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOfficeRequest {

    @NotBlank(message = "El nombre de la oficina es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String name;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 50, message = "La ciudad no puede superar los 50 caracteres")
    private String city;

    @Size(max = 500, message = "La dirección no puede superar los 500 caracteres")
    private String address;

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    private String phone;
}
