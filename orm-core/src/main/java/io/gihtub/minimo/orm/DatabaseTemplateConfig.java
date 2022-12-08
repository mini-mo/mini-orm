package io.gihtub.minimo.orm;

import io.gihtub.minimo.orm.parameter.*;
import io.gihtub.minimo.orm.resultset.*;

import java.sql.JDBCType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 配置
 */
public class DatabaseTemplateConfig {

  private final Map<Class<?>, PreparedStatementParameterSetter<?>> parameterSetterMap;
  private final Map<Pair<JDBCType, Class<?>>, ResultTypeMapper<?>> resultTypeMapperMap;

  private int defaultLimit = 100;

  public DatabaseTemplateConfig(Map<Class<?>, PreparedStatementParameterSetter<?>> parameterSetterMap, Map<Pair<JDBCType, Class<?>>, ResultTypeMapper<?>> resultTypeMapperMap) {
    this.parameterSetterMap = parameterSetterMap;
    this.resultTypeMapperMap = resultTypeMapperMap;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public int getDefaultLimit() {
    return defaultLimit;
  }

  public <T> ResultTypeMapper<T> lookupResultMapper(Pair<JDBCType, Class<T>> key) {
    return (ResultTypeMapper<T>) this.resultTypeMapperMap.get(key);
  }

  public <T> PreparedStatementParameterSetter<T> lookupSetter(Class<T> cls) {
    return (PreparedStatementParameterSetter<T>) this.parameterSetterMap.get(cls);
  }

  public static class Builder {
    private final Map<Class<?>, PreparedStatementParameterSetter<?>> parameterSetterMap;
    private final Map<Pair<JDBCType, Class<?>>, ResultTypeMapper<?>> resultTypeMapperMap;

    public Builder() {
      this.parameterSetterMap = new HashMap<>();
      this.resultTypeMapperMap = new HashMap<>();
    }

    public Builder enableBuiltinParameterSetters() {
      registerParameterSetter(int.class, new IntegerSetter());
      registerParameterSetter(long.class, new LongSetter());

      registerParameterSetter(new IntegerSetter());
      registerParameterSetter(new LongSetter());
      registerParameterSetter(new StringSetter());
      registerParameterSetter(new DateSetter());
      registerParameterSetter(new InstantSetter());

      return this;
    }

    public Builder enableBuiltinResultTypeMappers() {
      registerResultMapper(new StringResultTypeMapper());
      registerResultMapper(new IntegerResultTypeMapper());
      registerResultMapper(new LongResultTypeMapper());
      registerResultMapper(new DateResultTypeMapper());
      registerResultMapper(new InstantResultTypeMapper());

      return this;
    }

    public Builder registerResultMapper(ResultTypeMapper<?> mapper) {
      Objects.requireNonNull(mapper);
      for (JDBCType type : mapper.types()) {
        registerResultMapper(type, mapper);
      }
      return this;
    }

    public Builder registerResultMapper(JDBCType type, ResultTypeMapper<?> mapper) {
      Objects.requireNonNull(type);
      Objects.requireNonNull(mapper);

      Pair<JDBCType, Class<?>> key = Pair.of(type, mapper.genericType());
      if (resultTypeMapperMap.containsKey(key)) {
        throw new IllegalStateException("result type mapper duplicate for " + mapper.getClass().getCanonicalName());
      }
      this.resultTypeMapperMap.put(key, mapper);

      return this;
    }

    private void registerParameterSetter(Class<?> clazz, PreparedStatementParameterSetter<?> setter) {
      Objects.requireNonNull(clazz);
      Objects.requireNonNull(setter);
      if (parameterSetterMap.containsKey(clazz)) {
        throw new IllegalStateException("parameter setter duplicate for " + clazz.getCanonicalName());
      }
      this.parameterSetterMap.put(clazz, setter);
    }

    public Builder registerParameterSetter(PreparedStatementParameterSetter<?> setter) {
      Objects.requireNonNull(setter);
      // 获取 setter 泛型类
      registerParameterSetter(setter.genericType(), setter);
      return this;
    }

    public DatabaseTemplateConfig build() {
      return new DatabaseTemplateConfig(parameterSetterMap, resultTypeMapperMap);
    }
  }

}
