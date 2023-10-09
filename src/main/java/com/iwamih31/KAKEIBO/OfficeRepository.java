package com.iwamih31.KAKEIBO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OfficeRepository extends JpaRepository<Office, Integer> {

	/**	item_name 列の値が引数 item_name の値と同じ行の item_value 列の値を文字列で返す */
	@Query("select office.item_value"
			+ " from Office office"
			+ " where office.item_name = :item_name")
	public List<String> item_value(@Param("item_name")String item_name);

}
