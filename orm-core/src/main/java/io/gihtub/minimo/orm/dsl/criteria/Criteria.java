package io.gihtub.minimo.orm.dsl.criteria;

import io.gihtub.minimo.orm.Pair;
import io.gihtub.minimo.orm.dsl.Generator;

public interface Criteria {

  static BinaryCriteria column(String column) {
    return new BinaryCriteria(column);
  }

  static Criteria and(Criteria... criteria) {
    return new AndCriteria(criteria);
  }

  static Criteria or(Criteria... criteria) {
    return new OrCriteria(criteria);
  }

  default Pair<String, Object[]> gen(Generator generator) {
    return generator.gen(this);
  }
}
