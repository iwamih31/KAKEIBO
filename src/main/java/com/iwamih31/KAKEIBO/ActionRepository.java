package com.iwamih31.KAKEIBO;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionRepository extends JpaRepository<Action, Integer> {

	/**	Action リスト取得（date 指定 the_day 順） */
	@Query("select action"
			+ " from Action action"
			+ " where action.the_day like CONCAT(:the_day, '%')"
			+ " order by action.the_day asc")
	public List<Action> list(
			@Param("the_day") String the_day);

	/**	Action リスト取得（item_id, the_day 指定 the_day 順） */
	@Query("select action"
			+ " from Action action"
			+ " where action.item_id = :item_id"
			+ " and action.the_day like CONCAT(:the_day, '%')"
			+ " order by action.the_day asc")
	public List<Action> list(
			@Param("item_id") Integer item_id,
			@Param("the_day") String the_day);


	/**	Action リスト取得（the_day 順） */
	@Query("select action"
			+ " from Action action"
			+ " order by action.the_day asc")
	public List<Action> all();

	/**	Action リスト取得（the_day前日まで the_day 順） */
	@Query("select action"
			+ " from Action action"
			+ " where action.the_day < :the_day"
			+ " order by action.the_day asc")
	public List<Action> all(
			@Param("the_day") LocalDate the_day);

	/**	Action リスト取得（item_id 指定, the_day前日まで, the_day 順） */
	@Query("select action"
			+ " from Action action"
			+ " where action.item_id = :item_id"
			+ " and action.the_day < :the_day"
			+ " order by action.the_day asc")
	public List<Action> all(
			@Param("item_id") Integer item_id,
			@Param("the_day") LocalDate the_day);

}
