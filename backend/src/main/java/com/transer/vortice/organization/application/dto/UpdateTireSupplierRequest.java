package com.transer.vortice.organization.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de actualización de un proveedor de llantas.
 * El código y la oficina NO se pueden actualizar.
 *
 * @author Vórtice Development Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTireSupplierRequest {

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String name;

    @NotBlank(message = "El NIT o identificación tributaria es obligatorio")
    @Size(max = 20, message = "El NIT no puede superar los 20 caracteres")
    private String taxId;

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
