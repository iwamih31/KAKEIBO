package com.iwamih31.KAKEIBO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "item")
public class Item {

  // ID
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Integer id;

  // typeテーブルのid
  @Column(name = "type_id", nullable = false)
  private Integer type_id;

  // 項目の値
  @Column(name = "name", nullable = true, unique = true)
  private String name;

  // 値の説明
  @Column(name = "note", nullable = true)
  private String note;

}