package com.vimukti.accounter.web.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.vimukti.accounter.web.client.Global;
import com.vimukti.accounter.web.client.core.ClientEstimate;
import com.vimukti.accounter.web.client.core.ClientPortletConfiguration;
import com.vimukti.accounter.web.client.ui.core.Action;
import com.vimukti.accounter.web.client.ui.core.ActionFactory;

public class QuickLinksPortlet extends Portlet {

	private Map<String, String> quickLinksMap;

	public QuickLinksPortlet(ClientPortletConfiguration configuration) {
		super(configuration, messages.quickLinks(), null);
	}

	@Override
	public void createBody() {
		quickLinksMap = new HashMap<String, String>();

		FlexTable linksTable = new FlexTable();
		linksTable.addStyleName("quick-links-table");
		// initializing links...
		quickLinksMap.put(messages.newInvoice(), ActionFactory
				.getNewInvoiceAction().getHistoryToken());
		quickLinksMap.put(messages.expenses(),
				ActionFactory.getExpensesAction("").getHistoryToken());
		quickLinksMap.put(messages.payees(Global.get().Customer()),
				ActionFactory.getCustomersAction().getHistoryToken());
		quickLinksMap.put(messages.banking(), ActionFactory
				.getBankingHomeAction().getHistoryToken());
		quickLinksMap.put(messages.reports(), ActionFactory
				.getReportsHomeAction().getHistoryToken());
		quickLinksMap.put(messages.bills(), ActionFactory.getBillsAction()
				.getHistoryToken());
		quickLinksMap.put(messages.cashExpense(), ActionFactory
				.CashExpenseAction().getHistoryToken());
		quickLinksMap.put(messages.chartOfAccounts(), ActionFactory
				.getChartOfAccountsAction().getHistoryToken());
		quickLinksMap.put(messages.creditCardExpense(), ActionFactory
				.CreditCardExpenseAction().getHistoryToken());
		quickLinksMap.put(
				messages.newQuote(),
				ActionFactory.getNewQuoteAction(ClientEstimate.QUOTES,
						messages.newQuote()).getHistoryToken());
		quickLinksMap.put(
				messages.newCharge(),
				ActionFactory.getNewQuoteAction(ClientEstimate.CHARGES,
						messages.newCharge()).getHistoryToken());
		quickLinksMap.put(messages.newJournalEntry(), ActionFactory
				.getNewJournalEntryAction().getHistoryToken());
		quickLinksMap.put(messages.receivePayment(), ActionFactory
				.getReceivePaymentAction().getHistoryToken());
		quickLinksMap.put(messages.Reconcile(), ActionFactory
				.getReconciliationsListAction().getHistoryToken());
		quickLinksMap.put(messages.payees(Global.get().Vendor()), ActionFactory
				.getVendorListAction().getHistoryToken());
		quickLinksMap.put(messages.writeCheck(), ActionFactory
				.getWriteChecksAction().getHistoryToken());
		quickLinksMap.put(messages.enterBill(), ActionFactory
				.getEnterBillsAction().getHistoryToken());

		// adding to flex table...
		Iterator<String> iterator = quickLinksMap.keySet().iterator();
		int j = 0, i = 0;
		while (iterator.hasNext()) {
			if (i == 3) {
				i = 0;
				j++;
			} else {
				final String linkName = iterator.next();
				Anchor link = new Anchor(linkName);
				link.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						historyChanged(quickLinksMap.get(linkName));
					}
				});
				linksTable.setWidget(j, i, link);
				i++;
			}
		}
		body.add(linksTable);

	}

	protected void historyChanged(String value) {
		Action<?> action = MainFinanceWindow.actions.get(value);
		action.run();
	}

	@Override
	public void refreshWidget() {
		this.body.clear();
		createBody();
	}
}