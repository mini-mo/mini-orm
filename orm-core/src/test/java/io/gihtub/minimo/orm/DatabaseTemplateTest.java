package io.gihtub.minimo.orm;

import io.gihtub.minimo.orm.dsl.MysqlDialectGenerator;
import io.gihtub.minimo.orm.executor.JdbcTemplateExecutor;
import io.gihtub.minimo.orm.dsl.criteria.Criteria;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTemplateTest {

  static HikariDataSource hds;

  @BeforeAll
  static public void beforeAll() {
    hds = new HikariDataSource();
    hds.setDriverClassName("org.h2.Driver");
    hds.setJdbcUrl("jdbc:h2:mem:test;MODE=MYSQL;");
    hds.setUsername("sa");
    hds.setPassword("sa");

    // init tables
    try (Connection conn = hds.getConnection()) {
      var sts = conn.createStatement();
      sts.execute("create table users(id int primary key, name varchar(20), nick varchar(20), createdAt timestamp)");
      sts.execute("create table books(id int primary key auto_increment, name varchar(20), createdAt timestamp)");
      sts.execute("create table relations(id int primary key auto_increment, uid int not null, bid int not null)");
      sts.execute("insert into users values(1, 'test', 'vvv', '2022-12-02 11:11:11') ");
      sts.execute("insert into users values(2, 'test-2', 'vvv', '2022-12-02 11:11:11') ");
      sts.execute("insert into books values(1, 'test', '2022-12-02 11:11:11') ");
      sts.execute("insert into books values(2, 'test-2', '2022-12-02 11:11:11') ");

      sts.execute("insert into relations(uid,bid) values(1, 1) ");
      sts.execute("insert into relations(uid,bid) values(1, 2) ");
      sts.execute("insert into relations(uid,bid) values(2, 2) ");

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @AfterAll
  static public void afterAll() {
    hds.close();
  }

  DatabaseTemplate db;
  DataSource ds;

  JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setUp() {
    ds = hds;
    jdbcTemplate = new JdbcTemplate(ds);

    var cfg = DatabaseTemplateConfig.newBuilder()
        .enableBuiltinParameterSetters()
        .enableBuiltinResultTypeMappers()
        .registerResultMapper(new UserNameTypeMapper())
        .registerParameterSetter(new UserNameParameterSetter())
        .build();
    db = new DatabaseTemplate(new OrmContext(cfg, new MysqlDialectGenerator(), new JdbcTemplateExecutor(jdbcTemplate)));
  }

  @Test
  void test_native_query_api() {
    // native query
    Map<String, Object> one = db.nativeQuery("select * from users where name = ?")
        .bind(1, "test")
        .asMap()
        .one();
    assertNotNull(one);
    assertTrue(one.size() > 2);

    List<Map<String, Object>> list = db.nativeQuery("select * from users where name = ? limit 10")
        .bind(1, "test")
        .asMap()
        .list(0, 1);
    assertFalse(list.isEmpty());

    User one_2 = db.nativeQuery("select * from users where name = ?")
        .bind(1, "test")
        .map((rs, rowNum) -> new User(rs.getInt("id"), rs.getString("name")))
        .one();
    assertNotNull(one_2);

    List<User> list_2 = db.nativeQuery("select * from users where name = ?")
        .bind(1, "test")
        .map((rs, rowNum) -> new User(rs.getInt("id"), rs.getString("name")))
        .list();
    assertFalse(list_2.isEmpty());
  }

  @Test
  void test_query_api() {
    // auto binding
    var one = db.createQuery("select * from users where name = ?", new UserName("test"))
        .asMap()
        .one();
    assertNotNull(one);
    assertTrue(one.size() > 2);

    // auto binding
    var one_2 = db.createQuery("select * from users where name = ?", "test")
        .map(User.class)
        .one();
    assertNotNull(one_2);

    var list_2 = db.createQuery("select * from users where name = ?", "test")
        .map(User.class)
        .list();
    assertFalse(list_2.isEmpty());
  }

  @Test
  void test_single_table_query_api() {
    User one = db.from(User.class)
        .where(Criteria.column("id").is(1))
        .execute()
        .one();
    assertNotNull(one);
  }

  @Test
  void test_composite_query_api() {
    // users 用户表
    // books 书
    // relations 用户/书关系 (uid int, bid int)
    // 查询与用户有关的书名
    var sql = "select u.id as id, b.name as name from relations r inner join users u on r.uid = u.id inner join books b on b.id = r.bid";

    // map
    var lm = db.createCompositeQuery(sql)
        .where(Criteria.column("b.name").is("test"))
        .asMap()
        .list(10);
    assertFalse(lm.isEmpty());

    // class
    var ld = db.createCompositeQuery(sql)
        .where(Criteria.column("b.name").is("test"))
        .map(UserBookDTO.class)
        .list(10);
    assertFalse(ld.isEmpty());

    // row mapper
    var ld_2 = db.createCompositeQuery(sql)
        .where(Criteria.column("b.name").is("test"))
        .map((rs, i) -> new UserBookDTO(rs.getInt("id"), rs.getString("name")))
        .list(10);
    assertFalse(ld_2.isEmpty());

  }
}