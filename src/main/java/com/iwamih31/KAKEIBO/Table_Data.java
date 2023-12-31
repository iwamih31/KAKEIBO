package com.iwamih31.KAKEIBO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Table_Data {
	private Link section;
	private Set[] columns;
	private List<List<String>> data;
}
