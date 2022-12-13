package io.gihtub.minimo.orm.dsl.criteria;

import java.util.Collection;

public class LongInCriteria extends BinaryCriteria {
  public final Collection<Long> v;

  public LongInCriteria(BinaryCriteria binaryCriteria, Collection<Long> v) {
    super(binaryCriteria.name);
    this.v = v;
  }
}
