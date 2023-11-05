package com.iwamih31.KAKEIBO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Summary {
	private Link title;
	private Link date;
	private List<Link> menu;
	private Table_Data table;
}
