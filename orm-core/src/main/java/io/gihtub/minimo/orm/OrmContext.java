package io.gihtub.minimo.orm;

import io.gihtub.minimo.orm.annotations.NamingStrategy;
import io.gihtub.minimo.orm.annotations.PropertyNamingStrategy;
import io.gihtub.minimo.orm.dsl.Generator;
import io.gihtub.minimo.orm.executor.Executor;
import io.gihtub.minimo.orm.executor.PreparedStatementSetter;
import io.gihtub.minimo.orm.parameter.PreparedStatementParameterSetter;
import io.gihtub.minimo.orm.resultset.ResultTypeMapper;
import io.gihtub.minimo.orm.resultset.RowMapper;
import io.gihtub.minimo.orm.table.MetaTable;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public record OrmContext(DatabaseTemplateConfig config, Generator generator, Executor executor) {

  private static Map<Class<?>, RowMapper<?>> mappers = new ConcurrentHashMap<>();
  private static Map<Class<?>, MetaTable> tables = new ConcurrentHashMap<>();

  public int defaultLimit() {
    return config.getDefaultLimit();
  }

  public <T> ResultTypeMapper<T> lookupResultMapper(Pair<JDBCType, Class<T>> key) {
    return this.config.lookupResultMapper(key);
  }

  public <T> PreparedStatementParameterSetter<T> lookupSetter(Class<T> cls) {
    return this.config.lookupSetter(cls);
  }

  public <T> RowMapper<T> getOrCreateRowMapper(Class<T> cls) {
    if (mappers.containsKey(cls)) {
      return (RowMapper<T>) mappers.get(cls);
    }
    var m = new RowMapper<T>() {
      @Override
      public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        var obj = newInstance(cls);
        var md = rs.getMetaData();

        for (int i = 1; i < md.getColumnCount() + 1; i++) {
          var columnType = md.getColumnType(i);
          var jdbcType = JDBCType.valueOf(columnType);
          var columnName = md.getColumnName(i);

          var field = lookupField(cls, columnName);
          if (field == null) {
            log.debug("skip column for {} {}", columnName, cls.getCanonicalName());
            continue;
          }
          Class<?> javaType = field.getType();

          ResultTypeMapper<?> mapper = lookupResultMapper(Pair.of(jdbcType, javaType));
          Object nv = mapper.map(jdbcType, rs.getObject(i));
          set(field, obj, nv);
        }

        return obj;
      }
    };
    mappers.putIfAbsent(cls, m);
    return (RowMapper<T>) mappers.get(cls);
  }

  public PreparedStatementSetter setter(Object[] params) {
    if (params == null || params.length == 0) {
      return null;
    }
    return ps -> {

      for (int i = 0; i < params.length; i++) {
        var input = params[i];
        var parameterIndex = i + 1;

        if (input == null) {
          ps.setNull(parameterIndex, Types.NULL);
          continue;
        }
        var setter = this.lookupSetter(input.getClass());
        setter.set(ps, parameterIndex, input);
      }
    };
  }

  private void set(Field field, Object target, Object value) {
    try {
      field.setAccessible(true);
      field.set(target, value);
    } catch (Exception e) {
      log.error("failed set property {} {} {}", field, target, value);
    }
  }

  private Field lookupField(Class<?> cls, String name) {
    var anno = cls.getAnnotation(NamingStrategy.class);
    PropertyNamingStrategy naming = new PropertyNamingStrategy.Noop();
    if (anno != null && !anno.value().equals(PropertyNamingStrategy.Noop.class)) {
      naming = newInstance(anno.value());
    }
    for (Field field : cls.getDeclaredFields()) {
      var n = naming.transform(field.getName());
      if (name.equalsIgnoreCase(n)) {
        return field;
      }
    }
    return null;
  }

  private <T> T newInstance(Class<T> cls) {
    try {
      return cls.newInstance();
    } catch (Exception e) {
      log.error("failed instance for {}", cls);
      throw new RuntimeException(e);
    }
  }

  public <T> MetaTable lookupTable(Class<T> entityCls) {
    if (tables.containsKey(entityCls)) {
      return tables.get(entityCls);
    }

    var table = new MetaTable(entityCls);
    tables.put(entityCls, table);
    return table;
  }
}
