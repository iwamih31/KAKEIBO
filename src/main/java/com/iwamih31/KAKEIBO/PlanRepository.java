package com.iwamih31.KAKEIBO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {


	/**	Plan リスト取得（date 指定 date 順） */
	@Query("select plan"
			+ " from Plan plan"
			+ " where plan.the_day like CONCAT(:the_day, '%')"
			+ " order by plan.the_day asc")
	public List<Plan> list(
			@Param("the_day") String the_day);

	/**	Plan リスト取得（item_id, date 指定 date 順） */
	@Query("select plan"
		    + " from Plan plan"
		    + " where plan.item_id = :item_id"
		    + " and plan.the_day like CONCAT(:the_day, '%')"
		    + " order by plan.the_day asc")
	public List<Plan> list(
			@Param("item_id") Integer item_id,
			@Param("the_day") String the_day);

	/**	Action リスト取得（date 順） */
	@Query("select plan"
			+ " from Plan plan"
			+ " order by plan.the_day asc")
	public List<Plan> all();

}
