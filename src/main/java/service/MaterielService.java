package service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import database.SkAuthorizedInfo;
import database.SkMaterielInfo;
import database.SkUserInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.HttpUtil;

class MaterielGetResp extends HttpServiceResponseData {
	public List<SkMaterielInfo> materiels;
}

class MaterielPostInput {
	public String name;
	public String remark;
	public int type;
	public int unit;
}

/**
 * Servlet implementation class MaterielService
 */
public class MaterielService extends HttpServiceFather {
	private static final long serialVersionUID = 1L;
	public static Logger logger = Logger.getLogger(MaterielService.class.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MaterielService() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		long startTime = System.currentTimeMillis();

		request.setCharacterEncoding("utf-8");
		EntityManager eManager = this.entityManagerFactory.createEntityManager();
		EntityTransaction eTransaction = eManager.getTransaction();
		eTransaction.begin();
		HttpServiceOutput output = new HttpServiceOutput();
		Error error = null;
		HttpUtil httpUtil = new HttpUtil();
		String body = httpUtil.getBodyString(request);
		error = httpUtil.checkHttpRequestAuthorized(eManager, request, body, needCheckUser);
		if (error != null) {
			this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);
			logger.info("Materiel Get : " + (System.currentTimeMillis() - startTime));
			return;
		}

		int userId = httpUtil.getUserIdByToken(request, eManager);

		Query query = eManager.createNamedQuery("SkMaterielInfo.findByUser");
		query.setParameter("userId", userId);
		var resultList = query.getResultList();
		MaterielGetResp resp = new MaterielGetResp();
		List<SkMaterielInfo> materiels = new ArrayList<>();
		for (Object object : resultList) {
			SkMaterielInfo materiel = (SkMaterielInfo) object;
			materiels.add(materiel);
		}
		resp.materiels = materiels;

		output.resp = resp;

		this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);

		logger.info("Materiel Get : " + (System.currentTimeMillis() - startTime));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		long startTime = System.currentTimeMillis();

		request.setCharacterEncoding("utf-8");
		EntityManager eManager = this.entityManagerFactory.createEntityManager();
		EntityTransaction eTransaction = eManager.getTransaction();
		eTransaction.begin();
		HttpServiceOutput output = new HttpServiceOutput();
		Error error = null;
		HttpUtil httpUtil = new HttpUtil();
		String body = httpUtil.getBodyString(request);
		error = httpUtil.checkHttpRequestAuthorized(eManager, request, body, needCheckUser);
		if (error != null) {
			this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);
			logger.info("UserLogin Post : " + (System.currentTimeMillis() - startTime));
			return;
		}
		
		int userId = httpUtil.getUserIdByToken(request, eManager);

		// 转换input
		MaterielPostInput input = null;
		try {
			input = new Gson().fromJson(body.toString(), MaterielPostInput.class);
		} catch (JsonSyntaxException e) {
			error = HTTPRequestParameterError;
			this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);
			logger.info("UserLogin Post : " + (System.currentTimeMillis() - startTime));
			return;
		}
		
		SkMaterielInfo info = new SkMaterielInfo();
		info.setCount(0);
		info.setCreateTime(new Timestamp(System.currentTimeMillis()));
		info.setName(input.name);
		info.setRemark(input.remark);
		info.setType(input.type);
		info.setUnit(input.unit);
		info.setUser(userId);
		
		eManager.persist(info);

		// 服务结束处理，写入response数据
		this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);

		logger.info("UserLogin Post : " + (System.currentTimeMillis() - startTime));

	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
