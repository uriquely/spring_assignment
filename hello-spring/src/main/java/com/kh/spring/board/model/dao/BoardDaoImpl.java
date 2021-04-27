package com.kh.spring.board.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.spring.board.model.vo.Attachment;
import com.kh.spring.board.model.vo.Board;

@Repository
public class BoardDaoImpl implements BoardDao {

	@Autowired
	private SqlSessionTemplate session;

	
	/**
	 * RowBounds : mybatis가 제공하는 paging기능
	 * 
	 * - offset : 건너뛸 행 수
	 * - limit : 한 페이지당 게시물 수 
	 */
	@Override
	public List<Board> selectBoardList(Map<String, Object> param) {
		
		int cPage = (int) param.get("cPage");
		int limit = (int) param.get("numPerPage");
		
		int offset = (cPage - 1) * limit; // 1 -> 0, 2 -> 5, 3 -> 10
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		return session.selectList("board.selectBoardList", null, rowBounds);
	}


	@Override
	public int getTotalContents() {
		return session.selectOne("board.getTotalContents");
	}


	@Override
	public int insertBoard(Board board) {
		return session.insert("board.insertBoard", board);
	}


	@Override
	public int insertAttachment(Attachment attach) {
		return session.insert("board.insertAttachment", attach);
	}


	@Override
	public Board boardDetail(int no) {
		return session.selectOne("board.boardDetail", no);
	}


	@Override
	public List<Attachment> selectAttachList(int no) {
		return session.selectList("board.selectAttachList", no);
	}
}
