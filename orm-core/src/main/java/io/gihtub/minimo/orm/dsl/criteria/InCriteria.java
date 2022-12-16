package io.gihtub.minimo.orm.dsl.criteria;

import java.util.Collection;

public class InCriteria extends BinaryCriteria {
  public final Collection<? extends Object> v;

  public InCriteria(BinaryCriteria binaryCriteria, Collection<? extends Object> v) {
    super(binaryCriteria.name);
    this.v = v;
  }
}
