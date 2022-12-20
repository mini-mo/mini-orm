package io.gihtub.minimo.orm.dsl;

import io.gihtub.minimo.orm.OrmContext;
import io.gihtub.minimo.orm.dsl.criteria.Criteria;
import io.gihtub.minimo.orm.resultset.RowMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TypeQueryDsl<T> extends BaseQueryDsl {
  private final Class<T> clazz;
  private final String tableName;

  private List<Sort> sorts = new ArrayList<>();

  public TypeQueryDsl(OrmContext context, Class<T> clazz) {
    super(null, null, context, null);
    this.clazz = clazz;

    var t = this.context.lookupTable(this.clazz);
    this.tableName = t.tableName();
    this.sql = "SELECT * FROM " + tableName;
  }

  public TypeQueryDsl<T> where(Criteria criteria) {
    Objects.requireNonNull(criteria);
    var p = this.context.generator().gen(criteria);
    this.sql = this.sql + " WHERE " + p.left();
    this.params = p.right();
    this.preparedStatementSetter = this.context.setter(params);
    return this;
  }

  public TypeQueryDsl<T> orderBy(Sort... sorts) {
    this.sorts.addAll(Arrays.stream(sorts).toList());
    return this;
  }

  public NativeQueryWithResultMapper<T> execute() {
    if (!this.sorts.isEmpty()) {
      this.sql = this.sql + genSorts();
    }
    RowMapper<T> rowMapper = this.context.getOrCreateRowMapper(clazz);
    return new NativeQueryWithResultMapper<>(sql, params, preparedStatementSetter, context, rowMapper);
  }

  private String genSorts() {
    if (this.sorts.isEmpty()) {
      return "";
    }
    var sb = new StringBuilder();
    sb.append(" order by ");
    var body = sorts.stream().map(it -> this.context.generator().gen(it)).collect(Collectors.joining(","));
    sb.append(body).append(" ");
    return sb.toString();
  }
}
