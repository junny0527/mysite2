package com.javaex.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.javaex.dao.GuestBookDao;
import com.javaex.vo.GuestBookVo;
import com.javex.webutil.WebUtil;

@WebServlet("/gbc")
public class GuestController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 포스트 방식일때 한글깨짐 방지
		request.setCharacterEncoding("UTF-8");

		// action 파라미터꺼내기
		String action = request.getParameter("action");
		System.out.println(action);

		if ("addList".equals(action)) {
			// 다오를통해서 데이터 가져오기
			GuestBookDao guestbookDao = new GuestBookDao();
			List<GuestBookVo> bookList = guestbookDao.guestBookList();
			System.out.println(bookList);

			// request에 데이터 추가
			request.setAttribute("bList", bookList);

			// 데이터 + html --> jsp로 실행
			WebUtil.forward(request, response, "/WEB-INF/views/guestbook/addList.jsp");
		} else if ("add".equals(action)) {
			String name = request.getParameter("name");
			String password = request.getParameter("password");
			String content = request.getParameter("content");
			String regDate = request.getParameter("reg_date");
			GuestBookDao guestbookDao = new GuestBookDao();
			GuestBookVo guestbookVo = new GuestBookVo(name, password, content, regDate);

			guestbookDao.guestBookInsert(guestbookVo);

			// 리다이텍트 list
			WebUtil.redirect(request, response, "/mysite2/gbc?action=addList");
		} else if ("deleteForm".equals(action)) {
			GuestBookDao guestbookDao = new GuestBookDao();
			int no = Integer.parseInt(request.getParameter("no"));
			GuestBookVo guestbookVo = guestbookDao.guestBookList(no);
			System.out.println(guestbookVo);
			request.setAttribute("guestbookVo", guestbookVo);

			WebUtil.forward(request, response, "/WEB-INF/views/guestbook/deleteForm.jsp");

		} else if ("delete".equals(action)) {

			int no = Integer.parseInt(request.getParameter("no"));

			GuestBookDao guestbookDao = new GuestBookDao();
			GuestBookVo guestbookVo = guestbookDao.guestBookList(no);
			guestbookDao.guestBookDelete(guestbookVo);
			WebUtil.redirect(request, response, "/mysite2/gbc?action=addList");

		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
