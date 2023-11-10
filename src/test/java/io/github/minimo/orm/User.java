package io.github.minimo.orm;

import io.github.minimo.orm.annotations.Id;
import io.github.minimo.orm.annotations.Table;

import java.util.Date;

@Table("users")
public class User {

  @Id
  private Integer id;

  private UserName name;
  private String nick;

  private Date createdAt;

  public User() {
  }

  public User(Integer id, String name) {
    this.id = id;
    this.name = new UserName(name);
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public UserName getName() {
    return name;
  }

  public void setName(UserName name) {
    this.name = name;
  }

  public String getNick() {
    return nick;
  }

  public void setNick(String nick) {
    this.nick = nick;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }
}
