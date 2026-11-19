package br.com.fiap.filainvisivel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RegistroFilaRequest(
        @NotBlank(message = "ID da UBS é obrigatório.")
        String ubsId,

        @NotNull(message = "Data e hora do registro são obrigatórias.")
        LocalDateTime dataHora,

        @NotNull(message = "Quantidade de pessoas na fila é obrigatória.")
        Integer pessoasNaFila,

        @NotNull(message = "Tempo de espera em minutos é obrigatório.")
        Integer tempoEsperaMin
) {
}
