package io.github.minimo.orm;

import io.github.minimo.orm.annotations.Table;

@Table("cars")
public class Cars {

  private Integer id;

  private Brand brand;

  private CarCode code;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Brand getBrand() {
    return brand;
  }

  public void setBrand(Brand brand) {
    this.brand = brand;
  }

  public CarCode getCode() {
    return code;
  }

  public void setCode(CarCode code) {
    this.code = code;
  }
}
