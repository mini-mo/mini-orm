package io.github.minimo.orm.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class StringSetter implements PreparedStatementParameterSetter<String> {
  @Override
  public void setNull(PreparedStatement ps, int index) throws SQLException {
    ps.setNull(index, Types.VARBINARY);
  }

  @Override
  public void set(PreparedStatement ps, int index, Object value) throws SQLException {
    Objects.requireNonNull(value);
    ps.setString(index, (String) value);
  }
}
