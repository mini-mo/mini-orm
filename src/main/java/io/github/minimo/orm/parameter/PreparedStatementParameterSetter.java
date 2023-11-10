package io.github.minimo.orm.parameter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * prepared statement 参数设置器
 */
public interface PreparedStatementParameterSetter<T> {

  void setNull(PreparedStatement ps, int index) throws SQLException;

  void set(PreparedStatement ps, int index, Object value) throws SQLException;

//  /**
//   * @param v instance
//   * @return instance with type T
//   * @throws ClassCastException
//   */
//  default T convert(Object v) throws ClassCastException {
//    if (v == null) {
//      return (T) null;
//    }
//    return ((T) v);
//  }

  /**
   * 泛型参数 T
   *
   * @return class from T
   */
  default Class<T> genericType() {
    Class<? extends PreparedStatementParameterSetter> cls = this.getClass();
    for (Type genericInterface : cls.getGenericInterfaces()) {
      if (genericInterface instanceof ParameterizedType pt) {
        if (pt.getRawType()
            .getTypeName()
            .equalsIgnoreCase(PreparedStatementParameterSetter.class.getCanonicalName())) {
          var actualTypeArguments = pt.getActualTypeArguments();
          return ((Class<T>) actualTypeArguments[0]);
        }
      }
    }
    throw new IllegalStateException();
  }
}
