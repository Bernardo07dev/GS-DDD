package br.com.fiap.filainvisivel.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AlertaRequest(
        @NotBlank(message = "ID da UBS é obrigatório.")
        String ubsId,

        @NotBlank(message = "Contato do cidadão é obrigatório.")
        String contato,

        @NotBlank(message = "Horário preferido é obrigatório.")
        String horarioPreferido
) {
}
