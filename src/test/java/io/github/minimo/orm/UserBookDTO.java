package io.github.minimo.orm;

public class UserBookDTO {
    private Integer id;
    private String name;

  public UserBookDTO() {
  }

  public UserBookDTO(Integer id, String name) {
    this.id = id;
    this.name = name;
  }

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
}
