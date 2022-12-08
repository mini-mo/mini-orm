package io.gihtub.minimo.orm.dsl;

import io.gihtub.minimo.orm.dsl.criteria.Criteria;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorTest {

  Generator generator = new MysqlDialectGenerator();

  @Test
  void test_0() {
    var p1 = generator.gen(Criteria.column("name").is("test"));
    assertEquals("name = ?", p1.left());
    assertEquals("test", p1.right()[0]);
  }

  @Test
  void test_rewrite_limit() {
    var sql = generator.rewriteLimit("select * from users limit 10;", 1, 2);
    assertTrue(sql.endsWith("LIMIT 1 , 2"));

    sql = generator.rewriteLimit("select * from users  limit  10 , 10 ;", 1, 2);
    assertTrue(sql.endsWith("LIMIT 1 , 2"));
  }
}