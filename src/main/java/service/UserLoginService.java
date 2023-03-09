package service;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.tomcat.jakartaee.commons.io.IOUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.HttpUtil;

/**
 * Servlet implementation class UserLoginService
 */
public final class UserLoginService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserLoginService() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		Error error;

		BufferedReader bReader = request.getReader();
		String body = new HttpUtil().getBodyString(request);
		if (body == null) {
			error = HttpUtil.HTTPBodyReadError;
		}
		error = new HttpUtil().checkHttpRequestAuthorizedError(request, body, false);
		if (error == null) {

		}
		
		
		
		new HttpUtil().setStatus(response, error);
		
	}

}
