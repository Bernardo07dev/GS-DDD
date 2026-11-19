package br.com.fiap.filainvisivel.bo;

import br.com.fiap.filainvisivel.dao.RegistroFilaDao;
import br.com.fiap.filainvisivel.dao.UbsDao;
import br.com.fiap.filainvisivel.dto.request.RegistroFilaRequest;
import br.com.fiap.filainvisivel.dto.response.RegistroFilaResponse;
import br.com.fiap.filainvisivel.exception.EntidadeNaoEncontradaException;
import br.com.fiap.filainvisivel.exception.RegraNegocioException;
import br.com.fiap.filainvisivel.model.RegistroFila;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RegistroFilaBo {

    private final RegistroFilaDao registroFilaDao;
    private final UbsDao ubsDao;

    public RegistroFilaBo(RegistroFilaDao registroFilaDao, UbsDao ubsDao) {
        this.registroFilaDao = registroFilaDao;
        this.ubsDao = ubsDao;
    }

    public List<RegistroFilaResponse> listarTodos() {
        List<RegistroFila> registros = registroFilaDao.listarTodos();
        return converterParaListaResponse(registros);
    }

    public List<RegistroFilaResponse> listarPorUbs(String ubsId) {
        validarExistenciaUbs(ubsId);
        List<RegistroFila> registros = registroFilaDao.listarPorUbsId(ubsId);
        return converterParaListaResponse(registros);
    }

    public RegistroFilaResponse buscarPorId(String id) {
        RegistroFila registro = registroFilaDao.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Registro de fila com id '" + id + "' não encontrado."));
        return RegistroFilaResponse.fromModel(registro);
    }

    public RegistroFilaResponse criar(RegistroFilaRequest request) {
        validarExistenciaUbs(request.ubsId());
        validarDadosRegistro(request);

        RegistroFila registro = new RegistroFila(
                UUID.randomUUID().toString(),
                request.ubsId().trim(),
                request.dataHora(),
                request.pessoasNaFila(),
                request.tempoEsperaMin()
        );
        registroFilaDao.inserir(registro);
        return RegistroFilaResponse.fromModel(registro);
    }

    public RegistroFilaResponse atualizar(String id, RegistroFilaRequest request) {
        RegistroFila registro = registroFilaDao.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Registro de fila com id '" + id + "' não encontrado."));

        validarExistenciaUbs(request.ubsId());
        validarDadosRegistro(request);

        registro.setUbsId(request.ubsId().trim());
        registro.setDataHora(request.dataHora());
        registro.setPessoasNaFila(request.pessoasNaFila());
        registro.setTempoEsperaMin(request.tempoEsperaMin());

        registroFilaDao.atualizar(registro);
        return RegistroFilaResponse.fromModel(registro);
    }

    public void excluir(String id) {
        if (!registroFilaDao.existePorId(id)) {
            throw new EntidadeNaoEncontradaException("Registro de fila com id '" + id + "' não encontrado.");
        }
        registroFilaDao.excluir(id);
    }

    private void validarExistenciaUbs(String ubsId) {
        if (ubsId == null || ubsId.isBlank() || !ubsDao.existePorId(ubsId.trim())) {
            throw new EntidadeNaoEncontradaException("UBS com id '" + ubsId + "' não encontrada.");
        }
    }

    private void validarDadosRegistro(RegistroFilaRequest request) {
        if (request.dataHora() == null) {
            throw new RegraNegocioException("Data e hora do registro são obrigatórias.");
        }
        if (request.pessoasNaFila() == null || request.pessoasNaFila() < 0) {
            throw new RegraNegocioException("A quantidade de pessoas na fila não pode ser negativa.");
        }
        if (request.tempoEsperaMin() == null || request.tempoEsperaMin() < 0) {
            throw new RegraNegocioException("O tempo de espera em minutos não pode ser negativo.");
        }
    }

    private List<RegistroFilaResponse> converterParaListaResponse(List<RegistroFila> registros) {
        List<RegistroFilaResponse> respostas = new ArrayList<>();
        for (RegistroFila registro : registros) {
            respostas.add(RegistroFilaResponse.fromModel(registro));
        }
        return respostas;
    }
}
