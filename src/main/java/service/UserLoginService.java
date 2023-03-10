package service;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Query;

import database.SkAuthorizedInfo;
import database.SkUserInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.bytebuddy.asm.Advice.This;


class UserLoginInput {
	public String username;
	public String password;
}

class UserLoginResp extends HttpServiceResponseData {
	public String authToken;
	public long expiredTime;
}


/**
 * Servlet implementation class UserLoginService
 */
public final class UserLoginService extends HttpServiceFather {
	
	public final static Error UserNotExistError = new Error("用户不存在");
	public final static Error PasswordVerifyError = new Error("密码错误");
	
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
		super.doPost(request, response);
	}
	
	protected void postLogic() {
		Query query = eManager.createNamedQuery("SkUserInfo.findByUsername");
		query.setParameter("username", input.username);
		
		var resultList = query.getResultList();
		if (resultList.isEmpty()) {
			this.error = UserNotExistError;
			return;
		}
		
		SkUserInfo userInfo = (SkUserInfo) resultList.get(0);
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		byte[] bs = digest.digest(userInfo.getPassword().getBytes());
		String passwordMD5 = new BigInteger(1, bs).toString(16);
		if (!passwordMD5.equals(input.password)) {
			this.error = PasswordVerifyError;
			return;
		}
		
		String authToken = UUID.randomUUID().toString().replace("-", "");
		long expiredTime = new Date().getTime() + 10 * 24 * 60 * 60 * 1000;
		UserLoginResp loginResp = new UserLoginResp();
		loginResp.authToken = authToken;
		loginResp.expiredTime = expiredTime;
		
		query = eManager.createNamedQuery("SkAuthorizedInfo.findWithUserIdAndDeviceId");
		query.setParameter("deviceId", this.request.getHeader("device_id"));
		query.setParameter("userId", userInfo.getId());
		resultList = query.getResultList();
		if (resultList.size() != 0) {
			for (var obj : resultList) {
				SkAuthorizedInfo result = (SkAuthorizedInfo) obj;
				this.eManager.remove(result);
			}
		}
		
		SkAuthorizedInfo authorizedInfo = new SkAuthorizedInfo();
		authorizedInfo.setDeviceId(this.request.getHeader("device_id"));
		authorizedInfo.setExpiredTime(new Timestamp(expiredTime));
		authorizedInfo.setToken(authToken);
		authorizedInfo.setUserId(userInfo.getId());
		authorizedInfo.setCreateTime(new Timestamp(new Date().getTime()));
		this.eManager.persist(authorizedInfo);
		
		this.output.resp = loginResp;
	}

}

