package io.github.minimo.orm.dsl;

import io.github.minimo.orm.OrmContext;
import io.github.minimo.orm.resultset.RowMapper;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryDsl extends BaseQueryDsl {
  private static final Logger log = LoggerFactory.getLogger(QueryDsl.class);

  public QueryDsl(String sql, Object[] params, OrmContext context) {
    super(sql, params, context, null);
    this.preparedStatementSetter = this.context.setter(params);
  }

  public <T> NativeQueryWithResultMapper<T> map(Class<T> cls) {
    Objects.requireNonNull(cls);
    Objects.requireNonNull(sql);

    // generate row mapper
    RowMapper<T> rowMapper = this.context.getOrCreateRowMapper(cls);
    return map(rowMapper);
  }
}
