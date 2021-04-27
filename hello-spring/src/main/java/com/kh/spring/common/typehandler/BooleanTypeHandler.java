package com.kh.spring.common.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class BooleanTypeHandler implements TypeHandler<Boolean> {
	
	/**
	 * 마이바티스가 PreparedStatement에 파라미터를 설정하고 
	 * ResultSet에서 값을 가져올때마다 TypeHandler는 적절한 자바 타입의 값을 가져오기 위해 사용된다.
	 * 
	 * VO -> PreparedStatement -> DB -> ResultSet -> VO
	 */

	@Override
	public void setParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException {
		//boolean값을 String으로 변환
		//commons-lang3 라이브러리의 BooleanUtils를 통해 간단하게 변경 가능하다.
		ps.setString(i, BooleanUtils.toString(parameter, "Y", "N"));
	}

	@Override
	public Boolean getResult(ResultSet rs, String columnName) throws SQLException {
		//String을 받는 toBoolean 사용하여 ResultSet전달
		return BooleanUtils.toBoolean(rs.getString(columnName));
	}

	@Override
	public Boolean getResult(ResultSet rs, int columnIndex) throws SQLException {
		return BooleanUtils.toBoolean(rs.getString(columnIndex));
	}

	@Override
	public Boolean getResult(CallableStatement cs, int columnIndex) throws SQLException {
		return BooleanUtils.toBoolean(cs.getString(columnIndex));
	}

}
