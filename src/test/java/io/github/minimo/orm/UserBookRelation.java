package io.github.minimo.orm;

import io.github.minimo.orm.annotations.Id;
import io.github.minimo.orm.annotations.Table;

@Table("relations")
public class UserBookRelation {

  @Id
  private Integer id;

  private Integer uid;
  private Integer bid;

  public UserBookRelation() {
  }

  public UserBookRelation(Integer id, Integer uid, Integer bid) {
    this.id = id;
    this.uid = uid;
    this.bid = bid;
  }

  public UserBookRelation(Integer uid, Integer bid) {
    this.uid = uid;
    this.bid = bid;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getUid() {
    return uid;
  }

  public void setUid(Integer uid) {
    this.uid = uid;
  }

  public Integer getBid() {
    return bid;
  }

  public void setBid(Integer bid) {
    this.bid = bid;
  }
}
