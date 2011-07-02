package com.vimukti.accounter.web.client.ui.customers;

import com.google.gwt.resources.client.ImageResource;
import com.vimukti.accounter.web.client.ui.FinanceApplication;
import com.vimukti.accounter.web.client.ui.MainFinanceWindow;
import com.vimukti.accounter.web.client.ui.core.AccounterAsync;
import com.vimukti.accounter.web.client.ui.core.Action;
import com.vimukti.accounter.web.client.ui.core.CreateViewAsyncCallBack;
import com.vimukti.accounter.web.client.ui.core.ParentCanvas;

/**
 * 
 * @author Raj Vimal
 */

public class ReceivedPaymentsAction extends Action {

	protected ReceivedPaymentListView view;

	public ReceivedPaymentsAction(String text) {
		super(text);
		this.catagory = FinanceApplication.getCustomersMessages().customer();
	}

	public ReceivedPaymentsAction(String text, String iconString) {
		super(text, iconString);
		this.catagory = FinanceApplication.getCustomersMessages().customer();
	}

	@Override
	public void run(Object data, Boolean isDependent) {
		runAsync(data, isDependent);

	}

	public void runAsync(final Object data, final Boolean isEditable) {

		AccounterAsync.createAsync(new CreateViewAsyncCallBack() {

			public void onCreated() {
				try {

					view = new ReceivedPaymentListView();
					MainFinanceWindow.getViewManager().showView(view, data,
							isEditable, ReceivedPaymentsAction.this);
					// UIUtils.setCanvas(view, getViewConfiguration());

				} catch (Throwable e) {
					onCreateFailed(e);
				}
			}

			public void onCreateFailed(Throwable t) {
				// //UIUtils.logError("Failed to load Recieve Payment List", t);

			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public ParentCanvas getView() {
		return this.view;
	}

	public ImageResource getBigImage() {
		return null;
	}

	public ImageResource getSmallImage() {
		return FinanceApplication.getFinanceMenuImages().receivedPayments();
	}
	@Override
	public String getImageUrl() {
		
		return "/images/recived_payment_list.png";
	}

	@Override
	public String getHistoryToken() {
		// TODO Auto-generated method stub
		return "receivePayments";
	}
}
