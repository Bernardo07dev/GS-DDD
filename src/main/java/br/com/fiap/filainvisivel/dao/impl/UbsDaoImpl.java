package br.com.fiap.filainvisivel.dao.impl;

import br.com.fiap.filainvisivel.dao.UbsDao;
import br.com.fiap.filainvisivel.model.Ubs;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UbsDaoImpl implements UbsDao {

    private final JdbcTemplate jdbcTemplate;

    public UbsDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Ubs> rowMapper = new RowMapper<Ubs>() {
        @Override
        public Ubs mapRow(ResultSet rs, int rowNum) throws SQLException {
            Ubs ubs = new Ubs();
            ubs.setId(rs.getString("ID_UBS"));
            ubs.setNome(rs.getString("NM_UBS"));
            ubs.setBairro(rs.getString("NM_BAIRRO"));
            ubs.setCapacidade(rs.getInt("NR_CAPACIDADE"));
            return ubs;
        }
    };

    @Override
    public void inserir(Ubs ubs) {
        String sql = "INSERT INTO TB_FILA_UBS (ID_UBS, NM_UBS, NM_BAIRRO, NR_CAPACIDADE) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, ubs.getId(), ubs.getNome(), ubs.getBairro(), ubs.getCapacidade());
    }

    @Override
    public Optional<Ubs> buscarPorId(String id) {
        String sql = "SELECT ID_UBS, NM_UBS, NM_BAIRRO, NR_CAPACIDADE FROM TB_FILA_UBS WHERE ID_UBS = ?";
        List<Ubs> lista = jdbcTemplate.query(sql, rowMapper, id);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    @Override
    public List<Ubs> listarTodos() {
        String sql = "SELECT ID_UBS, NM_UBS, NM_BAIRRO, NR_CAPACIDADE FROM TB_FILA_UBS ORDER BY NM_UBS ASC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public void atualizar(Ubs ubs) {
        String sql = "UPDATE TB_FILA_UBS SET NM_UBS = ?, NM_BAIRRO = ?, NR_CAPACIDADE = ? WHERE ID_UBS = ?";
        jdbcTemplate.update(sql, ubs.getNome(), ubs.getBairro(), ubs.getCapacidade(), ubs.getId());
    }

    @Override
    public void excluir(String id) {
        String sql = "DELETE FROM TB_FILA_UBS WHERE ID_UBS = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existePorId(String id) {
        String sql = "SELECT COUNT(1) FROM TB_FILA_UBS WHERE ID_UBS = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public boolean existePorNome(String nome, String ignorarId) {
        if (ignorarId == null) {
            String sql = "SELECT COUNT(1) FROM TB_FILA_UBS WHERE LOWER(NM_UBS) = LOWER(?)";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, nome);
            return count != null && count > 0;
        }
        String sql = "SELECT COUNT(1) FROM TB_FILA_UBS WHERE LOWER(NM_UBS) = LOWER(?) AND ID_UBS <> ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, nome, ignorarId);
        return count != null && count > 0;
    }
}
