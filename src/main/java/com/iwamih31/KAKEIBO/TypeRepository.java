package com.iwamih31.KAKEIBO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRepository extends JpaRepository<Type, Integer> {

	/**	表示 Type リスト取得（表示順） */
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
	public Integer getID(
			@Param("name") String type
			);

	/**	種別名 一覧取得 */
	@Query("select distinct type.name"
			+ " from Type type")
	public List<String> nameList();

}