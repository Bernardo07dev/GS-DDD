package br.com.fiap.filainvisivel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record UbsRequest(
        @NotBlank(message = "Nome da UBS é obrigatório.")
        String nome,

        @NotBlank(message = "Bairro da UBS é obrigatório.")
        String bairro,

        @NotNull(message = "Capacidade da UBS é obrigatória.")
        @Positive(message = "Capacidade da UBS deve ser maior que zero.")
        Integer capacidade,

        List<String> especialidadeIds
) {
}
