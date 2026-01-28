package com.transer.vortice.organization.application.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para la solicitud de creación de un proveedor de llantas.
 *
 * @author Vórtice Development Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTireSupplierRequest {

    @NotBlank(message = "El código del proveedor es obligatorio")
    @Size(min = 2, max = 10, message = "El código debe tener entre 2 y 10 caracteres")
    @Pattern(regexp = "^[A-Z0-9\\-]{2,10}$", message = "El código debe contener solo letras mayúsculas, números y guiones")
    private String code;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String name;

    @NotBlank(message = "El NIT o identificación tributaria es obligatorio")
    @Size(max = 20, message = "El NIT no puede superar los 20 caracteres")
    private String taxId;

    @NotNull(message = "La oficina es obligatoria")
    private UUID officeId;

    @Size(max = 100, message = "El nombre de contacto no puede superar los 100 caracteres")
    private String contactName;

    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede superar los 100 caracteres")
    private String email;

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    private String phone;

    @Size(max = 500, message = "La dirección no puede superar los 500 caracteres")
    private String address;
}
