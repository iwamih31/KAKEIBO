package com.iwamih31.KAKEIBO;

public class LabelSet {

	public static Set[] summary_Set = {
			set("種別",	 	10),
			set("項目",	 	25),
			set("入金",	 	 7),
			set("出金",		 7),
	};

	public static Set[] type_Set = {
			set("項目",	 	25),
			set("日付",	 	10),
			set("詳細",	 	10),
			set("入金",	 	 7),
			set("出金",		 7),
	};

	public static Set[] settingType_Set = {
			set("種別",	 	7),
			set("内容",	 	30),
			set("並び順",	 	5),
			set("",	 	5),
	};

	public static Set[] insertType_Set = {
			set("種別",	 	7),
			set("内容",	 	25),
	};

	public static Set[] daily_Set = {
			set("科目",	 	10),
			set("適用",	 	25),
			set("収入",	 	 7),
			set("支出",		 7),
			set("残高",		 7),
	};

	public static Set[] daily_Output_Set = {
			set("科目",	 	10),
			set("適用",	 	25),
			set("収入",	 	 7),
			set("支出",		 7),
			set("残高",		 7),
	};

	public static Set[] monthly_Output_Set = {
			set("日付",		 7),
			set("科目",	 	10),
			set("適用",	 	15),
			set("収入",	 	 7),
			set("支出",		 7),
			set("残高",		 7),
	};

	public static Set[] year_Output_Set = {
			set("日付",	 	 7),
			set("科目",	 	10),
			set("適用",	 	15),
			set("収入",	 	 7),
			set("支出",		 7),
			set("残高",		 7),
	};

	public static Set[] action_List_Set = {
			set("日付",		 8),
			set("科目",		10),
			set("適用",		20),
			set("収入",	     8),
			set("支出",		 8),
			set("残高",		 8),
	};

	public static Set[] insertAction_Set = {
			set("日付",	 	10),
			set("種別",	 	 7),
			set("項目",	 	 7),
			set("詳細",	 	10),
			set("入金",	 	 7),
			set("出金",	 	 7),
			set("備考",	 	25),
	};

	public static Set[] actionUpdate_Set = {
			set("ID",		 4),
			set("日付",		 6),
			set("科目",		 6),
			set("適用",		20),
			set("収入",	  	 5),
			set("支出",		 5),
	};

	public static Set[] actionDelete_Set = {
			set("ID",		 4),
			set("日付",		 6),
			set("科目",		 6),
			set("適用",		20),
			set("収入",	  	 6),
			set("支出",		 6),
	};

	public static Set[] ownerReport_Set = {
			set("ID",		 6),
			set("項目",		 4),
			set("内容",		 8),
	};
	public static Set[] cash_Set = {
			set("金種",		10),
			set("枚数",		10),
	};

	public static Set[] updateType_Set = {
			set("ID",	 	5),
			set("種別",	 	7),
			set("内容",	 	25),
			set("並び順",	 	5),
	};

	public static Set[] deleteType_Set = {
			set("種別",	 	7),
			set("内容",	 	25),
			set("並び順",	 	5),
	};


	public static Set set(String label_Name, int width) {
		return new Set(label_Name, width);
	}
}
