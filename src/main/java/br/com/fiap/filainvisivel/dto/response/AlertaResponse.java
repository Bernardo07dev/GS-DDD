package br.com.fiap.filainvisivel.dto.response;

import br.com.fiap.filainvisivel.model.Alerta;

public record AlertaResponse(
        String id,
        String ubsId,
        String contatoAnonimizado,
        String horarioPreferido
) {
    public static AlertaResponse fromModel(Alerta alerta) {
        return new AlertaResponse(
                alerta.getId(),
                alerta.getUbsId(),
                alerta.getContatoAnonimizado(),
                alerta.getHorarioPreferido()
        );
    }
}
