package io.github.minimo.orm.dsl.criteria;

public class RawCriteria implements Criteria{
  public final String raw;

  public RawCriteria(String raw) {
    this.raw = raw;
  }
}
