package br.com.fiap.filainvisivel.dto.response;

import br.com.fiap.filainvisivel.model.RegistroFila;

import java.time.LocalDateTime;

public record RegistroFilaResponse(
        String id,
        String ubsId,
        LocalDateTime dataHora,
        int pessoasNaFila,
        int tempoEsperaMin
) {
    public static RegistroFilaResponse fromModel(RegistroFila registro) {
        return new RegistroFilaResponse(
                registro.getId(),
                registro.getUbsId(),
                registro.getDataHora(),
                registro.getPessoasNaFila(),
                registro.getTempoEsperaMin()
        );
    }
}
