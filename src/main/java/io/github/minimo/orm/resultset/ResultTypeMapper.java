package io.github.minimo.orm.resultset;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.JDBCType;
import java.util.Set;

/**
 * resultSet to biz object
 */
public interface ResultTypeMapper<T> {

  /**
   * 支持的 jdbc types
   */
  Set<JDBCType> types();

  T map(JDBCType type, Object value);


  /**
   * 泛型参数 JavaTypeT
   *
   * @return class from JavaTypeT
   */
  default Class<T> genericType() {
    Class<? extends ResultTypeMapper> cls = this.getClass();
    for (Type genericInterface : cls.getGenericInterfaces()) {
      if (genericInterface instanceof ParameterizedType pt) {
        if (pt.getRawType()
            .getTypeName()
            .equalsIgnoreCase(ResultTypeMapper.class.getCanonicalName())) {
          var actualTypeArguments = pt.getActualTypeArguments();
          return ((Class<T>) actualTypeArguments[0]);
        }
      }
    }
    throw new IllegalStateException();
  }
}
