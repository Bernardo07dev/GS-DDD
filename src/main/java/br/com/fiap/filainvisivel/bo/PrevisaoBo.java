package br.com.fiap.filainvisivel.bo;

import br.com.fiap.filainvisivel.dao.PrevisaoDao;
import br.com.fiap.filainvisivel.dao.UbsDao;
import br.com.fiap.filainvisivel.dto.request.PrevisaoRequest;
import br.com.fiap.filainvisivel.dto.response.PrevisaoResponse;
import br.com.fiap.filainvisivel.exception.ConflitoException;
import br.com.fiap.filainvisivel.exception.EntidadeNaoEncontradaException;
import br.com.fiap.filainvisivel.exception.RegraNegocioException;
import br.com.fiap.filainvisivel.factory.PrevisaoFactory;
import br.com.fiap.filainvisivel.model.Previsao;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PrevisaoBo {

    private final PrevisaoDao previsaoDao;
    private final UbsDao ubsDao;

    public PrevisaoBo(PrevisaoDao previsaoDao, UbsDao ubsDao) {
        this.previsaoDao = previsaoDao;
        this.ubsDao = ubsDao;
    }

    public List<PrevisaoResponse> listarTodos() {
        List<Previsao> previsoes = previsaoDao.listarTodos();
        return converterParaListaResponse(previsoes);
    }

    public List<PrevisaoResponse> listarPorUbs(String ubsId) {
        validarExistenciaUbs(ubsId);
        List<Previsao> previsoes = previsaoDao.listarPorUbsId(ubsId);
        return converterParaListaResponse(previsoes);
    }

    public PrevisaoResponse buscarPorId(String id) {
        Previsao previsao = previsaoDao.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Previsão com id '" + id + "' não encontrada."));
        return PrevisaoResponse.fromModel(previsao);
    }

    public PrevisaoResponse criar(PrevisaoRequest request) {
        validarExistenciaUbs(request.ubsId());
        validarDadosPrevisao(request);

        String ubsIdLimpo = request.ubsId().trim();
        if (previsaoDao.existePorUbsEDataHora(ubsIdLimpo, request.dataHora(), null)) {
            throw new ConflitoException("Já existe uma previsão cadastrada para esta UBS no horário informado.");
        }

        Previsao previsao = PrevisaoFactory.criar(
                UUID.randomUUID().toString(),
                ubsIdLimpo,
                request.dataHora(),
                request.probLotacao(),
                request.esperaEstimadaMin()
        );
        previsaoDao.inserir(previsao);
        return PrevisaoResponse.fromModel(previsao);
    }

    public PrevisaoResponse atualizar(String id, PrevisaoRequest request) {
        if (!previsaoDao.existePorId(id)) {
            throw new EntidadeNaoEncontradaException("Previsão com id '" + id + "' não encontrada.");
        }

        validarExistenciaUbs(request.ubsId());
        validarDadosPrevisao(request);

        String ubsIdLimpo = request.ubsId().trim();
        if (previsaoDao.existePorUbsEDataHora(ubsIdLimpo, request.dataHora(), id)) {
            throw new ConflitoException("Já existe outra previsão cadastrada para esta UBS no horário informado.");
        }

        Previsao previsaoAtualizada = PrevisaoFactory.criar(
                id,
                ubsIdLimpo,
                request.dataHora(),
                request.probLotacao(),
                request.esperaEstimadaMin()
        );
        previsaoDao.atualizar(previsaoAtualizada);
        return PrevisaoResponse.fromModel(previsaoAtualizada);
    }

    public void excluir(String id) {
        if (!previsaoDao.existePorId(id)) {
            throw new EntidadeNaoEncontradaException("Previsão com id '" + id + "' não encontrada.");
        }
        previsaoDao.excluir(id);
    }

    private void validarExistenciaUbs(String ubsId) {
        if (ubsId == null || ubsId.isBlank() || !ubsDao.existePorId(ubsId.trim())) {
            throw new EntidadeNaoEncontradaException("UBS com id '" + ubsId + "' não encontrada.");
        }
    }

    private void validarDadosPrevisao(PrevisaoRequest request) {
        if (request.dataHora() == null) {
            throw new RegraNegocioException("Data e hora da previsão são obrigatórias.");
        }
        if (request.probLotacao() == null || request.probLotacao() < 0.0 || request.probLotacao() > 1.0) {
            throw new RegraNegocioException("A probabilidade de lotação deve estar entre 0.0 e 1.0.");
        }
        if (request.esperaEstimadaMin() == null || request.esperaEstimadaMin() < 0) {
            throw new RegraNegocioException("A espera estimada em minutos não pode ser negativa.");
        }
    }

    private List<PrevisaoResponse> converterParaListaResponse(List<Previsao> previsoes) {
        List<PrevisaoResponse> respostas = new ArrayList<>();
        for (Previsao previsao : previsoes) {
            respostas.add(PrevisaoResponse.fromModel(previsao));
        }
        return respostas;
    }
}
