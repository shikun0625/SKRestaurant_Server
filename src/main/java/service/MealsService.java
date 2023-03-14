package service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import database.SkMaterielInfo;
import database.SkMealsInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.HttpUtil;


class CreateMealsInput {
	public String name;
	public int status;
	public float value;
	public int materielIds[];
	public String remark;
}

class MealsRespInfo extends HttpServiceResponseData {
	public int id;
	public String name;
	public int status;
	public float value;
	public List<MaterielRespInfo> materiels;
	public String remark;
	public long createTime;
}

/**
 * Servlet implementation class MealsService
 */
public final class MealsService extends HttpServiceFather {
	private static final long serialVersionUID = 1L;
	public static Logger logger = Logger.getLogger(MaterielService.class.getName());
	private final static Error MaterielIdAndUserNotMatch = new Error("物料不属于该用户");
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MealsService() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
			logger.info("Meals Post : " + (System.currentTimeMillis() - startTime));
			return;
		}
		
		int userId = httpUtil.getUserIdByToken(request, eManager);

		// 转换input
		CreateMealsInput input = null;
		try {
			input = new Gson().fromJson(body.toString(), CreateMealsInput.class);
		} catch (JsonSyntaxException e) {
			error = HTTPRequestParameterError;
			this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);
			logger.info("Meals Post : " + (System.currentTimeMillis() - startTime));
			return;
		}
		
		ArrayList<MaterielRespInfo> materiels = new ArrayList<>();
		
		for (int i = 0; i < input.materielIds.length; i++) {
			int materielId = input.materielIds[i];
			SkMaterielInfo materiel = eManager.find(SkMaterielInfo.class, materielId);
			if (materiel != null && materiel.getUser() == userId) {
				MaterielRespInfo mInfo = new MaterielRespInfo(materiel);
				materiels.add(mInfo);
			} else {
				error = MaterielIdAndUserNotMatch;
				this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);
				logger.info("Meals Post : " + (System.currentTimeMillis() - startTime));
				return;
			}
		}
		
		SkMealsInfo mealsInfo = new SkMealsInfo();
		mealsInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
		mealsInfo.setName(input.name);
		mealsInfo.setRemark(input.remark);
		mealsInfo.setStatus(input.status);
		mealsInfo.setValue(input.value);
		mealsInfo.setMaterielIds(new Gson().toJson(input.materielIds));
		mealsInfo.setUser(userId);
		eManager.persist(mealsInfo);
		
		MealsRespInfo resp = new MealsRespInfo();
		resp.id = mealsInfo.getId();
		resp.name = mealsInfo.getName();
		resp.status = mealsInfo.getStatus();
		resp.value = mealsInfo.getValue();
		resp.remark = mealsInfo.getRemark();
		resp.createTime = mealsInfo.getCreateTime().getTime();
		resp.materiels = materiels;
		output.resp = resp;

		// 服务结束处理，写入response数据
		this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);

		logger.info("Meals Post : " + (System.currentTimeMillis() - startTime));
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
