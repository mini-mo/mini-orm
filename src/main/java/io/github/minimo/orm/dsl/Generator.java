package io.github.minimo.orm.dsl;

import io.github.minimo.orm.Pair;
import io.github.minimo.orm.dsl.criteria.AndCriteria;
import io.github.minimo.orm.dsl.criteria.BinaryCriteria;
import io.github.minimo.orm.dsl.criteria.Criteria;
import io.github.minimo.orm.dsl.criteria.OrCriteria;
import io.github.minimo.orm.dsl.criteria.RawCriteria;

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

  Pair<String, Object[]> gen(RawCriteria raw);

  String gen(Sort sort);

  String gen(SortAsc asc);
  String gen(SortDesc desc);

  String rewriteLimit(String sql, long offset, int limit);

  String rewriteProjectForCount(String sql);
}
