package io.github.minimo.orm.annotations;

import io.github.minimo.orm.resultset.ResultColumnMapper;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Version {

  /**
   * 列名，可覆盖 table naming strategy.
   */
  String value() default "";

  Class<? extends ResultColumnMapper> mapper() default ResultColumnMapper.Noop.class;

}
