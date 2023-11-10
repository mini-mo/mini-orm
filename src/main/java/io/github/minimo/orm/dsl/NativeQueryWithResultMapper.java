package io.github.minimo.orm.dsl;

import io.github.minimo.orm.OrmContext;
import io.github.minimo.orm.executor.PreparedStatementSetter;
import io.github.minimo.orm.resultset.RowMapper;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NativeQueryWithResultMapper<T> {
  private static final Logger log = LoggerFactory.getLogger(NativeQueryWithResultMapper.class);

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
    log.debug("sql : {} ;\n params: {}", newSql, this.params);
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
    log.debug("sql : {} ;\n params: {}", newSql, this.params);
    if (setter == null) {
      return this.context.executor().query(newSql, params, mapper);
    }
    return this.context.executor().query(newSql, setter, mapper);
  }

  public long count() {
    // rewrite sql
    var newSql = this.context.generator().rewriteProjectForCount(this.sql);
    log.debug("sql : {} ;\n params: {}", newSql, this.params);
    if (setter == null) {
      return this.context.executor().query(newSql, params, (rs, i) -> rs.getLong(1)).get(0);
    }

    return this.context.executor().query(newSql, setter, (rs, i) -> rs.getLong(1)).get(0);
  }
}
