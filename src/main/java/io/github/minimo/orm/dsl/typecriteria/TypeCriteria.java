package io.github.minimo.orm.dsl.typecriteria;

public interface TypeCriteria<T> {

  TypeCriteria<T> test(Supplier<T> supplier);

}
