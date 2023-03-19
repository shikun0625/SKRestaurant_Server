package service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import database.SkMaterielActionInfo;
import database.SkMaterielInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.HttpUtil;
import util.LoggerUtil;

class MaterielActionPostInput {
	public int materielId;
	public int delta;
	public int actionType;
	public int reason;
}

/**
 * Servlet implementation class MaterielActionService
 */
public final class MaterielActionService extends HttpServiceFather {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerUtil.getLogger(MaterielActionService.class.getName());
	private final static Error MaterielNotExistError = new Error("物料不存在");

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MaterielActionService() {
		super();
		// TODO Auto-generated constructor stub
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
			logger.info("Materiel Action Post : " + (System.currentTimeMillis() - startTime));
			return;
		}

		int userId = httpUtil.getUserIdByToken(request, eManager);

		// 转换input
		MaterielActionPostInput input = null;
		try {
			input = new Gson().fromJson(body.toString(), MaterielActionPostInput.class);
		} catch (JsonSyntaxException e) {
			error = HTTPRequestParameterError;
			this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);
			logger.info("Materiel Action Post : " + (System.currentTimeMillis() - startTime));
			return;
		}

		Query query = eManager.createNamedQuery("SkMaterielInfo.findByIdAndUser");
		query.setParameter("userId", userId);
		query.setParameter("id", input.materielId);
		SkMaterielInfo materielInfo = null;
		try {
			materielInfo = (SkMaterielInfo) query.getSingleResult();
		} catch (Exception e) {
			error = MaterielNotExistError;
			this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);
			logger.info("Materiel Put : " + (System.currentTimeMillis() - startTime));
			return;
		}

		materielInfo.setCount(materielInfo.getCount() + input.delta);

		materielInfo = eManager.merge(materielInfo);

		SkMaterielActionInfo actionInfo = new SkMaterielActionInfo();
		actionInfo.setActionType(input.actionType);
		actionInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
		actionInfo.setDelta(input.delta);
		actionInfo.setId(UUID.randomUUID().toString().replace("-", ""));
		actionInfo.setMaterielId(input.materielId);
		actionInfo.setReason(input.reason);
		eManager.persist(actionInfo);

		MaterielRespInfo resp = new MaterielRespInfo(materielInfo);
		output.resp = resp;

		// 服务结束处理，写入response数据
		this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);

		logger.info("Materiel Action Post : " + (System.currentTimeMillis() - startTime));
	}

}
