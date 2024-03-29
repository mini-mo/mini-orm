package io.github.minimo.orm.dsl;

import io.github.minimo.orm.Pair;

import io.github.minimo.orm.dsl.criteria.AndCriteria;
import io.github.minimo.orm.dsl.criteria.BinaryCriteria;
import io.github.minimo.orm.dsl.criteria.Criteria;
import io.github.minimo.orm.dsl.criteria.EqCriteria;
import io.github.minimo.orm.dsl.criteria.GteCriteria;
import io.github.minimo.orm.dsl.criteria.InCriteria;
import io.github.minimo.orm.dsl.criteria.LikeCriteria;
import io.github.minimo.orm.dsl.criteria.LteCriteria;
import io.github.minimo.orm.dsl.criteria.NotEqCriteria;
import io.github.minimo.orm.dsl.criteria.NotNullCriteria;
import io.github.minimo.orm.dsl.criteria.NullCriteria;
import io.github.minimo.orm.dsl.criteria.OrCriteria;
import io.github.minimo.orm.dsl.criteria.RangeCriteria;
import io.github.minimo.orm.dsl.criteria.RawCriteria;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class MysqlDialectGenerator implements Generator {

  @Override
  public Pair<String, Object[]> gen(Criteria criteria) {
    if (criteria instanceof AndCriteria and) {
      return gen(and);
    }
    if (criteria instanceof OrCriteria or) {
      return gen(or);
    }
    if (criteria instanceof BinaryCriteria bin) {
      return gen(bin);
    }
    if (criteria instanceof RawCriteria raw) {
      return gen(raw);
    }
    throw new IllegalStateException();
  }


  @Override
  public Pair<String, Object[]> gen(BinaryCriteria bin) {
    if (bin instanceof EqCriteria eq) {
      return Pair.of(eq.name + " = ?", new Object[]{eq.v});
    }
    if (bin instanceof NullCriteria nil) {
      return Pair.of(nil.name + " IS NULL", new Object[0]);
    }
    if (bin instanceof NotNullCriteria notnull) {
      return Pair.of(notnull.name + " IS NOT NULL", new Object[0]);
    }
    if (bin instanceof NotEqCriteria ne) {
      return Pair.of(ne.name + " != ?", new Object[]{ne.v});
    }
    if (bin instanceof LikeCriteria like) {
      return Pair.of(like.name + " like ?", new Object[]{"%" + like.v + "%"});
    }
    if (bin instanceof InCriteria ii) {
      var len = ii.v.size();
      List<String> qs = new ArrayList<>();
      Object[] objs = new Object[len];
      int i = 0;
      for (Object v : ii.v) {
        qs.add("?");
        objs[i++] = v;
      }
      var q = String.join(",", qs);
      return Pair.of(ii.name + " in ( " + q + " )", objs);
    }
    if (bin instanceof GteCriteria gte) {
      return Pair.of(gte.name + " >= ?", new Object[]{gte.v});
    }
    if (bin instanceof LteCriteria lte) {
      return Pair.of(lte.name + " <= ?", new Object[]{lte.v});
    }
    if (bin instanceof RangeCriteria range) {
      return Pair.of(range.name + " BETWEEN ? AND ?", new Object[]{
          range.left,
          range.right
      });
    }
    throw new IllegalStateException();
  }

  @Override
  public Pair<String, Object[]> gen(OrCriteria or) {
    var children = or.criteria;
    if (children.length == 0) {
      throw new IllegalStateException();
    }
    List<String> sts = new ArrayList<>();
    List<Object> objects = new ArrayList<>();
    Arrays.stream(children).map(this::gen)
        .forEach(it -> {
          sts.add(it.left());
          for (Object obj : it.right()) {
            objects.add(obj);
          }
        });

    return Pair.of("(" + String.join(" OR ", sts) + ")", objects.toArray(new Object[0]));
  }

  @Override
  public Pair<String, Object[]> gen(RawCriteria raw) {
    return Pair.of(raw.raw, new Object[0]);
  }

  @Override
  public String gen(Sort sort) {
    if (sort instanceof SortDesc sd) {
      return gen(sd);
    }
    if (sort instanceof SortAsc sa) {
      return gen(sa);
    }
    throw new IllegalStateException();
  }

  @Override
  public String gen(SortAsc asc) {
    return asc.column() + " ASC";
  }

  @Override
  public String gen(SortDesc desc) {
    return desc.column() + " DESC";
  }

  @Override
  public String rewriteLimit(String sql, long offset, int limit) {
    var str = sql.toLowerCase();
    if (!str.contains(" limit ")) { // 没有 limit
      return sql + genLimit(offset, limit);
    }

    // try match
    var p = Pattern.compile(".*(\\s*limit\\s*\\d+(\\s*,\\s*\\d+)?(\\s*;\\s*)?\\s*$)");
    var matcher = p.matcher(str);
    if (!matcher.matches()) {
      return sql + genLimit(offset, limit);
    }

    var start = matcher.start(1);

    return sql.substring(0, start) + genLimit(offset, limit);
  }

  @Override
  public String rewriteProjectForCount(String sql) {
    var s = sql.toLowerCase();
    var si = s.indexOf("select ");
    var ei = s.indexOf(" from ", si);

    return sql.substring(0, si + 7) + " count(1) " + sql.substring(ei);
  }

  private String genLimit(long offset, int limit) {
    if (offset == 0L) {
      return " LIMIT " + limit;
    }
    return " LIMIT " + offset + " , " + limit;
  }

  public Pair<String, Object[]> gen(AndCriteria and) {
    var children = and.criteria;
    if (children.length == 0) {
      return Pair.of("(1 = 1)", new Object[0]);
    }
    List<String> sts = new ArrayList<>();
    List<Object> objects = new ArrayList<>();
    Arrays.stream(children).map(this::gen)
        .forEach(it -> {
          sts.add(it.left());
          for (Object obj : it.right()) {
            objects.add(obj);
          }
        });

    return Pair.of("(" + String.join(" AND ", sts) + ")", objects.toArray(new Object[0]));
  }

}
