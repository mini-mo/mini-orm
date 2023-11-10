package io.github.minimo.orm;

import io.github.minimo.orm.dsl.criteria.Criteria;
import io.github.minimo.orm.table.MetaTable;

import io.github.minimo.orm.dsl.AdvanceQueryDsl;
import io.github.minimo.orm.dsl.InsertDsl;
import io.github.minimo.orm.dsl.NativeInsertDsl;
import io.github.minimo.orm.dsl.NativeQueryDsl;
import io.github.minimo.orm.dsl.NativeUpdateDsl;
import io.github.minimo.orm.dsl.QueryDsl;
import io.github.minimo.orm.dsl.TypeQueryDsl;
import io.github.minimo.orm.dsl.UpdateDsl;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DatabaseTemplate {
  private final OrmContext context;

  public DatabaseTemplate(OrmContext context) {
    this.context = context;
  }

  public NativeQueryDsl nativeQuery(String sql) {
    return new NativeQueryDsl(sql, context);
  }

  public QueryDsl createQuery(String sql, Object... params) {
    return new QueryDsl(sql, params, context);
  }

  /**
   * @param sql do not provider where condition.
   */
  public AdvanceQueryDsl createCompositeQuery(String sql) {
    return new AdvanceQueryDsl(context, sql);
  }

  public <T> TypeQueryDsl<T> from(Class<T> cls) {
    return new TypeQueryDsl<>(context, cls);
  }

  public NativeUpdateDsl createNativeUpdate(String sql) {
    return new NativeUpdateDsl(sql, context);
  }

  public NativeInsertDsl createNativeInsert(String sql) {
    return new NativeInsertDsl(sql, context);
  }

  public NativeUpdateDsl createNativeDelete(String sql) {
    return new NativeUpdateDsl(sql, context);
  }

  public UpdateDsl createDelete(String sql, Object... params) {
    return new UpdateDsl(sql, params, context);
  }

  public UpdateDsl createUpdate(String sql, Object... params) {
    return new UpdateDsl(sql, params, context);
  }

  public InsertDsl createInsert(String sql, Object... params) {
    return new InsertDsl(sql, params, context);
  }

  /**
   * @return true if create or update count is 1, false if others case.
   */
  public boolean save(Object obj) {
    Objects.requireNonNull(obj);

    var table = this.context.lookupTable(obj.getClass());
    Number idV = table.getIdValue(obj);
    if (idV == null) { // new one
      return doInsert(obj, table);
    } else {
      Object exists = this.findById(idV, obj.getClass());
      if (exists == null) { // new one
        // insert into xx(x,y,z) values (x,y,z)
        return doInsert(obj, table);
      }
    }

    // id exists ?
    // update users set name = ? where id = ? ;
    Map<String, Object> params = table.mappings(obj);
    if (params.isEmpty()) {
      throw new IllegalStateException();
    }
    // version?
    Pair<String, Object> version = table.version(obj);
    int append = 1;
    if (version != null) {
      append++;
    }
    Object[] pa = new Object[params.size() + append];
    var vsa = params.values().toArray();
    System.arraycopy(vsa, 0, pa, 0, vsa.length);
    pa[params.size()] = idV;
    if (version != null) {
      pa[params.size() + 1] = version.right();
    }

    var ks = params.keySet().toArray(new String[0]);
    var body = Arrays.stream(ks).map(it -> "`" + it + "` = ?")
        .collect(Collectors.joining(","));

    var sb = new StringBuilder();
    sb.append("update ").append(table.tableName())
        .append(" set ")
        .append(body);

    if (version != null) {
      sb.append(", ").append(version.left()).append(" = version + 1");
    }

    sb.append(" where ")
        .append(table.getIdColumn())
        .append(" = ?");

    if (version != null) {
      sb.append(" and ").append(version.left()).append(" = ? ");
    }

    sb.append(" limit 1")
    ;

    var sql = sb.toString();
    return new UpdateDsl(sql, pa, context).execute() == 1;
  }

  private boolean doInsert(Object obj, MetaTable table) {
    Map<String, Object> params = table.mappings(obj);
    if (params.isEmpty()) {
      throw new IllegalStateException();
    }
    var keys = params.keySet().stream().map(it -> "`" + it + "`").toArray(String[]::new);
    var values = params.values().toArray();
    var sb = new StringBuilder();
    sb.append("insert into ").append(table.tableName())
        .append(" (")
        .append(String.join(",", keys))
        .append(" )")
        .append(" values (")
        .append(String.join(",", Collections.nCopies(keys.length, "?")))
        .append(" )");

    var sql = sb.toString();

    var gk = new InsertDsl(sql, values, this.context).executeAndReturnGeneratedKey();
    if (gk <= 0L) {
      throw new IllegalStateException();
    }
    table.setId(obj, gk);
    return true;
  }

  public <T> T findById(Number id, Class<T> entityCls) {
    var query = new TypeQueryDsl<T>(this.context, entityCls);
    var table = this.context.lookupTable(entityCls);
    String ic = table.getIdColumn();
    query.where(Criteria.column(ic).is(id));
    return query.execute().one();
  }

  public <T> Collection<T> findByIds(List<? extends Number> ids, Class<T> entityCls) {
    var query = new TypeQueryDsl<T>(this.context, entityCls);
    var table = this.context.lookupTable(entityCls);
    String ic = table.getIdColumn();
    query.where(Criteria.column(ic).in(ids));
    return query.execute().list(ids.size());
  }

//  public <T> T getMapper(Class<T> mapper) {
//    var classLoader = mapper.getClassLoader();
//
//    for (Method method : mapper.getDeclaredMethods()) {
//
//    }
//
//    var proxy = Proxy.newProxyInstance(classLoader, (Class<?>[]) new Object[]{mapper}, new InvocationHandler() {
//      @Override
//      public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
//        if (Object.class.equals(method.getDeclaringClass())) {
//          return method.invoke(this, args);
//        }
//        return null;
//      }
//    });
//    return (T) proxy;
//  }
}
