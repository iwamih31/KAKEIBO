package com.iwamih31.KAKEIBO;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

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
@Table(name = "action")
public class Action {

  // ID
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Integer id;

  // 項目ID
  @Column(name = "item_id", nullable = false)
  private Integer item_id;

  // 詳細
  @Column(name = "detail", nullable = true)
  private String detail;

  // 日付
  @DateTimeFormat(pattern = "yyyy-MM-dd")   // 入力時の期待フォーマット
  @JsonFormat(pattern = "yyyy/MM/dd")   // 出力時の期待フォーマット
  @Column(name = "date", nullable = true)
  private LocalDate date;

  // 入金
  @Column(name = "income", nullable = false)
  private Integer income;

  // 出金
  @Column(name = "spending", nullable = false)
  private Integer spending;

  // 備考
  @Column(name = "note", nullable = true)
  private String note;

}
