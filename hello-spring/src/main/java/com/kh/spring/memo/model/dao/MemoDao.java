package com.kh.spring.memo.model.dao;

import java.util.List;

import com.kh.spring.memo.model.vo.Memo;

public interface MemoDao {

	List<Memo> selectMemoList();

	int insertMemo(Memo memo);

}
