package br.com.fiap.filainvisivel.bo;

import br.com.fiap.filainvisivel.dao.EspecialidadeDao;
import br.com.fiap.filainvisivel.dao.PrevisaoDao;
import br.com.fiap.filainvisivel.dao.UbsDao;
import br.com.fiap.filainvisivel.dto.request.UbsRequest;
import br.com.fiap.filainvisivel.dto.response.MelhorHorarioResponse;
import br.com.fiap.filainvisivel.dto.response.PrevisaoResponse;
import br.com.fiap.filainvisivel.dto.response.UbsResponse;
import br.com.fiap.filainvisivel.exception.ConflitoException;
import br.com.fiap.filainvisivel.exception.EntidadeNaoEncontradaException;
import br.com.fiap.filainvisivel.exception.RegraNegocioException;
import br.com.fiap.filainvisivel.model.Previsao;
import br.com.fiap.filainvisivel.model.Ubs;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UbsBo {

    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private final UbsDao ubsDao;
    private final EspecialidadeDao especialidadeDao;
    private final PrevisaoDao previsaoDao;

    public UbsBo(UbsDao ubsDao, EspecialidadeDao especialidadeDao, PrevisaoDao previsaoDao) {
        this.ubsDao = ubsDao;
        this.especialidadeDao = especialidadeDao;
        this.previsaoDao = previsaoDao;
    }

    public List<UbsResponse> listarTodos() {
        List<Ubs> listaUbs = ubsDao.listarTodos();
        List<UbsResponse> respostas = new ArrayList<>();
        for (Ubs ubs : listaUbs) {
            ubs.setEspecialidades(especialidadeDao.listarPorUbsId(ubs.getId()));
            respostas.add(UbsResponse.fromModel(ubs));
        }
        return respostas;
    }

    public UbsResponse buscarPorId(String id) {
        Ubs ubs = buscarEntidadeOuLancarExcecao(id);
        ubs.setEspecialidades(especialidadeDao.listarPorUbsId(id));
        return UbsResponse.fromModel(ubs);
    }

    @Transactional
    public UbsResponse criar(UbsRequest request) {
        validarCampos(request);
        String nomeLimpo = request.nome().trim();
        if (ubsDao.existePorNome(nomeLimpo, null)) {
            throw new ConflitoException("Já existe uma UBS cadastrada com o nome '" + nomeLimpo + "'.");
        }

        List<String> especialidadesUnicas = validarEspecialidades(request.especialidadeIds());

        Ubs ubs = new Ubs(
                UUID.randomUUID().toString(),
                nomeLimpo,
                request.bairro().trim(),
                request.capacidade()
        );
        ubsDao.inserir(ubs);

        for (String espId : especialidadesUnicas) {
            especialidadeDao.vincularUbs(ubs.getId(), espId);
        }

        ubs.setEspecialidades(especialidadeDao.listarPorUbsId(ubs.getId()));
        return UbsResponse.fromModel(ubs);
    }

    @Transactional
    public UbsResponse atualizar(String id, UbsRequest request) {
        Ubs ubs = buscarEntidadeOuLancarExcecao(id);
        validarCampos(request);

        String nomeLimpo = request.nome().trim();
        if (ubsDao.existePorNome(nomeLimpo, id)) {
            throw new ConflitoException("Já existe outra UBS cadastrada com o nome '" + nomeLimpo + "'.");
        }

        List<String> especialidadesUnicas = null;
        if (request.especialidadeIds() != null) {
            especialidadesUnicas = validarEspecialidades(request.especialidadeIds());
        }

        ubs.setNome(nomeLimpo);
        ubs.setBairro(request.bairro().trim());
        ubs.setCapacidade(request.capacidade());
        ubsDao.atualizar(ubs);

        if (especialidadesUnicas != null) {
            especialidadeDao.desvincularTodasDeUbs(id);
            for (String espId : especialidadesUnicas) {
                especialidadeDao.vincularUbs(id, espId);
            }
        }

        ubs.setEspecialidades(especialidadeDao.listarPorUbsId(id));
        return UbsResponse.fromModel(ubs);
    }

    public void excluir(String id) {
        if (!ubsDao.existePorId(id)) {
            throw new EntidadeNaoEncontradaException("UBS com id '" + id + "' não encontrada.");
        }
        ubsDao.excluir(id);
    }

    public MelhorHorarioResponse recomendarMelhorHorario(String ubsId) {
        Ubs ubs = buscarEntidadeOuLancarExcecao(ubsId);
        List<Previsao> previsoes = previsaoDao.listarPorUbsId(ubsId);
        if (previsoes.isEmpty()) {
            throw new EntidadeNaoEncontradaException("Nenhuma previsão encontrada para a UBS '" + ubs.getNome() + "'.");
        }

        Previsao melhor = previsoes.get(0);
        List<PrevisaoResponse> analisadas = new ArrayList<>();
        for (Previsao atual : previsoes) {
            analisadas.add(PrevisaoResponse.fromModel(atual));
            if (isMelhorPrevisao(atual, melhor)) {
                melhor = atual;
            }
        }

        String recomendacao = "Melhor horário para ir à " + ubs.getNome()
                + " (" + ubs.getBairro() + "): "
                + melhor.getDataHora().format(FORMATO_DATA_HORA)
                + " - " + melhor.orientarCidadao();

        return new MelhorHorarioResponse(
                ubs.getId(),
                ubs.getNome(),
                ubs.getBairro(),
                PrevisaoResponse.fromModel(melhor),
                recomendacao,
                analisadas
        );
    }

    private Ubs buscarEntidadeOuLancarExcecao(String id) {
        return ubsDao.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("UBS com id '" + id + "' não encontrada."));
    }

    private void validarCampos(UbsRequest request) {
        if (request.nome() == null || request.nome().isBlank()) {
            throw new RegraNegocioException("Nome da UBS é obrigatório.");
        }
        if (request.bairro() == null || request.bairro().isBlank()) {
            throw new RegraNegocioException("Bairro da UBS é obrigatório.");
        }
        if (request.capacidade() == null || request.capacidade() <= 0) {
            throw new RegraNegocioException("Capacidade da UBS deve ser maior que zero.");
        }
    }

    private List<String> validarEspecialidades(List<String> especialidadeIds) {
        List<String> unicas = new ArrayList<>();
        if (especialidadeIds == null) {
            return unicas;
        }
        for (String espId : especialidadeIds) {
            if (espId == null || espId.isBlank()) {
                continue;
            }
            String idLimpo = espId.trim();
            if (!especialidadeDao.existePorId(idLimpo)) {
                throw new EntidadeNaoEncontradaException("Especialidade com id '" + idLimpo + "' não encontrada.");
            }
            if (!unicas.contains(idLimpo)) {
                unicas.add(idLimpo);
            }
        }
        return unicas;
    }

    private boolean isMelhorPrevisao(Previsao candidata, Previsao atualMelhor) {
        if (candidata.getProbLotacao() < atualMelhor.getProbLotacao()) {
            return true;
        }
        if (Double.compare(candidata.getProbLotacao(), atualMelhor.getProbLotacao()) == 0) {
            if (candidata.getEsperaEstimadaMin() < atualMelhor.getEsperaEstimadaMin()) {
                return true;
            }
            if (candidata.getEsperaEstimadaMin() == atualMelhor.getEsperaEstimadaMin()
                    && candidata.getDataHora().isBefore(atualMelhor.getDataHora())) {
                return true;
            }
        }
        return false;
    }
}
