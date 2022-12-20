package io.gihtub.minimo.orm.dsl;

import io.gihtub.minimo.orm.OrmContext;
import io.gihtub.minimo.orm.resultset.RowMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class QueryDsl extends BaseQueryDsl {

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
