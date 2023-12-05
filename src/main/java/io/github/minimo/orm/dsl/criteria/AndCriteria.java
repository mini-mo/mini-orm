package io.github.minimo.orm.dsl.criteria;

public class AndCriteria implements Criteria {
  public final Criteria[] criteria;

  public AndCriteria() {
    this.criteria = new Criteria[0];
  }

  public AndCriteria(Criteria[] criteria) {
    this.criteria = criteria;
  }

  public AndCriteria append(Criteria... criteria) {
    if (criteria == null || criteria.length == 0) {
      return this;
    }
    var newLen = this.criteria.length + criteria.length;

    var nc = new Criteria[newLen];
    System.arraycopy(this.criteria, 0, nc, 0, this.criteria.length);
    System.arraycopy(criteria, 0, nc, this.criteria.length, criteria.length);
    return new AndCriteria(nc);
  }
}
