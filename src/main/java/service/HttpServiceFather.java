package service;

import java.io.IOException;
import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import database.SkRequestInfo;
import util.HttpUtil;

public class HttpServiceFather extends HttpServlet {
	public final static Error HTTPRequestParameterError = new Error("请求参数错误");

	private static final long serialVersionUID = 1L;

	protected EntityManagerFactory entityManagerFactory;
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

	protected void afterLogic(HttpServletRequest request, HttpServletResponse response, String body,
			HttpServiceOutput output, EntityManager eManager, Error error, EntityTransaction eTransaction,
			HttpUtil httpUtil) throws IOException {
		SkRequestInfo skRequestInfo = new SkRequestInfo();
		if (body.length() > 0) {
			skRequestInfo.setBody(body);
		}
		skRequestInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
		skRequestInfo.setRequestId(request.getHeader("request_id"));
		skRequestInfo.setParameter(request.getQueryString());
		skRequestInfo.setHost(request.getRemoteHost());
		skRequestInfo.setAction(request.getServletPath());
		skRequestInfo.setStatus(output.status);
		skRequestInfo.setReason(output.errorMessage);
		skRequestInfo.setDeviceId(request.getHeader("device_id"));
		skRequestInfo.setDevice(request.getHeader("device"));
		skRequestInfo.setOsVersion(request.getHeader("os_version"));
		skRequestInfo.setOs(request.getHeader("os"));

		eManager.persist(skRequestInfo);

		eManager.flush();
		eManager.close();
		eTransaction.commit();
		httpUtil.setStatus(response, output, error);
		response.setCharacterEncoding("utf-8");
		response.getWriter().write(new Gson().toJson(output));
	}
}
