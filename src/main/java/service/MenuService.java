package service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.google.gson.Gson;

import database.SkMaterielInfo;
import database.SkMealsInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.HttpUtil;
import util.LoggerUtil;

class MenuRespInfo extends HttpServiceResponseData {
	public int id;
	public String name;
	public int type;
	public float value;
	public int[] materielIds;
}

class MenuGetResp extends HttpServiceResponseData {
	public List<MenuRespInfo> menus;
	public List<MaterielRespInfo> materiels;
}

/**
 * Servlet implementation class MenuService
 */
public final class MenuService extends HttpServiceFather {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerUtil.getLogger(MenuService.class.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MenuService() {
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
			logger.info("Meals Get : " + (System.currentTimeMillis() - startTime));
			return;
		}

		int userId = httpUtil.getUserIdByToken(request, eManager);

		Query query = eManager.createNamedQuery("SkMealsInfo.findByStatusAndUser");
		query.setParameter("status", 0);
		query.setParameter("userId", userId);
		var resultList = query.getResultList();
		MenuGetResp resp = new MenuGetResp();
		List<MenuRespInfo> mealses = new ArrayList<>();
		for (Object object : resultList) {
			SkMealsInfo meals = (SkMealsInfo) object;
			MenuRespInfo info = new MenuRespInfo();
			info.id = meals.getId();
			info.name = meals.getName();
			info.type = meals.getType();
			info.value = meals.getValue();
			info.materielIds = new Gson().fromJson(meals.getMaterielIds(), int[].class);
			mealses.add(info);
		}
		resp.menus = mealses;

		query = eManager.createNamedQuery("SkMaterielInfo.findByTypeAndUser");
		query.setParameter("type", 0);
		query.setParameter("userId", userId);
		resultList = query.getResultList();
		List<MaterielRespInfo> materiels = new ArrayList<>();
		for (Object object : resultList) {
			MaterielRespInfo info = new MaterielRespInfo((SkMaterielInfo) object);
			materiels.add(info);
		}
		resp.materiels = materiels;

		output.resp = resp;

		this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);

		logger.info("Meals Get : " + (System.currentTimeMillis() - startTime));
	}

}
