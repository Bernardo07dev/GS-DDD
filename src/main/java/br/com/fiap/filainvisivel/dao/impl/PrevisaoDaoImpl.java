package br.com.fiap.filainvisivel.dao.impl;

import br.com.fiap.filainvisivel.dao.PrevisaoDao;
import br.com.fiap.filainvisivel.factory.PrevisaoFactory;
import br.com.fiap.filainvisivel.model.Previsao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class PrevisaoDaoImpl implements PrevisaoDao {

    private final JdbcTemplate jdbcTemplate;

    public PrevisaoDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Previsao> rowMapper = new RowMapper<Previsao>() {
        @Override
        public Previsao mapRow(ResultSet rs, int rowNum) throws SQLException {
            Timestamp ts = rs.getTimestamp("DT_HORA");
            LocalDateTime dataHora = ts != null ? ts.toLocalDateTime() : null;
            return PrevisaoFactory.criar(
                    rs.getString("ID_PREVISAO"),
                    rs.getString("ID_UBS"),
                    dataHora,
                    rs.getDouble("VL_PROB_LOTACAO"),
                    rs.getInt("NR_ESPERA_ESTIMADA_MIN")
            );
        }
    };

    @Override
    public void inserir(Previsao previsao) {
        String sql = "INSERT INTO TB_FILA_PREVISAO (ID_PREVISAO, ID_UBS, DT_HORA, VL_PROB_LOTACAO, NR_ESPERA_ESTIMADA_MIN, DS_SEMAFORO) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                previsao.getId(),
                previsao.getUbsId(),
                Timestamp.valueOf(previsao.getDataHora()),
                previsao.getProbLotacao(),
                previsao.getEsperaEstimadaMin(),
                previsao.getSemaforo().name()
        );
    }

    @Override
    public Optional<Previsao> buscarPorId(String id) {
        String sql = "SELECT ID_PREVISAO, ID_UBS, DT_HORA, VL_PROB_LOTACAO, NR_ESPERA_ESTIMADA_MIN, DS_SEMAFORO "
                + "FROM TB_FILA_PREVISAO WHERE ID_PREVISAO = ?";
        List<Previsao> lista = jdbcTemplate.query(sql, rowMapper, id);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    @Override
    public List<Previsao> listarTodos() {
        String sql = "SELECT ID_PREVISAO, ID_UBS, DT_HORA, VL_PROB_LOTACAO, NR_ESPERA_ESTIMADA_MIN, DS_SEMAFORO "
                + "FROM TB_FILA_PREVISAO ORDER BY DT_HORA ASC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public List<Previsao> listarPorUbsId(String ubsId) {
        String sql = "SELECT ID_PREVISAO, ID_UBS, DT_HORA, VL_PROB_LOTACAO, NR_ESPERA_ESTIMADA_MIN, DS_SEMAFORO "
                + "FROM TB_FILA_PREVISAO WHERE ID_UBS = ? ORDER BY DT_HORA ASC";
        return jdbcTemplate.query(sql, rowMapper, ubsId);
    }

    @Override
    public void atualizar(Previsao previsao) {
        String sql = "UPDATE TB_FILA_PREVISAO SET ID_UBS = ?, DT_HORA = ?, VL_PROB_LOTACAO = ?, "
                + "NR_ESPERA_ESTIMADA_MIN = ?, DS_SEMAFORO = ? WHERE ID_PREVISAO = ?";
        jdbcTemplate.update(sql,
                previsao.getUbsId(),
                Timestamp.valueOf(previsao.getDataHora()),
                previsao.getProbLotacao(),
                previsao.getEsperaEstimadaMin(),
                previsao.getSemaforo().name(),
                previsao.getId()
        );
    }

    @Override
    public void excluir(String id) {
        String sql = "DELETE FROM TB_FILA_PREVISAO WHERE ID_PREVISAO = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existePorId(String id) {
        String sql = "SELECT COUNT(1) FROM TB_FILA_PREVISAO WHERE ID_PREVISAO = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public boolean existePorUbsEDataHora(String ubsId, LocalDateTime dataHora, String ignorarId) {
        if (ignorarId == null) {
            String sql = "SELECT COUNT(1) FROM TB_FILA_PREVISAO WHERE ID_UBS = ? AND DT_HORA = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, ubsId, Timestamp.valueOf(dataHora));
            return count != null && count > 0;
        }
        String sql = "SELECT COUNT(1) FROM TB_FILA_PREVISAO WHERE ID_UBS = ? AND DT_HORA = ? AND ID_PREVISAO <> ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, ubsId, Timestamp.valueOf(dataHora), ignorarId);
        return count != null && count > 0;
    }
}
