package com.iwamih31.KAKEIBO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionRepository extends JpaRepository<Action, Integer> {

	/**	Action リスト取得（item_id, date 指定 date 順） */
	@Query("select action"
			+ " from Action action"
			+ " where action.item_id = :item_id"
			+ " and action.date like CONCAT(:date, '%')"
			+ " order by action.date asc")
	public List<Action> list(
			@Param("item_id") Integer item_id,
			@Param("date") String date);

}
