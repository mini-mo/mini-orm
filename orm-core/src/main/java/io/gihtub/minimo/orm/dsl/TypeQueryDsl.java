package io.gihtub.minimo.orm.dsl;

import io.gihtub.minimo.orm.OrmContext;
import io.gihtub.minimo.orm.annotations.Table;
import io.gihtub.minimo.orm.dsl.criteria.Criteria;
import io.gihtub.minimo.orm.resultset.RowMapper;

import java.util.List;
import java.util.Objects;

public class TypeQueryDsl<T> extends BaseQueryDsl {
  private final Class<T> clazz;

  public TypeQueryDsl(OrmContext context, Class<T> clazz) {
    super(null, null, context, null);
    this.clazz = clazz;

    var anno = this.clazz.getAnnotation(Table.class);
    if (anno == null) {
      throw new IllegalStateException("failed get table annotation from type query with class " + clazz.getCanonicalName());
    }
    this.sql = "SELECT * FROM " + this.clazz.getAnnotation(Table.class).value();
  }

  public TypeQueryDsl<T> where(Criteria criteria) {
    Objects.requireNonNull(criteria);
    var p = this.context.generator().gen(criteria);
    this.sql = this.sql + " WHERE " + p.left();
    this.params = p.right();
    this.preparedStatementSetter = this.context.setter(params);
    return this;
  }

//  public T one() {
//    var v = list(1);
//    return v == null || v.isEmpty() ? null : v.get(0);
//  }
//
//  public List<T> list() {
//    return list(0, context.defaultLimit());
//  }
//
//  public List<T> list(int limit) {
//    return list(0, limit);
//  }
//
//  public List<T> list(long offset, int limit) {
//    return execute().list(offset, limit);
//  }

  public NativeQueryWithResultMapper<T> execute() {
    RowMapper<T> rowMapper = this.context.getOrCreateRowMapper(clazz);
    return new NativeQueryWithResultMapper<>(sql, params, preparedStatementSetter, context, rowMapper);
  }
}
