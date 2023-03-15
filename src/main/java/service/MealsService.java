package service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

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
	public int type;
}

class UpdateMealsInput {
	public int id;
	public float value;
	public int status;
}

class MealsRespInfo extends HttpServiceResponseData {
	public MealsRespInfo() {}
	public MealsRespInfo(SkMealsInfo info) {
		this.id = info.getId();
		this.createTime = info.getCreateTime().getTime();
		this.name = info.getName();
		this.status = info.getStatus();
		this.value = info.getValue();
		this.remark = info.getRemark();
		this.type = info.getType();
	}
	
	public int id;
	public String name;
	public int type;
	public int status;
	public float value;
	public List<MaterielRespInfo> materiels;
	public String remark;
	public long createTime;
}

class MealsGetResp extends HttpServiceResponseData {
	public List<MealsRespInfo> mealses;
}

/**
 * Servlet implementation class MealsService
 */
public final class MealsService extends HttpServiceFather {
	private static final long serialVersionUID = 1L;
	public static Logger logger = Logger.getLogger(MaterielService.class.getName());
	private final static Error MaterielIdNotMatchUser = new Error("物料不属于该用户");
	private final static Error MealsNotExist = new Error("菜品不存在");
	private final static Error MealsNotMatchUser = new Error("菜品不属于该用户");
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
			logger.info("Meals Get : " + (System.currentTimeMillis() - startTime));
			return;
		}
		
		int userId = httpUtil.getUserIdByToken(request, eManager);

		Query query = eManager.createNamedQuery("SkMealsInfo.findByUser");
		query.setParameter("userId", userId);
		var resultList = query.getResultList();
		MealsGetResp resp = new MealsGetResp();
		List<MealsRespInfo> mealses = new ArrayList<>();
		for (Object object : resultList) {
			SkMealsInfo meals = (SkMealsInfo) object;
			MealsRespInfo info = new MealsRespInfo(meals);
			Type type =new TypeToken<int[]>(){}.getType(); 
			int[] materielIds = new Gson().fromJson(meals.getMaterielIds(), type); 
			ArrayList<MaterielRespInfo> materiels = new ArrayList<>();
			for (int i = 0; i < materielIds.length; i++) {
				int materielId = materielIds[i];
				SkMaterielInfo materiel = eManager.find(SkMaterielInfo.class, materielId);
				if (materiel != null && materiel.getUser() == userId) {
					MaterielRespInfo mInfo = new MaterielRespInfo(materiel);
					materiels.add(mInfo);
				} else {
					error = MaterielIdNotMatchUser;
					this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);
					logger.info("Meals Get : " + (System.currentTimeMillis() - startTime));
					return;
				}
			}
			info.materiels = materiels;
			mealses.add(info);
		}
		resp.mealses = mealses;

		output.resp = resp;

		this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);

		logger.info("Meals Get : " + (System.currentTimeMillis() - startTime));
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
				error = MaterielIdNotMatchUser;
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
		mealsInfo.setType(input.type);
		eManager.persist(mealsInfo);
		
		MealsRespInfo resp = new MealsRespInfo(mealsInfo);
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
		UpdateMealsInput input = null;
		try {
			input = new Gson().fromJson(body.toString(), UpdateMealsInput.class);
		} catch (JsonSyntaxException e) {
			error = HTTPRequestParameterError;
			this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);
			logger.info("Meals Put : " + (System.currentTimeMillis() - startTime));
			return;
		}
		
		SkMealsInfo mealsInfo = eManager.find(SkMealsInfo.class, input.id);
		if (mealsInfo == null) {
			error = MealsNotExist;
			this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);
			logger.info("Meals Put : " + (System.currentTimeMillis() - startTime));
			return;
		}
		if (mealsInfo.getUser() != userId) {
			error = MealsNotMatchUser;
			this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);
			logger.info("Meals Put : " + (System.currentTimeMillis() - startTime));
			return;
		}
		
		Type type =new TypeToken<int[]>(){}.getType(); 
		int[] materielIds = new Gson().fromJson(mealsInfo.getMaterielIds(), type); 
		ArrayList<MaterielRespInfo> materiels = new ArrayList<>();
		
		for (int i = 0; i < materielIds.length; i++) {
			int materielId = materielIds[i];
			SkMaterielInfo materiel = eManager.find(SkMaterielInfo.class, materielId);
			if (materiel != null && materiel.getUser() == userId) {
				MaterielRespInfo mInfo = new MaterielRespInfo(materiel);
				materiels.add(mInfo);
			} else {
				error = MaterielIdNotMatchUser;
				this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);
				logger.info("Meals Put : " + (System.currentTimeMillis() - startTime));
				return;
			}
		}
		
		mealsInfo.setValue(input.value);
		mealsInfo.setStatus(input.status);
		mealsInfo = eManager.merge(mealsInfo);
		
		MealsRespInfo resp = new MealsRespInfo(mealsInfo);
		resp.materiels = materiels;
		output.resp = resp;

		// 服务结束处理，写入response数据
		this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);

		logger.info("Meals Put : " + (System.currentTimeMillis() - startTime));
	}

}
