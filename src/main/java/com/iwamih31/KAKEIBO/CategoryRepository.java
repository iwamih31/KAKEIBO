package com.iwamih31.KAKEIBO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

	/**	Category 取得（name 指定） */
	@Query("select category"
			+ " from Category category"
			+ " where category.name = :category")
	public List<Category> itemList(
			@Param("category") String category
			);

	/**	カテゴリ名 一覧取得 */
	@Query("select distinct category.name"
			+ " from Category category")
	public List<String> categoryList();

}