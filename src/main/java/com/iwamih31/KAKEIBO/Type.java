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
@Table(name = "type")
public class Type {

  // ID
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Integer id;

  // 項目の値
  @Column(name = "value", nullable = true)
  private String value;

  // 値の説明
  @Column(name = "note", nullable = true)
  private String note;

  // 並び順　0は非表示
  @Column(name = "rank", nullable = false)
  private Integer rank;

}