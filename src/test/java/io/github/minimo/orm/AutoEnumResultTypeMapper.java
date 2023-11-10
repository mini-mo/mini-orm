package io.github.minimo.orm;

import io.github.minimo.orm.resultset.BaseResultTypeMapper;
import io.github.minimo.orm.resultset.DefaultEnumTypeMapper;

import java.sql.JDBCType;
import java.util.Set;


public class AutoEnumResultTypeMapper<E extends Enum<E>> extends BaseResultTypeMapper<E> {

  private final BaseResultTypeMapper<E> typeMapper;

  public AutoEnumResultTypeMapper(Class<E> cls) {
    if (BaseEnum.class.isAssignableFrom(cls)) {
      typeMapper = new BaseEnumTypeMapper(cls);
    } else {
      typeMapper = new DefaultEnumTypeMapper<>(cls);
    }
  }

  @Override
  public Set<JDBCType> types() {
    return typeMapper.types();
  }

  @Override
  public E map(JDBCType type, Object value) {
    return typeMapper.map(type, value);
  }
}
