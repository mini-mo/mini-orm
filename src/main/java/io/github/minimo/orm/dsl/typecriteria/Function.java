package io.github.minimo.orm.dsl.typecriteria;

import java.io.Serializable;

// SerializedLambda
// https://www.jianshu.com/p/2ed82b6cd07b
@FunctionalInterface
public interface Function<T, R> extends java.util.function.Function<T, R>, Serializable {

}
