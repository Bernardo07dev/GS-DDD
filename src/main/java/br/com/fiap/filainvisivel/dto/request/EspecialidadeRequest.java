package br.com.fiap.filainvisivel.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EspecialidadeRequest(
        @NotBlank(message = "Nome da especialidade é obrigatório.")
        String nome
) {
}
