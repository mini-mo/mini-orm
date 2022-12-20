package io.gihtub.minimo.orm.dsl;

import io.gihtub.minimo.orm.OrmContext;

import java.util.HashMap;
import java.util.Map;

public class NativeInsertDsl {
  protected final OrmContext context;

  protected String sql;
  private Map<Integer, Object> pm;

  public NativeInsertDsl(String sql, OrmContext context) {
    this.context = context;
    this.sql = sql;
    this.pm = new HashMap<>();
  }

  public NativeInsertDsl bind(int index, Object value) {
    pm.put(index, value);
    return this;
  }

  private Object[] params() {
    return pm.keySet().stream().map(pm::get).toArray();
  }

  public int execute() {
    return this.context.executor().update(sql, params());
  }

  public long executeAndReturnGeneratedKey() {
    return this.context.executor().insert(sql, params());
  }

}
