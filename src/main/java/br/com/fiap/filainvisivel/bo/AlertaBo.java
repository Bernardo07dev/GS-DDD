package br.com.fiap.filainvisivel.bo;

import br.com.fiap.filainvisivel.dao.AlertaDao;
import br.com.fiap.filainvisivel.dao.UbsDao;
import br.com.fiap.filainvisivel.dto.request.AlertaRequest;
import br.com.fiap.filainvisivel.dto.response.AlertaResponse;
import br.com.fiap.filainvisivel.exception.EntidadeNaoEncontradaException;
import br.com.fiap.filainvisivel.exception.RegraNegocioException;
import br.com.fiap.filainvisivel.factory.AlertaFactory;
import br.com.fiap.filainvisivel.model.Alerta;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AlertaBo {

    private final AlertaDao alertaDao;
    private final UbsDao ubsDao;

    public AlertaBo(AlertaDao alertaDao, UbsDao ubsDao) {
        this.alertaDao = alertaDao;
        this.ubsDao = ubsDao;
    }

    public List<AlertaResponse> listarTodos() {
        List<Alerta> alertas = alertaDao.listarTodos();
        return converterParaListaResponse(alertas);
    }

    public List<AlertaResponse> listarPorUbs(String ubsId) {
        validarExistenciaUbs(ubsId);
        List<Alerta> alertas = alertaDao.listarPorUbsId(ubsId);
        return converterParaListaResponse(alertas);
    }

    public AlertaResponse buscarPorId(String id) {
        Alerta alerta = alertaDao.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Alerta com id '" + id + "' não encontrado."));
        return AlertaResponse.fromModel(alerta);
    }

    public AlertaResponse criar(AlertaRequest request) {
        validarExistenciaUbs(request.ubsId());
        validarCampos(request);

        Alerta alerta = AlertaFactory.criar(
                UUID.randomUUID().toString(),
                request.ubsId().trim(),
                request.contato(),
                request.horarioPreferido().trim()
        );
        alertaDao.inserir(alerta);
        return AlertaResponse.fromModel(alerta);
    }

    public AlertaResponse atualizar(String id, AlertaRequest request) {
        if (!alertaDao.existePorId(id)) {
            throw new EntidadeNaoEncontradaException("Alerta com id '" + id + "' não encontrado.");
        }

        validarExistenciaUbs(request.ubsId());
        validarCampos(request);

        Alerta alertaAtualizado = AlertaFactory.criar(
                id,
                request.ubsId().trim(),
                request.contato(),
                request.horarioPreferido().trim()
        );
        alertaDao.atualizar(alertaAtualizado);
        return AlertaResponse.fromModel(alertaAtualizado);
    }

    public void excluir(String id) {
        if (!alertaDao.existePorId(id)) {
            throw new EntidadeNaoEncontradaException("Alerta com id '" + id + "' não encontrado.");
        }
        alertaDao.excluir(id);
    }

    private void validarExistenciaUbs(String ubsId) {
        if (ubsId == null || ubsId.isBlank() || !ubsDao.existePorId(ubsId.trim())) {
            throw new EntidadeNaoEncontradaException("UBS com id '" + ubsId + "' não encontrada.");
        }
    }

    private void validarCampos(AlertaRequest request) {
        if (request.contato() == null || request.contato().isBlank()) {
            throw new RegraNegocioException("O contato do cidadão é obrigatório.");
        }
        if (request.horarioPreferido() == null || request.horarioPreferido().isBlank()) {
            throw new RegraNegocioException("O horário preferido é obrigatório.");
        }
    }

    private List<AlertaResponse> converterParaListaResponse(List<Alerta> alertas) {
        List<AlertaResponse> respostas = new ArrayList<>();
        for (Alerta alerta : alertas) {
            respostas.add(AlertaResponse.fromModel(alerta));
        }
        return respostas;
    }
}
