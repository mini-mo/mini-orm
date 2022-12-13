package io.gihtub.minimo.orm.dsl;

import io.gihtub.minimo.orm.OrmContext;
import io.gihtub.minimo.orm.executor.PreparedStatementSetter;
import io.gihtub.minimo.orm.resultset.RowMapper;

import java.util.List;

public class NativeQueryWithResultMapper<T> {

  private final String sql;
  private final Object[] params;

  private final PreparedStatementSetter setter;
  private final OrmContext context;
  private final RowMapper<T> mapper;

  public NativeQueryWithResultMapper(String sql, Object[] params, PreparedStatementSetter setter, OrmContext context, RowMapper<T> mapper) {
    this.sql = sql;
    this.params = params;
    this.setter = setter;
    this.context = context;
    this.mapper = mapper;
  }

  public boolean exists() {
    var newSql = this.context.generator().rewriteLimit(this.sql, 0, 1);
    if (setter == null) {
      return !this.context.executor().query(newSql, params, mapper).isEmpty();
    }

    return !this.context.executor().query(newSql, setter, mapper).isEmpty();
  }

  public T one() {
    var v = list(1);
    return v == null || v.isEmpty() ? null : v.get(0);
  }

  public List<T> list() {
    return list(0, context.defaultLimit());
  }

  public List<T> list(int limit) {
    return list(0, limit);
  }

  public List<T> list(long offset, int limit) {
    var newSql = this.context.generator().rewriteLimit(this.sql, offset, limit);
    if (setter == null) {
      return this.context.executor().query(newSql, params, mapper);
    }
    return this.context.executor().query(newSql, setter, mapper);
  }

  public long count() {
    // rewrite sql
    var newSql = this.context.generator().rewriteProjectForCount(this.sql);
    if (setter == null) {
      return this.context.executor().query(newSql, params, (rs, i) -> rs.getLong(1)).get(0);
    }

    return this.context.executor().query(newSql, setter, (rs, i) -> rs.getLong(1)).get(0);
  }
}
