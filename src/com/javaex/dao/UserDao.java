package com.javaex.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.javaex.vo.UserVo;

public class UserDao {

	// 0. import java.sql.*;
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String url = "jdbc:oracle:thin:@localhost:1521:xe";
	private String id = "webdb";
	private String pw = "webdb";
	UserVo authUser = null;

	// 메소드 드라이버 연결
	public void getConnecting() {
		try {
			// 1. JDBC 드라이버 (Oracle) 로딩
			Class.forName(driver);

			// 2. Connection 얻어오기
			conn = DriverManager.getConnection(url, id, pw);
		} catch (Exception e) {
			System.out.println("error:" + e);
		}
	}

	public void close() {
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

	// 회원가입 --> 회원정보 저장
	public int insert(UserVo userVo) {
		int count = -1;
		getConnecting();
		try {

			// 3. SQL문준비/ 바인딩/ 실행
			// SQL문준비
			String query = "";
			query += " insert into users ";
			query += " values (seq_users_no.nextval, ?, ?, ?, ?) ";

			// 바인딩
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, userVo.getId());
			pstmt.setString(2, userVo.getPassword());
			pstmt.setString(3, userVo.getName());
			pstmt.setString(4, userVo.getGender());

			// 실행
			count = pstmt.executeUpdate();
			// 4.결과처리

			System.out.println(count + "건이 추가되었습니다.");
		} catch (SQLException e) {
			System.out.println("error:" + e);
		}
		close();

		return count;
	}

	public int update(UserVo userVo) {
		int count = -1;
		getConnecting();
		try {

			// 3. SQL문준비/ 바인딩/ 실행
			// SQL문준비
			String query = "";
			query += " update users ";
			query += " set password = ? ";
			query += "     ,name = ? ";
			query += "     ,gender = ? ";
			query += " where no = ? ";
			
			System.out.println(query);

			// 바인딩
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, userVo.getPassword());
			pstmt.setString(2, userVo.getName());
		pstmt.setString(3, userVo.getGender());
			pstmt.setInt(4, userVo.getNo());

			// 실행
			count = pstmt.executeUpdate();

			// 4.결과처리
			System.out.println(count + "건이 수정되었습니다.");

		} catch (SQLException e) {
			System.out.println("error:" + e);
		}
		close();

		return count;
	}

	public int delete(UserVo userVo) {
		int count = -1;
		getConnecting();
		try {

			// 3. SQL문준비/ 바인딩/ 실행
			// SQL문준비
			String query = "";
			query += " delete from users ";
			query += " where password = ? ";
			query += " and   no = ? ";

			// 바인딩
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, userVo.getPassword());
			pstmt.setInt(2, userVo.getNo());

			// 실행
			count = pstmt.executeUpdate();

			// 4.결과처리
			System.out.println(count + "건이 삭제되었습니다.");

		} catch (SQLException e) {
			System.out.println("error:" + e);
		}
		close();

		return count;
	}

	// 사용자 정보 가져오기(로그인시 사용,no,id)
	public UserVo getUser(UserVo userVo) {

		this.getConnecting();
		try {

			// 3. SQL문준비/ 바인딩/ 실행
			// SQL문준비
			String query = "";
			query += " select no, ";
			query += "  id, ";
			query += " password, ";
			query += " name, ";
			query += " gender ";
			query += " from users  ";
			query += " where id = ?  ";
			query += " and password = ? ";
			System.out.println(query);

			// 바인딩
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, userVo.getId());
			pstmt.setString(2, userVo.getPassword());

			// 실행
			rs = pstmt.executeQuery();
			// 4.결과처리
			while (rs.next()) {
				int no = rs.getInt("no");
				String name = rs.getString("name");

				authUser = new UserVo();
				authUser.setNo(no);
				authUser.setName(name);
			}
		} catch (SQLException e) {
			System.out.println("error:" + e);
		}
		this.close();
		return authUser;
	}

	// 사용자 정보 가져오기(회원정보 수정폼, no id password name gender)
	public UserVo getUser(int no) {
		UserVo userVo = null;

		this.getConnecting();

		try {

			// 3. SQL문 준비 / 바인딩 / 실행
			// SQL문 준비
			String query = "";
			query += " select  no, ";
			query += "         id, ";
			query += "         password, ";
			query += "         name, ";
			query += "         gender ";
			query += " from users ";
			query += " where no = ? ";

			// 바인딩
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, no);

			// 실행
			rs = pstmt.executeQuery();

			// 4.결과처리
			while (rs.next()) {
				int userno = rs.getInt("no");
				String id = rs.getString("id");
				String password = rs.getString("password");
				String name = rs.getString("name");
				String gender = rs.getString("gender");

				userVo = new UserVo(userno, id,password,name, gender);
			}

		} catch (SQLException e) {
			System.out.println("error:" + e);
		}

		this.close();

		return userVo;
	}



	public List<UserVo> userList() {
		List<UserVo> userList = new ArrayList<UserVo>();
		getConnecting();
		try {

			// 3. SQL문준비/ 바인딩/ 실행
			// SQL문준비
			String query = "";
			query += " select  no ";
			query += "         ,id ";
			query += "         ,password ";
			query += "         ,name ";
			query += "         ,gender ";
			query += " from    users ";

			// 바인딩
			pstmt = conn.prepareStatement(query);

			// 실행
			rs = pstmt.executeQuery();

			// 4.결과처리
			while (rs.next()) {

				int no = rs.getInt("no");
				String id = rs.getString("id");
				String password = rs.getString("password");
				String name = rs.getString("name");
				String gender = rs.getString("gender");

				UserVo userVo = new UserVo(no, id, password, name, gender);

				userList.add(userVo);

			}

		} catch (SQLException e) {
			System.out.println("error:" + e);
		}
		close();

		return userList;
	}

	public UserVo userList(int no) {
		UserVo userVo = null;
		getConnecting();
		try {

			// 3. SQL문준비/ 바인딩/ 실행
			// SQL문준비
			String query = "";
			query += " select  no ";
			query += "         ,id ";
			query += "         ,password ";
			query += "         ,name ";
			query += "         ,gender ";
			query += " from    users ";
			query += " where    no = ? ";

			// 바인딩
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, no);

			// 실행
			rs = pstmt.executeQuery();

			// 4.결과처리
			while (rs.next()) {

				int num = rs.getInt("no");
				String id = rs.getString("id");
				String password = rs.getString("password");
				String name = rs.getString("name");
				String gender = rs.getString("gender");

				userVo = new UserVo(num, id, password, name, gender);

			}

		} catch (SQLException e) {
			System.out.println("error:" + e);
		}
		close();

		return userVo;
	}

}
