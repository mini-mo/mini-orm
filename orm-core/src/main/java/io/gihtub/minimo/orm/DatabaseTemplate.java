package io.gihtub.minimo.orm;

import io.gihtub.minimo.orm.dsl.*;

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

  public NativeUpdateDsl nativeDelete(String sql) {
    return new NativeUpdateDsl(sql, context);
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
