package io.github.minimo.orm;

import io.github.minimo.orm.resultset.BaseResultTypeMapper;

import java.sql.JDBCType;
import java.util.Set;

public class BaseEnumTypeMapper<E extends BaseEnum> extends BaseResultTypeMapper<E> {

  private final Class<E> cls;

  public BaseEnumTypeMapper(Class<E> cls) {
    this.cls = cls;
  }

  @Override
  public Set<JDBCType> types() {
    return Set.of(JDBCType.VARCHAR);
  }

  @Override
  public E map(JDBCType type, Object value) {
    return BaseEnum.of(((Integer) value), cls);
  }

}
