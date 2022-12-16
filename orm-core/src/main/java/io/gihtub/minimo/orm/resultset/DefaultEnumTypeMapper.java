package io.gihtub.minimo.orm.resultset;

import java.sql.JDBCType;
import java.util.Set;

public class DefaultEnumTypeMapper<E extends Enum<E>> extends BaseResultTypeMapper<E> {

  private final Class<E> cls;

  public DefaultEnumTypeMapper(Class<E> cls) {
    this.cls = cls;
  }

  @Override
  public Set<JDBCType> types() {
    return Set.of(JDBCType.VARCHAR);
  }

  @Override
  public E map(JDBCType type, Object value) {
    return Enum.valueOf(cls, value.toString());
  }

}
