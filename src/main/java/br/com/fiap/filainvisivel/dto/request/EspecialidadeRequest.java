package br.com.fiap.filainvisivel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EspecialidadeRequest(
        @NotBlank(message = "Nome da especialidade é obrigatório.")
        @Size(max = 100, message = "Nome da especialidade deve ter no máximo 100 caracteres.")
        String nome
) {
}
