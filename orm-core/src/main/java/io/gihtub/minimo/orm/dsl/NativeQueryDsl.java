package io.gihtub.minimo.orm.dsl;

import io.gihtub.minimo.orm.OrmContext;

import java.util.HashMap;
import java.util.Map;

public class NativeQueryDsl extends BaseQueryDsl {
  private Map<Integer, Object> pm;

  public NativeQueryDsl(String sql, OrmContext context) {
    super(sql, null, context, null);
    this.pm = new HashMap<>();
  }

  public NativeQueryDsl bind(int index, Object value) {
    pm.put(index, value);
    return this;
  }

  @Override
  protected void beforeExecute() {
    this.params = params();
  }

  private Object[] params() {
    return pm.keySet().stream().map(pm::get).toArray();
  }
}
