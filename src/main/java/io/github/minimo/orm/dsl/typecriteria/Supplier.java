package io.github.minimo.orm.dsl.typecriteria;

import java.io.Serializable;

// SerializedLambda
// https://www.jianshu.com/p/2ed82b6cd07b
public interface Supplier<T> extends java.util.function.Supplier<T>, Serializable {

}
