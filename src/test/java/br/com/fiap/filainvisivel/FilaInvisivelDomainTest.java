package br.com.fiap.filainvisivel;

import br.com.fiap.filainvisivel.bo.PrevisaoBo;
import br.com.fiap.filainvisivel.bo.RegistroFilaBo;
import br.com.fiap.filainvisivel.bo.UbsBo;
import br.com.fiap.filainvisivel.dao.EspecialidadeDao;
import br.com.fiap.filainvisivel.dao.PrevisaoDao;
import br.com.fiap.filainvisivel.dao.RegistroFilaDao;
import br.com.fiap.filainvisivel.dao.UbsDao;
import br.com.fiap.filainvisivel.dto.request.PrevisaoRequest;
import br.com.fiap.filainvisivel.dto.request.RegistroFilaRequest;
import br.com.fiap.filainvisivel.dto.response.MelhorHorarioResponse;
import br.com.fiap.filainvisivel.dto.response.PrevisaoResponse;
import br.com.fiap.filainvisivel.dto.response.RegistroFilaResponse;
import br.com.fiap.filainvisivel.exception.ConflitoException;
import br.com.fiap.filainvisivel.exception.EntidadeNaoEncontradaException;
import br.com.fiap.filainvisivel.exception.RegraNegocioException;
import br.com.fiap.filainvisivel.factory.AlertaFactory;
import br.com.fiap.filainvisivel.factory.PrevisaoFactory;
import br.com.fiap.filainvisivel.model.Alerta;
import br.com.fiap.filainvisivel.model.Especialidade;
import br.com.fiap.filainvisivel.model.Previsao;
import br.com.fiap.filainvisivel.model.RegistroFila;
import br.com.fiap.filainvisivel.model.Semaforo;
import br.com.fiap.filainvisivel.model.Ubs;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilaInvisivelDomainTest {

    @Test
    void previsaoFactoryCalculaSemaforoNosLimitesConfigurados() {
        LocalDateTime horario = LocalDateTime.of(2026, 11, 20, 14, 30);

        Previsao verde = PrevisaoFactory.criar("p1", "u1", horario, 0.22, 10);
        Previsao limiteVerde = PrevisaoFactory.criar("p2", "u1", horario, 0.399, 15);
        Previsao amareloInicio = PrevisaoFactory.criar("p3", "u1", horario, 0.40, 25);
        Previsao amareloFim = PrevisaoFactory.criar("p4", "u1", horario, 0.69, 35);
        Previsao vermelhoInicio = PrevisaoFactory.criar("p5", "u1", horario, 0.70, 50);

        assertEquals(Semaforo.VERDE, verde.getSemaforo());
        assertEquals(Semaforo.VERDE, limiteVerde.getSemaforo());
        assertEquals(Semaforo.AMARELO, amareloInicio.getSemaforo());
        assertEquals(Semaforo.AMARELO, amareloFim.getSemaforo());
        assertEquals(Semaforo.VERMELHO, vermelhoInicio.getSemaforo());
        assertThrows(RegraNegocioException.class, () -> PrevisaoFactory.calcularSemaforo(-0.01));
        assertThrows(RegraNegocioException.class, () -> PrevisaoFactory.calcularSemaforo(1.01));
    }

    @Test
    void alertaFactoryAnonimizaContatoRealEMantemJaAnonimizado() {
        Alerta deTelefoneReal = AlertaFactory.criar("a1", "u1", "11999991242", "14:00 - 16:00");
        assertTrue(deTelefoneReal.getContatoAnonimizado().startsWith("anon-"));
        assertTrue(deTelefoneReal.getContatoAnonimizado().endsWith("-***42"));
        assertFalse(deTelefoneReal.getContatoAnonimizado().contains("11999991242"));

        Alerta jaAnonimizado = AlertaFactory.criar("a2", "u1", "anon-7f8a9b1c-***42", "14:00 - 16:00");
        assertEquals("anon-7f8a9b1c-***42", jaAnonimizado.getContatoAnonimizado());

        Alerta tentativaBypass = AlertaFactory.criar("a3", "u1", "11999991242 ***", "14:00 - 16:00");
        assertTrue(tentativaBypass.getContatoAnonimizado().startsWith("anon-"));
        assertFalse(tentativaBypass.getContatoAnonimizado().contains("11999991242"));

        assertThrows(RegraNegocioException.class, () -> AlertaFactory.anonimizarContato("   "));
    }

    @Test
    void registroFilaBoRejeitaPessoasOuTempoNegativos() {
        InMemoryUbsDao ubsDao = new InMemoryUbsDao();
        ubsDao.inserir(new Ubs("u1", "UBS Jardim Paulista", "Jardim Paulista", 80));
        RegistroFilaBo bo = new RegistroFilaBo(new InMemoryRegistroFilaDao(), ubsDao);

        LocalDateTime agora = LocalDateTime.of(2026, 11, 20, 9, 0);

        assertThrows(RegraNegocioException.class,
                () -> bo.criar(new RegistroFilaRequest("u1", agora, -1, 20)));
        assertThrows(RegraNegocioException.class,
                () -> bo.criar(new RegistroFilaRequest("u1", agora, 5, -10)));

        RegistroFilaResponse criado = bo.criar(new RegistroFilaRequest("u1", agora, 12, 18));
        assertNotNull(criado.id());
        assertEquals(12, criado.pessoasNaFila());
        assertEquals(18, criado.tempoEsperaMin());
    }

    @Test
    void previsaoBoRejeitaConflitoDeUbsEMesmoHorario() {
        InMemoryUbsDao ubsDao = new InMemoryUbsDao();
        ubsDao.inserir(new Ubs("u1", "UBS Jardim Paulista", "Jardim Paulista", 80));
        PrevisaoBo bo = new PrevisaoBo(new InMemoryPrevisaoDao(), ubsDao);

        LocalDateTime horario = LocalDateTime.of(2026, 11, 20, 14, 30);
        PrevisaoResponse criada = bo.criar(new PrevisaoRequest("u1", horario, 0.25, 12));
        assertEquals(Semaforo.VERDE, criada.semaforo());

        assertThrows(ConflitoException.class,
                () -> bo.criar(new PrevisaoRequest("u1", horario, 0.30, 15)));

        assertThrows(EntidadeNaoEncontradaException.class,
                () -> bo.criar(new PrevisaoRequest("ubs-inexistente", horario, 0.30, 15)));
    }

    @Test
    void ubsBoRecomendaMelhorHorarioComMenorLotacao() {
        InMemoryUbsDao ubsDao = new InMemoryUbsDao();
        ubsDao.inserir(new Ubs("u1", "UBS Jardim Paulista", "Jardim Paulista", 80));
        InMemoryPrevisaoDao previsaoDao = new InMemoryPrevisaoDao();
        UbsBo ubsBo = new UbsBo(ubsDao, new InMemoryEspecialidadeDao(), previsaoDao);

        assertThrows(EntidadeNaoEncontradaException.class, () -> ubsBo.recomendarMelhorHorario("u1"));

        previsaoDao.inserir(PrevisaoFactory.criar("p1", "u1", LocalDateTime.of(2026, 11, 20, 8, 0), 0.85, 55));
        previsaoDao.inserir(PrevisaoFactory.criar("p2", "u1", LocalDateTime.of(2026, 11, 20, 11, 0), 0.55, 28));
        previsaoDao.inserir(PrevisaoFactory.criar("p3", "u1", LocalDateTime.of(2026, 11, 20, 14, 30), 0.22, 10));

        MelhorHorarioResponse recomendacao = ubsBo.recomendarMelhorHorario("u1");
        assertEquals("p3", recomendacao.melhorPrevisao().id());
        assertEquals(Semaforo.VERDE, recomendacao.melhorPrevisao().semaforo());
        assertEquals(3, recomendacao.previsoesAnalisadas().size());
        assertTrue(recomendacao.recomendacaoCidadao().contains("UBS Jardim Paulista"));
    }

    private static class InMemoryUbsDao implements UbsDao {
        private final List<Ubs> dados = new ArrayList<>();

        @Override
        public void inserir(Ubs ubs) {
            dados.add(ubs);
        }

        @Override
        public Optional<Ubs> buscarPorId(String id) {
            for (Ubs u : dados) {
                if (u.getId().equals(id)) {
                    return Optional.of(u);
                }
            }
            return Optional.empty();
        }

        @Override
        public List<Ubs> listarTodos() {
            return new ArrayList<>(dados);
        }

        @Override
        public void atualizar(Ubs ubs) {
        }

        @Override
        public void excluir(String id) {
        }

        @Override
        public boolean existePorId(String id) {
            return buscarPorId(id).isPresent();
        }

        @Override
        public boolean existePorNome(String nome, String ignorarId) {
            return false;
        }
    }

    private static class InMemoryEspecialidadeDao implements EspecialidadeDao {
        @Override
        public void inserir(Especialidade especialidade) {
        }

        @Override
        public Optional<Especialidade> buscarPorId(String id) {
            return Optional.empty();
        }

        @Override
        public List<Especialidade> listarTodos() {
            return List.of();
        }

        @Override
        public void atualizar(Especialidade especialidade) {
        }

        @Override
        public void excluir(String id) {
        }

        @Override
        public boolean existePorId(String id) {
            return false;
        }

        @Override
        public boolean existePorNome(String nome, String ignorarId) {
            return false;
        }

        @Override
        public List<Especialidade> listarPorUbsId(String ubsId) {
            return List.of();
        }

        @Override
        public void vincularUbs(String ubsId, String especialidadeId) {
        }

        @Override
        public void desvincularTodasDeUbs(String ubsId) {
        }
    }

    private static class InMemoryRegistroFilaDao implements RegistroFilaDao {
        private final List<RegistroFila> dados = new ArrayList<>();

        @Override
        public void inserir(RegistroFila registro) {
            dados.add(registro);
        }

        @Override
        public Optional<RegistroFila> buscarPorId(String id) {
            return Optional.empty();
        }

        @Override
        public List<RegistroFila> listarTodos() {
            return new ArrayList<>(dados);
        }

        @Override
        public List<RegistroFila> listarPorUbsId(String ubsId) {
            return List.of();
        }

        @Override
        public void atualizar(RegistroFila registro) {
        }

        @Override
        public void excluir(String id) {
        }

        @Override
        public boolean existePorId(String id) {
            return false;
        }
    }

    private static class InMemoryPrevisaoDao implements PrevisaoDao {
        private final List<Previsao> dados = new ArrayList<>();

        @Override
        public void inserir(Previsao previsao) {
            dados.add(previsao);
        }

        @Override
        public Optional<Previsao> buscarPorId(String id) {
            for (Previsao p : dados) {
                if (p.getId().equals(id)) {
                    return Optional.of(p);
                }
            }
            return Optional.empty();
        }

        @Override
        public List<Previsao> listarTodos() {
            return new ArrayList<>(dados);
        }

        @Override
        public List<Previsao> listarPorUbsId(String ubsId) {
            List<Previsao> filtradas = new ArrayList<>();
            for (Previsao p : dados) {
                if (p.getUbsId().equals(ubsId)) {
                    filtradas.add(p);
                }
            }
            return filtradas;
        }

        @Override
        public void atualizar(Previsao previsao) {
        }

        @Override
        public void excluir(String id) {
        }

        @Override
        public boolean existePorId(String id) {
            return buscarPorId(id).isPresent();
        }

        @Override
        public boolean existePorUbsEDataHora(String ubsId, LocalDateTime dataHora, String ignorarId) {
            for (Previsao p : dados) {
                if (p.getUbsId().equals(ubsId) && p.getDataHora().equals(dataHora)) {
                    if (ignorarId == null || !p.getId().equals(ignorarId)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
