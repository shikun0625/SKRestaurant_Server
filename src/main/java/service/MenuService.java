package service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import database.SkMaterielInfo;
import database.SkMealsInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.HttpUtil;

class MenuRespInfo extends HttpServiceResponseData {	
	public int id;
	public String name;
	public int type;
	public float value;
	public int availableCount;
}

class MenuGetResp extends HttpServiceResponseData {
	public List<MenuRespInfo> mealses;
}
/**
 * Servlet implementation class MenuService
 */
public final class MenuService extends HttpServiceFather {
	private static final long serialVersionUID = 1L;
	public static Logger logger = Logger.getLogger(MaterielService.class.getName());
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MenuService() {
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
			int minCount = Integer.MAX_VALUE;
			Type type =new TypeToken<int[]>(){}.getType(); 
			int[] materielIds = new Gson().fromJson(meals.getMaterielIds(), type); 
			for (int i = 0; i < materielIds.length; i++) {
				int materielId = materielIds[i];
				SkMaterielInfo materiel = eManager.find(SkMaterielInfo.class, materielId);
				if (materiel != null && materiel.getUser() == userId) {
					MaterielRespInfo mInfo = new MaterielRespInfo(materiel);
					if (mInfo.count < minCount) {
						minCount = Math.min(mInfo.count, minCount);
					}
				} else {
					minCount = 0;
					break;
				}
			}
			info.availableCount = minCount;
			mealses.add(info);
		}
		resp.mealses = mealses;

		output.resp = resp;

		this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);

		logger.info("Meals Get : " + (System.currentTimeMillis() - startTime));
	}

}
