package br.com.fiap.filainvisivel.dao;

import br.com.fiap.filainvisivel.model.Especialidade;

import java.util.List;
import java.util.Optional;

public interface EspecialidadeDao {

    void inserir(Especialidade especialidade);

    Optional<Especialidade> buscarPorId(String id);

    List<Especialidade> listarTodos();

    void atualizar(Especialidade especialidade);

    void excluir(String id);

    boolean existePorId(String id);

    boolean existePorNome(String nome, String ignorarId);

    List<Especialidade> listarPorUbsId(String ubsId);

    void vincularUbs(String ubsId, String especialidadeId);

    void desvincularTodasDeUbs(String ubsId);
}
