package com.kh.spring.memo.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.memo.model.dao.MemoDao;
import com.kh.spring.memo.model.vo.Memo;

@Service
public class MemoServiceImpl implements MemoService {

	@Autowired
	private MemoDao memoDao;

	@Override
	public List<Memo> selectMemoList() {
		return memoDao.selectMemoList();
	}

	@Override
	public int insertMemo(Memo memo) {
		return memoDao.insertMemo(memo);
	}
}
