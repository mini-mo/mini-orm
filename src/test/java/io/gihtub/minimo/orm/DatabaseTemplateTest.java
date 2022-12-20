package io.gihtub.minimo.orm;

import io.gihtub.minimo.orm.dsl.MysqlDialectGenerator;
import io.gihtub.minimo.orm.dsl.Sort;
import io.gihtub.minimo.orm.dsl.criteria.Criteria;
import io.gihtub.minimo.orm.executor.JdbcTemplateExecutor;
import io.gihtub.minimo.orm.setter.BaseEnumSetter;
import io.gihtub.minimo.orm.setter.DefaultEnumSetter;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTemplateTest {

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
      sts.execute("create table cars(id int primary key auto_increment, code int not null, brand varchar(20) not null)");
      sts.execute("create table versions(id int primary key, name varchar(20), version int not null)");

      sts.execute("insert into users values(1, 'test', 'vvv', '2022-12-02 11:11:11') ");
      sts.execute("insert into users values(2, 'test-2', 'vvv', '2022-12-02 11:11:11') ");

      sts.execute("insert into books values(1, 'test', '2022-12-02 11:11:11') ");
      sts.execute("insert into books values(2, 'test-2', '2022-12-02 11:11:11') ");

      sts.execute("insert into relations(uid,bid) values(1, 1) ");
      sts.execute("insert into relations(uid,bid) values(1, 2) ");
      sts.execute("insert into relations(uid,bid) values(2, 2) ");

      sts.execute("insert into cars(code, brand) values(1, 'xx') ");
      sts.execute("insert into cars(code, brand) values(2, 'yy') ");

      sts.execute("insert into versions(id, name, version) values(1, 'yy', 1) ");
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
  public void setUp() {
    ds = hds;
    jdbcTemplate = new JdbcTemplate(ds);

    var cfg = DatabaseTemplateConfig.newBuilder()
        .enableBuiltinParameterSetters()
        .enableBuiltinResultTypeMappers()
        .registerResultMapper(new UserNameTypeMapper())
        .registerParameterSetter(new UserNameParameterSetter())
        .enumMapper(AutoEnumResultTypeMapper.class)
        .registerParameterSetter(new DefaultEnumSetter())
        .registerParameterSetter(new BaseEnumSetter())
        .build();

    db = new DatabaseTemplate(new OrmContext(cfg, new MysqlDialectGenerator(), new JdbcTemplateExecutor(jdbcTemplate)));
  }

  @Test
  public void test_native_query_api() {
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
  public void test_query_api() {
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
  public void test_single_table_query_api() {
    var q = db.from(User.class)
        .where(Criteria.column("id").is(1))
        .execute();
    assertTrue(q.exists());

    User one = q.one();
    assertNotNull(one);

    var u2 = db.findById(1, User.class);
    assertNotNull(u2);

    var us = db.findByIds(List.of(1, 2), User.class);
    assertEquals(2, us.size());

  }

  @Test
  public void test_composite_query_api() {
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

  @Test
  public void test_update() {
    var sql = "update users set name = ? where id = ? limit 1";
    int wr = db.createNativeUpdate(sql)
        .bind(1, "test")
        .bind(2, 1)
        .execute();
    assertEquals(1, wr);

    var wr2 = db.createUpdate(sql, "test", 1)
        .execute();

    assertEquals(1, wr2);
  }

  @Test
  public void test_insert() {
    var sql = "insert into users(id, name) values (?, ?)";
    var wr = db.createNativeInsert(sql)
        .bind(1, 3)
        .bind(2, "test-insert")
        .execute();
    assertEquals(1, wr);

    var s2 = "insert into relations(bid, uid) values ( ?, ?)";
    var key = db.createNativeInsert(s2)
        .bind(1, 2)
        .bind(2, 3)
        .executeAndReturnGeneratedKey();

    assertTrue(key > 4);

    var wr2 = db.createInsert(sql, 4, "test-insert-2")
        .execute();
    assertEquals(1, wr2);

    var k2 = db.createInsert(s2, 3, 4)
        .executeAndReturnGeneratedKey();
    assertTrue(k2 > key);
  }

  @Test
  public void test_delete() {
    var s = "delete from relations where id = ?";
    int wr = db.createNativeDelete(s)
        .bind(1, 3)
        .execute();
    assertEquals(1, wr);

    var wr2 = db.createDelete(s, 2)
        .execute();
    assertEquals(1, wr2);
  }

  @Test
  public void test_save() {
    // new one
    var u = new User(5, "test-save");
    boolean wr = db.save(u);
    assertTrue(wr);

    // update exists one
    User u2 = db.findById(5, User.class);
    assertNotNull(u2);

    // return generated id
    var r = new UserBookRelation(10, 10);
    wr = db.save(r);
    assertTrue(wr);
    assertNotNull(r.getId());
  }

  @Test
  public void test_order() {
    var u = db.from(User.class)
        .orderBy(Sort.desc("id"))
        .execute()
        .one();
    assertTrue(u.getId() > 1);
  }

  @Test
  public void test_without_annotation() {
    var b = db.from(Books.class)
        .execute().one();
    assertNotNull(b);

    var obj = new Books();
    obj.setName("test-one");
    obj.setCreatedAt(new Date());
    db.save(obj);
    var id = obj.getId();
    assertNotNull(id);

    var nb = db.findById(id, Books.class);
    var un = "test-update";
    nb.setName(un);
    db.save(nb);
    var nb2 = db.findById(id, Books.class);
    assertEquals(un, nb2.getName());
  }

  @Test
  public void test_count() {
    var cnt = db.from(Books.class).execute().count();
    assertTrue(cnt > 1);
  }

  @Test
  public void test_enum() {
    var list = db.from(Cars.class)
        .execute()
        .list(2);
    assertTrue(list.size() == 2);

    var et = new Cars();
    et.setCode(CarCode.z);
    et.setBrand(Brand.yy);

    db.save(et);
    assertEquals(3, et.getId());

    var neo = db.findById(3, Cars.class);
    assertNotNull(neo);
  }

  @Test
  public void test_version() {
    var v = db.findById(1, Versions.class);
    assertNotNull(v);

    v.setName("hhh");
    db.save(v);

    var v2 = db.findById(1, Versions.class);
    assertEquals(2, v2.getVersion());
    assertEquals("hhh", v2.getName());
  }
}
