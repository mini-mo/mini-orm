package io.github.minimo.orm.dsl;

import io.github.minimo.orm.OrmContext;
import io.github.minimo.orm.dsl.criteria.Criteria;
import io.github.minimo.orm.resultset.RowMapper;

import java.util.Objects;

public class AdvanceQueryDsl extends BaseQueryDsl {

  public AdvanceQueryDsl(OrmContext context, String header) {
    super(header, null, context, null);
  }

  public AdvanceQueryDsl where(Criteria criteria) {
    Objects.requireNonNull(criteria);

    var p = this.context.generator().gen(criteria);
    this.sql = this.sql + " WHERE " + p.left();
    this.params = p.right();
    this.preparedStatementSetter = this.context.setter(params);
    return this;
  }

  public <T> NativeQueryWithResultMapper<T> map(Class<T> cls) {
    // generate row mapper
    RowMapper<T> rowMapper = this.context.getOrCreateRowMapper(cls);
    return map(rowMapper);
  }
}
