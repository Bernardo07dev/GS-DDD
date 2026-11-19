package br.com.fiap.filainvisivel.dao.impl;

import br.com.fiap.filainvisivel.dao.RegistroFilaDao;
import br.com.fiap.filainvisivel.model.RegistroFila;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class RegistroFilaDaoImpl implements RegistroFilaDao {

    private final JdbcTemplate jdbcTemplate;

    public RegistroFilaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<RegistroFila> rowMapper = new RowMapper<RegistroFila>() {
        @Override
        public RegistroFila mapRow(ResultSet rs, int rowNum) throws SQLException {
            RegistroFila registro = new RegistroFila();
            registro.setId(rs.getString("ID_REGISTRO"));
            registro.setUbsId(rs.getString("ID_UBS"));
            Timestamp ts = rs.getTimestamp("DT_HORA");
            if (ts != null) {
                registro.setDataHora(ts.toLocalDateTime());
            }
            registro.setPessoasNaFila(rs.getInt("NR_PESSOAS_FILA"));
            registro.setTempoEsperaMin(rs.getInt("NR_TEMPO_ESPERA_MIN"));
            return registro;
        }
    };

    @Override
    public void inserir(RegistroFila registro) {
        String sql = "INSERT INTO TB_FILA_REGISTRO (ID_REGISTRO, ID_UBS, DT_HORA, NR_PESSOAS_FILA, NR_TEMPO_ESPERA_MIN) "
                + "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                registro.getId(),
                registro.getUbsId(),
                Timestamp.valueOf(registro.getDataHora()),
                registro.getPessoasNaFila(),
                registro.getTempoEsperaMin()
        );
    }

    @Override
    public Optional<RegistroFila> buscarPorId(String id) {
        String sql = "SELECT ID_REGISTRO, ID_UBS, DT_HORA, NR_PESSOAS_FILA, NR_TEMPO_ESPERA_MIN "
                + "FROM TB_FILA_REGISTRO WHERE ID_REGISTRO = ?";
        List<RegistroFila> lista = jdbcTemplate.query(sql, rowMapper, id);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    @Override
    public List<RegistroFila> listarTodos() {
        String sql = "SELECT ID_REGISTRO, ID_UBS, DT_HORA, NR_PESSOAS_FILA, NR_TEMPO_ESPERA_MIN "
                + "FROM TB_FILA_REGISTRO ORDER BY DT_HORA DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public List<RegistroFila> listarPorUbsId(String ubsId) {
        String sql = "SELECT ID_REGISTRO, ID_UBS, DT_HORA, NR_PESSOAS_FILA, NR_TEMPO_ESPERA_MIN "
                + "FROM TB_FILA_REGISTRO WHERE ID_UBS = ? ORDER BY DT_HORA DESC";
        return jdbcTemplate.query(sql, rowMapper, ubsId);
    }

    @Override
    public void atualizar(RegistroFila registro) {
        String sql = "UPDATE TB_FILA_REGISTRO SET ID_UBS = ?, DT_HORA = ?, NR_PESSOAS_FILA = ?, NR_TEMPO_ESPERA_MIN = ? "
                + "WHERE ID_REGISTRO = ?";
        jdbcTemplate.update(sql,
                registro.getUbsId(),
                Timestamp.valueOf(registro.getDataHora()),
                registro.getPessoasNaFila(),
                registro.getTempoEsperaMin(),
                registro.getId()
        );
    }

    @Override
    public void excluir(String id) {
        String sql = "DELETE FROM TB_FILA_REGISTRO WHERE ID_REGISTRO = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existePorId(String id) {
        String sql = "SELECT COUNT(1) FROM TB_FILA_REGISTRO WHERE ID_REGISTRO = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}
