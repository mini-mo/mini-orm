package io.gihtub.minimo.orm;

import io.gihtub.minimo.orm.annotations.Id;
import io.gihtub.minimo.orm.annotations.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table("relations")
public class UserBookRelation {

  @Id
  private Integer id;

  private Integer uid;
  private Integer bid;

  public UserBookRelation(Integer uid, Integer bid) {
    this.uid = uid;
    this.bid = bid;
  }
}
