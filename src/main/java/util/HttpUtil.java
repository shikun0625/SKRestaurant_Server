package util;

import javax.persistence.Query;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import database.SkAuthorizedInfo;
import jakarta.servlet.http.HttpServletRequest;

public final class HttpUtil {

	public final static Error AuthorizedError = new Error("用户验证失败");
	public final static Error AuthorizedExpiredError = new Error("用户验证过期");

	private static EntityManagerFactory eManagerFactory = Persistence.createEntityManagerFactory("SKRestaurant_Server");

	public Error checkHttpRequestAuthorizedError(HttpServletRequest request, boolean checkUser) {

		EntityManager entityManager = eManagerFactory.createEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		String timeString = request.getHeader("request_time");
		String deviceIdString = request.getHeader("device_id");
		String deviceNameString = request.getHeader("device");
		String osString = request.getHeader("os");
		String osVersionString = request.getHeader("os_version");
		String requestIdString = request.getHeader("request_id");

		if (checkUser) {
			String userTokenString = request.getHeader("user_token");
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
}
