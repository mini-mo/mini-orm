package io.github.minimo.orm.dsl.criteria;

import io.github.minimo.orm.Pair;
import io.github.minimo.orm.dsl.Generator;

public interface Criteria {

  static BinaryCriteria column(String column) {
    return new BinaryCriteria(column);
  }

  static AndCriteria and(Criteria... criteria) {
    return new AndCriteria(criteria);
  }

  static Criteria or(Criteria... criteria) {
    return new OrCriteria(criteria);
  }

  default Pair<String, Object[]> gen(Generator generator) {
    return generator.gen(this);
  }
}
