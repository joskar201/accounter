package com.vimukti.accounter.web.client.ui.core;

import com.vimukti.accounter.web.client.ui.reports.PurchaseOpenOrderAction;
import com.vimukti.accounter.web.client.ui.vendors.PurchaseOrderAction;
import com.vimukti.accounter.web.client.ui.vendors.PurchaseOrderListAction;

public class PurchaseOrderActionFactory extends AbstractActionFactory {

	public static PurchaseOrderAction getPurchaseOrderAction() {
		return new PurchaseOrderAction("Purchase Order", "");
	}

	public static PurchaseOrderListAction getPurchaseOrderListAction() {
		return new PurchaseOrderListAction("PurchaseOrder List", "");
	}

	public static PurchaseOpenOrderAction getPurchaseOpenOrderListAction() {
		return new PurchaseOpenOrderAction("PurchaseOrder Report",
				"/images/icons/report/reports.png");
	}
}
