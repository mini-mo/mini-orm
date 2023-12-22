# Mini-ORM

另一个轻量级的 ORM，基于 JDBC 的易用性封装。

- 无 XML
- 简化单表操作
- 灵活构建查询条件

## 快速开始

可参考测试用例 [`src/test/java/io/github/minimo/orm/DatabaseTemplateTest.java`](src/test/java/io/github/minimo/orm/DatabaseTemplateTest.java)

可通过 maven 中央仓库引入

<https://central.sonatype.dev/artifact/io.github.mini-mo/mini-orm/0.0.1>

```xml
<dependency>
    <groupId>io.github.mini-mo</groupId>
    <artifactId>mini-orm</artifactId>
    <version>0.0.1</version>
</dependency>
```

## 案例

1. 单表操作

```java

// save
User u = new User();
// set ...
db.save(u);
assertNotNull(u.getId()); // 自动获取自增ID

// findById
User u = db.findById(1, User.class);

// update
u.setName("name-2")    
db.save(u);

// query list
List<User> users = db.from(User.class)
    .where(and(
            column("name").like("test"),
            column("createdAt").range(begin, end)
    )
    .order(by("id").desc()))
    .execute()
    .list(0, 10) // list() | list(10)
```

2. Native Query

```java
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

```

3. 默认查询

```java
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
```

3. 高级查询

```java
// users 用户表
// books 书
// relations 用户/书关系 (uid int, bid int)
// 查询与用户有关的书名
var sql = "select u.id as id, b.name as name from relations r inner join users u on r.uid = u.id inner join books b on b.id = r.bid";

// map
var lm = db.createCompositeQuery(sql)
    .where(column("b.name").is("test"))
    .asMap()
    .list(10);
assertFalse(lm.isEmpty());

// class
var ld = db.createCompositeQuery(sql)
    .where(column("b.name").is("test"))
    .map(UserBookDTO.class)
    .list(10);
assertFalse(ld.isEmpty());

// row mapper
var ld_2 = db.createCompositeQuery(sql)
    .where(column("b.name").is("test"))
    .map((rs, i) -> new UserBookDTO(rs.getInt("id"), rs.getString("name")))
    .list(10);
assertFalse(ld_2.isEmpty());

```

## Ref

- [Jdbi](https://jdbi.org/)
- [MyBatis](https://mybatis.org/mybatis-3/)
- [JdbcTemplate](https://spring.io/projects/spring-data-jdbc)
- [JOOQ](https://www.jooq.org/)
- [设计ORM](https://www.liaoxuefeng.com/wiki/1252599548343744/1282383340896289)
 
