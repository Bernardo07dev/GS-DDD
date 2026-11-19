package br.com.fiap.filainvisivel.dto.response;

import br.com.fiap.filainvisivel.model.Previsao;
import br.com.fiap.filainvisivel.model.Semaforo;

import java.time.LocalDateTime;

public record PrevisaoResponse(
        String id,
        String ubsId,
        LocalDateTime dataHora,
        double probLotacao,
        int esperaEstimadaMin,
        Semaforo semaforo,
        String orientacao
) {
    public static PrevisaoResponse fromModel(Previsao previsao) {
        return new PrevisaoResponse(
                previsao.getId(),
                previsao.getUbsId(),
                previsao.getDataHora(),
                previsao.getProbLotacao(),
                previsao.getEsperaEstimadaMin(),
                previsao.getSemaforo(),
                previsao.orientarCidadao()
        );
    }
}
