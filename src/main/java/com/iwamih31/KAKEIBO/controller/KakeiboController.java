package com.iwamih31.KAKEIBO.controller;

import java.time.LocalDate;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iwamih31.OptionData;
import com.iwamih31.KAKEIBO.Action;
import com.iwamih31.KAKEIBO.Cash;
import com.iwamih31.KAKEIBO.Item;
import com.iwamih31.KAKEIBO.LabelSet;
import com.iwamih31.KAKEIBO.Owner;
import com.iwamih31.KAKEIBO.Plan;
import com.iwamih31.KAKEIBO.Type;
import com.iwamih31.KAKEIBO.service.KakeiboService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@RequestMapping("/KAKEIBO")
public class KakeiboController {

	@Autowired
	private KakeiboService service;
	@Autowired
	private HttpSession session;

	/** RequestMappingのURL */
	public String req() {
		return "/KAKEIBO";
	}

	/** RequestMappingのURL + path */
	public String req(String path) {
		return req() + path;
	}

	/** このクラス内の@GetMapping(req() + path)へredirect */
	public String redirect(String path) {
		return "redirect:" + req() + path;
	}

	/** このクラス内の@GetMapping(req() + path)へforward */
	public String forward(String path) {
		return "forward:" + req() + path;
	}

	@GetMapping("/")
	public String index() {
		return redirect("/Main");
	}

	@GetMapping("/Main")
	public String main(
			Model model) {
		model.addAttribute("title", "メイン画面");
		model.addAttribute("req", req());
		model.addAttribute("app_name", req().replace("/", ""));
		return "main";
	}

	@GetMapping("/Start")
	public String start(
			RedirectAttributes redirectAttributes) {
		redirectAttributes.addAttribute("date", service.this_Year_Month());
		redirectAttributes.addAttribute("section", "実績");
		return redirect("/Summary_Action");
	}

	@PostMapping("/Summary")
	public String summary(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			RedirectAttributes redirectAttributes) {
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		return redirect("/Summary");
	}

	@PostMapping("/Plan")
	public String plan(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			RedirectAttributes redirectAttributes) {
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		return redirect("/Plan");
	}

	@GetMapping("/Summary")
	public String summary(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			Model model) {
		add_View_Data_(model, "summary");
		model.addAttribute("page", service.page("項目別一覧", section, date));
		HashMap<String, Integer> sum_Set = service.sum_Set(service.action_List(date));
		model.addAttribute("sum_income", sum_Set.get("income"));
		model.addAttribute("sum_spending", sum_Set.get("spending"));
		model.addAttribute("total", sum_Set.get("total"));
		int carryover = service.carryover(section, date);
		model.addAttribute("carryover", carryover);
		model.addAttribute("asset", carryover + sum_Set.get("total"));
		model.addAttribute("row_url", "/Item");
		return "view";
	}

	@GetMapping("/Plan")
	public String plan(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			Model model) {
		add_View_Data_(model, "summary");
		model.addAttribute("page", service.page("項目別一覧", section, date));
		HashMap<String, Integer> sum_Set = service.sum_Set_Plan(service.plan_List(date));
		model.addAttribute("sum_income", sum_Set.get("income"));
		model.addAttribute("sum_spending", sum_Set.get("spending"));
		model.addAttribute("total", sum_Set.get("total"));
		int carryover = service.carryover(section, date);
		model.addAttribute("carryover", carryover);
		model.addAttribute("asset", carryover + sum_Set.get("total"));
		model.addAttribute("row_url", "/Item");
		return "view";
	}

	@GetMapping("/Date")
	public String date(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			Model model) {
		add_View_Data_(model, "date");
		model.addAttribute("page", service.page("期間選択", section, date));
		return "view";
	}

	@PostMapping("/Year")
	public String year(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			Model model) {
		add_View_Data_(model, "year");
		model.addAttribute("page", service.page("年度選択", section, date));
		model.addAttribute("url", service.url(section));
		model.addAttribute("options", OptionData.year);
		model.addAttribute("selected_year", service.year(date));
		return "view";
	}

	@PostMapping("/Day")
	public String day(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			Model model) {
		add_View_Data_(model, "day");
		model.addAttribute("page", service.page("日付選択", section, date));
		model.addAttribute("url", service.url(section));
		model.addAttribute("day", service.today());
		return "view";
	}

	@PostMapping("/Month")
	public String month(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			Model model) {
		add_View_Data_(model, "month");
		model.addAttribute("page", service.page("月次選択", section, date));
		model.addAttribute("year_List", OptionData.year);
		model.addAttribute("month_List", OptionData.month);
		model.addAttribute("selected_year", service.year(date));
		model.addAttribute("selected_month", service.month(date));
		return "view";
	}

	@PostMapping("/Summary_Action")
	public String summary_Action(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			RedirectAttributes redirectAttributes) {
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		return redirect("/Summary_Action");
	}

	@GetMapping("/Summary_Action")
	public String summary_Action(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			Model model) {
		add_View_Data_(model, "summary");
		model.addAttribute("page", service.page("データ毎一覧", section, date));
		HashMap<String, Integer> sum_Set = service.sum_Set(service.action_List(date));
		model.addAttribute("sum_income", sum_Set.get("income"));
		model.addAttribute("sum_spending", sum_Set.get("spending"));
		model.addAttribute("total", sum_Set.get("total"));
		int carryover = service.carryover(section, date);
		model.addAttribute("carryover", carryover);
		model.addAttribute("asset", carryover + sum_Set.get("total"));
		model.addAttribute("row_url", "/UpdateAction");
		return "view";
	}

	@PostMapping("/Summary_Plan")
	public String summary_Plan(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			RedirectAttributes redirectAttributes) {
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		return redirect("/Summary_Plan");
	}

	@GetMapping("/Summary_Plan")
	public String summary_Plan(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			Model model) {
		add_View_Data_(model, "summary");
		model.addAttribute("page", service.page("月毎一覧", section, date));
		HashMap<String, Integer> sum_Set = service.sum_Set_Plan(service.plan_List(date));
		model.addAttribute("sum_income", sum_Set.get("income"));
		model.addAttribute("sum_spending", sum_Set.get("spending"));
		model.addAttribute("total", sum_Set.get("total"));
		int carryover = service.carryover(section, date);
		model.addAttribute("carryover", carryover);
		model.addAttribute("asset", carryover + sum_Set.get("total"));
		model.addAttribute("row_url", "/UpdateAction");
		return "view";
	}

	@PostMapping("/Summary_Type")
	public String summary_Type(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			Model model) {
		add_View_Data_(model, "summary");
		model.addAttribute("page", service.page("種別毎一覧", section, date));
		HashMap<String, Integer> sum_Set = service.sum_Set(service.action_List(date));
		model.addAttribute("sum_income", sum_Set.get("income"));
		model.addAttribute("sum_spending", sum_Set.get("spending"));
		model.addAttribute("total", sum_Set.get("total"));
		int carryover = service.carryover(section, date);
		model.addAttribute("carryover", carryover);
		model.addAttribute("asset", carryover + sum_Set.get("total"));
		model.addAttribute("row_url", "/Type");
		return "view";
	}

	@PostMapping("/Plan_Type")
	public String Plan_Type(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			Model model) {
		add_View_Data_(model, "summary");
		model.addAttribute("page", service.page("種別毎一覧", section, date));
		HashMap<String, Integer> sum_Set = service.sum_Set_Plan(service.plan_List(date));
		model.addAttribute("sum_income", sum_Set.get("income"));
		model.addAttribute("sum_spending", sum_Set.get("spending"));
		model.addAttribute("total", sum_Set.get("total"));
		int carryover = service.carryover(section, date);
		model.addAttribute("carryover", carryover);
		model.addAttribute("asset", carryover + sum_Set.get("total"));
		model.addAttribute("row_url", "/Type");
		return "view";
	}

	@PostMapping("/All")
	public String all(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			RedirectAttributes redirectAttributes) {
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		return redirect("/All");
	}

	@GetMapping("/All")
	public String all(
			@Param("date")String date,
			@Param("section")String section,
			Model model) {
		add_View_Data_(model, "summary");
		model.addAttribute("page", service.page("全データ一覧", section, date));
		HashMap<String, Integer> sum_Set = service.sum_Set(service.action_List_All());
		model.addAttribute("sum_income", sum_Set.get("income"));
		model.addAttribute("sum_spending", sum_Set.get("spending"));
		model.addAttribute("total", sum_Set.get("total"));
		model.addAttribute("row_url", "/Action");
		return "view";
	}

	@PostMapping("/Action")
	public String action(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")int id,
			Model model) {
		add_View_Data_(model, "action");
		model.addAttribute("page", service.page("入出金データ", section, date));
		Action action = service.action(id);
		model.addAttribute("action", action);
		String typeName = service.type(action).getName();
		model.addAttribute("type", typeName);
		String itemName = service.item(action).getName();
		model.addAttribute("item", itemName);
		return "view";
	}

	@PostMapping("/Type")
	public String type(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")int type_id,
			Model model) {
		add_View_Data_(model, "type");
		String title = "種別毎内訳";
		model.addAttribute("page", service.page(title, section, date, type_id));
		model.addAttribute("type", service.type(type_id));
		HashMap<String, Integer> sum_Set = service.sum_Set(title, section, date, type_id);
		model.addAttribute("sum_income", sum_Set.get("income"));
		model.addAttribute("sum_spending", sum_Set.get("spending"));
		model.addAttribute("total", sum_Set.get("total"));
		int carryover = service.carryover(title, section, date, type_id);
		model.addAttribute("carryover", carryover);
		model.addAttribute("asset", carryover + sum_Set.get("total"));
		model.addAttribute("row_url", service.row_url(title, section));
		return "view";
	}

	@PostMapping("/Item")
	public String item(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")int item_id,
			Model model) {
		add_View_Data_(model, "item");
		String title = "項目毎内訳";
		model.addAttribute("page", service.page(title, section, date, item_id));
		Item item = service.item(item_id);
		model.addAttribute("type", service.type(item.getType_id()));
		model.addAttribute("item", item);
		HashMap<String, Integer> sum_Set = service.sum_Set(title, section, date, item_id);
		model.addAttribute("sum_income", sum_Set.get("income"));
		model.addAttribute("sum_spending", sum_Set.get("spending"));
		model.addAttribute("total", sum_Set.get("total"));
		int carryover = service.carryover(title, section, date, item_id);
		model.addAttribute("carryover", carryover);
		model.addAttribute("asset", carryover + sum_Set.get("total"));
		model.addAttribute("row_url", service.row_url(title, section));
		return "view";
	}

	@PostMapping("/SelectType")
	public String selectType(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			Model model) {
		add_View_Data_(model, "select");
		model.addAttribute("page", service.page("種別選択", section, date));
		model.addAttribute("url", service.insert_URL(section));
		return "view";
	}

	@PostMapping("/ChangeType")
	public String changeType(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			Model model) {
		add_View_Data_(model, "change");
		model.addAttribute("page", service.page("種別変更", section, date));
		model.addAttribute("url", "/Type");
		return "view";
	}

	@PostMapping("/ChangeItem")
	public String changeItem(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")int type_id,
			Model model) {
		add_View_Data_(model, "changeItem");
		model.addAttribute("page", service.page("項目変更", section, date, type_id));
		model.addAttribute("type", service.type(type_id).getName());
		model.addAttribute("url", "/Item");
		return "view";
	}

	@PostMapping("/SelectItem")
	public String selectItem(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")int type_id,
			Model model) {
		add_View_Data_(model, "select");
		model.addAttribute("page", service.page("項目選択" , section, date, type_id));
		model.addAttribute("url", "/InsertAction");
		return "view";
	}

	@PostMapping("/InsertAction")
	public String insertAction(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")Integer type_id,
			RedirectAttributes redirectAttributes) {
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		redirectAttributes.addAttribute("type_id", type_id);
		return redirect("/InsertAction");
	}

	@PostMapping("/InsertPlan")
	public String insertPlan(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")String type_id,
			RedirectAttributes redirectAttributes) {
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		redirectAttributes.addAttribute("type_id", type_id);
		return redirect("/InsertPlan");
	}

	@GetMapping("/InsertAction")
	public String insertAction(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("type_id")Integer type_id,
			Model model) {
		service.___consoleOut___("date = " + date);
		add_View_Data_(model, "insertAction");
		model.addAttribute("page", service.page("新規入力", section, date));
		model.addAttribute("action", service.new_Action(date));
		model.addAttribute("type", service.type_Name(type_id));
		model.addAttribute("itemList", service.itemList(type_id));
		model.addAttribute("type_id", type_id);
		return "view";
	}

	@GetMapping("/InsertPlan")
	public String insertPlan(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("type_id")Integer type_id,
			Model model) {
		service.___consoleOut___("date = " + date);
		add_View_Data_(model, "insertPlan");
		model.addAttribute("page", service.page("新規入力", section, date));
		model.addAttribute("plan", service.new_Plan(date));
		model.addAttribute("type", service.type_Name(type_id));
		model.addAttribute("itemList", service.itemList(type_id));
		model.addAttribute("type_id", type_id);
		return "view";
	}

	@GetMapping("/To/Plan")
	public String to_Plan(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			RedirectAttributes redirectAttributes) {
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", "予算");
		return redirect("/Plan");
	}

	@GetMapping("/To/Summary")
	public String to_Summary(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			RedirectAttributes redirectAttributes) {
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", "実績");
		return redirect("/Summary");
	}

	@PostMapping("/UpdateAction")
	public String updateAction(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")Integer id,
			RedirectAttributes redirectAttributes) {
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		redirectAttributes.addAttribute("id", id);
		switch (section) {
		case "実績":
			return redirect("/UpdateAction");
		case "予算":
			return redirect("/UpdatePlan");
		default:
			return redirect("/Summary");
		}
	}

	@GetMapping("/UpdateAction")
	public String updateAction(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")Integer id,
			Model model) {
		add_View_Data_(model, "updateAction");
		model.addAttribute("page", service.page("データ修正", section, date));
		Action action = service.action(id);
		model.addAttribute("action", action);
		String typeName = service.type(action).getName();
		model.addAttribute("type", typeName);
		model.addAttribute("itemList", service.itemList(typeName));
		return "view";
	}

	@GetMapping("/UpdatePlan")
	public String updatePlan(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")Integer id,
			Model model) {
		add_View_Data_(model, "updatePlan");
		model.addAttribute("page", service.page("データ修正", section, date));
		Plan plan = service.plan(id);
		model.addAttribute("plan", plan);
		String typeName = service.type(plan).getName();
		model.addAttribute("type", typeName);
		model.addAttribute("itemList", service.itemList(typeName));
		return "view";
	}

	@PostMapping("/UpdateOwner")
	public String updateOwner(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")int id,
			Model model) {
		add_View_Data_(model, "updateOwner");
		model.addAttribute("page", service.page("使用者情報更新", section, date));
		model.addAttribute("object", service.owner(id));
		return "view";
	}

	@PostMapping("/Setting")
	public String setting(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			Model model) {
		add_View_Data_(model, "menu");
		model.addAttribute("page", service.page("各種設定", section, date));
		return "view";
	}

	@PostMapping("/SettingOwner")
	public String settingOwner(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			RedirectAttributes redirectAttributes) {
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		return redirect("/SettingOwner");
	}

	@PostMapping("/SettingType")
	public String settingType(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			RedirectAttributes redirectAttributes) {
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		return redirect("/SettingType");
	}

	@PostMapping("/SettingItem")
	public String settingItem(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("type")String type,
			RedirectAttributes redirectAttributes) {
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		redirectAttributes.addAttribute("type", type);
		return redirect("/SettingItem");
	}

	@GetMapping("/SettingOwner")
	public String settingOwner(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			Model model) {
		add_View_Data_(model, "setting");
		model.addAttribute("page", service.page("使用者設定", section, date));
		model.addAttribute("url", "/UpdateOwner");
		return "view";
	}

	@GetMapping("/SettingType")
	public String settingType(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			Model model) {
		add_View_Data_(model, "setting");
		model.addAttribute("page", service.page("種別設定", section, date));
		model.addAttribute("url", "/UpdateType");
		return "view";
	}

	@GetMapping("/SettingItem")
	public String settingItem(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("type")String type,
			Model model) {
		add_View_Data_(model, "setting");
		int type_id = service.type(type).getId();
		model.addAttribute("page", service.page("項目設定", section, date, type_id));
		model.addAttribute("url", "/UpdateItem");
		return "view";
	}

	@PostMapping("/InsertType")
	public String insertType(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			Model model) {
		add_View_Data_(model, "insertType");
		model.addAttribute("page", service.page("種別登録", section, date));
		model.addAttribute("type", service.new_Type());
		return "view";
	}

	@PostMapping("/InsertItem")
	public String insertItem(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("type")String type,
			Model model) {
		add_View_Data_(model, "insertItem");
		model.addAttribute("page", service.page("項目作成", section, date));
		model.addAttribute("item", service.new_Item(type));
		model.addAttribute("type", type);
		return "view";
	}

	@PostMapping("/OrderType")
	public String orderType(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			RedirectAttributes redirectAttributes) {
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		return redirect("/OrderType");
	}

	@GetMapping("/OrderType")
	public String orderType(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			Model model) {
		add_View_Data_(model, "order");
		model.addAttribute("page", service.page("並び順変更", section, date));
		model.addAttribute("url", "/Order/Type");
		return "view";
	}

	@PostMapping("/UpdateType")
	public String updateType(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")int id,
			Model model) {
		add_View_Data_(model, "updateType");
		model.addAttribute("page", service.page("種別更新", section, date));
		Type type = service.type(id);
		model.addAttribute("object", type);
		model.addAttribute("rank", service.rank(type));
		return "view";
	}

	@PostMapping("/UpdateItem")
	public String updateItem(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")int id,
			Model model) {
		add_View_Data_(model, "updateItem");
		Item item = service.item(id);
		model.addAttribute("page", service.page("項目更新", section, date, id));
		model.addAttribute("object", item);
		model.addAttribute("type", service.type_Name(item));
		return "view";
	}

	@PostMapping("/DeleteType")
	public String deleteType(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")int id,
			Model model) {
		add_View_Data_(model, "deleteType");
		model.addAttribute("page", service.page("種別削除", section, date, id));
		model.addAttribute("id", id);
		model.addAttribute("delete_name", "種別");
		return "view";
	}

	@PostMapping("/DeleteAction")
	public String deleteAction(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")int id,
			Model model) {
		add_View_Data_(model, "delete");
		String delete_name = "データ";
		model.addAttribute("page", service.page(delete_name + "削除", section, date, id));
		model.addAttribute("id", id);
		model.addAttribute("delete_name", delete_name);
		model.addAttribute("target", "Action");
		return "view";
	}

	@PostMapping("/DeleteOwner")
	public String deleteOwner(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")int id,
			Model model) {
		add_View_Data_(model, "delete");
		String delete_name = "使用者情報";
		model.addAttribute("page", service.page(delete_name + "削除", section, date, id));
		model.addAttribute("id", id);
		model.addAttribute("delete_name", delete_name);
		model.addAttribute("target", "Owner");
		return "view";
	}

	@PostMapping("/DeleteItem")
	public String deleteItem(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")int id,
			Model model) {
		add_View_Data_(model, "deleteItem");
		model.addAttribute("page", service.page("項目削除", section, date, id));
		model.addAttribute("id", id);
		model.addAttribute("delete_name", "項目");
		return "view";
	}

	@PostMapping("/Delete/Type")
	public String delete_Type(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")int id,
			RedirectAttributes redirectAttributes) {
		String message = service.delete_Type(id);
		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		return redirect("/SettingType");
	}

	@PostMapping("/Delete/Action")
	public String delete_Action(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")int id,
			RedirectAttributes redirectAttributes) {
		String message = service.delete(id, section);
		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		return redirect("/Summary_Action");
	}

	@PostMapping("/Delete/Owner")
	public String delete_Owner(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")int id,
			RedirectAttributes redirectAttributes) {
		String message = service.delete_Owner(id);
		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		return redirect("/SettingOwner");
	}

	@PostMapping("/Order/Type")
	public String order_Type(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")int id,
			@RequestParam("move")String move,
			RedirectAttributes redirectAttributes) {
		String message = service.order_Type(id, move);
		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		return redirect("/OrderType");
	}

	@PostMapping("/Delete/Item")
	public String delete_Item(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")int id,
			RedirectAttributes redirectAttributes) {
		int type_id = service.item(id).getType_id();
		String message = service.delete_Item(id);
		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		redirectAttributes.addAttribute("type", service.type(type_id).getName());
		return redirect("/SettingItem");
	}

	@PostMapping("/Select/Type")
	public String select_Type(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("id")int id,
			RedirectAttributes redirectAttributes) {
		String message = service.delete_Type(id);
		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addFlashAttribute("date", date);
		redirectAttributes.addFlashAttribute("section", section);
		return redirect("/Type");
	}

	@PostMapping("/Insert/Action")
	public String insert_Action(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@ModelAttribute("action")Action action,
			@RequestParam("type_id")String type_id,
			RedirectAttributes redirectAttributes) {
		String message = service.insert_Action(action);
		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		redirectAttributes.addAttribute("type_id", type_id);
		return redirect("/InsertAction");
	}

	@PostMapping("/Insert/Plan")
	public String insert_Plan(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("type_id")String type_id,
			@ModelAttribute("plan")Plan plan,
			RedirectAttributes redirectAttributes) {
		String message = service.insert_Plan(plan);
		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		redirectAttributes.addAttribute("type_id", type_id);
		return redirect("/InsertPlan");
	}

	@PostMapping("/Insert/Type")
	public String insert_Type(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@ModelAttribute("type")Type type,
			RedirectAttributes redirectAttributes) {
		String message = service.insert_Type(type);
		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		return redirect("/SettingType");
	}

	@PostMapping("/Insert/Owner")
	public String insert_Owner(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@ModelAttribute("owner")Owner owner,
			RedirectAttributes redirectAttributes) {
		String message = service.insert_Owner(owner);
		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		return redirect("/SettingOwner");
	}

	@PostMapping("/Set/Month")
	public String set_Month(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@ModelAttribute("year")String year,
			@ModelAttribute("month")String month,
			RedirectAttributes redirectAttributes) {
		redirectAttributes.addAttribute("date", service.date(year, month));
		redirectAttributes.addAttribute("section", section);
		return redirect(service.url(section));
	}

	@PostMapping("/Update/Type")
	public String update_Type(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@ModelAttribute("type")Type type,
			RedirectAttributes redirectAttributes) {
		String message = service.update_Type(type);
		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		return redirect("/SettingType");
	}

	@PostMapping("/Update/Owner")
	public String update_Owner(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@ModelAttribute("owner")Owner owner,
			RedirectAttributes redirectAttributes) {
		String message = service.update_Owner(owner);
		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		return redirect("/SettingOwner");
	}

	@PostMapping("/Update/Action")
	public String update_Action(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("income")String income,
			@RequestParam("spending")String spending,
			@ModelAttribute("action")Action action,
			RedirectAttributes redirectAttributes) {
		String message = service.update_Action(action, income, spending);
		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		return redirect("/Summary_Action");
	}

	@PostMapping("/Update/Plan")
	public String update_Plan(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@RequestParam("income")String income,
			@RequestParam("spending")String spending,
			@ModelAttribute("plan")Plan plan,
			RedirectAttributes redirectAttributes) {
		String message = service.update_Plan(plan, income, spending);
		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		return redirect("/Summary_Plan");
	}

	@PostMapping("/Update/Item")
	public String update_Item(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@ModelAttribute("item")Item item,
			RedirectAttributes redirectAttributes) {
		String message = service.update_Item(item);
		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		redirectAttributes.addAttribute("id", item.getType_id());
		return redirect("/InsertAction");
	}

	@PostMapping("/Insert/Item")
	public String insert_Item(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			@ModelAttribute("item")Item item,
			RedirectAttributes redirectAttributes) {
		String message = service.insert_Item(item);
		redirectAttributes.addFlashAttribute("message", message);
		redirectAttributes.addAttribute("date", date);
		redirectAttributes.addAttribute("section", section);
		redirectAttributes.addAttribute("id", item.getType_id());
		return redirect(service.insert_URL(section));
	}

	@GetMapping("/List")
	public String list(
			Model model) {
		add_View_Data_(model, "list", "各種一覧");
		return "view";
	}

	@PostMapping("/Owner")
	public String owner(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			Model model) {
		add_View_Data_(model, "owner");
		model.addAttribute("page", service.page("使用者情報", section, date));
		String[] item_Names = service.owner_Item_Names();
		model.addAttribute("name", item_Names[0]);
		model.addAttribute("department", item_Names[1]);
		model.addAttribute("owner_data", service.owner_All());
		return "view";
	}


	@PostMapping("/InsertOwner")
	public String insertOwner(
			@RequestParam("date")String date,
			@RequestParam("section")String section,
			Model model) {
		add_View_Data_(model, "insertOwner");
		model.addAttribute("page", service.page("新規項目追加", section, date));
		model.addAttribute("owner", service.new_Owner());
		return "view";
	}

	@PostMapping("/ActionSubject/Select")
	public String actionSubject_Select(
			@RequestParam("date")String date,
			Model model) {
		add_View_Data_(model, "select", "科目選択");
		model.addAttribute("url", "/ActionSubject/Input");
		model.addAttribute("date", date);
		model.addAttribute("displayed_Date", service.japanese_Date(date));
		model.addAttribute("name", service.name());
		model.addAttribute("guide", "科目を選択して下さい");
		Action action = service.new_Action(date);
		model.addAttribute("object", action);
		model.addAttribute("field", action.getItem_id());
		return "view";
	}

	@PostMapping("/ActionSubject/Input")
	public String actionSubject_Input(
			@RequestParam("post_date")String date,
			@RequestParam("field")String subject,
			@ModelAttribute("object")Action action,
			Model model) {
		add_View_Data_(model, "input", "科目入力");
		model.addAttribute("url", "/ActionApply/Select");
		model.addAttribute("date", date);
		model.addAttribute("displayed_Date", service.japanese_Date(date));
		model.addAttribute("name", service.name());
		model.addAttribute("guide", "科目を入力して下さい");
		model.addAttribute("object", action);
		model.addAttribute("field", subject);
		return "view";
	}

	@PostMapping("/ActionApply/Select")
	public String actionApply_Select(
			@RequestParam("post_date")String date,
			@RequestParam("field")String item,
			@ModelAttribute("object")Action action,
			Model model) {
		add_View_Data_(model, "select", "適用選択");
		model.addAttribute("url", "/ActionApply/Input");
		model.addAttribute("date", date);
		model.addAttribute("displayed_Date", service.japanese_Date(date));
		model.addAttribute("name", service.name());
		model.addAttribute("guide", "適用を選択して下さい");
		model.addAttribute("object", action);
		model.addAttribute("field", item);
		return "view";
	}

	@PostMapping("/ActionApply/Input")
	public String actionApply_Input(
			@RequestParam("post_date")String date,
			@RequestParam("field")String apply,
			@ModelAttribute("object")Action action,
			Model model) {
		add_View_Data_(model, "input", "適用入力");
		model.addAttribute("url", "/ActionAccount/Select");
		model.addAttribute("date", date);
		model.addAttribute("displayed_Date", service.japanese_Date(date));
		model.addAttribute("name", service.name());
		model.addAttribute("guide", "適用を入力して下さい");
		model.addAttribute("object", action);
		model.addAttribute("field", apply);
		return "view";
	}

	@PostMapping("/ActionAccount/Select")
	public String actionAccount_Select(
			@RequestParam("post_date")String date,
			@RequestParam("field")String detail,
			@ModelAttribute("object")Action action,
			Model model) {
		add_View_Data_(model, "select", "収支選択");
		model.addAttribute("url", "/ActionAccount/Input");
		model.addAttribute("date", date);
		model.addAttribute("displayed_Date", service.japanese_Date(date));
		model.addAttribute("name", service.name());
		model.addAttribute("guide", "収支を選択して下さい");
		action.setDetail(detail);
		model.addAttribute("object", action);
		model.addAttribute("field", "");
		model.addAttribute("options", service.accounts());
		return "view";
	}

	@PostMapping("/ActionAccount/Input")
	public String actionAccount_Input(
			@RequestParam("post_date")String date,
			@RequestParam("field")String account,
			@ModelAttribute("object")Action action,
			Model model) {
		add_View_Data_(model, "input", "金額入力");
		model.addAttribute("date", date);
		model.addAttribute("displayed_Date", service.japanese_Date(date));
		model.addAttribute("name", service.name());
		model.addAttribute("guide", account +"金額を入力して下さい");
		model.addAttribute("object", action);
		switch (account) {
			case "収入":
				model.addAttribute("url", "/ActionIncome/Insert");
				model.addAttribute("field", action.getIncome());
				break;
			case "支出":
				model.addAttribute("url", "/ActionSpending/Insert");
				model.addAttribute("field", action.getSpending());
				break;
		}
		return "view";
	}

	@PostMapping("/Owner/Insert")
	public String owner_Insert(
			@RequestParam("post_id")int id,
			@ModelAttribute("owner")Owner owner,
			RedirectAttributes redirectAttributes) {
		String message = service.owner_Insert(owner, id);
		redirectAttributes.addFlashAttribute("message", message);
		return redirect("/OwnerSetting");
	}

	@PostMapping("/ActionIncome/Insert")
	public String actionIncome_Insert(
			@RequestParam("post_date")String date,
			@RequestParam("field")int income,
			@ModelAttribute("object")Action action,
			RedirectAttributes redirectAttributes) {
		action.setIncome(income);
		action.setSpending(0);
		String message = service.insert_Action(action);
		redirectAttributes.addFlashAttribute("message", message);
		return redirect("/Daily?date=" + date);
	}

	@PostMapping("/ActionSpending/Insert")
	public String actionSpending_Insert(
			@RequestParam("post_date")String date,
			@RequestParam("field")int spending,
			@ModelAttribute("object")Action action,
			RedirectAttributes redirectAttributes) {
		action.setIncome(0);
		action.setSpending(spending);
		service.___consoleOut___(action.toString());
		String message = service.insert_Action(action);
		redirectAttributes.addFlashAttribute("message", message);
		return redirect("/Daily?date=" + date);
	}

	@PostMapping("/Owner/Update")
	public String owner_Update(
			@RequestParam("post_id")int id,
			@ModelAttribute("owner")Owner owner,
			RedirectAttributes redirectAttributes) {
		String message = service.owner_Update(owner, id);
		redirectAttributes.addFlashAttribute("message", message);
		return redirect("/OwnerSetting");
	}

	@PostMapping("/OwnerReport")
	public String ownerReport() {
		return redirect("/OwnerReport");
	}

	@GetMapping("/OwnerReport")
	public String ownerReport(
			Model model) {
		add_View_Data_(model, "ownerReport", "使用者情報印刷");
		model.addAttribute("label_Set", LabelSet.ownerReport_Set);
		model.addAttribute("owner_Report", service.owner_Report());
		return "view";
	}

	@PostMapping("/Owner/Output/Excel")
	public String owner_Output_Excel(
			HttpServletResponse httpServletResponse,
			RedirectAttributes redirectAttributes) {
		String message = service.owner_Output_Excel(httpServletResponse);
		redirectAttributes.addFlashAttribute("message", message);
		return redirect("/OwnerReport");
	}

	@PostMapping("/LastMonth")
	public String lastMonth(
			@RequestParam("date")String date,
			RedirectAttributes redirectAttributes) {
		LocalDate lastMonth = service.to_LocalDate(date).minusMonths(1);
		return redirect("/Monthly?date=" + lastMonth);
	}

	@PostMapping("/NextMonth")
	public String nextMonth(
			@RequestParam("date")String date,
			RedirectAttributes redirectAttributes) {
		LocalDate nextMonth = service.to_LocalDate(date).plusMonths(1);
		return redirect("/Monthly?date=" + nextMonth);
	}

	@PostMapping("/Monthly")
	public String monthly(
			@RequestParam("date")String date,
			RedirectAttributes redirectAttributes) {
		return redirect("/Monthly?date=" + date);
	}

	@PostMapping("/Subject")
	public String subject(
			@RequestParam("year")String year,
			@RequestParam("subject")String subject,
			Model model) {
		add_View_Data_(model, "subject", "科目指定");
		if(year  == null) year = service.this_Year();
		model.addAttribute("year", year);
		model.addAttribute("name", service.name());
		model.addAttribute("japanese_year", service.japanese_Date(year, "G y 年"));
		model.addAttribute("guide", "科目を選択して下さい");
		if(subject.equals("全科目")) subject = "";
		model.addAttribute("subject", subject);
		return "view";
	}

	@PostMapping("/SelectYear")
	public String selectYear(
			@RequestParam("year") String year,
			@RequestParam("subject") String subject,
			Model model) {
		add_View_Data_(model, "selectYear", "年度選択");
		model.addAttribute("selected_year", year);
		model.addAttribute("subject", subject);
		model.addAttribute("years", service.years());
		return "view";
	}

	@PostMapping("/Daily/Output/Excel")
	public String daily_Output_Excel(
			@RequestParam("date") String date,
			HttpServletResponse httpServletResponse,
			RedirectAttributes redirectAttributes) {
		String message = service.daily_Output_Excel(date, httpServletResponse);
		redirectAttributes.addFlashAttribute("message", message);
	return redirect("/Daily?date=" + date);
	}

	@PostMapping("/Monthly/Output/Excel")
	public String monthly_Output_Excel(
			@RequestParam("date") String date,
			HttpServletResponse httpServletResponse,
			RedirectAttributes redirectAttributes) {
		String message = service.monthly_Output_Excel(date, httpServletResponse);
		redirectAttributes.addFlashAttribute("message", message);
		return redirect("/Monthly?date=" + date);
	}

	@PostMapping("/Year/Output/Excel")
	public String year_Output_Excel(
			@RequestParam("year") String date,
			@RequestParam("subject")String subject,
			HttpServletResponse httpServletResponse,
			RedirectAttributes redirectAttributes) {
		String message = service.year_Output_Excel(date, subject, httpServletResponse);
		redirectAttributes.addFlashAttribute("message", message);
		return redirect("/Year?date=" + date);
	}

	@PostMapping("/Daily")
	public String daily(
			@RequestParam("date") String date) {
		return redirect("/Daily?date=" + date);
	}

	@GetMapping("/Daily")
	public String daily(
			@Param("date") String date,
			Model model) {
		if (date == null) date = service.today();
		add_View_Data_(model, "daily", "出納帳");
		model.addAttribute("date", date);
		model.addAttribute("japanese_Date", service.japanese_Date(date));
		model.addAttribute("name", service.name());
		model.addAttribute("action_List", service.action_List(date));
		model.addAttribute("account", service.account_Monthly(date));
		model.addAttribute("label_Set_List", LabelSet.daily_Set);
		return "view";
	}

	@PostMapping("/Daily/Date")
	public String daily_Date(
			@RequestParam("date") String date,
			Model model) {
		add_View_Data_(model, "date", "日付選択");
		model.addAttribute("date", date);
		model.addAttribute("label", "日付を選んでください");
		model.addAttribute("url", req("/Daily"));
		return "view";
	}

	@PostMapping("/SelectMonth")
	public String selectMonth(
			@RequestParam("date") String date,
			Model model) {
		add_View_Data_(model, "date", "月選択");
		model.addAttribute("date", date);
		model.addAttribute("label", "日付を選んでください");
		model.addAttribute("url", req("/Monthly"));
		return "view";
	}

	@PostMapping("/DailyCash")
	public String daily_Cash(
			@RequestParam("date") String date,
			Model model) {
		add_View_Data_(model, "cash", "現金残高入力");
		model.addAttribute("date", date);
		model.addAttribute("japanese_Date", service.japanese_Date(date));
		model.addAttribute("cash", service.cash(date));
		model.addAttribute("label_Set_List", LabelSet.cash_Set);
		return "view";
	}

	@PostMapping("/DailyCashResult")
	public String dailyCashResult(
			@RequestParam("post_date") String date,
			@ModelAttribute("cash") Cash cash,
			Model model) {
		add_View_Data_(model, "cashResult", "現金残高確認");
		model.addAttribute("date", date);
		model.addAttribute("japanese_Date", service.japanese_Date(date));
		model.addAttribute("total", service.cash_Total(cash));
		model.addAttribute("balance", service.cash_Balance(date, cash));
		model.addAttribute("url", req("/DailyCash/Update"));
		model.addAttribute("label_Set_List", LabelSet.cash_Set);
		return "view";
	}

	@PostMapping("/DailyCash/Update")
	public String dailyCash_Update(
			@RequestParam("post_date") String date,
			@ModelAttribute("cash") Cash cash,
			RedirectAttributes redirectAttributes) {
			String message = service.cash_Update(cash);
			redirectAttributes.addFlashAttribute("message", message);
		return redirect("/Daily?date=" + date);
	}

	/** view 表示に必要な属性データをモデルに登録 */
	private void add_View_Data_(Model model, String template, String title) {
		model.addAttribute("library", template + "::library");
		model.addAttribute("main", template + "::main");
		model.addAttribute("title", title);
		model.addAttribute("req", req());
		System.out.println("template = " + template);
	}

	/** view 表示に必要な属性データをモデルに登録 */
	private void add_View_Data_(Model model, String template) {
		model.addAttribute("library", template + "::library");
		model.addAttribute("main", template + "::main");
		model.addAttribute("req", req());
		System.out.println("template = " + template);
	}
}
