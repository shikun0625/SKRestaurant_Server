package service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import database.SkMaterielInfo;
import util.HttpUtil;
import util.LoggerUtil;

class MaterielGetResp extends HttpServiceResponseData {
	public List<MaterielRespInfo> materiels;
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

class MaterielRespInfo extends HttpServiceResponseData {
	public MaterielRespInfo() {

	}

	public MaterielRespInfo(SkMaterielInfo materielInfo) {
		this.id = materielInfo.getId();
		this.count = materielInfo.getCount();
		this.name = materielInfo.getName();
		this.remark = materielInfo.getRemark();
		this.type = materielInfo.getType();
		this.unit = materielInfo.getUnit();
		this.createTime = materielInfo.getCreateTime().getTime();
	}

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
@WebServlet("/materiel")
public final class MaterielService extends HttpServiceFather {
	private static final long serialVersionUID = 1L;
	private final static Error MaterielIdNotMatchUser = new Error("物料不属于该用户");
	private static Logger logger = LoggerUtil.getLogger(MaterielService.class.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MaterielService() {
		super();
		// TODO Auto-generated constructor stub
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

		Query query = null;
		String typeFromInput = request.getParameter("type");
		if (typeFromInput == null) {
			query = eManager.createNamedQuery("SkMaterielInfo.findByUser");
		} else {
			query = eManager.createNamedQuery("SkMaterielInfo.findByTypeAndUser");
			query.setParameter("type", Integer.parseInt(typeFromInput));
		}
		query.setParameter("userId", userId);
		var resultList = query.getResultList();
		MaterielGetResp resp = new MaterielGetResp();
		List<MaterielRespInfo> materiels = new ArrayList<>();
		for (Object object : resultList) {
			SkMaterielInfo materiel = (SkMaterielInfo) object;
			MaterielRespInfo info = new MaterielRespInfo(materiel);
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

		MaterielRespInfo resp = new MaterielRespInfo(info);
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
			error = MaterielIdNotMatchUser;
			this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);
			logger.info("Materiel Put : " + (System.currentTimeMillis() - startTime));
			return;
		}

		materielInfo.setName(input.name);
		materielInfo.setRemark(input.remark);
		materielInfo.setType(input.type);
		materielInfo.setUnit(input.unit);
		materielInfo = eManager.merge(materielInfo);

		MaterielRespInfo resp = new MaterielRespInfo(materielInfo);
		output.resp = resp;

		// 服务结束处理，写入response数据
		this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);

		logger.info("Materiel Put : " + (System.currentTimeMillis() - startTime));
	}

}
