package io.github.minimo.orm.setter;

import io.github.minimo.orm.BaseEnum;
import io.github.minimo.orm.parameter.PreparedStatementParameterSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class BaseEnumSetter implements PreparedStatementParameterSetter<BaseEnum> {
    @Override
    public void setNull(PreparedStatement ps, int index) throws SQLException {
        ps.setNull(index, Types.INTEGER);
    }

    @Override
    public void set(PreparedStatement ps, int index, Object value) throws SQLException {
        ps.setInt(index, ((BaseEnum) value).code());
    }
}
