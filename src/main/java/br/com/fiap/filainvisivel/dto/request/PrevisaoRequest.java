package br.com.fiap.filainvisivel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PrevisaoRequest(
        @NotBlank(message = "ID da UBS é obrigatório.")
        String ubsId,

        @NotNull(message = "Data e hora da previsão são obrigatórias.")
        LocalDateTime dataHora,

        @NotNull(message = "Probabilidade de lotação é obrigatória.")
        Double probLotacao,

        @NotNull(message = "Espera estimada em minutos é obrigatória.")
        Integer esperaEstimadaMin
) {
}
