package com.vimukti.accounter.web.client.ui.edittable.tables;

import com.vimukti.accounter.web.client.ui.core.ICurrencyProvider;

public abstract class VendorTransactionTable extends AbstractTransactionTable {

	public VendorTransactionTable(boolean needDiscount,
			ICurrencyProvider currencyProvider) {
		super(needDiscount, false, currencyProvider);
	}

}
