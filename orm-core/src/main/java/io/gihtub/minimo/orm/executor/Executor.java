package io.gihtub.minimo.orm.executor;

import io.gihtub.minimo.orm.resultset.RowMapper;

import java.util.List;
import java.util.Map;

public interface Executor {

  List<Map<String, Object>> queryForMap(String sql, Object[] params);

  List<Map<String, Object>> queryForMap(String sql, PreparedStatementSetter setter);

  <T> List<T> query(String sql, Object[] params, RowMapper<T> mapper);

  <T> List<T> query(String sql, PreparedStatementSetter setter, RowMapper<T> mapper);

  int update(String sql, PreparedStatementSetter setter);

  int update(String sql, Object[] params);

  long insert(String sql, PreparedStatementSetter preparedStatementSetter);

  long insert(String sql, Object[] params);
}
