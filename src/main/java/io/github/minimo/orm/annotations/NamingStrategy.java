package io.github.minimo.orm.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface NamingStrategy {

  /**
   * 属性命名策略
   */
  Class<? extends PropertyNamingStrategy> value() default PropertyNamingStrategy.Noop.class;
}
