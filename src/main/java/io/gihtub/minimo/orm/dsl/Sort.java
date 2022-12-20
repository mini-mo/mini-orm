package io.gihtub.minimo.orm.dsl;

import java.util.Objects;

public interface Sort {

  static Sort desc(String column) {
    Objects.requireNonNull(column);
    return new SortDesc(column);
  }

  static Sort asc(String column) {
    Objects.requireNonNull(column);
    return new SortAsc(column);
  }
}
