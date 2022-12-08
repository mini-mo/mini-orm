package io.gihtub.minimo.orm.annotations;

import io.gihtub.minimo.orm.resultset.ResultColumnMapper;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

  /**
   * 列名，可覆盖 table naming strategy.
   */
  String value() default "";

  Class<? extends ResultColumnMapper> mapper() default ResultColumnMapper.Noop.class;

}
