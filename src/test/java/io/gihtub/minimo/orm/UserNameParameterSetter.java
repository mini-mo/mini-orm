package io.gihtub.minimo.orm;


import io.gihtub.minimo.orm.parameter.PreparedStatementParameterSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class UserNameParameterSetter implements PreparedStatementParameterSetter<UserName> {
  @Override
  public void setNull(PreparedStatement ps, int index) throws SQLException {
    ps.setNull(index, Types.VARBINARY);
  }

  @Override
  public void set(PreparedStatement ps, int index, Object value) throws SQLException {
    ps.setString(index, ((UserName) value).getValue());
  }
}
