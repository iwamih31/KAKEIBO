package com.iwamih31.KAKEIBO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

	/**	ID 取得（value 指定） */
	@Query("select item.id"
			+ " from Item item"
			+ " where item.type_id = :type_id"
			+ " and item.value = :value")
	public Integer getID(
			@Param("type_id") Integer type_id,
			@Param("value") String item
			);

	/**	項目名 一覧取得 */
	@Query("select distinct item.name"
			+ " from Item item")
	public List<String> itemList();

}