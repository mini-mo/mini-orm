package io.gihtub.minimo.orm.dsl.criteria;

import java.util.Collection;

public class IntegerInCriteria extends BinaryCriteria {
  public final Collection<Integer> v;

  public IntegerInCriteria(BinaryCriteria binaryCriteria, Collection<Integer> v) {
    super(binaryCriteria.name);
    this.v = v;
  }
}
