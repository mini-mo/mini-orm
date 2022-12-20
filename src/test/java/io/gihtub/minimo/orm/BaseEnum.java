package io.gihtub.minimo.orm;

import java.util.Objects;

public interface BaseEnum {

  int code();

  static <T extends BaseEnum> T of(int code, Class<T> cls) {
    if (!cls.isEnum()) {
      return null;
    }
    for (T e : cls.getEnumConstants()) {
      if (Objects.equals(code, e.code())) {
        return e;
      }
    }
    throw new IllegalStateException();
  }
}
