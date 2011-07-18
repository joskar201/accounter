package com.vimukti.accounter.web.client.commet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.vimukti.accounter.web.client.IAccounterGETService;
import com.vimukti.accounter.web.client.core.IAccounterCore;
import com.vimukti.accounter.web.client.ui.FinanceApplication;

public class AccounterCometClient {

	public static void start() {
		final SerializationStreamFactory ssf = GWT
				.create(IAccounterGETService.class);
		CometClient.register("", FinanceApplication.getCustomersMessages()
				.accounter(), new ICometListener() {

			public void onPayload(final Object obj) {
				processCommand((IAccounterCore) obj);
			}

			@Override
			public void onTerminate() {
			}

			@Override
			public void onReset() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStatusChange(int status) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSyncStatusChange(int status) {
				// TODO Auto-generated method stub
			}
		}, ssf);
	}

	protected static void processCommand(IAccounterCore obj) {
		FinanceApplication.getCompany().processCommand(obj);
	}

	public static void cometStop() {
		CometClient.unRegister("", FinanceApplication.getCustomersMessages()
				.accounter());
	}

}
