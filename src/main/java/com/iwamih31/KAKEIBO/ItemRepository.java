package com.iwamih31.KAKEIBO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

	/**	Item リスト取得（type_id 指定 name 順） */
	@Query("select item"
			+ " from Item item"
			+ " where item.type_id = :type_id"
			+ " order by item.name asc")
	public List<Item> list(
			@Param("type_id") Integer type_id
			);

	/**	ID 取得（type_id, name 指定） */
	@Query("select item.id"
			+ " from Item item"
			+ " where item.type_id = :type_id"
			+ " and item.name = :name")
	public Integer getID(
			@Param("type_id") Integer type_id,
			@Param("name") String item
			);

	/**	項目名 一覧取得 */
	@Query("select distinct item.name"
			+ " from Item item")
	public List<String> itemList();

	/**	項目名 一覧取得（type_id 指定） */
	@Query("select distinct item.name"
			+ " from Item item"
			+ " where item.type_id = :type_id"
			+ " order by item.name asc")
	public List<String> itemList(
			@Param("type_id") Integer type_id);

	/**	id から name 取得 */
	@Query("select item.name"
			+ " from Item item"
			+ " where item.id = :id")
	public String item(Integer id);

}