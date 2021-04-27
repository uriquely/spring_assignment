package com.kh.spring.board.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.spring.board.model.dao.BoardDao;
import com.kh.spring.board.model.vo.Attachment;
import com.kh.spring.board.model.vo.Board;

import jdk.internal.org.jline.utils.Log;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardDao boardDao;

	@Override
	public List<Board> selectBoardList(Map<String, Object> param) {
		return boardDao.selectBoardList(param);
	}

	@Override
	public int getTotalContents() {
		return boardDao.getTotalContents();
	}

	/**
	 * @Transactional 
	 * - class level : 모든 메소드 실행결과 Runtime예외가 던져지면, rollback.
	 * - method level : 해당메소드 실행결과 Runtime예외가 던져지면, rollback.
	 * 
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int insertBoard(Board board) {
		
		int result = 0;
		
		//1. board객체 등록
		result =  boardDao.insertBoard(board);
		log.debug("board.no = {}", board.getNo());
		
		//2. attachment 객체 등록
		//insert into attachment (no, board_no, original_filename. rename_filename)
		//values(seq_attachment_no.nextval, #{boardNo}, #{originalFileName}, #{renamedFileName}
		if(!board.getAttachList().isEmpty()) {
			for(Attachment attach : board.getAttachList()) {
				
				attach.setBoardNo(board.getNo());
				result = boardDao.insertAttachment(attach);
			}
		}
		
		return result;
	}

	@Override
	public Board boardDetail(int no) {
		
		Board board = boardDao.boardDetail(no);
		
		List<Attachment> attachList = boardDao.selectAttachList(no);
		board.setAttachList(attachList);
		
		//attachList전달 확인
		log.debug("attachList = {}", attachList);
		
		return board;
	}

}
