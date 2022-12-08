package io.gihtub.minimo.orm;

import io.gihtub.minimo.orm.parameter.DateSetter;
import io.gihtub.minimo.orm.parameter.InstantSetter;
import io.gihtub.minimo.orm.parameter.IntegerSetter;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatabaseTemplateConfigTest {

  @Test
  void test_config() {
    var builder = DatabaseTemplateConfig.newBuilder();
    builder.enableBuiltinParameterSetters();
    builder.enableBuiltinResultTypeMappers();
    var config = builder.build();
    assertNotNull(config);
  }

  @Test
  void test_setter() {
    Object[] args = new Object[]{
        10,
        Instant.ofEpochMilli(1670387374684L)
    };

    var v = new IntegerSetter().convert(args[0]);
    assertEquals(10, v);

    var v2 = new InstantSetter().convert(args[1]);
    assertEquals(1670387374684L, v2.toEpochMilli());

    var ds = new DateSetter();
    assertEquals(Date.class, ds.genericType());
  }

}