package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.HttpServiceOutput;

public final class HttpUtil {

	public final static Error AuthorizedError = new Error("用户验证失败");
	public final static Error AuthorizedExpiredError = new Error("用户验证过期");
	public final static Error HTTPBodyReadError = new Error("请求数据读取失败");

	public Error checkHttpRequestAuthorizedError(EntityManager eManager, HttpServletRequest request, String bodyStr, boolean checkUser) {
		String timeString = request.getHeader("request_time");
		String deviceIdString = request.getHeader("device_id");
		String deviceNameString = request.getHeader("device");
		String osString = request.getHeader("os");
		String osVersionString = request.getHeader("os_version");
		String requestIdString = request.getHeader("request_id");
		String userTokenString = request.getHeader("user_token");
		String requestAuth = request.getHeader("request_auth");

		String headerString = timeString + deviceIdString + deviceNameString + osString + osVersionString
				+ requestIdString;
		if (userTokenString != null) {
			headerString = headerString + userTokenString;
		}

		String requestString = "skrestaurant_key" + bodyStr + headerString;
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		byte[] bs = digest.digest(requestString.getBytes());
		String requestToken = new BigInteger(1, bs).toString(16);
		if (!requestToken.equals(requestAuth)) {
			return AuthorizedError;
		}

		if (checkUser) {
			Query query = eManager.createNamedQuery("SkAuthorizedInfo.findAuthorizedNotExpired");
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
			while ((str = br.readLine()) != null) {
				wholeStr += str;
			}
			return wholeStr;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setStatus(HttpServletResponse response, HttpServiceOutput output, Error error) {
		if (error == null) {
			response.setStatus(200);
			output.status = 200;
		} else if (error == AuthorizedError || error == AuthorizedExpiredError) {
			response.setStatus(403);
			output.errorMessage = error.getMessage();
			output.status = 403;
		} else if (error == HTTPBodyReadError) {
			response.setStatus(500);
			output.errorMessage = error.getMessage();
			output.status = 500;
		} else {
			response.setStatus(500);
			output.errorMessage = error.getMessage();
			output.status = 500;
		}
	}
}
