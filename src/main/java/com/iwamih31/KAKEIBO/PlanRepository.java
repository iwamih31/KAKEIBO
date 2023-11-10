package com.iwamih31.KAKEIBO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {

	/**	Plan リスト取得（item_id, date 指定 date 順） */
	@Query("select plan"
		    + " from Plan plan"
		    + " where plan.item_id = :item_id"
		    + " and plan.date like CONCAT(:date, '%')"
		    + " order by plan.date asc")
	public List<Plan> list(
			@Param("item_id") Integer item_id,
			@Param("date") String date);

}
