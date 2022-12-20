package io.gihtub.minimo.orm;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JdbcTest {

  @BeforeAll
  static void beforeAll() {
    try (var conn = getConnection()) {
      var st = conn.createStatement();
      var cr = st.execute("create table users (id int primary key auto_increment, name varchar(20), nick varchar(20), createdAt timestamp )");
      assertFalse(cr); // true if the first result is a ResultSet object; false if it is an update count or there are no results
      assertEquals(0, st.getUpdateCount());
      assertNull(st.getResultSet());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @BeforeEach
  void setup() {
  }

  @AfterEach
  void tearDown() {
    try (var conn = getConnection()) {
      var cr = conn.createStatement()
          .execute("truncate table users;");
      assertFalse(cr); // true if the first result is a ResultSet object; false if it is an update count or there are no results
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void test_raw_jdbc() throws SQLException {
    try (var conn = getConnection()) {
      var rs = conn.createStatement()
          .executeQuery("select 1");
      assertTrue(rs.next());
      assertEquals(1, rs.getInt(1));
    }
  }

  @Test
  void test_insert_one() throws Exception {
    try (var conn = getConnection()) {
      var stmt = conn.createStatement();
      var ur = stmt.execute("insert into users(id, name) values(1, 'test')");
      assertFalse(ur);
      assertEquals(1, stmt.getUpdateCount());
      assertNull(stmt.getResultSet());

      // auto increment
      var stmt2 = conn.createStatement();
      stmt2.execute("insert into users(name) values ('test1');", Statement.RETURN_GENERATED_KEYS);
      var generatedKeys = stmt2.getGeneratedKeys();
      assertNotNull(generatedKeys);
      generatedKeys.next();
      assertEquals(2, generatedKeys.getInt(1));

      // prepare statement
      var pst = conn.prepareStatement("insert into users(name) values (?);", Statement.RETURN_GENERATED_KEYS);
      pst.setString(1, "test2");
      pst.execute();
      var gkrs = pst.getGeneratedKeys();
      assertNotNull(gkrs);
      assertTrue(gkrs.next());
      assertEquals(3, gkrs.getInt(1));
    }
  }

  @Test
  void test_update_one() throws Exception {
    initUsers();

    var conn = getConnection();
    try (conn) {
      var st = conn.createStatement();
      st.execute("update users set name = 'uuu' where id = 1 ");
      assertEquals(1, st.getUpdateCount());

      var st2 = conn.createStatement();
      st2.execute("update users set name = 'xxx' limit 10 ");
      assertEquals(10, st2.getUpdateCount());
    }
  }

  @Test
  void test_select() throws Exception {
    initUsers();
    try (var conn = getConnection()) {
      var sql = "select * from users limit 10";
      var st = conn.createStatement();
      var cr = st.execute(sql);
      assertTrue(cr);

      var rs = st.getResultSet();
      while (rs.next()) {
        var u = new User(rs.getInt("id"), rs.getString("name"));
        System.out.println(u);
      }
    }
  }

  @Test
  void test_select_with_prepare() throws Exception {
    initUsers();

    try (var conn = getConnection()) {
      var sql = "select * from users where id = ? limit 1";
      var pst = conn.prepareStatement(sql);
      pst.setInt(1, 1);

      var rs = pst.executeQuery();
      assertTrue(rs.next());
      assertEquals(1, rs.getInt("id"));
    }
  }

  @Test
  void test_table_meta() throws Exception {
    try (var conn = getConnection()) {
      var metaData = conn.getMetaData();

      var crs = metaData.getColumns("TEST", null, "USERS", null);
      List<String> columns = new ArrayList<>();
      while (crs.next()) {
        columns.add(crs.getString("COLUMN_NAME") + "_" + crs.getString("TYPE_NAME"));
      }
      assertTrue(columns.stream().anyMatch(it -> it.startsWith("ID_")));
      assertTrue(columns.stream().anyMatch(it -> it.startsWith("NAME_")));
      assertEquals("H2", metaData.getDatabaseProductName());
    }
  }

  // init users
  private void initUsers() throws Exception {
    for (int i = 0; i < 10; i++) {
      execute("insert into users(name) values('test-" + i + "')");
    }
  }

  private void execute(String sql) throws SQLException {
    try (var conn = getConnection()) {
      conn.createStatement().execute(sql);
    }
  }

  private static Connection getConnection() {
    try {
      // MODE=MYSQL mysql 兼容模式
      // DB_CLOSE_DELAY 内存模式下，JVM 内数据保持  <https://www.h2database.com/html/features.html#in_memory_databases>
      return DriverManager.getConnection("jdbc:h2:mem:test;MODE=MYSQL;DB_CLOSE_DELAY=-1", "sa", "");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}