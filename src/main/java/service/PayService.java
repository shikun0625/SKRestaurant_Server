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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.factory.Factory.Payment;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePayResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import database.SkMaterielActionInfo;
import database.SkMaterielInfo;
import database.SkMealsInfo;
import database.SkOrderInfo;
import util.AlipayUtil;
import util.HttpUtil;
import util.LoggerUtil;

class PayInput {
	public String barCode;
	public int barCodeType;
	public float amount;
	public String orderId;
	public String subject;
}

class PayRespInfo extends HttpServiceResponseData {
	public int payType;
	public String payTradeNo;
	public String tradeCode;
}

/**
 * Servlet implementation class PayService
 */
@WebServlet("/pay")
public final class PayService extends HttpServiceFather {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerUtil.getLogger(PayService.class.getName());
	private final static Error UnsupportedBarCodeError = new Error("不支持的付款码");
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PayService() {
        super();
        // TODO Auto-generated constructor stub
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
			logger.info("Order Post : " + (System.currentTimeMillis() - startTime));
			return;
		}

		int userId = httpUtil.getUserIdByToken(request, eManager);

		// 转换input
		PayInput input = null;
		try {
			input = new Gson().fromJson(body.toString(), PayInput.class);
		} catch (JsonSyntaxException e) {
			error = HTTPRequestParameterError;
			this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);
			logger.info("Order Post : " + (System.currentTimeMillis() - startTime));
			return;
		}
		
		SkOrderInfo order = eManager.find(SkOrderInfo.class, input.orderId);
		
		PayRespInfo resp = new PayRespInfo();
		
		if (input.barCodeType == 1) {
			// TODO 支付宝
//			AlipayUtil.InitAlipay();
//			try {
//				AlipayTradePayResponse alipayResponse =	Factory.Payment.FaceToFace().pay(body, body, body, body);
//				String code = alipayResponse.code;
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			order.setPayType(1);
			order.setPayTradeNo("alipay test");
			resp.payTradeNo = "alipay test";
			resp.tradeCode = "SUCCESS";
		} else if (input.barCodeType == 2) {
			// TODO 微信
			order.setPayType(2);
			order.setPayTradeNo("weichat test");
			resp.payTradeNo = "weichat test";
			resp.tradeCode = "SUCCESS";
		} else {
			error = UnsupportedBarCodeError;
			this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);
			logger.info("Order Post : " + (System.currentTimeMillis() - startTime));
			return;
		}
		
		eManager.merge(order);
		
		resp.payType = order.getPayType();
		output.resp = resp;
		
		// 服务结束处理，写入response数据
		this.afterLogic(request, response, body, output, eManager, error, eTransaction, httpUtil);

		logger.info("Order Post : " + (System.currentTimeMillis() - startTime));
	}

}
