package io.gihtub.minimo.orm.executor;

import io.gihtub.minimo.orm.resultset.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcTemplateExecutor implements Executor {
  private final JdbcTemplate jdbcTemplate;

  public JdbcTemplateExecutor(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<Map<String, Object>> queryForMap(String sql, Object[] params) {
    return this.jdbcTemplate.queryForList(sql, params);
  }

  @Override
  public List<Map<String, Object>> queryForMap(String sql, PreparedStatementSetter setter) {
    return this.jdbcTemplate.query(sql, setter::setValues, (rs, i) -> {
      Map<String, Object> v = new HashMap<>();
      var md = rs.getMetaData();
      for (int j = 1; j < md.getColumnCount() + 1; j++) {
        v.put(md.getColumnName(j), rs.getObject(j));
      }
      return v;
    });
  }

  @Override
  public <T> List<T> query(String sql, Object[] params, RowMapper<T> mapper) {
    return this.jdbcTemplate.query(sql, mapper::mapRow, params);
  }

  @Override
  public <T> List<T> query(String sql, PreparedStatementSetter setter, RowMapper<T> mapper) {
    return this.jdbcTemplate.query(sql, setter::setValues, mapper::mapRow);
  }
}
