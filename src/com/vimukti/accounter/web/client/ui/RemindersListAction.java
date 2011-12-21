package com.vimukti.accounter.web.client.ui;

import com.google.gwt.resources.client.ImageResource;
import com.vimukti.accounter.web.client.core.ClientReminder;
import com.vimukti.accounter.web.client.ui.core.AccounterAsync;
import com.vimukti.accounter.web.client.ui.core.Action;
import com.vimukti.accounter.web.client.ui.core.CreateViewAsyncCallback;

public class RemindersListAction extends Action<ClientReminder> {

	public RemindersListAction() {
		this.catagory = Accounter.messages().company();
	}

	@Override
	public void run() {
		AccounterAsync.createAsync(new CreateViewAsyncCallback() {

			@Override
			public void onCreated() {
				RemindersListView view = new RemindersListView();

				MainFinanceWindow.getViewManager().showView(view, data,
						isDependent, RemindersListAction.this);
			}
		});
	}

	@Override
	public ImageResource getBigImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageResource getSmallImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHistoryToken() {
		return "recurringReminders";
	}

	@Override
	public String getHelpToken() {
		return "reminders-list";
	}

	@Override
	public String getText() {
		return messages.remindersList();
	}
}