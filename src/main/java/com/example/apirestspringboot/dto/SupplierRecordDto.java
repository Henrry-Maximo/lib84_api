package com.example.apirestspringboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CNPJ;

public record SupplierRecordDto(
        @Schema(example = "Editora Exemplo Ltda")
        @NotBlank(message = "name is required")
        @Size(max = 150, message = "name must have at most 150 characters")
        String name,

        @Schema(example = "contato@editoraexemplo.com")
        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid address")
        String email,

        @Schema(example = "+55 11 3333-4444")
        @Pattern(regexp = "^\\+?[0-9()\\-\\s]{8,20}$", message = "phone has an invalid format")
        String phone,

        @Schema(example = "12.345.678/0001-95") @CNPJ
        @Pattern(
                regexp = "^[A-Z0-9]{2}\\.?[A-Z0-9]{3}\\.?[A-Z0-9]{3}/?[A-Z0-9]{4}-?[0-9]{2}$",
                message = "cnpj has an invalid format")
        String cnpj
) {
}