package br.com.fiap.filainvisivel.dao;

import br.com.fiap.filainvisivel.model.Alerta;

import java.util.List;
import java.util.Optional;

public interface AlertaDao {

    void inserir(Alerta alerta);

    Optional<Alerta> buscarPorId(String id);

    List<Alerta> listarTodos();

    List<Alerta> listarPorUbsId(String ubsId);

    void atualizar(Alerta alerta);

    void excluir(String id);

    boolean existePorId(String id);
}
