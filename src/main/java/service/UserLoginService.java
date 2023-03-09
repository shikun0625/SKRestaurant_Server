package service;

import java.io.IOException;

import javax.persistence.Query;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.HttpUtil;
import util.JPAUtil;


class UserLoginInput {
	public String username;
	public String password;
}

class UserLoginResp extends HttpServiceResponseData {
	
}


/**
 * Servlet implementation class UserLoginService
 */
public final class UserLoginService extends HttpServiceFather {
	
	public final static Error UserNotExistError = new Error("用户不存在");
	
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
		this.eManager = JPAUtil.sharedEntityManagerFactory().createEntityManager();
		this.eTransaction = this.eManager.getTransaction();
		this.eTransaction.begin();
		
		this.output = new HttpServiceOutput();
		this.response = response;

		String body = new HttpUtil().getBodyString(request);
		if (body == null) {
			error = HttpUtil.HTTPBodyReadError;
		}
		error = new HttpUtil().checkHttpRequestAuthorizedError(eManager, request, body, false);
		if (error == null) {
			input = new Gson().fromJson(body, UserLoginInput.class);
			postLogic();
		}
		
		eTransaction.commit();
		new HttpUtil().setStatus(response, output, error);
		this.response.getWriter().write(new Gson().toJson(output));
	}
	
	private void postLogic() throws IOException {
		Query query = eManager.createNamedQuery("SkUserInfo.findByUsername");
		query.setParameter("username", input.username);
		
		var resultList = query.getResultList();
		if (resultList.isEmpty()) {
			this.error = UserNotExistError;
			return;
		}
		
		UserLoginResp loginResp = new UserLoginResp();
		this.output.resp = loginResp;
	}

}

