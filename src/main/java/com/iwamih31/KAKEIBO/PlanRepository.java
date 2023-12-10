package com.iwamih31.KAKEIBO;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {


	/**	Plan リスト取得（the_day 指定 the_day 順） */
	@Query("select plan"
			+ " from Plan plan"
			+ " where plan.the_day like CONCAT(:the_day, '%')"
			+ " order by plan.the_day asc")
	public List<Plan> list(
			@Param("the_day") String the_day);

	/**	Plan リスト取得（item_id, the_day 指定 the_day 順） */
	@Query("select plan"
		    + " from Plan plan"
		    + " where plan.item_id = :item_id"
		    + " and plan.the_day like CONCAT(:the_day, '%')"
		    + " order by plan.the_day asc")
	public List<Plan> list(
			@Param("item_id") Integer item_id,
			@Param("the_day") String the_day);

	/**	Plan リスト取得（the_day 順） */
	@Query("select plan"
			+ " from Plan plan"
			+ " order by plan.the_day asc")
	public List<Plan> all();


	/**	Plan取得（item_id, the_day 指定） */
	@Query("select plan"
		    + " from Plan plan"
		    + " where plan.item_id = :item_id"
		    + " and plan.the_day like CONCAT(:the_day, '%')")
	public Plan plan(
			@Param("item_id") Integer item_id,
			@Param("the_day") String the_day);


	/**	Plan リスト取得（the_day前日まで the_day 順） */
	@Query("select plan"
			+ " from Plan plan"
			+ " where plan.the_day < :the_day"
			+ " order by plan.the_day asc")
	public List<Plan> all(
			@Param("the_day") LocalDate the_day);

	/**	Plan リスト取得（item_id 指定, the_day前日まで, the_day 順） */
	@Query("select plan"
			+ " from Plan plan"
		    + " where plan.item_id = :item_id"
			+ " and plan.the_day < :the_day"
			+ " order by plan.the_day asc")
	public List<Plan> all(
			@Param("item_id") Integer item_id,
			@Param("the_day") LocalDate the_day);

}
