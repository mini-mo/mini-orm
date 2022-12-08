package io.gihtub.minimo.orm.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class LongSetter implements PreparedStatementParameterSetter<Long> {
  @Override
  public void setNull(PreparedStatement ps, int index) throws SQLException {
    ps.setNull(index, Types.BIGINT);
  }

  @Override
  public void set(PreparedStatement ps, int index, Object value) throws SQLException {
    Objects.requireNonNull(value);
    ps.setLong(index, (Long) value);
  }
}
