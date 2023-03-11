package service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Query;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import database.SkAuthorizedInfo;
import database.SkUserInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.HttpUtil;

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
		this.needCheckUser = false;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 服务开始前统一处理
		this.beforeLogic(request, response);
		if (this.error != null) {
			this.afterLogic();
			return;
		}

		// 转换input
		UserLoginInput input = null;
		try {
			input = new Gson().fromJson(body, UserLoginInput.class);
		} catch (JsonSyntaxException e) {
			this.error = HTTPRequestParameterError;
			this.afterLogic();
			return;
		}

		// 查询是否有用户
		Query query = eManager.createNamedQuery("SkUserInfo.findByUsername");
		query.setParameter("username", input.username);
		var resultList = query.getResultList();
		if (resultList.isEmpty()) {
			this.error = UserNotExistError;
			this.afterLogic();
			return;
		}

		// 密码是否正确
		SkUserInfo userInfo = (SkUserInfo) resultList.get(0);
		String passwordMD5 = new HttpUtil().getMD5(userInfo.getPassword());
		if (!passwordMD5.equals(input.password)) {
			this.error = PasswordVerifyError;
			this.afterLogic();
			return;
		}

		// 写入授权信息
		String authToken = UUID.randomUUID().toString().replace("-", "");
		long expiredTime = System.currentTimeMillis() + 10 * 24 * 60 * 60 * 1000;
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
		authorizedInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
		this.eManager.persist(authorizedInfo);

		this.output.resp = loginResp;

		// 服务结束处理，写入response数据
		this.afterLogic();
	}

}
