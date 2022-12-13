package io.gihtub.minimo.orm.dsl.criteria;

import java.util.Collection;

public class NumberInCriteria extends BinaryCriteria {
  public final Collection<? extends Number> v;

  public NumberInCriteria(BinaryCriteria binaryCriteria, Collection<? extends Number> v) {
    super(binaryCriteria.name);
    this.v = v;
  }
}
