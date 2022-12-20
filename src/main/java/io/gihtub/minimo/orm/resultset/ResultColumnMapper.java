package io.gihtub.minimo.orm.resultset;

import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultColumnMapper<T> {

  T mapColumn(ResultSet rs, int index, String column, JDBCType type) throws SQLException;

  class Noop implements ResultColumnMapper<Object> {
    @Override
    public Object mapColumn(ResultSet rs, int index, String column, JDBCType type) throws SQLException {
      return rs.getObject(index);
    }
  }

}
