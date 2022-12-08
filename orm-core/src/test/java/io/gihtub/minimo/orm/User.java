package io.gihtub.minimo.orm;

import io.gihtub.minimo.orm.annotations.Id;
import io.gihtub.minimo.orm.annotations.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@Table("users")
public class User {

  @Id
  private Integer id;

  private UserName name;
  private String nick;

  private Date createdAt;

  public User(Integer id, String name) {
    this.id = id;
    this.name = new UserName(name);
  }
}
