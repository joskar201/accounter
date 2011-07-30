package com.vimukti.accounter.servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vimukti.accounter.core.Client;
import com.vimukti.accounter.core.Company;
import com.vimukti.accounter.core.IAccounterServerCore;
import com.vimukti.accounter.utils.HibernateUtil;
import com.vimukti.accounter.web.server.FinanceTool;

public class BaseServlet extends HttpServlet {
	public static final String USER_ID = "userID";
	public static final String OUR_COOKIE = "_accounter_01_infinity_2711";

	public static final String COMPANY_COOKIE = "cid";

	public static final String USER_COOKIE = "user";

	public static final String COMPANY_NAME = "companyName";

	public static final String EMAIL_ID = "emailId";
	public static final String PASSWORD = "password";

	protected static final String PARAM_DESTINATION = "destination";
	protected static final String ATTR_MESSAGE = "message";
	protected static final String ATTR_COMPANY_LIST = "companeyList";

	protected static final String LOGIN_URL = "/login";
	protected static final String ACCOUNTER_URL = "/accounter";

	protected static final String RESET_PASSWORD_URL = "/resetpassword";
	public static final String COMPANIES_URL = "/companies";

	/**
	 * 
	 */
	protected static final long serialVersionUID = 1L;
	protected static final String LOCAL_DATABASE = "accounter";
	public static final int MAIL_ID = 0;
	public static final int NAME = 1;
	public static final int PHONE_NO = 2;

	protected String getCompanyName(HttpServletRequest req) {
		return null;

	}

	protected boolean isCompanyExits(String companyName) {
		if (companyName == null) {
			return false;
		}
		Session openSession = HibernateUtil.openSession(LOCAL_DATABASE);
		try {
			Company uniqueResult = (Company) openSession
					.getNamedQuery("getCompanyName.is.unique")
					.setParameter("companyName", companyName).uniqueResult();
			return uniqueResult == null ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			openSession.close();
		}
	}

	protected FinanceTool getFinanceTool(HttpServletRequest request) {
		return null;
	}

	protected String getCookie(HttpServletRequest request, String ourCookie) {
		Cookie[] clientCookies = request.getCookies();
		if (clientCookies != null) {
			for (Cookie cookie : clientCookies) {
				if (cookie.getName().equals(ourCookie)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	protected boolean isValidInputs(int inputType, String... values) {
		for (String value : values) {
			switch (inputType) {
			case MAIL_ID:
				return value
						.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,4}$");

			case NAME:
				return value.matches("^[a-zA-Z]+$");

			case PHONE_NO:
				return value.matches("^[0-9][0-9-]+$");

			}
		}
		return false;
	}

	protected void dispatch(HttpServletRequest req, HttpServletResponse resp,
			String page) {
		try {
			RequestDispatcher reqDispatcher = getServletContext()
					.getRequestDispatcher(page);
			reqDispatcher.forward(req, resp);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void dispatchMessage(String message, HttpServletRequest req,
			HttpServletResponse resp, String page) {
		req.setAttribute("message", message);
		try {
			req.getRequestDispatcher(page).forward(req, resp);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected void saveEntry(IAccounterServerCore object) {

		Session currentSession = HibernateUtil.getCurrentSession();
		Transaction transaction = currentSession.beginTransaction();
		currentSession.save(object);
		try {
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
		}

	}

	protected Client getClient(String emailId) {
		Session session = HibernateUtil.getCurrentSession();
		Client client = (Client) session.getNamedQuery("getClient.by.mailId")
				.setString(EMAIL_ID, emailId).uniqueResult();
		// session.close();
		return client;
	}

	protected Client getClientById(long id) {
		Session session = HibernateUtil.getCurrentSession();
		Client client = (Client) session.getNamedQuery("getClient.by.Id")
				.setLong(0, id).uniqueResult();
		// session.close();
		return client;
	}

	/**
	 * Redirect to the External URL {@url}
	 * 
	 * @param req
	 * @param resp
	 * @param string
	 * @throws IOException
	 */
	protected void redirectExternal(HttpServletRequest req,
			HttpServletResponse resp, String url) throws IOException {
		resp.sendRedirect(url);
	}

}
