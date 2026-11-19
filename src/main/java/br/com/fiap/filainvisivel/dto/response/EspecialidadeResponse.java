package br.com.fiap.filainvisivel.dto.response;

import br.com.fiap.filainvisivel.model.Especialidade;

public record EspecialidadeResponse(
        String id,
        String nome
) {
    public static EspecialidadeResponse fromModel(Especialidade especialidade) {
        return new EspecialidadeResponse(especialidade.getId(), especialidade.getNome());
    }
}
