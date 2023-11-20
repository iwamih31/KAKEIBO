package com.iwamih31.KAKEIBO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRepository extends JpaRepository<Type, Integer> {

	/**	表示 Type リスト取得（非表示抜きrank順） */
	@Query("select type"
			+ " from Type type"
			+ " where type.rank != 0"
			+ " order by type.rank asc"
			)
	public List<Type> list();

	/**	ID 取得（name 指定） */
	@Query("select type.id"
			+ " from Type type"
			+ " where type.name = :name")
	public Integer getID(@Param("name") String name);

	/**	種別名 一覧取得 */
	@Query("select distinct type.name"
			+ " from Type type")
	public List<String> nameList();

	/**	デフォルトType名 取得 */
	@Query("select type.name"
			+ " from Type type"
			+ " where type.rank = 1"
			)
	public String default_Type();

	/**	max_Rank 取得 */
	@Query("select type.rank"
			+ " from Type type"
			+ " where type.rank = 1"
			)
	public Integer max_Rank();

	/**	表示 Type リスト取得（rank順） */
	@Query("select type"
			+ " from Type type"
			+ " order by type.rank asc"
			)
	public List<Type> asc_Rank();

	/**	Type 取得（name 指定） */
	@Query("select type"
			+ " from Type type"
			+ " where type.name = :name"
			)
	public Type type(@Param("name") String name);

	/**	name 取得（id 指定） */
	@Query("select type.name"
			+ " from Type type"
			+ " where type.id = :id"
			)
	public String type(@Param("id") int id);

}