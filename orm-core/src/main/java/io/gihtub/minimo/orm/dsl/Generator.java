package io.gihtub.minimo.orm.dsl;

import io.gihtub.minimo.orm.Pair;
import io.gihtub.minimo.orm.dsl.criteria.AndCriteria;
import io.gihtub.minimo.orm.dsl.criteria.BinaryCriteria;
import io.gihtub.minimo.orm.dsl.criteria.Criteria;
import io.gihtub.minimo.orm.dsl.criteria.OrCriteria;

/**
 * sql 生成器
 */
public interface Generator {

  /**
   * 生成语句，以及关联的参数
   */
  Pair<String, Object[]> gen(Criteria criteria);


  Pair<String, Object[]> gen(BinaryCriteria binary);

  Pair<String, Object[]> gen(AndCriteria and);

  Pair<String, Object[]> gen(OrCriteria or);

  String rewriteLimit(String sql, long offset, int limit);
}
