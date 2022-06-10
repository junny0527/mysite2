package com.javaex.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.javaex.vo.BoardVo;

public class BoardDao {

	// 0. import java.sql.*;
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;

	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String url = "jdbc:oracle:thin:@localhost:1521:xe";
	private String id = "webdb";
	private String pw = "webdb";

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

	// 자원정리
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

	// 글쓰기
	public int write(BoardVo boardVo) {
		int count = 0;
		getConnection();

		try {
			// 3. SQL문 준비 / 바인딩 / 실행
			String query = "";
			query += " INSERT INTO board ";
			query += " VALUES (seq_board_no.NEXTVAL, ?, ?, 0, SYSDATE, ?) ";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, boardVo.getTitle());
			pstmt.setString(2, boardVo.getContent());
			pstmt.setInt(3, boardVo.getUserNo());

			count = pstmt.executeUpdate();

			// 4.결과처리
			System.out.println("boardDao.write(): " + count + "건 글 저장");

		} catch (SQLException e) {
			System.out.println("error:" + e);
		}

		close();

		return count;
	}

	// 글 가져오기
	public BoardVo getBoard(int no) {
		BoardVo boardVo = null;

		getConnection();

		try {
			
			upCount(no);

			// 3. SQL문 준비 / 바인딩 / 실행
			String query = "";
			query += " SELECT bo.no ";
			query += "      , bo.title ";
			query += "      , bo.content ";
			query += "      , us.name ";
			query += "      , bo.hit ";
			query += "      , TO_CHAR(bo.reg_date, 'YYYY-MM-DD HH:MI') regDate ";
			query += "      , us.no userNo";
			query += " FROM board bo, users us ";
			query += " WHERE  bo.user_no = us.no ";
			query += "  AND	bo.no = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, no);

			// 4.결과처리
			rs = pstmt.executeQuery();
			while (rs.next()) {
				int rno = rs.getInt("no");
				String title = rs.getString("title");
				String content = rs.getString("content");
				int hit = rs.getInt("hit");
				String regDate = rs.getString("regDate");
				int userNo = rs.getInt("userNo");
				String userName = rs.getString("name");

				boardVo = new BoardVo(rno, title, content, hit, regDate, userNo, userName);
			}

			

		} catch (SQLException e) {
			System.out.println("error:" + e);
			
		}
		close();
		return boardVo;
	}

	// 삭제
	public int delete(int no) {
		int count = 0;

		getConnection();

		try {
			// 3. SQL문 준비 / 바인딩 / 실행
			String query = "";
			query += " DELETE board ";
			query += " WHERE no = ? ";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, no);

			count = pstmt.executeUpdate();

			// 4.결과처리
			System.out.println("boardDao.delete(): " + count + " 건 삭제");

		} catch (SQLException e) {
			System.out.println("error:" + e);
		}

		close();

		return count;
	}

	// 수정
	public int Update(BoardVo boardVo) {
		int count = 0;

		getConnection();

		try {
			// 3. SQL문 준비 / 바인딩 / 실행
			String query = "";
			query += " UPDATE board ";
			query += " SET 	title = ? ";
			query += "	  , content = ? ";
			query += " WHERE no = ? ";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, boardVo.getTitle());
			pstmt.setString(2, boardVo.getContent());
			pstmt.setInt(3, boardVo.getNo());

			count = pstmt.executeUpdate();

			// 4.결과처리
			System.out.println("boardDao.Update(): " + count + " 건 수정");

		} catch (SQLException e) {
			System.out.println("error:" + e);
		}

		close();

		return count;

	}

	// 조회수 1개 올리기
	private int upCount(int no) {
		int count = 0;

		try {
			// 3. SQL문 준비 / 바인딩 / 실행
			String query = "";
			query += " UPDATE board ";
			query += " SET hit = hit + 1 ";
			query += " WHERE no = ?";

			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, no);

			count = pstmt.executeUpdate();

			// 4.결과처리
			System.out.println("boardDao.upCount(): " + no + " 글 hit 증가");

		} catch (SQLException e) {
			System.out.println("error:" + e);
		}

		return count;
	}

	public List<BoardVo> getList(String word) {
		List<BoardVo> boardList = new ArrayList<>();
		getConnection();

		try {
			// 3. SQL문 준비 / 바인딩 / 실행
			String query = "";
			query += " SELECT bo.no ";
			query += "      , bo.title ";
			query += "      , bo.content ";
			query += "      , us.name ";
			query += "      , bo.hit ";
			query += "      , TO_CHAR(bo.reg_date, 'YYYY-MM-DD HH:MI') regDate ";
			query += "		, us.no userNo ";
			query += " FROM board bo, users us ";
			query += " WHERE  bo.user_no = us.no ";

			if (word == null || word == "") {
				query += " and  bo.title like ? ";
				query += " order by reg_date asc  ";
				pstmt = conn.prepareStatement(query);
				pstmt.setString(1, '%' + word + '%');

			} 

			rs = pstmt.executeQuery();

			// 4.결과처리
			while (rs.next()) {
				int no = rs.getInt("no");
				String title = rs.getString("title");
				String content = rs.getString("content");
				int hit = rs.getInt("hit");
				String regDate = rs.getString("regDate");
				int userNo = rs.getInt("userNo");
				String userName = rs.getString("name");

				BoardVo vo = new BoardVo(no, title, content, hit, regDate, userNo, userName);
				boardList.add(vo);
			}

			System.out.println("boardDao.getList(): " + boardList.toString());

		} catch (SQLException e) {
			System.out.println("error:" + e);
		}

		close();

		return boardList;
	}

}