package io.gihtub.minimo.orm;

import io.gihtub.minimo.orm.annotations.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table("cars")
public class Cars {

  private Integer id;

  private Brand brand;

  private CarCode code;
}
