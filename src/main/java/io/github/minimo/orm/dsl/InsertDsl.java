package io.github.minimo.orm.dsl;

import io.github.minimo.orm.OrmContext;

public class InsertDsl {

  protected final OrmContext context;

  protected String sql;
  private Object[] params;

  public InsertDsl(String sql, Object[] params, OrmContext context) {
    this.context = context;
    this.sql = sql;
    this.params = params;
  }

  public int execute() {
    return this.context.executor().update(sql, this.context.setter(params));
  }

  public long executeAndReturnGeneratedKey() {
    return this.context.executor().insert(sql, this.context.setter(params));
  }

}
