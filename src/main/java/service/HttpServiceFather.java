package service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.google.gson.Gson;

import database.SkRequestInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.HttpUtil;

public class HttpServiceFather extends HttpServlet {
	public final static Error HTTPRequestParameterError = new Error("请求参数错误");
	
	private static final long serialVersionUID = 1L;

	private EntityManagerFactory entityManagerFactory;
	protected EntityManager eManager;
	protected EntityTransaction eTransaction;
	protected HttpServiceOutput output;
	protected HttpServletResponse response;
	protected HttpServletRequest request;
	protected Error error;
	protected String body;
	protected boolean needCheckUser = true;
	
	@Override
	public void init() throws ServletException {
		super.init();
		this.entityManagerFactory = Persistence.createEntityManagerFactory("SKRestaurant_Server");
	}
	
	@Override
	public void destroy() {
		super.destroy();
		this.entityManagerFactory.close();
	}
	
	protected void beforeLogic (HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		this.eManager = this.entityManagerFactory.createEntityManager();
		this.eTransaction = this.eManager.getTransaction();
		this.eTransaction.begin();

		this.output = new HttpServiceOutput();
		this.request = request;
		this.request.setCharacterEncoding("utf-8");
		this.response = response;
		
		this.body = new HttpUtil().getBodyString(request);
		error = new HttpUtil().checkHttpRequestAuthorized(eManager, request, body, needCheckUser);
	}
	
	protected void afterLogic() throws IOException {
		SkRequestInfo skRequestInfo = new SkRequestInfo();
		skRequestInfo.setBody(body);
		skRequestInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
		skRequestInfo.setRequestId(this.request.getHeader("request_id"));
		skRequestInfo.setParameter(this.request.getQueryString());
		skRequestInfo.setHost(this.request.getRemoteHost());
		skRequestInfo.setAction(this.request.getServletPath());
		skRequestInfo.setStatus(output.status);
		skRequestInfo.setReason(output.errorMessage);
		skRequestInfo.setDeviceId(this.request.getHeader("device_id"));
		skRequestInfo.setDevice(this.request.getHeader("device"));
		skRequestInfo.setOsVersion(this.request.getHeader("os_version"));
		skRequestInfo.setOs(this.request.getHeader("os"));
		
		this.eManager.persist(skRequestInfo);

		this.eManager.flush();
		this.eManager.close();
		this.eTransaction.commit();
		new HttpUtil().setStatus(response, output, error);
		this.response.getWriter().write(new Gson().toJson(output));
	}
}
