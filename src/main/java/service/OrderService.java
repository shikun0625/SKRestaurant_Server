package service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import database.SkMaterielActionInfo;
import database.SkMaterielInfo;
import database.SkMealsInfo;
import database.SkOrderInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.HttpUtil;
import util.LoggerUtil;

class CreateOrderInput {
	public Map<String, Integer> menus;
	public float totalAmount;
	public int type;
}

class OrderRespInfo extends HttpServiceResponseData {
	@SuppressWarnings("unchecked")
	public OrderRespInfo(SkOrderInfo info) {
		this.orderId = info.getId();
		this.createTime = info.getCreateTime().getTime();
		this.menus = new Gson().fromJson(info.getMenus(), Map.class);
		this.number = info.getNumber();
		this.remark = info.getRemark();
		this.status = info.getStatus();
		this.takeoutOrder = info.getTakeoutOrder();
		this.takeoutPlatform = info.getTakeoutPlatform();
		this.takeoutStatus = info.getTakeoutStatus();
		this.totalAmount = info.getTotalAmount();
		this.type = info.getType();
		this.payType = info.getPayType();
	}

	public String orderId;
	public long createTime;
	public Map<String, Integer> menus;
	public String number;
	public String remark;
	public int status;
	public String takeoutOrder;
	public Integer takeoutPlatform;
	public Integer takeoutStatus;
	public float totalAmount;
	public int type;
	public Integer payType;
}

class SetOrderInput {
	public String orderId;
	public Integer status;
	public Integer payType;
}

/**
 * Servlet implementation class OrderService
 */
public final class OrderService extends HttpServiceFather {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerUtil.getLogger(OrderService.class.getName());
	private final static Error OrderNotExistError = new Error("订单不存在");

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OrderService() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
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
			logger.info("Order Post : " + (System.currentTimeMillis() - startTime));
			return;
		}

		int userId = httpUtil.getUserIdByToken(request, eManager);

		// 转换input
		CreateOrderInput input = null;
		try {
			input = new Gson().fromJson(body.toString(), CreateOrderInput.class);
		} catch (JsonSyntaxException e) {
			error = HTTPRequestParameterError;
			this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);
			logger.info("Order Post : " + (System.currentTimeMillis() - startTime));
			return;
		}

		// TODO 检查物料数量 （可做可不做）

		// 创建订单
		SkOrderInfo info = new SkOrderInfo();
		info.setId(UUID.randomUUID().toString().replace("-", ""));
		info.setMenus(new Gson().toJson(input.menus));
		info.setCreateTime(new Timestamp(System.currentTimeMillis()));
		info.setStatus(0);
		info.setUser(userId);
		info.setType(input.type);
		info.setTotalAmount(input.totalAmount);

		Query query = eManager.createNamedQuery("SkOrderInfo.countTodayWithType");
		query.setParameter("type", input.type);
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
		query.setParameter("minTime", new Timestamp(calendar.getTimeInMillis()));
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 23, 59,
				59);
		query.setParameter("maxTime", new Timestamp(calendar.getTimeInMillis()));

		long count = (long) query.getSingleResult() + 1;

		switch (input.type) {
		case 0: {
			info.setNumber("T" + count);
			break;
		}
		case 1: {
			info.setNumber("D" + count);
			break;
		}
		case 2: {
			info.setNumber("W" + count);
			break;
		}
		default:
			break;
		}
		eManager.persist(info);

		// 修改物料数量
		for (Map.Entry<String, Integer> entry : input.menus.entrySet()) {
			String menuId = entry.getKey();
			int outCount = entry.getValue();

			SkMealsInfo meals = eManager.find(SkMealsInfo.class, Integer.parseInt(menuId));
			int[] materielIds = new Gson().fromJson(meals.getMaterielIds(), int[].class);
			for (int materielId : materielIds) {
				SkMaterielInfo materiel = eManager.find(SkMaterielInfo.class, materielId);
				materiel.setCount(materiel.getCount() - outCount);
				eManager.merge(materiel);

				// 记录物料事件
				SkMaterielActionInfo actionInfo = new SkMaterielActionInfo();
				actionInfo.setActionType(1);
				actionInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
				actionInfo.setDelta(-outCount);
				actionInfo.setId(UUID.randomUUID().toString().replace("-", ""));
				actionInfo.setMaterielId(materielId);
				actionInfo.setReason(1);
				eManager.persist(actionInfo);
			}
		}

		OrderRespInfo orderRespInfo = new OrderRespInfo(info);

		output.resp = orderRespInfo;

		// 服务结束处理，写入response数据
		this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);

		logger.info("Order Post : " + (System.currentTimeMillis() - startTime));
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
			logger.info("Order Put : " + (System.currentTimeMillis() - startTime));
			return;
		}

		int userId = httpUtil.getUserIdByToken(request, eManager);

		// 转换input
		SetOrderInput input = null;
		try {
			input = new Gson().fromJson(body.toString(), SetOrderInput.class);
		} catch (JsonSyntaxException e) {
			error = HTTPRequestParameterError;
			this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);
			logger.info("Order Put : " + (System.currentTimeMillis() - startTime));
			return;
		}

		Query query = eManager.createNamedQuery("SkOrderInfo.findByIdAndUser");
		query.setParameter("id", input.orderId);
		query.setParameter("user", userId);
		SkOrderInfo orderInfo = null;
		try {
			orderInfo = (SkOrderInfo) query.getSingleResult();
		} catch (Exception e) {
			error = OrderNotExistError;
			this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);
			logger.info("Order Put : " + (System.currentTimeMillis() - startTime));
			return;
		}

		if (input.status != null) {
			orderInfo.setStatus(input.status);
		}
		if (input.payType != null) {
			orderInfo.setPayType(input.payType);
		}

		orderInfo = eManager.merge(orderInfo);

		output.resp = new OrderRespInfo(orderInfo);

		// 服务结束处理，写入response数据
		this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);

		logger.info("Order Put : " + (System.currentTimeMillis() - startTime));
	}

}
