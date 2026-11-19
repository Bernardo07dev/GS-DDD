package br.com.fiap.filainvisivel.dao;

import br.com.fiap.filainvisivel.model.Ubs;

import java.util.List;
import java.util.Optional;

public interface UbsDao {

    void inserir(Ubs ubs);

    Optional<Ubs> buscarPorId(String id);

    List<Ubs> listarTodos();

    void atualizar(Ubs ubs);

    void excluir(String id);

    boolean existePorId(String id);

    boolean existePorNome(String nome, String ignorarId);
}
