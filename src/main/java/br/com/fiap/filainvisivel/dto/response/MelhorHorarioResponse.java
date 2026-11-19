package br.com.fiap.filainvisivel.dto.response;

import java.util.List;

public record MelhorHorarioResponse(
        String ubsId,
        String nomeUbs,
        String bairro,
        PrevisaoResponse melhorPrevisao,
        String recomendacaoCidadao,
        List<PrevisaoResponse> previsoesAnalisadas
) {
}
