package br.com.fiap.filainvisivel.dao;

import br.com.fiap.filainvisivel.model.RegistroFila;

import java.util.List;
import java.util.Optional;

public interface RegistroFilaDao {

    void inserir(RegistroFila registro);

    Optional<RegistroFila> buscarPorId(String id);

    List<RegistroFila> listarTodos();

    List<RegistroFila> listarPorUbsId(String ubsId);

    void atualizar(RegistroFila registro);

    void excluir(String id);

    boolean existePorId(String id);
}
