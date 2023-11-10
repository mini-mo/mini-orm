package io.github.minimo.orm.dsl.criteria;

import java.util.Collection;

/**
 * 二元
 */
public class BinaryCriteria implements Criteria {
  public final String name;

  public BinaryCriteria(String name) {
    this.name = name;
  }

  public EqCriteria is(Object v) {
    return new EqCriteria(this, v);
  }

  public NotEqCriteria ne(Object v) {
    return new NotEqCriteria(this, v);
  }

  public NullCriteria isNull() {
    return new NullCriteria(this);
  }

  public NotNullCriteria notNull() {
    return new NotNullCriteria(this);
  }

  public <T> RangeCriteria range(T left, T right) {
    return new RangeCriteria(this, left, right);
  }

  public Criteria like(String value) {
    return new LikeCriteria(this, value);
  }

  public Criteria in(Collection<? extends Object> ids) {
    return new InCriteria(this, ids);
  }

  public Criteria lte(Object v) {
    return new LteCriteria(this, v);
  }

  public Criteria gte(Object v) {
    return new GteCriteria(this, v);
  }
}
