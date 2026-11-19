package br.com.fiap.filainvisivel.config;

import br.com.fiap.filainvisivel.dao.AlertaDao;
import br.com.fiap.filainvisivel.dao.EspecialidadeDao;
import br.com.fiap.filainvisivel.dao.PrevisaoDao;
import br.com.fiap.filainvisivel.dao.RegistroFilaDao;
import br.com.fiap.filainvisivel.dao.UbsDao;
import br.com.fiap.filainvisivel.factory.AlertaFactory;
import br.com.fiap.filainvisivel.factory.PrevisaoFactory;
import br.com.fiap.filainvisivel.model.Alerta;
import br.com.fiap.filainvisivel.model.Especialidade;
import br.com.fiap.filainvisivel.model.Previsao;
import br.com.fiap.filainvisivel.model.RegistroFila;
import br.com.fiap.filainvisivel.model.Ubs;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DatabaseInitializer {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final JdbcTemplate jdbcTemplate;
    private final EspecialidadeDao especialidadeDao;
    private final UbsDao ubsDao;
    private final RegistroFilaDao registroFilaDao;
    private final PrevisaoDao previsaoDao;
    private final AlertaDao alertaDao;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate,
                               EspecialidadeDao especialidadeDao,
                               UbsDao ubsDao,
                               RegistroFilaDao registroFilaDao,
                               PrevisaoDao previsaoDao,
                               AlertaDao alertaDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.especialidadeDao = especialidadeDao;
        this.ubsDao = ubsDao;
        this.registroFilaDao = registroFilaDao;
        this.previsaoDao = previsaoDao;
        this.alertaDao = alertaDao;
    }

    @PostConstruct
    public void inicializar() {
        try {
            criarTabelas();
            popularDadosIniciais();
        } catch (Exception ex) {
            log.warn("Inicialização automática do banco Oracle ignorada: {}", ex.getMessage());
        }
    }

    private void criarTabelas() {
        String[] ddls = {
                "CREATE TABLE TB_FILA_UBS ("
                        + "ID_UBS VARCHAR2(36) PRIMARY KEY, "
                        + "NM_UBS VARCHAR2(120) NOT NULL, "
                        + "NM_BAIRRO VARCHAR2(100) NOT NULL, "
                        + "NR_CAPACIDADE NUMBER(5) NOT NULL)",
                "CREATE TABLE TB_FILA_ESPECIALIDADE ("
                        + "ID_ESPECIALIDADE VARCHAR2(36) PRIMARY KEY, "
                        + "NM_ESPECIALIDADE VARCHAR2(100) NOT NULL UNIQUE)",
                "CREATE TABLE TB_FILA_UBS_ESPECIALIDADE ("
                        + "ID_UBS VARCHAR2(36) NOT NULL, "
                        + "ID_ESPECIALIDADE VARCHAR2(36) NOT NULL, "
                        + "CONSTRAINT PK_UBS_ESPECIALIDADE PRIMARY KEY (ID_UBS, ID_ESPECIALIDADE), "
                        + "CONSTRAINT FK_UE_UBS FOREIGN KEY (ID_UBS) REFERENCES TB_FILA_UBS(ID_UBS) ON DELETE CASCADE, "
                        + "CONSTRAINT FK_UE_ESP FOREIGN KEY (ID_ESPECIALIDADE) REFERENCES TB_FILA_ESPECIALIDADE(ID_ESPECIALIDADE) ON DELETE CASCADE)",
                "CREATE TABLE TB_FILA_REGISTRO ("
                        + "ID_REGISTRO VARCHAR2(36) PRIMARY KEY, "
                        + "ID_UBS VARCHAR2(36) NOT NULL, "
                        + "DT_HORA TIMESTAMP NOT NULL, "
                        + "NR_PESSOAS_FILA NUMBER(5) NOT NULL, "
                        + "NR_TEMPO_ESPERA_MIN NUMBER(5) NOT NULL, "
                        + "CONSTRAINT FK_REGISTRO_UBS FOREIGN KEY (ID_UBS) REFERENCES TB_FILA_UBS(ID_UBS) ON DELETE CASCADE)",
                "CREATE TABLE TB_FILA_PREVISAO ("
                        + "ID_PREVISAO VARCHAR2(36) PRIMARY KEY, "
                        + "ID_UBS VARCHAR2(36) NOT NULL, "
                        + "DT_HORA TIMESTAMP NOT NULL, "
                        + "VL_PROB_LOTACAO NUMBER(5,4) NOT NULL, "
                        + "NR_ESPERA_ESTIMADA_MIN NUMBER(5) NOT NULL, "
                        + "DS_SEMAFORO VARCHAR2(20) NOT NULL, "
                        + "CONSTRAINT FK_PREVISAO_UBS FOREIGN KEY (ID_UBS) REFERENCES TB_FILA_UBS(ID_UBS) ON DELETE CASCADE, "
                        + "CONSTRAINT UQ_PREVISAO_UBS_HORA UNIQUE (ID_UBS, DT_HORA))",
                "CREATE TABLE TB_FILA_ALERTA ("
                        + "ID_ALERTA VARCHAR2(36) PRIMARY KEY, "
                        + "ID_UBS VARCHAR2(36) NOT NULL, "
                        + "DS_CONTATO_ANONIMIZADO VARCHAR2(120) NOT NULL, "
                        + "DS_HORARIO_PREFERIDO VARCHAR2(40) NOT NULL, "
                        + "CONSTRAINT FK_ALERTA_UBS FOREIGN KEY (ID_UBS) REFERENCES TB_FILA_UBS(ID_UBS) ON DELETE CASCADE)"
        };

        for (String ddl : ddls) {
            executarDdlIdempotente(ddl);
        }
    }

    private void executarDdlIdempotente(String ddl) {
        try {
            jdbcTemplate.execute(ddl);
        } catch (DataAccessException ex) {
            if (!isTabelaJaExistente(ex)) {
                throw ex;
            }
        }
    }

    private boolean isTabelaJaExistente(DataAccessException ex) {
        Throwable atual = ex;
        while (atual != null) {
            if (atual instanceof SQLException sqlEx && sqlEx.getErrorCode() == 955) {
                return true;
            }
            if (atual.getMessage() != null && atual.getMessage().contains("ORA-00955")) {
                return true;
            }
            atual = atual.getCause();
        }
        return false;
    }

    private void popularDadosIniciais() {
        Integer totalUbs = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM TB_FILA_UBS", Integer.class);
        if (totalUbs != null && totalUbs > 0) {
            return;
        }

        String espClinicaId = obterOuCriarEspecialidade("Clínica Geral");
        String espGeriatriaId = obterOuCriarEspecialidade("Geriatria");
        String espPediatriaId = obterOuCriarEspecialidade("Pediatria");

        Ubs ubsPaulista = new Ubs(UUID.randomUUID().toString(), "UBS Jardim Paulista", "Jardim Paulista", 80);
        Ubs ubsSantaCecilia = new Ubs(UUID.randomUUID().toString(), "UBS Santa Cecília", "Santa Cecília", 65);
        ubsDao.inserir(ubsPaulista);
        ubsDao.inserir(ubsSantaCecilia);

        especialidadeDao.vincularUbs(ubsPaulista.getId(), espClinicaId);
        especialidadeDao.vincularUbs(ubsPaulista.getId(), espGeriatriaId);
        especialidadeDao.vincularUbs(ubsSantaCecilia.getId(), espClinicaId);
        especialidadeDao.vincularUbs(ubsSantaCecilia.getId(), espPediatriaId);

        LocalDate hoje = LocalDate.now();
        registroFilaDao.inserir(new RegistroFila(
                UUID.randomUUID().toString(),
                ubsPaulista.getId(),
                LocalDateTime.of(hoje.getYear(), hoje.getMonth(), hoje.getDayOfMonth(), 8, 15),
                42,
                58
        ));
        registroFilaDao.inserir(new RegistroFila(
                UUID.randomUUID().toString(),
                ubsPaulista.getId(),
                LocalDateTime.of(hoje.getYear(), hoje.getMonth(), hoje.getDayOfMonth(), 14, 20),
                9,
                12
        ));

        LocalDate amanha = hoje.plusDays(1);
        Previsao pManha = PrevisaoFactory.criar(
                UUID.randomUUID().toString(),
                ubsPaulista.getId(),
                amanha.atTime(8, 0),
                0.85,
                55
        );
        Previsao pAlmoco = PrevisaoFactory.criar(
                UUID.randomUUID().toString(),
                ubsPaulista.getId(),
                amanha.atTime(11, 0),
                0.55,
                28
        );
        Previsao pTarde = PrevisaoFactory.criar(
                UUID.randomUUID().toString(),
                ubsPaulista.getId(),
                amanha.atTime(14, 30),
                0.22,
                10
        );
        Previsao pSantaCecilia = PrevisaoFactory.criar(
                UUID.randomUUID().toString(),
                ubsSantaCecilia.getId(),
                amanha.atTime(15, 0),
                0.30,
                12
        );
        previsaoDao.inserir(pManha);
        previsaoDao.inserir(pAlmoco);
        previsaoDao.inserir(pTarde);
        previsaoDao.inserir(pSantaCecilia);

        Alerta alertaDonaMarlene = AlertaFactory.criar(
                UUID.randomUUID().toString(),
                ubsPaulista.getId(),
                "anon-7f8a9b1c-***42",
                "14:00 - 16:00"
        );
        alertaDao.inserir(alertaDonaMarlene);

        log.info("Banco Oracle inicializado com dados de demonstração do Fila Invisível.");
    }

    private String obterOuCriarEspecialidade(String nome) {
        for (Especialidade existente : especialidadeDao.listarTodos()) {
            if (existente.getNome().equalsIgnoreCase(nome)) {
                return existente.getId();
            }
        }
        Especialidade nova = new Especialidade(UUID.randomUUID().toString(), nome);
        especialidadeDao.inserir(nova);
        return nova.getId();
    }
}
