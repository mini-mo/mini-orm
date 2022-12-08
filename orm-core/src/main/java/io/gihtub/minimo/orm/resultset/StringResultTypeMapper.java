package io.gihtub.minimo.orm.resultset;

import java.sql.JDBCType;
import java.util.Set;

public class StringResultTypeMapper implements ResultTypeMapper<String> {

  @Override
  public Set<JDBCType> types() {
    return Set.of(JDBCType.VARCHAR, JDBCType.LONGVARCHAR);
  }

  @Override
  public String map(JDBCType type, Object value) {
    return ((String) value);
  }
}
