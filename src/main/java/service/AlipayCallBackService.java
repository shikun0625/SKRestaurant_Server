package service;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.LoggerUtil;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Servlet implementation class AlipayCallBackService
 */
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
