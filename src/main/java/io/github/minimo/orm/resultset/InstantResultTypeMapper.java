package io.github.minimo.orm.resultset;

import java.sql.JDBCType;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;

public class InstantResultTypeMapper implements ResultTypeMapper<Instant> {

  @Override
  public Set<JDBCType> types() {
    return Set.of(JDBCType.TIMESTAMP);
  }

  @Override
  public Instant map(JDBCType type, Object value) {
    if (value == null) {
      return null;
    }
    var ts = (Timestamp) value;
    return Instant.ofEpochMilli(ts.getTime());
  }
}
