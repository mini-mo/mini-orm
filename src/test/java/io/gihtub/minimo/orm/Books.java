package io.gihtub.minimo.orm;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 默认情况下
 * 1. 使用类名的全大写作为数据表名
 * 2. 使用字段名 id 的字段作为主键
 */
@Data
@NoArgsConstructor
public class Books {

  private Integer id;
  private String name;
  private Date createdAt;
}
