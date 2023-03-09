package util;

import javax.persistence.Query;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import database.SkAuthorizedInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class HttpUtil {

	public final static Error AuthorizedError = new Error("用户验证失败");
	public final static Error AuthorizedExpiredError = new Error("用户验证过期");
	public final static Error HTTPBodyReadError = new Error("请求数据读取失败");

	private static EntityManagerFactory eManagerFactory = Persistence.createEntityManagerFactory("SKRestaurant_Server");

	public Error checkHttpRequestAuthorizedError(HttpServletRequest request, String bodyStr, boolean checkUser) {

		EntityManager entityManager = eManagerFactory.createEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		String timeString = request.getHeader("request_time");
		String deviceIdString = request.getHeader("device_id");
		String deviceNameString = request.getHeader("device");
		String osString = request.getHeader("os");
		String osVersionString = request.getHeader("os_version");
		String requestIdString = request.getHeader("request_id");
		String userTokenString = request.getHeader("user_token");
		
		String headerString = timeString + deviceIdString + deviceNameString + osString + osVersionString + requestIdString;
		if (userTokenString != null) {
			headerString = headerString + userTokenString;
		}
		

		String requeString = "skrestaurant_key" + bodyStr;

		if (checkUser) {
			
			Query query = entityManager.createNamedQuery("SkAuthorizedInfo.findAuthorizedNotExpired");
			query.setParameter("token", userTokenString);
			query.setParameter("deviceId", deviceIdString);
			try {
				query.getSingleResult();
			} catch (Exception e) {
				return AuthorizedError;
			}

			query.setParameter("expiredTime", System.currentTimeMillis());
			try {
				query.getSingleResult();
			} catch (Exception e) {
				return AuthorizedExpiredError;
			}

		}

		return null;
	}
	
	public String getBodyString(HttpServletRequest request) {
		BufferedReader br;
		try {
			br = request.getReader();
			String str, wholeStr = "";
			while((str = br.readLine()) != null){
				wholeStr += str;
			}
			return wholeStr;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void setStatus(HttpServletResponse response, Error error) {
		
	}
}
