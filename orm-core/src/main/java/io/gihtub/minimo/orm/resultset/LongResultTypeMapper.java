package io.gihtub.minimo.orm.resultset;

import java.sql.JDBCType;
import java.util.Set;

public class LongResultTypeMapper implements ResultTypeMapper<Long> {

  @Override
  public Set<JDBCType> types() {
    return Set.of(JDBCType.BIGINT);
  }

  @Override
  public Long map(JDBCType type, Object value) {
    return ((Long) value);
  }
}
