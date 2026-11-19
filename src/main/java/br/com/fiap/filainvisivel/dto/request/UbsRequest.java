package br.com.fiap.filainvisivel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UbsRequest(
        @NotBlank(message = "Nome da UBS é obrigatório.")
        @Size(max = 120, message = "Nome da UBS deve ter no máximo 120 caracteres.")
        String nome,

        @NotBlank(message = "Bairro da UBS é obrigatório.")
        @Size(max = 100, message = "Bairro da UBS deve ter no máximo 100 caracteres.")
        String bairro,

        @NotNull(message = "Capacidade da UBS é obrigatória.")
        @Positive(message = "Capacidade da UBS deve ser maior que zero.")
        Integer capacidade,

        List<String> especialidadeIds
) {
}
