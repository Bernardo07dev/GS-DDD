package br.com.fiap.filainvisivel.dao.impl;

import br.com.fiap.filainvisivel.dao.AlertaDao;
import br.com.fiap.filainvisivel.model.Alerta;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class AlertaDaoImpl implements AlertaDao {

    private final JdbcTemplate jdbcTemplate;

    public AlertaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Alerta> rowMapper = new RowMapper<Alerta>() {
        @Override
        public Alerta mapRow(ResultSet rs, int rowNum) throws SQLException {
            Alerta alerta = new Alerta();
            alerta.setId(rs.getString("ID_ALERTA"));
            alerta.setUbsId(rs.getString("ID_UBS"));
            alerta.setContatoAnonimizado(rs.getString("DS_CONTATO_ANONIMIZADO"));
            alerta.setHorarioPreferido(rs.getString("DS_HORARIO_PREFERIDO"));
            return alerta;
        }
    };

    @Override
    public void inserir(Alerta alerta) {
        String sql = "INSERT INTO TB_FILA_ALERTA (ID_ALERTA, ID_UBS, DS_CONTATO_ANONIMIZADO, DS_HORARIO_PREFERIDO) "
                + "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                alerta.getId(),
                alerta.getUbsId(),
                alerta.getContatoAnonimizado(),
                alerta.getHorarioPreferido()
        );
    }

    @Override
    public Optional<Alerta> buscarPorId(String id) {
        String sql = "SELECT ID_ALERTA, ID_UBS, DS_CONTATO_ANONIMIZADO, DS_HORARIO_PREFERIDO "
                + "FROM TB_FILA_ALERTA WHERE ID_ALERTA = ?";
        List<Alerta> lista = jdbcTemplate.query(sql, rowMapper, id);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    @Override
    public List<Alerta> listarTodos() {
        String sql = "SELECT ID_ALERTA, ID_UBS, DS_CONTATO_ANONIMIZADO, DS_HORARIO_PREFERIDO "
                + "FROM TB_FILA_ALERTA ORDER BY DS_HORARIO_PREFERIDO ASC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public List<Alerta> listarPorUbsId(String ubsId) {
        String sql = "SELECT ID_ALERTA, ID_UBS, DS_CONTATO_ANONIMIZADO, DS_HORARIO_PREFERIDO "
                + "FROM TB_FILA_ALERTA WHERE ID_UBS = ? ORDER BY DS_HORARIO_PREFERIDO ASC";
        return jdbcTemplate.query(sql, rowMapper, ubsId);
    }

    @Override
    public void atualizar(Alerta alerta) {
        String sql = "UPDATE TB_FILA_ALERTA SET ID_UBS = ?, DS_CONTATO_ANONIMIZADO = ?, DS_HORARIO_PREFERIDO = ? "
                + "WHERE ID_ALERTA = ?";
        jdbcTemplate.update(sql,
                alerta.getUbsId(),
                alerta.getContatoAnonimizado(),
                alerta.getHorarioPreferido(),
                alerta.getId()
        );
    }

    @Override
    public void excluir(String id) {
        String sql = "DELETE FROM TB_FILA_ALERTA WHERE ID_ALERTA = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existePorId(String id) {
        String sql = "SELECT COUNT(1) FROM TB_FILA_ALERTA WHERE ID_ALERTA = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}
