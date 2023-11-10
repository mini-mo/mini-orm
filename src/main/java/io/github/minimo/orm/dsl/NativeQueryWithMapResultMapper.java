package io.github.minimo.orm.dsl;

import io.github.minimo.orm.OrmContext;
import io.github.minimo.orm.executor.PreparedStatementSetter;

import java.util.List;
import java.util.Map;

public class NativeQueryWithMapResultMapper {
  private final String sql;
  private final Object[] params;
  private final PreparedStatementSetter setter;
  private final OrmContext context;

  public NativeQueryWithMapResultMapper(String sql, Object[] params, PreparedStatementSetter setter, OrmContext context) {
    this.sql = sql;
    this.params = params;
    this.setter = setter;
    this.context = context;
  }

  public Map<String, Object> one() {
    var v = list(1);
    return v == null || v.isEmpty() ? null : v.get(0);
  }

  public List<Map<String, Object>> list() {
    return list(0, 8); // magic
  }

  public List<Map<String, Object>> list(int limit) {
    return list(0, limit);
  }

  public List<Map<String, Object>> list(long offset, int limit) {
    var s = context.generator().rewriteLimit(sql, offset, limit);
    if (setter == null) {
      return context.executor().queryForMap(s, params);
    }
    return context.executor().queryForMap(s, setter);
  }
}
