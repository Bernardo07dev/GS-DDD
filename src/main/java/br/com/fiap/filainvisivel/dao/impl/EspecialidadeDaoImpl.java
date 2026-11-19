package br.com.fiap.filainvisivel.dao.impl;

import br.com.fiap.filainvisivel.dao.EspecialidadeDao;
import br.com.fiap.filainvisivel.model.Especialidade;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class EspecialidadeDaoImpl implements EspecialidadeDao {

    private final JdbcTemplate jdbcTemplate;

    public EspecialidadeDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Especialidade> rowMapper = new RowMapper<Especialidade>() {
        @Override
        public Especialidade mapRow(ResultSet rs, int rowNum) throws SQLException {
            Especialidade especialidade = new Especialidade();
            especialidade.setId(rs.getString("ID_ESPECIALIDADE"));
            especialidade.setNome(rs.getString("NM_ESPECIALIDADE"));
            return especialidade;
        }
    };

    @Override
    public void inserir(Especialidade especialidade) {
        String sql = "INSERT INTO TB_FILA_ESPECIALIDADE (ID_ESPECIALIDADE, NM_ESPECIALIDADE) VALUES (?, ?)";
        jdbcTemplate.update(sql, especialidade.getId(), especialidade.getNome());
    }

    @Override
    public Optional<Especialidade> buscarPorId(String id) {
        String sql = "SELECT ID_ESPECIALIDADE, NM_ESPECIALIDADE FROM TB_FILA_ESPECIALIDADE WHERE ID_ESPECIALIDADE = ?";
        List<Especialidade> lista = jdbcTemplate.query(sql, rowMapper, id);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    @Override
    public List<Especialidade> listarTodos() {
        String sql = "SELECT ID_ESPECIALIDADE, NM_ESPECIALIDADE FROM TB_FILA_ESPECIALIDADE ORDER BY NM_ESPECIALIDADE ASC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public void atualizar(Especialidade especialidade) {
        String sql = "UPDATE TB_FILA_ESPECIALIDADE SET NM_ESPECIALIDADE = ? WHERE ID_ESPECIALIDADE = ?";
        jdbcTemplate.update(sql, especialidade.getNome(), especialidade.getId());
    }

    @Override
    public void excluir(String id) {
        String sql = "DELETE FROM TB_FILA_ESPECIALIDADE WHERE ID_ESPECIALIDADE = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existePorId(String id) {
        String sql = "SELECT COUNT(1) FROM TB_FILA_ESPECIALIDADE WHERE ID_ESPECIALIDADE = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public boolean existePorNome(String nome, String ignorarId) {
        if (ignorarId == null) {
            String sql = "SELECT COUNT(1) FROM TB_FILA_ESPECIALIDADE WHERE LOWER(NM_ESPECIALIDADE) = LOWER(?)";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, nome);
            return count != null && count > 0;
        }
        String sql = "SELECT COUNT(1) FROM TB_FILA_ESPECIALIDADE WHERE LOWER(NM_ESPECIALIDADE) = LOWER(?) AND ID_ESPECIALIDADE <> ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, nome, ignorarId);
        return count != null && count > 0;
    }

    @Override
    public List<Especialidade> listarPorUbsId(String ubsId) {
        String sql = "SELECT e.ID_ESPECIALIDADE, e.NM_ESPECIALIDADE "
                + "FROM TB_FILA_ESPECIALIDADE e "
                + "INNER JOIN TB_FILA_UBS_ESPECIALIDADE ue ON ue.ID_ESPECIALIDADE = e.ID_ESPECIALIDADE "
                + "WHERE ue.ID_UBS = ? ORDER BY e.NM_ESPECIALIDADE ASC";
        return jdbcTemplate.query(sql, rowMapper, ubsId);
    }

    @Override
    public void vincularUbs(String ubsId, String especialidadeId) {
        String sql = "INSERT INTO TB_FILA_UBS_ESPECIALIDADE (ID_UBS, ID_ESPECIALIDADE) VALUES (?, ?)";
        jdbcTemplate.update(sql, ubsId, especialidadeId);
    }

    @Override
    public void desvincularTodasDeUbs(String ubsId) {
        String sql = "DELETE FROM TB_FILA_UBS_ESPECIALIDADE WHERE ID_UBS = ?";
        jdbcTemplate.update(sql, ubsId);
    }
}
