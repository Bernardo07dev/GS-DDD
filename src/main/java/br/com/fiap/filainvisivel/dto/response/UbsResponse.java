package br.com.fiap.filainvisivel.dto.response;

import br.com.fiap.filainvisivel.model.Especialidade;
import br.com.fiap.filainvisivel.model.Ubs;

import java.util.ArrayList;
import java.util.List;

public record UbsResponse(
        String id,
        String nome,
        String bairro,
        int capacidade,
        List<EspecialidadeResponse> especialidades
) {
    public static UbsResponse fromModel(Ubs ubs) {
        List<EspecialidadeResponse> listaEspecialidades = new ArrayList<>();
        if (ubs.getEspecialidades() != null) {
            for (Especialidade esp : ubs.getEspecialidades()) {
                listaEspecialidades.add(EspecialidadeResponse.fromModel(esp));
            }
        }
        return new UbsResponse(
                ubs.getId(),
                ubs.getNome(),
                ubs.getBairro(),
                ubs.getCapacidade(),
                listaEspecialidades
        );
    }
}
