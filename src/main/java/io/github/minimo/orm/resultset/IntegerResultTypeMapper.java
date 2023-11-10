package io.github.minimo.orm.resultset;

import java.sql.JDBCType;
import java.util.Set;

public class IntegerResultTypeMapper implements ResultTypeMapper<Integer> {

  @Override
  public Set<JDBCType> types() {
    return Set.of(JDBCType.INTEGER, JDBCType.SMALLINT, JDBCType.TINYINT);
  }

  @Override
  public Integer map(JDBCType type, Object value) {
    if (value == null) {
      return null;
    }
    return ((Integer) value);
  }
}
