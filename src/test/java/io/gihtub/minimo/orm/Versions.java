package io.gihtub.minimo.orm;

import io.gihtub.minimo.orm.annotations.Id;
import io.gihtub.minimo.orm.annotations.Table;
import io.gihtub.minimo.orm.annotations.Version;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table("versions")
public class Versions {

  @Id
  private Integer id;

  private String name;

  @Version
  private Integer version;

}
