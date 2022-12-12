package io.gihtub.minimo.orm.dsl;

import io.gihtub.minimo.orm.OrmContext;
import io.gihtub.minimo.orm.executor.PreparedStatementSetter;

import java.util.HashMap;
import java.util.Map;

public class NativeUpdateDsl {
  protected final OrmContext context;

  protected String sql;
  protected Object[] params;
  protected PreparedStatementSetter preparedStatementSetter;
  private Map<Integer, Object> pm;

  public NativeUpdateDsl(String sql, OrmContext context) {
    this.context = context;
    this.sql = sql;
    this.pm = new HashMap<>();
  }

  public NativeUpdateDsl bind(int index, Object value) {
    pm.put(index, value);
    return this;
  }

  private Object[] params() {
    return pm.keySet().stream().map(pm::get).toArray();
  }

  public int execute() {
    preparedStatementSetter = this.context.setter(params());
    return this.context.executor().update(sql, preparedStatementSetter);
  }
}
