package com.kh.spring.demo.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.kh.spring.demo.model.dao.DemoDao;
import com.kh.spring.demo.model.vo.Dev;

@Service
public class DemoServiceImpl implements DemoService {

	@Autowired
	private DemoDao demoDao;

	@Override
	public int insertDev(Dev dev) {
		//1. SqlSession 객체 생성
		//2. dao 업무 요청
		//3. 트랜잭션처리(DML)
		//4. SqlSession 반납
		
		//2. dao 업무 요청
		int result = demoDao.insertDev(dev);
		return result;
	}

	@Override
	public List<Dev> selectDevList() {
		//1. SqlSession 객체 생성
		//2. dao 업무 요청
		//3. 트랜잭션처리(DML)
		//4. SqlSession 반납
		
		//2. dao 업무 요청
//		List<Dev> list = demoDao.selectDevList();
//		return list;
		
		return demoDao.selectDevList();
	}

	@Override
	public Dev selectOneDev(int no) {
		return demoDao.selectOneDev(no);
	}
	
	/**
	 * 
	 * transaction 처리(AOP)
	 * - 예외가 발생하지 않으면 commint, 발생 시 rollback
	 * 
	 */
	@Override
	public int updateDev(Dev dev) {
		return demoDao.updateDev(dev);
	}

	@Override
	public int deleteDev(int no) {
		return demoDao.deleteDev(no);
	}
}
