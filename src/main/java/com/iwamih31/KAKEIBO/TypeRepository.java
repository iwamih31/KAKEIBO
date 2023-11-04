package com.iwamih31.KAKEIBO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRepository extends JpaRepository<Type, Integer> {

	/**	ID 取得（value 指定） */
	@Query("select type.id"
			+ " from Type type"
			+ " where type.value = :value")
	public Integer getID(
			@Param("value") String type
			);

	/**	種別名 一覧取得 */
	@Query("select distinct type.name"
			+ " from Type type")
	public List<String> typeList();

}