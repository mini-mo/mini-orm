package io.gihtub.minimo.orm.dsl;

import io.gihtub.minimo.orm.OrmContext;
import io.gihtub.minimo.orm.executor.PreparedStatementSetter;

public class UpdateDsl {
  protected final OrmContext context;

  protected String sql;
  protected Object[] params;
  protected PreparedStatementSetter preparedStatementSetter;

  public UpdateDsl(String sql, Object[] params, OrmContext context) {
    this.context = context;
    this.sql = sql;
    this.params = params;
  }

  public int execute() {
    preparedStatementSetter = this.context.setter(params);
    return this.context.executor().update(sql, preparedStatementSetter);
  }
}
