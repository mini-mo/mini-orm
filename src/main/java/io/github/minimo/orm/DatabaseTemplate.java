package io.github.minimo.orm;

import io.github.minimo.orm.dsl.AdvanceQueryDsl;
import io.github.minimo.orm.dsl.InsertDsl;
import io.github.minimo.orm.dsl.NativeInsertDsl;
import io.github.minimo.orm.dsl.NativeQueryDsl;
import io.github.minimo.orm.dsl.NativeUpdateDsl;
import io.github.minimo.orm.dsl.QueryDsl;
import io.github.minimo.orm.dsl.TypeQueryDsl;
import io.github.minimo.orm.dsl.UpdateDsl;
import io.github.minimo.orm.dsl.typecriteria.Function;
import java.util.Collection;
import java.util.List;

public interface DatabaseTemplate {

  NativeQueryDsl nativeQuery(String sql);

  QueryDsl createQuery(String sql, Object... params);

  /**
   * @param sql do not provider where condition.
   */
  AdvanceQueryDsl createCompositeQuery(String sql);

  <T> TypeQueryDsl<T> from(Class<T> cls);

  NativeUpdateDsl createNativeUpdate(String sql);

  NativeInsertDsl createNativeInsert(String sql);

  NativeUpdateDsl createNativeDelete(String sql);

  UpdateDsl createDelete(String sql, Object... params);

  UpdateDsl createUpdate(String sql, Object... params);

  InsertDsl createInsert(String sql, Object... params);

  /**
   * @return true if create or update count is 1, false if others case.
   */
  boolean save(Object obj);

  <T> T findById(Number id, Class<T> entityCls);

  <T> Collection<T> findByIds(List<? extends Number> ids, Class<T> entityCls);

  // lookup column name
  <T> String column(Function<T, ?> supplier);
}
