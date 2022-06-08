package com.javaex.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.javaex.vo.GuestBookVo;


public class GuestBookDao {
	
	// 0. import java.sql.*;
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;

	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String url = "jdbc:oracle:thin:@localhost:1521:xe";
	private String id = "webdb";
	private String pw = "webdb";

	//connection 얻어오기 메소드
	private void getConnection() {
		try {
			// 1. JDBC 드라이버 (Oracle) 로딩
			Class.forName(driver);

			// 2. Connection 얻어오기
			conn = DriverManager.getConnection(url, id, pw);
			

		} catch (ClassNotFoundException e) {
			System.out.println("error: 드라이버 로딩 실패 - " + e);
		} catch (SQLException e) {
			System.out.println("error:" + e);
		}
	}
	
	//자원정리 메소드
	private void close() {
		// 5. 자원정리
		try {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			System.out.println("error:" + e);
		}
	}
	
	//전체 글 가져오기
	public List<GuestBookVo> getList() {
		List<GuestBookVo> list = new ArrayList<GuestBookVo>();
		getConnection();
		
		try {
			// 3. SQL문 준비 / 바인딩 / 실행
			String query = ""; 
			query += " select no, name, password, content, reg_date " ;
			query += " from guestbook " ;
			query += " order by reg_date desc ";
			
			pstmt = conn.prepareStatement(query);

			rs = pstmt.executeQuery();
			
			// 4.결과처리
			while (rs.next()) {
				int no = rs.getInt("no");
				String name = rs.getString("name");
				String password = rs.getString("password");
				String content = rs.getString("content");
				String regDate = rs.getString("reg_date");

				GuestBookVo vo = new GuestBookVo(no, name, password, content, regDate);
				list.add(vo);
			}
			System.out.println("guestbookDao.getList(): " + list.toString());
			
		} catch (SQLException e) {
			System.out.println("error:" + e);
		}
		
		close();
		
		return list;
	}

	//글쓰기 
	public int insert(GuestBookVo vo) {
		int count = 0;
		getConnection();
		
		try {

			// 3. SQL문 준비 / 바인딩 / 실행
			String query = "insert into guestbook values (seq_guestbook_no.nextval, ?, ?, ?,sysdate)";
			pstmt = conn.prepareStatement(query);

			pstmt.setString(1, vo.getName());
			pstmt.setString(2, vo.getPassword());
			pstmt.setString(3, vo.getContent());

			count = pstmt.executeUpdate();

			// 4.결과처리
			System.out.println("guestbookDao.insert(): " + count + "건 방명록 글 저장");

		} catch (SQLException e) {
			System.out.println("error:" + e);
		}
		
		close();
		return count;
	}

	//글 삭제 
	public int delete(GuestBookVo vo) {
		
		int count = 0;
		getConnection();
		
		try {

			// 3. SQL문 준비 / 바인딩 / 실행
			String query = "delete from guestbook where no= ? and password= ?";
			pstmt = conn.prepareStatement(query);

			pstmt.setInt(1, vo.getNo());
			pstmt.setString(2, vo.getPassword());

			count = pstmt.executeUpdate();

			// 4.결과처리
			System.out.println("guestbookDao.delete(): " + count + "건 방명록 글 삭제");
			
		} catch (SQLException e) {
			System.out.println("error:" + e);
		} 

		close();
		return count;
	}

}