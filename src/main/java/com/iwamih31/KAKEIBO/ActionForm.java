package com.iwamih31.KAKEIBO;

import java.time.LocalDate;

import lombok.Data;

@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class ActionForm {

  // ID
  private Integer id;

  // 項目ID
  private Integer item_id;

  // 詳細
  private String detail;

  // 日付
  private LocalDate date;

  // 入金
  private Integer income;

  // 出金
  private Integer spending;

  // 備考
  private String note;

}
