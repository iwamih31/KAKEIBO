package com.iwamih31.KAKEIBO.service;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.JapaneseChronology;
import java.time.chrono.JapaneseDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iwamih31.OptionData;
import com.iwamih31.KAKEIBO.Action;
import com.iwamih31.KAKEIBO.ActionRepository;
import com.iwamih31.KAKEIBO.Cash;
import com.iwamih31.KAKEIBO.CashRepository;
import com.iwamih31.KAKEIBO.Excel;
import com.iwamih31.KAKEIBO.Item;
import com.iwamih31.KAKEIBO.ItemRepository;
import com.iwamih31.KAKEIBO.LabelSet;
import com.iwamih31.KAKEIBO.Link;
import com.iwamih31.KAKEIBO.Owner;
import com.iwamih31.KAKEIBO.OwnerRepository;
import com.iwamih31.KAKEIBO.Page;
import com.iwamih31.KAKEIBO.Plan;
import com.iwamih31.KAKEIBO.PlanRepository;
import com.iwamih31.KAKEIBO.Set;
import com.iwamih31.KAKEIBO.State;
import com.iwamih31.KAKEIBO.Table_Data;
import com.iwamih31.KAKEIBO.Type;
import com.iwamih31.KAKEIBO.TypeRepository;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class KakeiboService {

	@Autowired
	private ActionRepository actionRepository;
	@Autowired
	private OwnerRepository ownerRepository;
	@Autowired
	private CashRepository cashRepository;
	@Autowired
	private TypeRepository typeRepository;
	@Autowired
	private ItemRepository itemRepository;
	@Autowired
	private PlanRepository planRepository;

	public String name() {
		List<String> name = ownerRepository.item_value("所有者名");
		if (name.isEmpty()) {
			Owner owner_Item = new Owner(null, "所有者名", "My家計簿");
			ownerRepository.save(owner_Item);
		}
		return ownerRepository.item_value("所有者名").get(0);
	}

	public Action action(int id) {
		return actionRepository.getReferenceById(id);
	}

	public Owner owner(int id) {
		return ownerRepository.getReferenceById(id);
	}

	public List<Owner> owner_All() {
		if (next_Owner_Id() == 1)
			set_Owner();
		return ownerRepository.findAll();
	}

	public String owner_Insert(Owner owner_Item, int id) {
		owner_Item.setId(id);
		String message = "ID = " + owner_Item.getId() + " の所有者データ登録";
		try {
			ownerRepository.save(owner_Item);
			message += "が完了しました";
		} catch (Exception e) {
			message += "が正常に行われませんでした";
			e.printStackTrace();
		}
		return message;
	}

	public String insert_Action(Action action) {
		int id = next_Action_Id();
		action.setId(id);
		if(action.getIncome() == null) action.setIncome(0);
		if(action.getSpending() == null) action.setSpending(0);
		___consoleOut___("action = " + action);
		String message = date(action.getThe_day()) + action.getDetail() + " の実績データを登録";
		try {
			actionRepository.save(action);
			message += " しました";
		} catch (Exception e) {
			message += "出来ませんでした";
			e.printStackTrace();
		}
		return message;
	}

	public String insert_Plan(Plan plan) {
		int id = next_Action_Id();
		plan.setId(id);
		if(plan.getIncome() == null) plan.setIncome(0);
		if(plan.getSpending() == null) plan.setSpending(0);
		___consoleOut___("plan = " + plan);
		String year_Month = year_Month(plan.getThe_day());
		String item_Name = item(plan.getItem_id()).getName();

		String message = year_Month + item_Name + " の予算データを登録";
		try {
			planRepository.save(plan);
			message += " しました";
		} catch (Exception e) {
			message += "出来ませんでした";
			e.printStackTrace();
		}
		return message;
	}

	public String insert_Type(Type type) {
		if(exist_Type_Name(type.getName())) {
			return type.getName() + " は既に存在します";
		}
		int id = next_Type_Id();
		type.setId(id);
		String message = type.getName() + " を登録";
		try {
			typeRepository.save(type);
			message += " しました";
		} catch (Exception e) {
			message += "できませんでした";
			e.printStackTrace();
		}
		return message;
	}

	/** 新規項目作成 */
	public String insert_Item(Item item) {
		if (item.getName().equals(""))  item.setName("その他");;
		if(exist_Item_Name(item.getType_id(), item.getName())) {
			return item.getName() + " は既に存在します";
		}
		int id = next_Item_Id();
		item.setId(id);
		String message = item.getName() + " の登録";
		try {
			itemRepository.save(item);
			message += " が完了しました";
		} catch (Exception e) {
			message += "ができませんでした";
			e.printStackTrace();
		}
		return message;
	}

	/** 項目名の存在確認 */
	private boolean exist_Item_Name(Integer type_id, String item_name) {
		if(itemRepository.getID(type_id, item_name) == null) return false;
		return true;
	}

	/** 種別名の存在確認 */
	private boolean exist_Type_Name(String type_name) {
		if(typeRepository.getID(type_name) == null) return false;
		return true;
	}

	public String owner_Update(Owner owner, int id) {
		owner.setId(id);
		String message = "ID = " + owner.getId() + " の所有者データ更新";
		try {
			ownerRepository.save(owner);
			message += "が完了しました";
		} catch (Exception e) {
			message += "が正常に行われませんでした";
			e.printStackTrace();
		}
		return message;
	}

	public String update_Action(Action action, String income, String spending) {
		if (income.equals("")) action.setIncome(0);
		else action.setIncome(Integer.valueOf(income));
		if (spending.equals("")) action.setSpending(0);
		else action.setSpending(Integer.valueOf(spending));
		String message = action.getDetail() + " データを更新";
		try {
			actionRepository.save(action);
			message += "しました";
		} catch (Exception e) {
			message += "することがきませんでした";
			e.printStackTrace();
		}
		return message;
	}

	public String update_Type(Type type) {
		String message = type.getName() + " の更新";
		try {
			typeRepository.save(type);
			message += "が完了しました";
		} catch (Exception e) {
			message += "が正常に行われませんでした";
			e.printStackTrace();
		}
		return message;
	}

	public String update_Item(Item item) {
		String message = item.getName() + " の更新";
		try {
			itemRepository.save(item);
			message += "が完了しました";
		} catch (Exception e) {
			message += "が正常に行われませんでした";
			e.printStackTrace();
		}
		return message;
	}

	public String delete_Action(int id) {
		Action action = action(id);
		String message = action.getDetail() + " データを削除";
		try {
			actionRepository.deleteById(id);
			message += "しました";
		} catch (Exception e) {
			e.printStackTrace();
			message += "できませんでした" + e.getMessage();
		}
		___consoleOut___(message);
		return message;
	}

	public String delete_Type(int id) {
		String type = typeRepository.type(id);
		String message = type + " を削除";
		try {
			typeRepository.deleteById(id);
			sort_Type();
			message += "しました";
		} catch (Exception e) {
			e.printStackTrace();
			message += "できませんでした" + e.getMessage();
		}
		___consoleOut___(message);
		return message;
	}

	public String order_Type(int type_id, String key) {
		Type type = type(type_id);
		String message = type.getName() + "を";
		double rank = type.getRank();
		switch (key) {
		case "up":
			message += "1つ上";
			rank -= 1.5;
			break;
		case "down":
			message += "1つ下";
			rank += 1.5;
			break;
		case "use":
			if (rank == 0) {
				message += "表示";
				rank = max_Rank() + 1;
			} else {
				message += "非表示";
				rank = 0;
			}
			break;
		default:
			break;
		}
		try {
			type.setRank(rank);
			typeRepository.save(type);
			sort_Type();
			message += "に変更しました";
		} catch (Exception e) {
			e.printStackTrace();
			message += "に変更出来ませんでした" + e.getMessage();
		}
		___consoleOut___(message);
		return message;
	}

	/* Type の rank を1から順番になるように揃える */
	private void sort_Type() {
		List<Type> type_List = typeRepository.list();
		for (int i = 0; i < type_List.size(); i++) {
			Type type = type_List.get(i);
			type.setRank((double) (i + 1));
			typeRepository.save(type);
		}
	}

	public String delete_Item(int id) {
		String item = itemRepository.item(id);
		String message = item + " を削除";
		try {
			itemRepository.deleteById(id);
			message += "しました";
		} catch (Exception e) {
			message += "できませんでした" + e.getMessage();
		}
		___consoleOut___(message);
		return message;
	}

	public String[] owner_Item_Names() {
		String[] item_Names = { "所有者名", "部署名" };
		return item_Names;
	}

	public List<Owner> owner_Report() {
		return ownerRepository.findAll();
	}

	public Owner new_Owner() {
		Owner new_Owner = new Owner(next_Owner_Id(), "", "");
		if (new_Owner.getId() == 1)
			set_Owner();
		return new Owner(next_Owner_Id(), "", "");
	}

	public Action new_Action() {
		return new Action(next_Action_Id(), 0,  "", null, 0, 0, "");
	}

	public Type new_Type() {
		return new Type(next_Type_Id(), "", "", max_Rank() + 1);
	}

	public Item new_Item(String type_Name) {
		int type_Id = typeRepository.getID(type_Name);
		___consoleOut___("type_id = " + type_Id);
		return new Item(next_Item_Id(), type_Id, "", "");
	}

	private double max_Rank() {
		double max_Rank = 0;
		Type lastElement = getLastElement(typeRepository.list());
		if (lastElement != null)
			max_Rank = lastElement.getRank();
		___consoleOut___("max_Rank() = " + max_Rank);
		return max_Rank;
	}

	public Action new_Action(String date) {
		return new Action(next_Action_Id(), 0, "", to_LocalDate(date), 0, 0, "");
	}

	public Plan new_Plan(String date) {
		return new Plan(next_Action_Id(), 0, to_LocalDate(date), 0, 0, "");
	}

	public int next_Owner_Id() {
		int nextId = 1;
		Owner lastElement = getLastElement(ownerRepository.findAll());
		if (lastElement != null)
			nextId = lastElement.getId() + 1;
		___consoleOut___("next_Owner_Id() = " + nextId);
		return nextId;
	}

	public int next_Action_Id() {
		int nextId = 1;
		Action lastElement = getLastElement(actionRepository.findAll());
		if (lastElement != null)
			nextId = lastElement.getId() + 1;
		___consoleOut___("next_Action_Id() = " + nextId);
		return nextId;
	}

	public int next_Type_Id() {
		int next_Id = 1;
		Type lastElement = getLastElement(typeRepository.findAll());
		if (lastElement != null)
			next_Id = lastElement.getId() + 1;
		___consoleOut___("next_Type_Id() = " + next_Id);
		return next_Id;
	}

	public int next_Item_Id() {
		int next_Id = 1;
		Item lastElement = getLastElement(itemRepository.findAll());
		if (lastElement != null)
			next_Id = lastElement.getId() + 1;
		___consoleOut___("next_Item_Id() = " + next_Id);
		return next_Id;
	}

	public int next_Cash_Id() {
		int nextId = 1;
		Action lastElement = getLastElement(actionRepository.findAll());
		if (lastElement != null)
			nextId = lastElement.getId() + 1;
		___consoleOut___("next_Cash_Id = " + nextId);
		return nextId;
	}

	private void set_Owner() {
		String[] item_Names = owner_Item_Names();
		ownerRepository.save(new Owner(1, item_Names[0], ""));
		ownerRepository.save(new Owner(2, item_Names[1], ""));
	}

	/** 所有者データをExcelファイルとして出力 */
	public String owner_Output_Excel(HttpServletResponse response) {
		Excel excel = new Excel();
		String message = null;
		String[] column_Names = Set.get_Name_Set(LabelSet.ownerReport_Set);
		int[] column_Width = Set.get_Value_Set(LabelSet.ownerReport_Set);
		List<Owner> owner_Report = owner_Report();
		String[][] table_Data = new String[owner_Report.size()][];
		for (int i = 0; i < table_Data.length; i++) {
			Owner owner = owner_Report.get(i);
			table_Data[i] = new String[] {
					String.valueOf(owner.getId()),
					String.valueOf(owner.getItem_name()),
					String.valueOf(owner.getItem_value())
			};
		}
		message = excel.output_Excel("所有者", column_Names, column_Width, table_Data, response);
		return message;
	}

	private List<String[]> head_Rows_Daily(String date) {
		String da = japanese_Date(date);
		String co = owner_item_value("所有者名");
		String[][] head_Rows = {
				{ "", co, "", "", ""},
				{ "", "", "", "", ""},
				{ "", "", "", "", da}
		};
		return to_List(head_Rows);
	}

	private List<String[]> head_Rows_Monthly(String date) {
		String da = japanese_Date(date, "Gy年M月分");
		String co = owner_item_value("所有者名");
		String[][] head_Rows = {
				{ da, "", "", "", co, ""},
				{ "", "", "", "", "", ""}
		};
		return to_List(head_Rows);
	}

	private List<String[]> head_Rows_Year(String date, String subject) {
		String da = japanese_Date(date, "Gy年度分");
		String su = subject;
		String co = owner_item_value("所有者名");
		String[][] head_Rows = {
				{ da, "", su, "", co, ""},
				{ "", "", "", "", "", ""}
		};
		return to_List(head_Rows);
	}

	private List<String[]> foot_Rows_Daily() {
		String re = "【領収証添付】";
		String[][] head_Rows = {
				{ "", "", "", "", ""},
				{ re, "", "", "", ""}
		};
		return to_List(head_Rows);
	}

	/** Excelシート（実績）作成用値データ */
	public String[][] make_Sheet(
			List<String[]> head_Rows,
			String[] labels,
			List<String[]> data_Rows,
			List<String[]> foot_Rows) {
		// 列追加用リスト作成
		List<String[]> sheet_Rows = new ArrayList<>();
		// ヘッド部分追加
		if (head_Rows != null) sheet_Rows.addAll(head_Rows);
		// ラベル行追加
		if (labels != null) sheet_Rows.add(labels);
		// データ行追加
		if (data_Rows != null) sheet_Rows.addAll(data_Rows);
		// フッター部分追加
		if (foot_Rows != null) sheet_Rows.addAll(foot_Rows);
		return to_Array(sheet_Rows);
	}

	private String new_Year(String date) {
		LocalDate new_Year = to_LocalDate(date).withDayOfYear(1);
		return new_Year.toString();
	}

	private String[] data_Row_Daily_carryover(int carryover) {
		return new String[] {"", "前日繰越", "", "", make_String(carryover)};
	}

	private String[] data_Row_carryover(int carryover, String label) {
		return new String[] {"", "", label, "", "", make_String(carryover)};
	}

	private String[] 	data_Row_Daily(Action action) {
		return new String[] {
				make_String(action.getItem_id()),
				make_String(action.getDetail()),
				make_String(Zero_Blank(action.getIncome())),
				make_String(Zero_Blank(action.getSpending())),
				make_String("")
			};
	}

	private String[] 	data_Row_Monthly(Action action, Integer remainder) {
		return new String[] {
				japanese_Date(action.getThe_day().toString(), "M月d日"),
				make_String(action.getItem_id()),
				make_String(action.getDetail()),
				make_String(Zero_Blank(action.getIncome())),
				make_String(Zero_Blank(action.getSpending())),
				make_String(remainder)
		};
	}

	private String[] 	data_Row_Year(Action action, Integer remainder) {
		return new String[] {
				japanese_Date(action.getThe_day().toString(), "Gy/M/d"),
				make_String(action.getItem_id()),
				make_String(action.getDetail()),
				make_String(Zero_Blank(action.getIncome())),
				make_String(Zero_Blank(action.getSpending())),
				make_String(remainder)
		};
	}

	private String[] data_Row_total(Integer income_today, Integer spending_today, Integer remainder) {
		return new String[] {
				"",
				"計",
				make_String(income_today),
				make_String(spending_today),
				make_String(remainder)
			};
	}

	private String[] data_Row_total_amount(Integer income_cumulative, Integer spending_cumulative) {
		return new String[] {
				"",
				"",
				"合計",
				make_String(income_cumulative),
				make_String(spending_cumulative),
				""
		};
	}

	private String[] data_Row_cumulative(Integer income_cumulative, Integer spending_cumulative) {
		return new String[] {"", "累計", make_String(income_cumulative), make_String(spending_cumulative), ""};
	}

	private String[] data_Row_carried_forward(Integer remainder, String label) {
		return new String[] {"", "", label, "", "", make_String(remainder)};
	}

	private Object Zero_Blank(int number) {
		if (number == 0) return "";
		return number;
	}

	private String make_String(Object object) {
		String make_String = "";
		if (object != null)
			make_String = String.valueOf(object);
		return make_String;
	}

	private String[][] to_Array(List<String[]> list) {
		// Listから2次配列へ変換
		String[][] array = new String[list.size()][];
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	private List<String[]> to_List(String[][] array) {
		// 2次配列からListへ変換
		List<String[]> list = new ArrayList<>();
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}

	private String owner_item_value(String item_name) {
		String value = "";
		List<String> item_value = ownerRepository.item_value(item_name);
		if (item_value.size() > 0)
			value = ownerRepository.item_value(item_name).get(0);
		return value;
	}

	/** List の最後の Element を返すジェネリックメソッド */
	public <E> E getLastElement(List<E> list) {
		E lastElement = null;
		if (list.size() > 0) {
			int lastIdx = list.size() - 1;
			lastElement = list.get(lastIdx);
		}
		return lastElement;
	}

	/** コンソールに String を出力 */
	public void ___consoleOut___(String message) {
		System.out.println("*");
		System.out.println(message);
		System.out.println("");
	}

	// 今日の日付の文字列を取得
	public String today() {
		// 今日の日付を取得
		LocalDate now = LocalDate.now();
		// 表示形式を指定
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return dateTimeFormatter.format(now);
	}

	// 和暦に変換
	public String japanese_Date(String date, String format_Pattern) {
		Integer[] split_Date = split_Date(date);
		JapaneseDate japaneseDate = JapaneseDate.of(split_Date[0], split_Date[1], split_Date[2]);
		// 表示形式を指定
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format_Pattern);
		return dateTimeFormatter.format(japaneseDate);
	}

	// 和暦に変換
	public String japanese_Date(String date) {
		return japanese_Date(date, "G y 年 M 月 d 日");
	}

	// 和暦に変換（年）
	public String japanese_Year(String date) {
		return japanese_Date(date, "G y 年");
	}

	// 和暦に変換（年 月）
	public String japanese_Year_Month(String date) {
		return japanese_Date(date, "G y 年 M 月");
	}

	// 和暦に変換
	public String japanese_Date(LocalDate localDate) {
		return japanese_Date(localDate.toString());
	}

	// 和暦の文字列をLocalDateに変換
	public LocalDate to_LocalDate(String japanese_Date, String format_Pattern) {

    // DateTimeFormatterオブジェクトを生成し、和暦のパターンを設定する
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format_Pattern, Locale.JAPAN)
            .withChronology(JapaneseChronology.INSTANCE)
            .withResolverStyle(ResolverStyle.SMART);

    // 和暦の文字列を JapaneseDate に変換する
    JapaneseDate wareki = JapaneseDate.from(formatter.parse(japanese_Date));

    // JapaneseDateからLocalDateに変換する
    return LocalDate.from(wareki);
}

	public State state(int id, String name, String date) {
		return new State(id, name, to_LocalDate(date));
	}

	public LocalDate to_LocalDate(String date) {
		Integer[] split_Date = split_Date(date);
		int year = split_Date[0];
		int month = split_Date[1];
		int day = split_Date[2];
		return to_LocalDate(year, month, day);
	}

	private LocalDate to_LocalDate(int year, int month, int day) {
		LocalDate localDate = LocalDate.of(year, month, day);
		___consoleOut___("localDate = " + localDate);
		return localDate;
	}

	private Integer[] split_Date(String date) {
		Integer[] split_Date = null;
		LocalDate localDate = LocalDate.now();
		if (date != null) {
			int year = localDate.getYear();
			int month = localDate.getMonthValue();
			int day = 1;
			date = date.trim();
			date = date.split(" ")[0];
			date = date.split("T")[0];
			date = date.replace("年", "-");
			date = date.replace("月", "-");
			date = date.replace("日", "-");
			date = date.replace("/", "-");
			String[] array = date.split("-");
			if (array.length > 0 && is_Int(array[0]))
				year = Integer.parseInt(array[0]);
			if (array.length == 1)
				month = 1;
			if (array.length > 1)
				month = Integer.parseInt(array[1]);
			if (array.length > 2)
				day = Integer.parseInt(array[2]);
			split_Date = new Integer[] { year, month, day };
		}
		return split_Date;
	}

	private boolean is_Int(String string) {
		boolean is_Int = true;
		try {
			___consoleOut___("String = " + Integer.parseInt(string) + " は Integer に変換出来ます");
		} catch (Exception e) {
			___consoleOut___("string = " + string + " は Integer に変換出来ません");
			is_Int = false;
		}
		return is_Int;
	}

	public OptionData options() {
		return new OptionData();
	}

	public String[] accounts() {
		return new String[] {"支出", "収入"};
	}

	public Cash cash(String date) {
		Cash cash;
		LocalDate localDate = to_LocalDate(date);
		___consoleOut___("localDate = " + localDate);
		if(cashRepository.cash(localDate).size() < 1) {
			int id = next_Cash_Id();
			cashRepository.save(new Cash(id, localDate, 0, 0, 0, 0, 0, 0, 0, 0, 0));
		}
		cash = cashRepository.cash(localDate).get(0);
		return cash;
	}

	public int cash_Total(Cash cash) {
		int total = 0;
		total += cash.getMan1() * 10000;
		total += cash.getSen5() * 5000;
		total += cash.getSen1() * 1000;
		total += cash.getHyaku5() * 500;
	  total += cash.getHyaku1() * 100;
	  total += cash.getJyuu5() * 50;
	  total += cash.getJyuu1() * 10;
	  total += cash.getEn5() * 5;
	  total += cash.getEn1() * 1;
		return total;
	}

	public String cash_Update(Cash cash) {
		String message = cash.getDate() + " の現金残高更新";
		try {
			cashRepository.save(cash);
			message += "が完了しました";
		} catch (Exception e) {
			message += "が正常に行われませんでした";
			e.printStackTrace();
		}
		return message;
	}

	public String this_Year_Month() {
		// 今日の日付を取得
		LocalDateTime now = LocalDateTime.now();
		// 表示形式を指定
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
		return dateTimeFormatter.format(now);
	}

	public String year_Month(LocalDate localDate) {
		// 表示形式を指定
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM");
		return dateTimeFormatter.format(localDate);
	}

	public String this_Year() {
		// 今日の日付を取得
		LocalDateTime now = LocalDateTime.now();
		// 表示形式を指定
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy");
		return dateTimeFormatter.format(now);
	}

	public String this_Month() {
		// 今日の日付を取得
		LocalDateTime now = LocalDateTime.now();
		// 表示形式を指定
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM");
		return dateTimeFormatter.format(now);
	}

	public List<Integer> income_List(List<Action> action_List) {
		int income = 0;
		List<Integer> income_List = new ArrayList<>();
		for (Action action : action_List) {
			income += action.getIncome();
			income_List.add(income);
		}
		return income_List;
	}

	public List<Integer> spending_List(List<Action> action_List) {
		int spending = 0;
		List<Integer> spending_List = new ArrayList<>();
		for (Action action : action_List) {
			spending += action.getSpending();
			spending_List.add(spending);
		}
		return spending_List;
	}

	public Object years() {
		int size = 100;
		LocalDate localDate = LocalDate.now();
		localDate = localDate.minusYears(size);
		List<String> years = new ArrayList<>();
		for (int i = 0; i <= size; i++) {
			years.add(japanese_Year(localDate.plusYears(i).toString()));
		}
		return years;
	}

	public int year(String date) {
		LocalDate localDate = to_LocalDate(date);
		Integer year = Integer.valueOf(localDate.getYear());
		___consoleOut___("year = " + year);
		return year;
	}

	public int month(String date) {
		LocalDate localDate = to_LocalDate(date);
		Integer month = Integer.valueOf(localDate.getMonthValue());
		___consoleOut___("month = " + month);
		return month;
	}

	public String year(String year, String format_Pattern) {
		___consoleOut___("year = " + year + "format_Pattern = " + format_Pattern );
		LocalDate localDate = to_LocalDate(year, format_Pattern);
		return String.valueOf(localDate.getYear());
	}

	public void setItem_id(Action action, String type, String item) {
		int type_id = typeRepository.getID(type);
		action.setItem_id(itemRepository.getID(type_id,item));
	}

	private String default_Type() {
		String default_Type = typeRepository.default_Type();
		if (default_Type == null) default_Type = "種別がありません";
		return default_Type;
	}

	private Link section_Link(String section) {
		String link = "/Result";
		switch (section) {
		case "実績":
			link = "/To/Plan";
			break;
		case "予算":
			link = "/To/Summary";
			break;
		default:
			link = "/";
			break;
		}
		return new Link(section, link);
	}

	/** Tableのデータ行作成 */
	private List<List<String>> data(String title, String section, String date) {
		List<List<String>> data = new ArrayList<>();
		switch (title) {
		case "項目別一覧":
			if (section.equals("実績")) {
				data = data_Action_List_Item(date);
			}
			if (section.equals("予算")) {
				data = data_Plan_List_Item(date);
			}
			break;
		case "データ毎一覧":
			if (section.equals("実績")) {
				data = data_Action_List(date);
			}
			if (section.equals("予算")) {
				List<Plan> plan_List = plan_List(date);
			}
			break;
		case "種別毎一覧":
			if (section.equals("実績")) {
				data = data_Type_List(date);
			}
			if (section.equals("予算")) {
				data = data_Plan_List_Type(date);
			}
			break;
		case "全データ一覧":
			if (section.equals("実績")) {
				data = data_Action_All();
			}
			if (section.equals("予算")) {
				data = data_Plan_All();
			}
			break;
		case "種別選択":
			data = data_Type_All();
			break;
		case "種別設定":
			data = data_Type_All();
			break;
		case "種別削除":
			data = data_Type(section);
			break;
		case "並び順変更":
			data = data_Type_All();
			break;
		default:
			data = data_Action_All();
			break;
		}
		return data;
	}

	private List<List<String>> data_Action_List_Item(String date) {
		List<List<String>> data = new ArrayList<>();
		for (Type type : typeList()) {
			String current_Type_Value = "0";
			String type_Value = type.getName();
			List<Item> itemList = itemList(type.getId());
			for (Item item : itemList) {
				if(current_Type_Value.equals(type.getName())) {
					type_Value = "0";
				} else {
					current_Type_Value = type.getName();
				}
				List<Action> actionList = action_List_Item(item.getId(), date);
				int income = 0;
				int spending = 0;
				for (Action action : actionList) {
					income += action.getIncome();
					spending += action.getSpending();
				}
				List<String> list = new ArrayList<>();
				add(list, type.getId());
				add(list, type_Value);
				add(list, item.getName());
				add(list, item.getNote());
				add(list, income);
				add(list, spending);
				add(list, income - spending);
				data.add(list);
			}
		}
		return data;
	}

	private List<List<String>> data_Plan_List_Item(String date) {
		List<List<String>> data = new ArrayList<>();
		String current_Type_Value = "0";
		for (Type type : typeList()) {
			String type_Value = type.getName();
			List<Item> itemList = itemList(type.getId());
			for (Item item : itemList) {
				if(current_Type_Value.equals(type.getName())) {
					type_Value = "0";
				} else {
					current_Type_Value = type.getName();
				}
				List<Plan> planList = plan_List_Item(item.getId(), date);
				int income = 0;
				int spending = 0;
				for (Plan plan : planList) {
					income += plan.getIncome();
					spending += plan.getSpending();
				}
				List<String> list = new ArrayList<>();
				add(list, type.getId());
				add(list, type_Value);
				add(list, item.getName());
				add(list, item.getNote());
				add(list, income);
				add(list, spending);
				add(list, income - spending);
				data.add(list);
			}

		}
		return data;
	}

	private List<List<String>> data_Action_All() {
		List<List<String>> data = new ArrayList<>();
		for (Action action : action_List_All()) {
			Item item = item(action.getItem_id());
			Type type = type(item.getType_id());
			List<String> list = new ArrayList<>();
			add(list, action.getId());
			add(list, date(action.getThe_day()));
			add(list, type.getName());
			add(list, item.getName());
			add(list, action.getDetail());
			add(list, action.getIncome());
			add(list, action.getSpending());
			add(list, action.getNote());
			data.add(list);
		}
		return data;
	}

	private List<List<String>> data_Plan_All() {
		List<List<String>> data = new ArrayList<>();
		for (Plan plan : plan_List_All()) {
			Item item = item(plan.getItem_id());
			Type type = type(item.getType_id());
			List<String> list = new ArrayList<>();
			add(list, plan.getId());
			add(list, date(plan.getThe_day()));
			add(list, type.getName());
			add(list, item.getName());
			add(list, item.getNote());
			add(list, plan.getIncome());
			add(list, plan.getSpending());
			add(list, plan.getNote());
			data.add(list);
		}
		return data;
	}

	private List<List<String>> data_Action_List(String date) {
		List<List<String>> data = new ArrayList<>();
		for (Action action : action_List(date)) {
			Item item = item(action.getItem_id());
			Type type = type(item.getType_id());
			List<String> list = new ArrayList<>();
			add(list, action.getId());
			add(list, date(action.getThe_day()));
			add(list, type.getName());
			add(list, item.getName());
			add(list, action.getDetail());
			add(list, action.getIncome());
			add(list, action.getSpending());
			add(list, action.getNote());
			___consoleOut___(list);
			data.add(list);
		}
		return data;
	}

	private List<List<String>> data_Plan_List_Type(String date) {
		List<List<String>> data = new ArrayList<>();
		for (Type type : typeList()) {
			int income = 0;
			int spending = 0;
			for (Item item : itemList(type.getId())) {
				int item_id = item.getId();
				for (Plan plan : plan_List_Item(item_id, date)) {
					income += plan.getIncome();
					spending += plan.getSpending();
				}
			}
			List<String> list = new ArrayList<>();
			add(list, type.getId());
			add(list, type.getName());
			add(list, type.getNote());
			add(list, income);
			add(list, spending);
			add(list, income + spending);
			___consoleOut___(list);
			data.add(list);
		}
		return data;
	}

	private List<List<String>> data_Type_List(String date) {
		List<List<String>> data = new ArrayList<>();
		for (Type type : typeList()) {
			int income = 0;
			int spending = 0;
			for (Item item : itemList(type.getId())) {
				int item_id = item.getId();
				for (Action action : action_List_Item(item_id, date)) {
					income += action.getIncome();
					spending += action.getSpending();
				}
			}
			List<String> list = new ArrayList<>();
			add(list, type.getId());
			add(list, type.getName());
			add(list, type.getNote());
			add(list, income);
			add(list, spending);
			add(list, income + spending);
			___consoleOut___(list);
			data.add(list);
		}
		return data;
	}

	private List<List<String>> data_Action(int type_id, String date) {
		List<List<String>> data = new ArrayList<>();
		Type type = type(type_id);
		List<Item> itemList = itemList(type.getId());
		for (Item item : itemList) {
			String item_Value = item.getName();
			List<Action> actionList = action_List_Item(item.getId(), date);
			for (Action action : actionList) {
				int id = action.getId();
				int income = action.getIncome();
				int spending = action.getSpending();
				String the_day = date(action.getThe_day());
				String detail = action.getDetail();
				List<String> list = new ArrayList<>();
				add(list, id);
				add(list, the_day);
				add(list, item_Value);
				add(list, detail);
				add(list, income);
				add(list, spending);
				add(list, income - spending);
				___consoleOut___(list);
				data.add(list);
			}
		}
		return data;
	}

    private void ___consoleOut___(List<String> text_List) {
    	for (String text : text_List) {
			___consoleOut___(text);
		}
	}

	public static String num3(int number) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.JAPAN);
        return "￥" + formatter.format(number);
    }

	private String date(LocalDate localDate) {
		return localDate.toString().replace("-", "/");
	}

	public String date(String date) {
		return date.replace("-", "/");
	}

	public String date(String year, String month) {
		return year + "-" + month;
	}

	/** Tableのデータ行作成（id 使用） */
	private List<List<String>> data(String title, String section, String date, int id) {
		List<List<String>> data = new ArrayList<>();
		switch (title) {
		case "項目選択":
			data = data_Item_List(id);
			break;
		case "項目設定":
			data = data_Item_List(id);
			break;
		case "項目更新":
			data = data_Item_List(id);
			break;
		case "項目削除":
			data = data_Item(id);
			break;
		case "データ削除":
			data = data_Action(id);
			break;
		case "種別毎内訳":
			data = data_Action(id, date);
			break;
		default:
			break;
		}
		return data;
	}

	private List<List<String>> data_Type_All() {
		List<List<String>> data = new ArrayList<>();
		for (Type type : typeAll()) {
			List<String> list = new ArrayList<>();
			add(list, type.getId());
			add(list, type.getName());
			add(list, type.getNote());
			int rank = (int)type.getRank();
			if (rank == 0) {
				add(list, "非表示");
			} else {
				add(list, (int)type.getRank());
			}
			data.add(list);
		}
		return data;
	}

	private List<List<String>> data_Item_List(int type_Id) {
		List<List<String>> data = new ArrayList<>();
		for (Item item : itemList(type_Id)) {
			List<String> list = new ArrayList<>();
			add(list, item.getId());
			add(list, type(type_Id).getName());
			add(list, item.getName());
			add(list, item.getNote());
			data.add(list);
		}
		return data;
	}

	private List<List<String>> data_Item(int item_Id) {
		List<List<String>> data = new ArrayList<>();
		Item item = item(item_Id);
		List<String> list = new ArrayList<>();
		add(list, item.getId());
		add(list, type(item.getType_id()).getName());
		add(list, item.getName());
		add(list, item.getNote());
		data.add(list);
		return data;
	}

	private List<List<String>> data_Action(int action_Id) {
		List<List<String>> data = new ArrayList<>();
		Action action = action(action_Id);
		List<String> list = new ArrayList<>();
		add(list, action.getId());
		add(list, date(action.getThe_day()));
		Item item = item(action.getItem_id());
		add(list, type(item.getType_id()).getName());
		add(list, item.getName());
		add(list, action.getDetail());
		add(list, to_Space(action.getIncome()));
		add(list, to_Space(action.getSpending()));
		add(list, action.getNote());
		data.add(list);
		return data;
	}

	private String to_Space(int number) {
		if (number == 0) return "";
		return String.valueOf(number);
	}

	private List<List<String>> data_Type(String name) {
		List<List<String>> data = new ArrayList<>();
		Type type = type(name);
		List<String> list = new ArrayList<>();
		add(list, type.getId());
		add(list, type.getName());
		add(list, type.getNote());
		add(list, type.getRank());
		data.add(list);
		return data;
	}

	private void add(List<String> list, Object object) {
		list.add(make_String(object));
	}

	public List<Item> itemList(Integer type_id) {
		return itemRepository.list(type_id);
	}

	public List<Item> itemList(String type_Name) {
		Integer type_id = type(type_Name).getId();
		return itemRepository.list(type_id);
	}

	private List<Type> typeList() {
		return typeRepository.list();
	}

	private List<Type> typeAll() {
		return typeRepository.asc_Rank();
	}

	public Table_Data table(String title, String section, String date) {
		section = null_Section(title, section);
		Link section_Link = section_Link(section);
		Set[] columns = columns(title, section);
		List<List<String>> data = data(title, section, date);
		return new Table_Data(section_Link, columns, data);
	}

	public Table_Data table(String title, String date, int id) {
		String section = "";
		switch (title) {
		case "種別毎内訳":
			section = type_Name(id);
			break;
		default:
			break;
		}
		Link section_Link = section_Link(section );
		Set[] columns = columns(title, section);
		List<List<String>> data = data(title, section, date, id);
		return new Table_Data(section_Link, columns, data);
	}

	private String null_Section(String title, String section) {
		if (section == null) {
			switch (title) {
			case "項目別一覧":
				return "実績";
			case "種別毎内訳":
				return default_Type();
			case "種別設定":
				return "種別設定";
			case "新規入力":
				return "実績";
			case "種別登録":
				return "種別登録";
			case "項目作成":
				return "実績";
			default:
				return "実績";
			}
		}
		return section;
	}

	public Page page(String title,String section, String date) {
		___consoleOut___("title = " + title);
		___consoleOut___("section = " + section);
		___consoleOut___("date = " + date);
		Link title_Link = title_Link(title);
		if (date == null) date = this_Year();
		Link date_Link = date_Link(date);
		List<Link> menu = menu(title, section);
		Table_Data table= table(title, section, date);
		return new Page(title_Link, date_Link, menu, table);
	}

	public Page page(String title,String section, String date, int id) {
		___consoleOut___("title = " + title);
		___consoleOut___("section = " + section);
		___consoleOut___("date = " + date);
		Link title_Link = title_Link(title);
		if (date == null) date = this_Year_Month();
		Link date_Link = date_Link(date);
		List<Link> menu = menu(title, section);
		Table_Data table= table(title, date, id);
		return new Page(title_Link, date_Link, menu, table);
	}

	private Set[] columns(String title, String section) {
		switch (title) {
		case "項目別一覧":
			return LabelSet.summary_Set;
		case "種別毎一覧":
			return LabelSet.summary_Type_Set;
		case "種別毎内訳":
			return LabelSet.type_Set;
		case "全データ一覧":
		case "データ毎一覧":
		case "データ修正":
		case "データ削除":
		case "入出金データ":
			return LabelSet.action_Set;
		case "新規入力":
			if (section.equals("実績")) return LabelSet.action_Set;
			if (section.equals("予算")) return LabelSet.plan_Set;
		case "種別選択":
			return LabelSet.selectType_Set;
		case "種別設定":
			return LabelSet.settingType_Set;
		case "項目設定":
			return LabelSet.settingItem_Set;
		case "種別登録":
			return LabelSet.insertType_Set;
		case "項目作成":
			return LabelSet.insertItem_Set;
		case "種別更新":
			return LabelSet.updateType_Set;
		case "項目更新":
			return LabelSet.updateItem_Set;
		case "種別削除":
			return LabelSet.deleteType_Set;
		case "項目削除":
			return LabelSet.deleteItem_Set;
		case "並び順変更":
			return LabelSet.orderType_Set;
		default:
			return new Set[]{};
		}
	}

	public List<Link> menu(String title, String section) {
		List<Link> menu = new ArrayList<>();
		switch (title) {
		case "項目別一覧":
			if (section.equals("実績")) {
				menu.add(new Link("Excel出力", "/Output/Excel"));
				menu.add(new Link("種別毎", "/Summary_Type"));
				menu.add(new Link("データ毎", "/Summary_Action"));
			}
			if (section.equals("予算")) {
				menu.add(new Link("Excel出力", "/Output/Excel"));
				menu.add(new Link("種別毎", "/Plan_Type"));
			}
			break;
		case "データ毎一覧":
			menu.add(new Link("Excel出力", "/Output/Excel"));
			menu.add(new Link("種別毎", "/Summary_Type"));
			menu.add(new Link("項目毎", "/Summary"));
			break;
		case "種別毎一覧":
			if (section.equals("実績")) {
				menu.add(new Link("Excel出力", "/Output/Excel"));
				menu.add(new Link("項目毎", "/Summary"));
				menu.add(new Link("データ毎", "/Summary_Action"));
			}
			if (section.equals("予算")) {
				menu.add(new Link("Excel出力", "/Output/Excel"));
				menu.add(new Link("項目毎", "/Summary"));
			}
			break;
		case "種別毎内訳":
			menu.add(new Link("Excel出力", "/Output/Excel"));
			menu.add(new Link("全種別", "/All"));
			break;
		case "新規入力":
			menu.add(new Link("種別選択", "/SelectType"));
			break;
		case "各種設定":
			menu.add(new Link("所有者設定", "/OwnerSetting"));
			menu.add(new Link("種別設定", "/SettingType"));
			break;
		case "種別設定":
			menu.add(new Link("種別登録", "/InsertType"));
			menu.add(new Link("並べ替え", "/OrderType"));
			break;
		case "種別選択":
			menu.add(new Link("種別登録", "/InsertType"));
			break;
		case "項目選択":
			menu.add(new Link("項目登録", "/InsertItem"));
			break;
		case "期間選択":
			menu.add(new Link("年間", "/Year"));
			menu.add(new Link("月間", "/Month"));
			menu.add(new Link("日別", "/Day"));
			break;
		default:
			break;
		}
		return menu;
	}

	private Link date_Link(String date) {
		return link(date, "/Date");
	}

	private Link title_Link(String title) {
		switch (title) {
		case "項目別一覧":
			return new Link(title, "/Type");
		case "種別毎内訳":
			return new Link(title, "/Summary");
		case "種別設定":
			return new Link(title, "/SettingType");
		case "種別登録":
			return new Link(title, "/SettingType");
		default:
			return new Link(title, "/");
		}
	}

	private Link link(String text, String url) {
		return new Link(text, url);
	}

	public Link title(String text) {
		return new Link(text, "/View");
	}

	public List<Action> monthly_List(String date, int i) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public Object carryover(String date) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public List<Action> year_List(String year, int i, String subject) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String daily_Output_Excel(String date, HttpServletResponse httpServletResponse) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String monthly_Output_Excel(String date, HttpServletResponse httpServletResponse) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public String year_Output_Excel(String date, String subject, HttpServletResponse httpServletResponse) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public List<Action> action_List(String date) {
		if (date == null) date = this_Year();
		return actionRepository.list(date);
	}

	public List<Plan> plan_List(String date) {
		if (date == null) date = this_Year();
		return planRepository.list(date);
	}

	public List<Action> action_List_All() {
		return actionRepository.all();
	}

	public List<Plan> plan_List_All() {
		return planRepository.all();
	}

public List<Action> action_List_Item(int item_id, String date) {
	if (date == null) date = this_Year();
	return actionRepository.list(item_id, date);
}

public List<Plan> plan_List_Item(int item_id, String date) {
	if (date == null) date = this_Year();
	return planRepository.list(item_id, date);
}

	public List<Action> action_List_Type(int type_id, String date) {
		List<Action> action_List = new ArrayList<>();
		if (date == null) date = this_Year_Month();
		List<Item> item_List = itemRepository.list(type_id);
		for (Item item : item_List) {
			action_List.addAll(action_List_Item(item.getId(), date));
		}
		return action_List;
	}

	public Object account_Monthly(String date) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public Object cash_Balance(String date, Cash cash) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public HashMap<String, Integer> sum_Set(List<Action> action_List) {
		HashMap<String, Integer> sum_Set = new HashMap<String, Integer>();
		int sum_income = 0;
		int sum_spending = 0;
		for (Action action : action_List) {
			sum_income += action.getIncome();
			sum_spending += action.getSpending();
		}
		sum_Set.put("income",sum_income );
		sum_Set.put("spending", sum_spending);
		sum_Set.put("total", sum_income - sum_spending);
		return sum_Set;
	}

	public HashMap<String, Integer> sum_Set_Plan(List<Plan> plan_List) {
		HashMap<String, Integer> sum_Set = new HashMap<String, Integer>();
		int sum_income = 0;
		int sum_spending = 0;
		for (Plan plan : plan_List) {
			sum_income += plan.getIncome();
			sum_spending += plan.getSpending();
		}
		sum_Set.put("income",sum_income );
		sum_Set.put("spending", sum_spending);
		sum_Set.put("total", sum_income - sum_spending);
		return sum_Set;
	}

	public Type type(int id) {
		return typeRepository.getReferenceById(id);
	}

	public Item item(int id) {
		return itemRepository.getReferenceById(id);
	}

	public Type type(String name) {
		return typeRepository.type(name);
	}

	public String type_Name(int type_id) {
		return typeRepository.type(type_id);
	}

	public String type_Name(Item item) {
		return type_Name(item.getType_id());
	}

	/* rank を int にして返す */
	public int rank(Type type) {
		return (int)type.getRank();
	}

	public Type type(Action action) {
		Item item = item(action.getItem_id());
		return type(item.getType_id());
	}

	public Item item(Action action) {
		return item(action.getItem_id());
	}

	public String insert_URL(String section) {
		switch (section) {
		case "実績":
			return "/InsertAction";
		case "予算":
			return "/InsertPlan";
		default:
			return "/Start";
		}
	}

	public String date_URL(String section) {
		switch (section) {
		case "実績":
			return "/Summary";
		case "予算":
			return "/Plan";
		default:
			return "/Start";
		}
	}

}