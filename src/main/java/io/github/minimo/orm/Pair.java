package io.github.minimo.orm;

public record Pair<L, R>(L left, R right) {

  public static <L, R> Pair<L, R> of(L left, R right) {
    return new Pair<>(left, right);
  }
}
