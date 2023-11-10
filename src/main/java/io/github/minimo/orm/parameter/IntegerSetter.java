package io.github.minimo.orm.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class IntegerSetter implements PreparedStatementParameterSetter<Integer> {
  @Override
  public void setNull(PreparedStatement ps, int index) throws SQLException {
    ps.setNull(index, Types.INTEGER);
  }

  @Override
  public void set(PreparedStatement ps, int index, Object value) throws SQLException {
    Objects.requireNonNull(value);
    ps.setInt(index, (Integer) value);
  }
}
