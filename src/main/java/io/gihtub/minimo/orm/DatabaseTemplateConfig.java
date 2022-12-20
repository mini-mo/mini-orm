package io.gihtub.minimo.orm;

import io.gihtub.minimo.orm.parameter.*;
import io.gihtub.minimo.orm.resultset.*;

import java.lang.reflect.Constructor;
import java.sql.JDBCType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * 配置
 */
public class DatabaseTemplateConfig {

  private final Map<Class<?>, PreparedStatementParameterSetter<?>> parameterSetterMap;
  private final Map<Pair<JDBCType, Class<?>>, ResultTypeMapper<?>> resultTypeMapperMap;

  private final Class<? extends BaseResultTypeMapper> enumTypeMapperCls;

  private int defaultLimit = 100;

  public DatabaseTemplateConfig(Map<Class<?>, PreparedStatementParameterSetter<?>> parameterSetterMap, Map<Pair<JDBCType, Class<?>>, ResultTypeMapper<?>> resultTypeMapperMap, Class<? extends BaseResultTypeMapper> enumTypeMapperCls) {
    this.parameterSetterMap = parameterSetterMap;
    this.resultTypeMapperMap = resultTypeMapperMap;
    this.enumTypeMapperCls = enumTypeMapperCls;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public int getDefaultLimit() {
    return defaultLimit;
  }

  public <T> ResultTypeMapper<T> lookupResultMapper(Pair<JDBCType, Class<T>> key) {
    if (this.resultTypeMapperMap.containsKey(key)) {
      return (ResultTypeMapper<T>) this.resultTypeMapperMap.get(key);
    }

    var type = key.right();
    if (Enum.class.isAssignableFrom(type)) {
      // new enum type mapper instance
      var instance = getInstance(type, enumTypeMapperCls);
      this.resultTypeMapperMap.put(Pair.of(key.left(), key.right()), instance);
      return (ResultTypeMapper<T>) instance;
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public <T> ResultTypeMapper<T> getInstance(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
    if (javaTypeClass != null) {
      try {
        Constructor<?> c = typeHandlerClass.getConstructor(Class.class);
        return (ResultTypeMapper<T>) c.newInstance(javaTypeClass);
      } catch (NoSuchMethodException ignored) {
        // ignored
      } catch (Exception e) {
        throw new IllegalStateException("Failed invoking constructor for handler " + typeHandlerClass, e);
      }
    }
    try {
      Constructor<?> c = typeHandlerClass.getConstructor();
      return (ResultTypeMapper<T>) c.newInstance();
    } catch (Exception e) {
      throw new IllegalStateException("Unable to find a usable constructor for " + typeHandlerClass, e);
    }
  }

  public <T> PreparedStatementParameterSetter<T> lookupSetter(Class<T> cls) {
    if (this.parameterSetterMap.containsKey(cls)) {
      return (PreparedStatementParameterSetter<T>) this.parameterSetterMap.get(cls);
    }

    if (cls.getInterfaces().length != 0) {
      for (Class<?> in : cls.getInterfaces()) {
        PreparedStatementParameterSetter<?> ps = lookupSetterByInterface(in);
        if (ps != null) {
          // cache
          this.parameterSetterMap.put(cls, ps);
          return (PreparedStatementParameterSetter<T>) ps;
        }
      }
    }

    // try super class
    var ps = lookupSetterByClass(cls.getSuperclass());
    if (ps != null) {
      this.parameterSetterMap.put(cls, ps);
      return (PreparedStatementParameterSetter<T>) ps;
    }

    return null;
  }

  private <T> PreparedStatementParameterSetter<T> lookupSetterByClass(Class<? super T> cls) {
    if (cls == null) {
      return null;
    }

    if (this.parameterSetterMap.containsKey(cls)) {
      return (PreparedStatementParameterSetter<T>) this.parameterSetterMap.get(cls);
    }

    return lookupSetterByClass(cls.getSuperclass());
  }


  // lookup by interface
  public <T> PreparedStatementParameterSetter<T> lookupSetterByInterface(Class<T> cls) {
    if (!cls.isInterface()) {
      return null;
    }
    if (this.parameterSetterMap.containsKey(cls)) {
      return (PreparedStatementParameterSetter<T>) this.parameterSetterMap.get(cls);
    }
    if (cls.getInterfaces().length == 0) {
      return null;
    }

    for (Class<?> ai : cls.getInterfaces()) {
      return (PreparedStatementParameterSetter<T>) lookupSetterByInterface(ai);
    }
    return null;
  }

  public static class Builder {
    private final Map<Class<?>, PreparedStatementParameterSetter<?>> parameterSetterMap;
    private final Map<Pair<JDBCType, Class<?>>, ResultTypeMapper<?>> resultTypeMapperMap;

    private Class<? extends BaseResultTypeMapper> enumTypeMapper = DefaultEnumTypeMapper.class;

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

    public Builder enumMapper(Class<? extends BaseResultTypeMapper> cls) {
      this.enumTypeMapper = cls;
      return this;
    }

    public DatabaseTemplateConfig build() {
      return new DatabaseTemplateConfig(parameterSetterMap, resultTypeMapperMap, enumTypeMapper);
    }
  }

}
