package io.github.minimo.orm;

public enum CarCode implements BaseEnum {

  z(1),
  y(2),
  ;

  private final int code;

  CarCode(int code) {
    this.code = code;
  }

  @Override
  public int code() {
    return code;
  }
}
