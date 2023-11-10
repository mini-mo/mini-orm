package io.github.minimo.orm;

import java.util.Date;

/**
 * 默认情况下
 * 1. 使用类名的全大写作为数据表名
 * 2. 使用字段名 id 的字段作为主键
 */
public class Books {

  private Integer id;
  private String name;
  private Date createdAt;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }
}
