package io.gihtub.minimo.orm.setter;

import io.gihtub.minimo.orm.parameter.PreparedStatementParameterSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class DefaultEnumSetter implements PreparedStatementParameterSetter<Enum> {
  @Override
  public void setNull(PreparedStatement ps, int index) throws SQLException {
    ps.setNull(index, Types.VARCHAR);
  }

  @Override
  public void set(PreparedStatement ps, int index, Object value) throws SQLException {
    ps.setString(index, ((Enum) value).name());
  }
}
