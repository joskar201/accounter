package com.vimukti.accounter.web.server.translate;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.vimukti.accounter.web.client.translate.ClientLanguage;
import com.vimukti.accounter.web.client.translate.ClientMessage;
import com.vimukti.accounter.web.client.translate.TranslateService;
import com.vimukti.accounter.web.server.FinanceTool;

public class TranslateServiceImpl extends RemoteServiceServlet implements
		TranslateService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String EMAIL_ID = "emailId";

	// @Override
	// public ArrayList<Status> getStatus() {
	// String userEmail = getUserEmail();
	// if (userEmail == null) {
	// return null;
	// }
	// return new FinanceTool().getTranslationStatus();
	// }

	// @Override
	// public ClientMessage getNext(String lang, int lastMessageId) {
	// String userEmail = getUserEmail();
	// if (userEmail == null) {
	// return null;
	// }
	//
	// try {
	// return new FinanceTool().getNextMessage(lang, lastMessageId);
	// } catch (AccounterException e) {
	// e.printStackTrace();
	// return null;
	// }
	// }

	@Override
	public boolean addTranslation(long id, String lang, String value) {
		String userEmail = getUserEmail();
		if (userEmail == null) {
			return false;
		}
		return new FinanceTool().addTranslation(userEmail, id, lang, value);
	}

	@Override
	public boolean vote(long localMessageId) {
		String userEmail = getUserEmail();
		if (userEmail == null) {
			return false;
		}
		return new FinanceTool().addVote(localMessageId, userEmail);
	}

	@Override
	public boolean setApprove(long localMessageId, boolean isApprove) {
		String userEmail = getUserEmail();
		if (userEmail == null) {
			return false;
		}
		return new FinanceTool().setApprove(localMessageId, isApprove);
	}

	protected String getUserEmail() {
		return (String) getThreadLocalRequest().getSession().getAttribute(
				EMAIL_ID);
	}

	@Override
	public ArrayList<ClientMessage> getMessages(String lang, int status,
			int from, int to) {
		String userEmail = getUserEmail();
		if (userEmail == null) {
			return null;
		}
		return new FinanceTool().getMessages(status, lang, userEmail, from, to);
	}

	public List<ClientLanguage> getLanguages() {
		return new FinanceTool().getLanguages();
	}

	@Override
	public boolean canApprove(String lang) {
		String userEmail = getUserEmail();
		if (userEmail == null) {
			return false;
		}
		return new FinanceTool().canApprove(userEmail, lang);
	}
}
