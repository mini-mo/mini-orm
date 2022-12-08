# Jdbc statement vs prepareStatement

```java

// java.sql.Connection.createStatement()
Statement createStatement() throws SQLException;

// java.sql.Connection.prepareStatement(java.lang.String)
PreparedStatement prepareStatement(String sql) throws SQLException;

// java.sql.Connection.prepareCall(java.lang.String)
CallableStatement prepareCall(String sql) throws SQLException;

```

java.sql.Connection 提供了三种创建 statement 的方式，prepareCall 与存储过程有关，实际应用中可以考虑忽略。

那么 createStatement 与 prepareStatement 这两种方式，业务开发中应该如何选择。

1. 需要防止 sql 注入时使用 prepareStatement.
2. prepareStatement 再数据库服务端可能有缓存相关的优化，频繁使用的带参数的语句优先使用 prepareStatement.
3. 一次性的, 无参的 sql 优先考虑 createStatement.

