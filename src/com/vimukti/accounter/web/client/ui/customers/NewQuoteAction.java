package com.vimukti.accounter.web.client.ui.customers;

import com.google.gwt.resources.client.ImageResource;
import com.vimukti.accounter.web.client.AccounterAsyncCallback;
import com.vimukti.accounter.web.client.Global;
import com.vimukti.accounter.web.client.core.ClientEstimate;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.MainFinanceWindow;
import com.vimukti.accounter.web.client.ui.core.AccounterAsync;
import com.vimukti.accounter.web.client.ui.core.Action;
import com.vimukti.accounter.web.client.ui.core.CreateViewAsyncCallback;

/**
 * 
 * @author kumar kasimala
 */

public class NewQuoteAction extends Action {

	protected QuoteView view;

	public NewQuoteAction(String text) {
		super(text);
		this.catagory = Global.get().Customer();
	}

	public NewQuoteAction(String text, ClientEstimate quote,
			AccounterAsyncCallback<Object> callback) {
		super(text);
		this.catagory = Global.get().Customer();
	}

	@Override
	public void run() {
		runAsync(data, isDependent);

	}

	public void runAsync(final Object data, final Boolean isDependent) {

		AccounterAsync.createAsync(new CreateViewAsyncCallback() {

			@Override
			public void onCreated() {
				view = QuoteView.getInstance();

				MainFinanceWindow.getViewManager().showView(view, data,
						isDependent, NewQuoteAction.this);

			}

		});
	}

	// @Override
	// public ParentCanvas getView() {
	// return this.view;
	// }

	public ImageResource getBigImage() {
		// NOTHING TO DO.
		return null;
	}

	public ImageResource getSmallImage() {
		return Accounter.getFinanceMenuImages().newQuote();
	}

	// @Override
	// public String getImageUrl() {
	//
	// return "/images/new_quote.png";
	// }

	@Override
	public String getHistoryToken() {
		return "newQuote";
	}

	@Override
	public String getHelpToken() {
		return "customer-quote";
	}
}
