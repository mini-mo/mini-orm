package io.github.minimo.orm;

import io.github.minimo.orm.annotations.Id;
import io.github.minimo.orm.annotations.Table;
import io.github.minimo.orm.annotations.Version;

@Table("versions")
public class Versions {

  @Id
  private Integer id;

  private String name;

  @Version
  private Integer version;


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

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }
}
