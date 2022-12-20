package io.gihtub.minimo.orm.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.Objects;

public class DateSetter implements PreparedStatementParameterSetter<Date> {
  @Override
  public void setNull(PreparedStatement ps, int index) throws SQLException {
    ps.setNull(index, Types.TIMESTAMP);
  }

  @Override
  public void set(PreparedStatement ps, int index, Object value) throws SQLException {
    Objects.requireNonNull(value);
    ps.setTimestamp(index, new Timestamp(((Date) value).getTime()));
  }
}
