package io.github.minimo.orm.dsl;

import io.github.minimo.orm.OrmContext;
import io.github.minimo.orm.executor.PreparedStatementSetter;
import io.github.minimo.orm.resultset.RowMapper;

import java.util.Objects;

public abstract class BaseQueryDsl {
  protected final OrmContext context;

  protected String sql;
  protected Object[] params;
  protected PreparedStatementSetter preparedStatementSetter;

  protected BaseQueryDsl(String sql, Object[] params, OrmContext context, PreparedStatementSetter preparedStatementSetter) {
    Objects.requireNonNull(context);
    this.sql = sql;
    this.params = params;
    this.context = context;
    this.preparedStatementSetter = preparedStatementSetter;
  }

  public String getSql() {
    return sql;
  }

  public Object[] getParams() {
    return params;
  }

  protected void beforeExecute() {
  }

  public NativeQueryWithMapResultMapper asMap() {
    this.beforeExecute();
    Objects.requireNonNull(sql);

    return new NativeQueryWithMapResultMapper(sql, params, this.preparedStatementSetter, context);
  }

  public <T> NativeQueryWithResultMapper<T> map(RowMapper<T> rowMapper) {
    this.beforeExecute();
    Objects.requireNonNull(rowMapper);
    Objects.requireNonNull(sql);
    return new NativeQueryWithResultMapper<>(sql, params, this.preparedStatementSetter, context, rowMapper);
  }
}
