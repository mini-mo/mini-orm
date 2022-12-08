package io.gihtub.minimo.orm;

import io.gihtub.minimo.orm.resultset.ResultTypeMapper;

import java.sql.JDBCType;
import java.util.Set;


public class UserNameTypeMapper implements ResultTypeMapper<UserName> {
  @Override
  public Set<JDBCType> types() {
    return Set.of(JDBCType.VARCHAR);
  }

  @Override
  public UserName map(JDBCType type, Object value) {
    return new UserName(((String) value));
  }
}
