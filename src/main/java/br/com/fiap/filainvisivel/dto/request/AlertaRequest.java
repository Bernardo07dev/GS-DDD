package br.com.fiap.filainvisivel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlertaRequest(
        @NotBlank(message = "ID da UBS é obrigatório.")
        String ubsId,

        @NotBlank(message = "Contato do cidadão é obrigatório.")
        @Size(max = 120, message = "Contato do cidadão deve ter no máximo 120 caracteres.")
        String contato,

        @NotBlank(message = "Horário preferido é obrigatório.")
        @Size(max = 40, message = "Horário preferido deve ter no máximo 40 caracteres.")
        String horarioPreferido
) {
}
