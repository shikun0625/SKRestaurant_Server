package service;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnit;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.HttpUtil;
import util.JPAUtil;

public class HttpServiceFather extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected EntityManager eManager;
	protected EntityTransaction eTransaction;
	protected HttpServiceOutput output;
	protected UserLoginInput input;
	protected HttpServletResponse response;
	protected HttpServletRequest request;
	protected Error error;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.eManager = JPAUtil.sharedEntityManagerFactory().createEntityManager();
		this.eTransaction = this.eManager.getTransaction();
		this.eTransaction.begin();

		this.output = new HttpServiceOutput();
		this.request = request;
		this.response = response;
		this.response.setCharacterEncoding("utf-8");
		
		String body = new HttpUtil().getBodyString(request);
		if (body == null) {
			error = HttpUtil.HTTPBodyReadError;
		}
		error = new HttpUtil().checkHttpRequestAuthorizedError(eManager, request, body, false);
		if (error == null) {
			input = new Gson().fromJson(body, UserLoginInput.class);
			postLogic();
		}

		this.eManager.flush();
		this.eManager.close();
		this.eTransaction.commit();
		new HttpUtil().setStatus(response, output, error);
		this.response.getWriter().write(new Gson().toJson(output));
	}

	protected void postLogic() {
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.eTransaction.begin();

		this.output = new HttpServiceOutput();
		this.request = request;
		this.response = response;

//		String body = new HttpUtil().getBodyString(request);
//		if (body == null) {
//			error = HttpUtil.HTTPBodyReadError;
//		}
//		error = new HttpUtil().checkHttpRequestAuthorizedError(eManager, request, body, false);
//		if (error == null) {
//			input = new Gson().fromJson(body, UserLoginInput.class);
//			getLogic();
//		}

		this.eManager.flush();
		this.eManager.close();
		this.eTransaction.commit();
		new HttpUtil().setStatus(response, output, error);
		this.response.getWriter().write(new Gson().toJson(output));
	}

	protected void getLogic() {
	}
}
