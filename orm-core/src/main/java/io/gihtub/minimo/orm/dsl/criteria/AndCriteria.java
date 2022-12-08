package io.gihtub.minimo.orm.dsl.criteria;

public class AndCriteria implements Criteria {
  public final Criteria[] criteria;

  public AndCriteria(Criteria[] criteria) {
    this.criteria = criteria;
  }
}
