package com.iwamih31.KAKEIBO.controller;

import java.time.LocalDate;
import java.util.List;

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

import com.iwamih31.KAKEIBO.Action;
import com.iwamih31.KAKEIBO.Cash;
import com.iwamih31.KAKEIBO.LabelSet;
import com.iwamih31.KAKEIBO.Owner;
import com.iwamih31.KAKEIBO.service.KakeiboService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@RequestMapping("/Kakeibo")
public class KakeiboController {

	@Autowired
	private KakeiboService service;
	@Autowired
	private HttpSession session;

	private String type = "";//////////////////// 仮

	/** RequestMappingのURL */
	public String req() {
		return "/Kakeibo";
	}

	/** RequestMappingのURL + path */
	public String req(String path) {
		return req() + path;
	}

	/** このクラスの@GetMapping(req() + path)にredirect */
	public String redirect(String path) {
		return "redirect:" + req() + path;
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
		return "main";
	}

	@GetMapping("/Start")
	public String start(
			Model model) {
		model.addAttribute("date", service.today());
		model.addAttribute("section", "実績");
		return redirect("/Summary");
	}

	@GetMapping("/Summary")
	public String summary(
			@Param("date")String date,
			@Param("section")String section,
			Model model) {
		String view = "summary";
		add_View_Data_(model, view, "項目別一覧");
		model.addAttribute("date", date);
		model.addAttribute("page", service.page(view, section, date));
		return "main";
	}

	@GetMapping("/Type")
	public String type(
			@Param("date")String date,
			@Param("section")String section,
			Model model) {
		add_View_Data_(model, "type", "種別毎内訳");
		model.addAttribute("date", date);
		model.addAttribute("page", service.type(section, date));
		return "main";
	}



	@GetMapping("/Setting")
	public String setting(
			Model model) {
		add_View_Data_(model, "setting", "各種設定");
		return "view";
	}

	@GetMapping("/List")
	public String list(
			Model model) {
		add_View_Data_(model, "list", "各種一覧");
		return "view";
	}

	@GetMapping("/Owner")
	public String owner(
			@Param("date")String date,
			Model model) {
		add_View_Data_(model, "owner", "所有者情報");
		String[] item_Names = service.owner_Item_Names();
		model.addAttribute("name", item_Names[0]);
		model.addAttribute("department", item_Names[1]);
		model.addAttribute("owner_data", service.owner_All());
		return "view";
	}

	@GetMapping("/OwnerSetting")
	public String ownerSetting(
			Model model) {
		add_View_Data_(model, "ownerSetting", "所有者設定");
		model.addAttribute("ownerList", service.owner_Report());
		return "view";
	}

	@GetMapping("/OwnerInsert")
	public String ownerInsert(
			Model model) {
		add_View_Data_(model, "ownerInsert", "新規項目追加");
		model.addAttribute("owner", service.new_Owner());
		model.addAttribute("next_id", service.next_Owner_Id());
		return "view";
	}

	@PostMapping("/ActionInsert")
	public String actionInsert(
			@RequestParam("date")String date,
			Model model) {
		add_View_Data_(model, "actionInsert", "新規出納追加");
		model.addAttribute("date", date);
		model.addAttribute("japanese_Date", service.japanese_Date(date));
		model.addAttribute("name", service.name());
		model.addAttribute("action", service.new_Action(date));
		model.addAttribute("next_id", service.next_Action_Id());
		model.addAttribute("label_Set_List", LabelSet.actionInsert_Set);
		model.addAttribute("subjects", service.subjects());
		return "view";
	}

	@PostMapping("/ActionInput")
	public String actionInput(
			@RequestParam("post_date")String date,
			@ModelAttribute("action")Action action,
			Model model) {
		add_View_Data_(model, "actionInput", "新規出納追加");
		model.addAttribute("date", date);
		model.addAttribute("japanese_Date", service.japanese_Date(date));
		model.addAttribute("name", service.name());
		model.addAttribute("action", action);
		model.addAttribute("next_id", service.next_Action_Id());
		model.addAttribute("label_Set_List", LabelSet.actionInsert_Set);
		model.addAttribute("subjects", service.subjects());
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
		model.addAttribute("options", service.subjects());
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

		service.setItem_id(action, type, item);
		model.addAttribute("object", action);
		model.addAttribute("field", item);
		model.addAttribute("options", service.applys());
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

	@PostMapping("/Action/Insert")
	public String action_Insert(
			@RequestParam("date")String date,
			@ModelAttribute("action")Action action,
			RedirectAttributes redirectAttributes) {
		String message = service.action_Insert(action);
		redirectAttributes.addFlashAttribute("message", message);
		return redirect("/Daily?date=" + date);
	}

	@PostMapping("/ActionIncome/Insert")
	public String actionIncome_Insert(
			@RequestParam("post_date")String date,
			@RequestParam("field")int income,
			@ModelAttribute("object")Action action,
			RedirectAttributes redirectAttributes) {
		action.setIncome(income);
		action.setSpending(0);
		String message = service.action_Insert(action);
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
		String message = service.action_Insert(action);
		redirectAttributes.addFlashAttribute("message", message);
		return redirect("/Daily?date=" + date);
	}

	@PostMapping("/OwnerUpdate")
	public String ownerUpdate(
			@RequestParam("id")int id,
			Model model) {
		add_View_Data_(model, "ownerUpdate", "所有者情報更新");
		model.addAttribute("id", id);
		model.addAttribute("owner", service.owner(id));
		return "view";
	}

	@PostMapping("/ActionUpdate")
	public String actionUpdate(
			@RequestParam("id")int id,
			@RequestParam("date")String date,
			Model model) {
		add_View_Data_(model, "actionUpdate", "出納情報更新");
		model.addAttribute("guide", "内容変更後更新ボタンを押してください");
		model.addAttribute("do_Name", "更新");
		model.addAttribute("cancel_url", req("/Daily"));
		model.addAttribute("do_url", req("/Action/Update"));
		model.addAttribute("id", id);
		model.addAttribute("date", date);
		model.addAttribute("object", service.action(id));
		model.addAttribute("label_Set_List", LabelSet.actionUpdate_Set);
		model.addAttribute("subjects", service.subjects());
		return "view";
	}

	@PostMapping("/Action/Update")
	public String action_Update(
			@RequestParam("post_id")int id,
			@RequestParam("post_date")String date,
			@ModelAttribute("action")Action action,
			RedirectAttributes redirectAttributes) {
		String message = service.action_Update(action, id);
		redirectAttributes.addFlashAttribute("message", message);
		LocalDate localDate = service.to_LocalDate(date);
		return redirect("/Daily?date=" + localDate);
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

	@PostMapping("/ActionDelete")
	public String actionDelete(
			@RequestParam("id")int id,
			@RequestParam("date")String date,
			Model model) {
		add_View_Data_(model, "delete", "出納情報削除");
		model.addAttribute("guide", "この行を削除してもよろしいですか？");
		model.addAttribute("cancel_url", req("/Daily"));
		model.addAttribute("delete_url", req("/Action/Delete"));
		model.addAttribute("id", id);
		model.addAttribute("date", date);
		model.addAttribute("object", service.action(id));
		model.addAttribute("label_Set_List", LabelSet.actionDelete_Set);
		return "view";
	}

	@PostMapping("/Action/Delete")
	public String action_Delete(
			@RequestParam("id")int id,
			@RequestParam("date")String date,
			RedirectAttributes redirectAttributes) {
		String message = service.action_Delete(id);
		redirectAttributes.addFlashAttribute("message", message);
		return redirect("/Daily?date=" + date);
	}


	@PostMapping("/OwnerReport")
	public String ownerReport() {
		return redirect("/OwnerReport");
	}

	@GetMapping("/OwnerReport")
	public String ownerReport(
			Model model) {
		add_View_Data_(model, "ownerReport", "所有者情報印刷");
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

	@GetMapping("/Monthly")
	public String monthly(
			@Param("year_month")String date,
			Model model) {
		add_View_Data_(model, "monthly", "月別出納一覧");
		if(date  == null) date = service.this_Year_Month();
		model.addAttribute("name", service.name());
		model.addAttribute("date", date);
		model.addAttribute("japanese_Date", service.japanese_Date(date, "G y 年 M 月"));
		model.addAttribute("carryover", service.carryover(date));
		List<Action> action_List = service.monthly_List(date, 1);
		model.addAttribute("action_List", action_List);
		model.addAttribute("income", service.income_List(action_List));
		model.addAttribute("spending",service.spending_List(action_List));
		model.addAttribute("label_Set_List", LabelSet.action_List_Set);
		return "view";
	}

	@PostMapping("/Year")
	public String year(
			@RequestParam("year")String year,
			@RequestParam("subject")String subject,
			RedirectAttributes redirectAttributes) {
		redirectAttributes.addAttribute("subject", subject);
		year = service.year(year + " 1 月 1 日", "G y 年 M 月 d 日");
		return redirect("/Year?year=" + year);
	}

	@GetMapping("/Year")
	public String year(
			@Param("year")String year,
			@Param("subject")String subject,
			Model model) {
		add_View_Data_(model, "year", "年度別出納一覧");
		if(year  == null) year = service.this_Year();
		model.addAttribute("name", service.name());
		model.addAttribute("year", service.japanese_Date(year, "G y 年"));
		year += "/01";
		model.addAttribute("carryover", service.carryover(year));
		List<Action> action_List = service.year_List(year, 1, subject);
		model.addAttribute("action_List", action_List);
		model.addAttribute("income", service.income_List(action_List));
		model.addAttribute("spending", service.spending_List(action_List));
		model.addAttribute("label_Set_List", LabelSet.action_List_Set);
		if(subject  == null) subject = "全科目";
		if(subject.equals("")) subject = "全科目";
		model.addAttribute("subject", subject);
		return "view";
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
		model.addAttribute("options", service.subjects());
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


}
