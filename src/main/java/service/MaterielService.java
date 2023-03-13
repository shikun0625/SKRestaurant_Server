package service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import database.SkMaterielInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.HttpUtil;

class MaterielGetResp extends HttpServiceResponseData {
	public List<MaterielServiceOutputInfo> materiels;
}

class MaterielPostInput {
	public String name;
	public String remark;
	public int type;
	public int unit;
}

class MaterielPutInput {
	public int id;
	public String name;
	public String remark;
	public int type;
	public int unit;
}

class MaterielServiceOutputInfo extends HttpServiceResponseData {
	public int id;
	public int count;
	public String name;
	public String remark;
	public int type;
	public int unit;
	public long createTime;
}

/**
 * Servlet implementation class MaterielService
 */
public class MaterielService extends HttpServiceFather {
	private static final long serialVersionUID = 1L;
	private final static Error MaterielIdAndUserNotMatch = new Error("物料不属于该用户");
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
		List<MaterielServiceOutputInfo> materiels = new ArrayList<>();
		for (Object object : resultList) {
			SkMaterielInfo materiel = (SkMaterielInfo) object;
			MaterielServiceOutputInfo info = new MaterielServiceOutputInfo();
			info.id = materiel.getId();
			info.name = materiel.getName();
			info.count = materiel.getCount();
			info.type = materiel.getType();
			info.remark = materiel.getRemark();
			info.unit = materiel.getUnit();
			info.createTime = materiel.getCreateTime().getTime();
			materiels.add(info);
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
			logger.info("Materiel Post : " + (System.currentTimeMillis() - startTime));
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
			logger.info("Materiel Post : " + (System.currentTimeMillis() - startTime));
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
		
		MaterielServiceOutputInfo resp = new MaterielServiceOutputInfo();
		resp.id = info.getId();
		resp.name = info.getName();
		resp.count = info.getCount();
		resp.type = info.getType();
		resp.remark = info.getRemark();
		resp.unit = info.getUnit();
		resp.createTime = info.getCreateTime().getTime();
		output.resp = resp;

		// 服务结束处理，写入response数据
		this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);

		logger.info("Materiel Post : " + (System.currentTimeMillis() - startTime));

	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
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
			logger.info("Materiel Put : " + (System.currentTimeMillis() - startTime));
			return;
		}
		
		int userId = httpUtil.getUserIdByToken(request, eManager);

		// 转换input
		MaterielPutInput input = null;
		try {
			input = new Gson().fromJson(body.toString(), MaterielPutInput.class);
		} catch (JsonSyntaxException e) {
			error = HTTPRequestParameterError;
			this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);
			logger.info("Materiel Put : " + (System.currentTimeMillis() - startTime));
			return;
		}
		
		Query query = eManager.createNamedQuery("SkMaterielInfo.findByIdAndUser");
		query.setParameter("userId", userId);
		query.setParameter("id", input.id);
		SkMaterielInfo materielInfo = null;
		try {
			materielInfo = (SkMaterielInfo) query.getSingleResult();
		} catch (Exception e) {
			error = MaterielIdAndUserNotMatch;
			this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);
			logger.info("Materiel Put : " + (System.currentTimeMillis() - startTime));
			return;
		}
		
		materielInfo.setName(input.name);
		materielInfo.setRemark(input.remark);
		materielInfo.setType(input.type);
		materielInfo.setUnit(input.unit);
		materielInfo = eManager.merge(materielInfo);
		
		MaterielServiceOutputInfo resp = new MaterielServiceOutputInfo();
		resp.id = materielInfo.getId();
		resp.name = materielInfo.getName();
		resp.count = materielInfo.getCount();
		resp.type = materielInfo.getType();
		resp.remark = materielInfo.getRemark();
		resp.unit = materielInfo.getUnit();
		resp.createTime = materielInfo.getCreateTime().getTime();
		output.resp = resp;

		// 服务结束处理，写入response数据
		this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);

		logger.info("Materiel Put : " + (System.currentTimeMillis() - startTime));
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
