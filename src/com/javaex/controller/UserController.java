package com.javaex.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.javaex.dao.UserDao;
import com.javex.webutil.WebUtil;
import com.javaex.vo.UserVo;

@WebServlet("/user")
public class UserController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String action = request.getParameter("action");
		
		if("joinForm".equals(action)) { 
			System.out.println("userController > 회원가입폼");
			
			//포워드
			WebUtil.forward(request, response, "/WEB-INF/views/user/joinForm.jsp");
			
		}else if("join".equals(action)) { 
			System.out.println("userController > 회원가입");
			
			//파라미터 값 추출 --> vo만들기
			String id = request.getParameter("id");
			String password = request.getParameter("password");
			String name = request.getParameter("name");
			String gender = request.getParameter("gender");

			UserVo userVo = new UserVo(id, password, name, gender);
			
			//Dao 사용
			UserDao userDao = new UserDao();
			userDao.insert(userVo);
			
			//포워드
			WebUtil.forward(request, response, "/WEB-INF/views/user/joinOk.jsp");
			
		}else if("loginForm".equals(action)) { 
			System.out.println("userController > 로그인 폼");
			
			//포워드
			WebUtil.forward(request, response, "/WEB-INF/views/user/loginForm.jsp");
		
		}else if("login".equals(action)) { 
			System.out.println("userController > 로그인");
		
			//파라미터 값 추출
			String id = request.getParameter("id");
			String password = request.getParameter("password");
			
			//Dao 사용
			UserDao userDao = new UserDao();
			UserVo authVo = userDao.getUser(id, password);
			
			if(authVo == null ) { 
				System.out.println("로그인실패");
				
				//리다이렉트
				WebUtil.redirect(request, response, "/mysite2/user?action=loginForm&result=fail");
				
			}else { 
				System.out.println("로그인성공");
				
				//세션영역 로그인한 사용자 데이터 추가
				HttpSession session = request.getSession();
				session.setAttribute("authUser", authVo);
				
				//리다이렉트
				WebUtil.redirect(request, response, "/mysite2/main");
			}
			
		}else if("logout".equals(action)) {
			System.out.println("userController > 로그아웃");
			
			//세션영역 데이터 제거
			HttpSession session = request.getSession();
			session.removeAttribute("authUser");
			session.invalidate();
			
			//리다이렉트
			WebUtil.redirect(request, response, "/mysite2/main");
		
		}else if("modifyForm".equals(action)) {
			System.out.println("userController > 수정폼");
			
			//사용자 정보를 가져오기
			HttpSession session = request.getSession();
			int no = ((UserVo)session.getAttribute("authUser")).getNo();
			
			
			//Dao 사용
			UserDao userDao = new UserDao();
			UserVo userVo = userDao.getUser(no);	
		
			//포워드
			request.setAttribute("userVo", userVo);
			WebUtil.forward(request, response, "/WEB-INF/views/user/modifyForm.jsp");
			
		}else if("modify".equals(action)) {
			System.out.println("userController > 수정");
			
			//세션에서 no
			HttpSession session =	request.getSession();
			UserVo authUser = (UserVo)session.getAttribute("authUser");
			int no = authUser.getNo();
			
			//파라미터 꺼내기
			String password =  request.getParameter("password");
			String name = request.getParameter("name");
			String gender = request.getParameter("gender");
			
			
			//VO에 담기
			UserVo userVo = new UserVo();
			userVo.setNo(no);
			userVo.setPassword(password);
			userVo.setName(name);
			userVo.setGender(gender);
			System.out.println(userVo);
			
			
			//다오만들어서 바꿔주기
			UserDao userDao = new UserDao();
			
			userDao.update(userVo);
			
			
			WebUtil.redirect(request, response, "/mysite2/main");
			
		}
	}
		
	

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}