package com.devbank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteUpdateRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100 , message= "O nome deve ter no máximo 100 caracteres")
        String nome,

        @NotBlank(message = "O E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email
) {
}
