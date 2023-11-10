package io.github.minimo.orm.annotations;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {

  /**
   * 列名
   */
  String value() default "id";

  /**
   * 数据库自增 默认 true
   */
  boolean autoIncrement() default true;
}
