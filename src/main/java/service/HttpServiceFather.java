package service;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;

public class HttpServiceFather extends HttpServlet{
	private static final long serialVersionUID = 1L;
	protected EntityManager eManager;
	protected EntityTransaction eTransaction;
	protected HttpServiceOutput output;
	protected UserLoginInput input;
	protected HttpServletResponse response;
	protected Error error;
}
