package com.javaex.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.javaex.dao.GuestBookDao;
import com.javex.webutil.WebUtil;
import com.javaex.vo.GuestBookVo;

@WebServlet("/guestbook")
public class GuestController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		String action = request.getParameter("action");

		if ("add".equals(action)) {
			System.out.println("guestbookController > 추가");

			//vo만들기
			String name = request.getParameter("name");
			String password = request.getParameter("pass");
			String content = request.getParameter("content");

			//Dao 사용
			GuestBookDao dao = new GuestBookDao();
			GuestBookVo vo = new GuestBookVo(name, password, content);
			dao.insert(vo);

			//리다이렉트
			WebUtil.redirect(request, response, "/mysite2/guestbook");

		} else if ("deleteForm".equals(action)) {
			System.out.println("guestbookController > 삭제폼");
			
			//포워드
			WebUtil.forward(request, response, "/WEB-INF/views/guestbook/deleteForm.jsp");

		} else if ("delete".equals(action)) {
			System.out.println("guestbookController > 삭제");
			
			//vo만들기
			int no = Integer.parseInt(request.getParameter("no"));
			String password = request.getParameter("pass");

			GuestBookVo vo = new GuestBookVo();
			vo.setNo(no);
			vo.setPassword(password);

			//Dao 사용
			GuestBookDao dao = new GuestBookDao();
			dao.delete(vo);

			//리다이렉트
			WebUtil.redirect(request, response, "/mysite2/guestbook");
			
		} else {
			System.out.println("guestbookController > 리스트");
			
			//Dao 사용
			GuestBookDao dao = new GuestBookDao();
			List<GuestBookVo> gList = dao.getList();

			//포워드 
			request.setAttribute("guestList", gList);
			WebUtil.forward(request, response, "/WEB-INF/views/guestbook/addList.jsp");
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}