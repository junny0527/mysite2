package com.javaex.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.javaex.dao.UserDao;
import com.javaex.vo.UserVo;
import com.javex.webutil.WebUtil;

@WebServlet("/user")
public class UserController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		System.out.println(action);

		if ("joinForm".equals(action)) {
			WebUtil.forward(request, response, "/WEB-INF/views/user/joinForm.jsp");
		} else if ("join".equals(action)) {
			String id = request.getParameter("id");
			String password = request.getParameter("password");
			String name = request.getParameter("name");
			String gender = request.getParameter("gender");

			// vo만들기
			UserVo userVo = new UserVo(id, password, name, gender);
			System.out.println(userVo);
			// Dao를 이용해 저장하기
			UserDao userDao = new UserDao();
			userDao.insert(userVo);
			// 포워드
			WebUtil.forward(request, response, "/WEB-INF/views/user/joinOk.jsp");
		} else if ("loginForm".equals(action)) {
			// 포워드
			WebUtil.forward(request, response, "/WEB-INF/views/user/loginForm.jsp");
		} else if ("login".equals(action)) {
			String id = request.getParameter("id");
			String password = request.getParameter("password");

			UserVo userVo = new UserVo(id, password);
			// dao
			UserDao userDao = new UserDao();
			UserVo authUser = userDao.getUser(userVo);

			// authUSer 주소값이있으면 -->로그인성공
			// authUser null이면 -->로그인실패
			if (authUser == null) {
				System.out.println("로그인실패");
				WebUtil.redirect(request, response, "/mysite2/user?action=loginForm");
			} else {
				System.out.println("로그인 성공");
				HttpSession session = request.getSession();
				session.setAttribute("authUser", authUser);

				WebUtil.redirect(request, response, "/mysite2/main");
			}

		} else if ("logout".equals(action)) {
			HttpSession session = request.getSession();
			session.removeAttribute("authUser");
			session.invalidate();

			WebUtil.redirect(request, response, "/mysite2/main");
		} else if ("modifyForm".equals(action)) { // 수정폼
			System.out.println("UserController>modifyForm");

			// 로그인한 사용자의 no 값을 세션에서 가져오기
			HttpSession session = request.getSession();
			UserVo authUser = (UserVo) session.getAttribute("authUser");
			int no = authUser.getNo();

			// no 로 사용자 정보 가져오기
			UserDao userDao = new UserDao();
			UserVo userVo = userDao.getUser(no); // no id password name gender

			// request 의 attribute 에 userVo 는 넣어서 포워딩
			request.setAttribute("userVo", userVo);
			WebUtil.forward(request, response, "/WEB-INF/views/user/modifyForm.jsp");
		} else if ("modify".equals(action)) {
			int no = Integer.parseInt(request.getParameter("no"));
			String password = request.getParameter("password");
			String name = request.getParameter("name");
			String gender = request.getParameter("gender");

			// VO에 담기
			UserVo authUser = new UserVo();
			authUser.setNo(no);
			authUser.setPassword(password);
			authUser.setName(name);
			authUser.setGender(gender);
			System.out.println(authUser);

			// 다오만들어서 바꿔주기
			UserDao userDao = new UserDao();

			userDao.update(authUser);

			HttpSession session = request.getSession();
			session.setAttribute("authUser", authUser);

			WebUtil.redirect(request, response, "/mysite2/main");

		} 
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

}
