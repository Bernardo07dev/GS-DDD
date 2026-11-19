package br.com.fiap.filainvisivel.dao;

import br.com.fiap.filainvisivel.model.Previsao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PrevisaoDao {

    void inserir(Previsao previsao);

    Optional<Previsao> buscarPorId(String id);

    List<Previsao> listarTodos();

    List<Previsao> listarPorUbsId(String ubsId);

    void atualizar(Previsao previsao);

    void excluir(String id);

    boolean existePorId(String id);

    boolean existePorUbsEDataHora(String ubsId, LocalDateTime dataHora, String ignorarId);
}
