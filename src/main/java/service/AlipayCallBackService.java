package service;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.LoggerUtil;

/**
 * Servlet implementation class AlipayCallBackService
 */
@WebServlet("/alipay")
public final class AlipayCallBackService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerUtil.getLogger(AlipayCallBackService.class.getName());
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AlipayCallBackService() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
