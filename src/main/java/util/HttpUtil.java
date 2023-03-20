package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import database.SkAuthorizedInfo;
import service.HttpServiceOutput;

public final class HttpUtil {

	public final static Error AuthorizedError = new Error("请求验证失败");
	public final static Error UserAuthorizedError = new Error("用户验证失败");
	public final static Error UserAuthorizedExpiredError = new Error("用户验证过期");
	public final static Error HTTPBodyReadError = new Error("请求数据读取失败");
	public final static Error HTTPRepeatRequestError = new Error("重复请求");

	public final static int USER_ID_NOT_FOUND = -899;

	public static Logger logger = LoggerUtil.getLogger(HttpUtil.class.getName());

	public Error checkHttpRequestAuthorized(EntityManager eManager, HttpServletRequest request, String bodyStr,
			boolean checkUser) {
		String timeString = request.getHeader("request_time");
		String deviceIdString = request.getHeader("device_id");
		String deviceNameString = request.getHeader("device");
		String osString = request.getHeader("os");
		String osVersionString = request.getHeader("os_version");
		String requestIdString = request.getHeader("request_id");
		String userTokenString = request.getHeader("user_token");
		String requestAuth = request.getHeader("request_auth");

		Query query = eManager.createNamedQuery("SkRequestInfo.findByRequestId");
		query.setParameter("requestId", "requestIdString");

		try {
			query.getSingleResult();
			return HTTPRepeatRequestError;
		} catch (Exception e) {

		}
		String headerString = timeString + deviceIdString + deviceNameString + osString + osVersionString
				+ requestIdString;
		if (userTokenString != null) {
			headerString = headerString + userTokenString;
		}

		String requestString = "skrestaurant_key" + bodyStr + headerString;
		logger.info("request string : " + requestString);
		String requestToken = getMD5(requestString);
		logger.info("request token : " + requestToken);
		if (!requestToken.equals(requestAuth)) {
			return AuthorizedError;
		}

		if (checkUser) {
			query = eManager.createNamedQuery("SkAuthorizedInfo.findByToken");
			query.setParameter("token", userTokenString);
			try {
				SkAuthorizedInfo authorizedInfo = (SkAuthorizedInfo) query.getSingleResult();
				if (authorizedInfo.getExpiredTime().before(new Date())) {
					return UserAuthorizedExpiredError;
				}
			} catch (Exception e) {
				return UserAuthorizedError;
			}

		}
		return null;
	}

	public int getUserIdByToken(HttpServletRequest request, EntityManager eManager) {
		Query query = eManager.createNamedQuery("SkAuthorizedInfo.findByToken");
		query.setParameter("token", request.getHeader("user_token"));
		try {
			SkAuthorizedInfo authorizedInfo = (SkAuthorizedInfo) query.getSingleResult();
			return authorizedInfo.getUserId();
		} catch (Exception e) {
			return USER_ID_NOT_FOUND;
		}
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
		} else if (error == AuthorizedError || error == UserAuthorizedExpiredError || error == UserAuthorizedError) {
			response.setStatus(403);
			output.errorMessage = error.getMessage();
			output.status = 403;
		} else {
			response.setStatus(500);
			output.errorMessage = error.getMessage();
			output.status = 500;
		}
	}

	public String getMD5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (Exception e) {
			return null;
		}
	}
}
