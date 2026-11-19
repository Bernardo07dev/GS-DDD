package br.com.fiap.filainvisivel.bo;

import br.com.fiap.filainvisivel.dao.EspecialidadeDao;
import br.com.fiap.filainvisivel.dto.request.EspecialidadeRequest;
import br.com.fiap.filainvisivel.dto.response.EspecialidadeResponse;
import br.com.fiap.filainvisivel.exception.ConflitoException;
import br.com.fiap.filainvisivel.exception.EntidadeNaoEncontradaException;
import br.com.fiap.filainvisivel.exception.RegraNegocioException;
import br.com.fiap.filainvisivel.model.Especialidade;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EspecialidadeBo {

    private final EspecialidadeDao especialidadeDao;

    public EspecialidadeBo(EspecialidadeDao especialidadeDao) {
        this.especialidadeDao = especialidadeDao;
    }

    public List<EspecialidadeResponse> listarTodos() {
        List<Especialidade> especialidades = especialidadeDao.listarTodos();
        List<EspecialidadeResponse> respostas = new ArrayList<>();
        for (Especialidade esp : especialidades) {
            respostas.add(EspecialidadeResponse.fromModel(esp));
        }
        return respostas;
    }

    public EspecialidadeResponse buscarPorId(String id) {
        Especialidade especialidade = especialidadeDao.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Especialidade com id '" + id + "' não encontrada."));
        return EspecialidadeResponse.fromModel(especialidade);
    }

    public EspecialidadeResponse criar(EspecialidadeRequest request) {
        String nomeLimpo = validarNome(request.nome());
        if (especialidadeDao.existePorNome(nomeLimpo, null)) {
            throw new ConflitoException("Já existe uma especialidade cadastrada com o nome '" + nomeLimpo + "'.");
        }

        Especialidade especialidade = new Especialidade(UUID.randomUUID().toString(), nomeLimpo);
        especialidadeDao.inserir(especialidade);
        return EspecialidadeResponse.fromModel(especialidade);
    }

    public EspecialidadeResponse atualizar(String id, EspecialidadeRequest request) {
        Especialidade especialidade = especialidadeDao.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Especialidade com id '" + id + "' não encontrada."));

        String nomeLimpo = validarNome(request.nome());
        if (especialidadeDao.existePorNome(nomeLimpo, id)) {
            throw new ConflitoException("Já existe outra especialidade cadastrada com o nome '" + nomeLimpo + "'.");
        }

        especialidade.setNome(nomeLimpo);
        especialidadeDao.atualizar(especialidade);
        return EspecialidadeResponse.fromModel(especialidade);
    }

    public void excluir(String id) {
        if (!especialidadeDao.existePorId(id)) {
            throw new EntidadeNaoEncontradaException("Especialidade com id '" + id + "' não encontrada.");
        }
        especialidadeDao.excluir(id);
    }

    private String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("Nome da especialidade é obrigatório.");
        }
        return nome.trim();
    }
}
