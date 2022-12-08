package io.gihtub.minimo.orm.resultset;

import java.sql.JDBCType;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

public class DateResultTypeMapper implements ResultTypeMapper<Date> {

  @Override
  public Set<JDBCType> types() {
    return Set.of(JDBCType.TIMESTAMP);
  }

  @Override
  public Date map(JDBCType type, Object value) {
    var ts = (Timestamp) value;
    return new Date(ts.getTime());
  }
}
