package io.gihtub.minimo.orm.dsl.criteria;

import java.util.Collection;

public class StringInCriteria extends BinaryCriteria {
  public final Collection<String> v;

  public StringInCriteria(BinaryCriteria binaryCriteria, Collection<String> v) {
    super(binaryCriteria.name);
    this.v = v;
  }
}
