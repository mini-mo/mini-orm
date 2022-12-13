package io.gihtub.minimo.orm.table;

import io.gihtub.minimo.orm.annotations.Id;
import io.gihtub.minimo.orm.annotations.NamingStrategy;
import io.gihtub.minimo.orm.annotations.PropertyNamingStrategy;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 表示实体类与数据库表的关系
 * <p>
 * 默认情况下
 * 1. 实体需要有 id 字段
 * 2. 实体所有字段均不可为空, （insert / update 时会忽略为空的字段）
 */
@Slf4j
public class MetaTable<T> {

  private final Class<T> entity;
  private final PropertyNamingStrategy namingStrategy;

  public MetaTable(Class<T> entity) {
    PropertyNamingStrategy ns;
    this.entity = entity;
    ns = new PropertyNamingStrategy.Noop();
    var anno = entity.getAnnotation(NamingStrategy.class);
    if (anno != null && !anno.value().equals(PropertyNamingStrategy.Noop.class)) {
      ns = newInstance(anno.value());
    }

    this.namingStrategy = ns;
  }

  public String getIdColumn() {
    var field = idField();
    if (field == null) {
      throw new IllegalStateException("entity class not found id column " + entity.getCanonicalName());
    }

    // try jpa
    var jpaId = field.getAnnotation(jakarta.persistence.Id.class);
    if (jpaId != null) {
      return field.getName();
    }

    Id anno = field.getAnnotation(Id.class);
    if (anno != null) {
      return anno.value();
    }
    return "id";
  }

  private Field idField() {
    Field f = null;
    for (Field field : entity.getDeclaredFields()) {
      if (field.getName().equalsIgnoreCase("id")) {
        if (f == null) {
          f = field;
        }

        var jpa = field.getAnnotation(jakarta.persistence.Id.class);
        if (jpa != null) {
          f = field;
        }

        var annotation = field.getAnnotation(Id.class);
        if (annotation != null) {
          f = field;
        }

      }

    }
    return f;
  }

  public String tableName() {
    // try jpa
    var jpa = this.entity.getAnnotation(Table.class);
    if (jpa != null) {
      return jpa.name();
    }

    var anno = this.entity.getAnnotation(io.gihtub.minimo.orm.annotations.Table.class);
    if (anno != null) {
      return anno.value();
    }
    log.warn("failed get table annotation form type query with class {}", entity.getCanonicalName());

//    throw new IllegalStateException("failed get table annotation from type query with class " + entity.getCanonicalName());
    return this.entity.getSimpleName().toUpperCase(); // 类名大写作为表名
  }

  public Number getIdValue(Object obj) {
    var field = idField();
    if (field == null) {
      throw new IllegalStateException();
    }
    try {
      field.setAccessible(true);
      var v = field.get(obj);
      if (v == null) {
        return null;
      }
      if (v instanceof Number) {
        return ((Number) v);
      }
      throw new ClassCastException("can not cast v to number, " + v.getClass().getCanonicalName());
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public Map<String, Object> mappings(Object obj) {
    Map<String, Object> map = new LinkedHashMap<>();
    for (Field field : this.entity.getDeclaredFields()) {
      if (Modifier.isTransient(field.getModifiers())) { //  transient kw
        continue; // skip
      }

      field.setAccessible(true);
      try {
        var v = field.get(obj);
        if (v == null) {
          continue;
        }
        map.put(namingStrategy.transform(field.getName()), v);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
    return map;
  }

  private <T> T newInstance(Class<T> cls) {
    try {
      return cls.newInstance();
    } catch (Exception e) {
      log.error("failed instance for {}", cls);
      throw new RuntimeException(e);
    }
  }

  public void setId(Object obj, long key) {
    var field = idField();
    if (field == null) {
      throw new IllegalStateException();
    }
    field.setAccessible(true);

    try {
      Class<?> type = field.getType();
      if (type.isAssignableFrom(Long.class)) {
        field.set(obj, key);
      } else if (type.isAssignableFrom(Integer.class)) {
        field.set(obj, ((int) key));
      } else {
        throw new IllegalStateException();
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
