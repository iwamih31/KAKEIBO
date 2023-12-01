package com.iwamih31.KAKEIBO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionRepository extends JpaRepository<Action, Integer> {

	/**	Action リスト取得（date 指定 date 順） */
	@Query("select action"
			+ " from Action action"
			+ " where action.the_day like CONCAT(:the_day, '%')"
			+ " order by action.the_day asc")
	public List<Action> list(
			@Param("the_day") String the_day);

	/**	Action リスト取得（item_id, date 指定 date 順） */
	@Query("select action"
			+ " from Action action"
			+ " where action.item_id = :item_id"
			+ " and action.the_day like CONCAT(:the_day, '%')"
			+ " order by action.the_day asc")
	public List<Action> list(
			@Param("item_id") Integer item_id,
			@Param("the_day") String the_day);

//	/**	Action リスト取得（item_id, start_date, end_date 指定 date 順） */
//	@Query("select action"
//			+ " from Action action"
//			+ " where action.item_id = :item_id"
//			+ " and action.the_day >= :start_date"
//			+ " and action.the_day <= :last_date"
//			+ " order by action.the_day asc")
//	public List<Action> list(
//			@Param("item_id") Integer item_id,
//			@Param("start_date") LocalDate start_date,
//			@Param("last_date") LocalDate last_date);

	/**	Action リスト取得（date 順） */
	@Query("select action"
			+ " from Action action"
			+ " order by action.the_day asc")
	public List<Action> all();

}
