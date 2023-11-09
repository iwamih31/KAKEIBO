package com.iwamih31.KAKEIBO;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {

	/**	Plan取得（日付指定） */
	@Query("select plan"
			+ " from Plan plan"
			+ " where plan.date >= :start_date"
			+ " and plan.date <= :end_date"
			+ " order by"
			+ " plan.date asc,"
			+ " plan.subject asc,"
			+ " plan.apply asc")
	public List<Plan> plan_List(
			@Param("start_date") LocalDate start_date,
			@Param("end_date") LocalDate end_date
			);

	/**	Plan取得（日付指定） */
	@Query("select plan"
			+ " from Plan plan"
			+ " where plan.date >= :start_date"
			+ " and plan.date <= :end_date"
			+ " and plan.subject = :subject"
			+ " order by"
			+ " plan.date asc,"
			+ " plan.subject asc,"
			+ " plan.apply asc")
	public List<Plan> plan_List(
			@Param("start_date") LocalDate start_date,
			@Param("end_date") LocalDate end_date,
			@Param("subject") String subject
			);

	/**	subjects取得 */
	@Query("select distinct plan.subject"
			+ " from Plan plan")
	public List<String> subjects();

	/**	applys取得 */
	@Query("select distinct plan.apply"
			+ " from Plan plan")
	public List<String> applys();

	/**	Plan取得（指定日まで） */
	@Query("select distinct plan"
			+ " from Plan plan"
			+ " where plan.date <= :date"
			+ " order by plan.date desc")
	public List<Plan> up_to_date(
			@Param("date") LocalDate localDate
			);

	/**	Plan リスト取得（item_id, date 指定 date 順） */
	@Query("select plan"
			+ " from Plan plan"
			+ " where plan.item_id = :item_id"
			+ " where plan.date like :date%"
			+ " order by plan.date asc")
	public List<Plan> list(
			@Param("item_id") Integer item_id,
			@Param("date") String date);

}
