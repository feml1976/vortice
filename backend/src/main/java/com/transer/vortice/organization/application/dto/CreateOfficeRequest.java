package com.transer.vortice.organization.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de creación de una oficina.
 *
 * @author Vórtice Development Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOfficeRequest {

    @NotBlank(message = "El código de la oficina es obligatorio")
    @Size(min = 2, max = 10, message = "El código debe tener entre 2 y 10 caracteres")
    @Pattern(regexp = "^[A-Z0-9]{2,10}$", message = "El código debe contener solo letras mayúsculas y números")
    private String code;

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
