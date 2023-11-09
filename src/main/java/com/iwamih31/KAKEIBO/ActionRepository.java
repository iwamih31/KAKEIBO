package com.iwamih31.KAKEIBO;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionRepository extends JpaRepository<Action, Integer> {

	/**	Action取得（日付指定） */
	@Query("select action"
			+ " from Action action"
			+ " where action.date >= :start_date"
			+ " and action.date <= :end_date"
			+ " order by"
			+ " action.date asc,"
			+ " action.subject asc,"
			+ " action.apply asc")
	public List<Action> action_List(
			@Param("start_date") LocalDate start_date,
			@Param("end_date") LocalDate end_date
			);

	/**	Action取得（日付指定） */
	@Query("select action"
			+ " from Action action"
			+ " where action.date >= :start_date"
			+ " and action.date <= :end_date"
			+ " and action.subject = :subject"
			+ " order by"
			+ " action.date asc,"
			+ " action.subject asc,"
			+ " action.apply asc")
	public List<Action> action_List(
			@Param("start_date") LocalDate start_date,
			@Param("end_date") LocalDate end_date,
			@Param("subject") String subject
			);

	/**	subjects取得 */
	@Query("select distinct action.subject"
			+ " from Action action")
	public List<String> subjects();

	/**	applys取得 */
	@Query("select distinct action.apply"
			+ " from Action action")
	public List<String> applys();

	/**	Action取得（指定日まで） */
	@Query("select distinct action"
			+ " from Action action"
			+ " where action.date <= :date"
			+ " order by action.date desc")
	public List<Action> up_to_date(
			@Param("date") LocalDate localDate
			);

	/**	Action リスト取得（item_id, date 指定 date 順） */
	@Query("select action"
			+ " from Action action"
			+ " where action.item_id = :item_id"
			+ " where action.date like :date%"
			+ " order by action.date asc")
	public List<Action> list(
			@Param("item_id") Integer item_id,
			@Param("date") String date);

}
