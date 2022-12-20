
package io.gihtub.minimo.orm.executor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementSetter {

	void setValues(PreparedStatement ps) throws SQLException;

}
