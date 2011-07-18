package com.vimukti.accounter.web.client.ui.grids;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.vimukti.accounter.web.client.core.AccounterCoreType;
import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.core.ClientPayBill;
import com.vimukti.accounter.web.client.core.ClientTransaction;
import com.vimukti.accounter.web.client.core.IAccounterCore;
import com.vimukti.accounter.web.client.core.Utility;
import com.vimukti.accounter.web.client.core.Lists.PaymentsList;
import com.vimukti.accounter.web.client.ui.DataUtils;
import com.vimukti.accounter.web.client.ui.FinanceApplication;
import com.vimukti.accounter.web.client.ui.UIUtils;
import com.vimukti.accounter.web.client.ui.core.Accounter;
import com.vimukti.accounter.web.client.ui.core.Accounter.AccounterType;
import com.vimukti.accounter.web.client.ui.core.ErrorDialogHandler;
import com.vimukti.accounter.web.client.ui.core.InvalidEntryException;
import com.vimukti.accounter.web.client.ui.core.ViewManager;
import com.vimukti.accounter.web.client.ui.reports.ReportsRPC;
import com.vimukti.accounter.web.client.ui.vendors.VendorsMessages;

public class VendorPaymentsListGrid extends BaseListGrid<PaymentsList> {

	public VendorPaymentsListGrid(boolean isMultiSelectionEnable) {
		super(isMultiSelectionEnable);
	}

	boolean isDeleted;

	@Override
	protected Object getColumnValue(PaymentsList list, int col) {
		switch (col) {
		case 0:
			return UIUtils.getDateByCompanyType(list.getPaymentDate());
		case 1:
			return list.getPaymentNumber();
		case 2:
			return Utility.getStatus(list.getType(), list.getStatus());
		case 3:
			return UIUtils.getDateByCompanyType(list.getIssuedDate());
		case 4:
			return list.getName();
		case 5:
			return Utility.getTransactionName(getType(list));
		case 6:
			return list.getPaymentMethodName();
		case 7:
			return DataUtils.getAmountAsString(list.getAmountPaid());
		case 8:
			if (!list.isVoided())
				return FinanceApplication.getFinanceImages().notvoid();
			// return "/images/not-void.png";
			else
				return FinanceApplication.getFinanceImages().voided();
			// return "/images/voided.png";
			// case 9:
			// if (list.isDeleted())
			// return FinanceApplication.getFinanceImages().delSuccess()
			// .getURL();
			// else
			// return FinanceApplication.getFinanceImages().delete().getURL();
		default:
			break;
		}
		return null;
	}

	private void showWarningDialog(final PaymentsList obj, final int col) {
		String msg = null;
		if (col == 8 && !obj.isVoided()) {
			msg = FinanceApplication.getCustomersMessages()
					.doyouwanttoVoidtheTransaction();
		}
		// else if (col == 9) {
		// msg = "Do you want to Delete the Transaction";
		//
		// }
		Accounter.showWarning(msg, AccounterType.WARNING,
				new ErrorDialogHandler() {

					@Override
					public boolean onCancelClick() throws InvalidEntryException {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public boolean onNoClick() throws InvalidEntryException {
						return true;
					}

					@Override
					public boolean onYesClick() throws InvalidEntryException {
						if (col == 8)
							voidTransaction(obj);
						// else if (col == 9)
						// deleteTransaction(obj);
						return true;
					}

				});
	}

	protected void voidTransaction(final PaymentsList obj) {
		ViewManager.getInstance().voidTransaction(
				UIUtils.getAccounterCoreType(obj.getType()),
				obj.getTransactionId(), this);
	}

	protected void deleteTransaction(final PaymentsList obj) {
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {

			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					if (viewType != null && !viewType.equalsIgnoreCase("All"))
						deleteRecord(obj);
					obj.setStatus(ClientTransaction.STATUS_DELETED);
					isDeleted = true;
					obj.setVoided(true);
					updateData(obj);

				}

			}
		};
		AccounterCoreType type = UIUtils.getAccounterCoreType(obj.getType());
		rpcDoSerivce.deleteTransaction(type, obj.getTransactionId(), callback);
	}

	@Override
	protected String[] getColumns() {
		vendorConstants = GWT.create(VendorsMessages.class);
		return new String[] { vendorConstants.paymentDate(),
				vendorConstants.paymentNo(), vendorConstants.status(),
				vendorConstants.issueDate(), vendorConstants.name(),
				vendorConstants.type(), vendorConstants.paymentMethod(),
				vendorConstants.amountPaid(), vendorConstants.IsVoid()
		// , ""
		};
	}

	@Override
	protected int[] setColTypes() {
		return new int[] { ListGrid.COLUMN_TYPE_TEXT,
				ListGrid.COLUMN_TYPE_TEXT, ListGrid.COLUMN_TYPE_TEXT,
				ListGrid.COLUMN_TYPE_TEXT, ListGrid.COLUMN_TYPE_TEXT,
				ListGrid.COLUMN_TYPE_TEXT, ListGrid.COLUMN_TYPE_TEXT,
				ListGrid.COLUMN_TYPE_DECIMAL_TEXT, ListGrid.COLUMN_TYPE_IMAGE
		// ,ListGrid.COLUMN_TYPE_IMAGE
		};
	}

	@Override
	protected void onClick(PaymentsList obj, int row, int col) {
		if (col == 8 && !obj.isVoided()) {
			showWarningDialog(obj, col);
		}
		// else if (col == 9)
		// if (!isDeleted)
		// showWarningDialog(obj, col);
		// else
		// return;

	}

	@Override
	public void onDoubleClick(PaymentsList paymentsList) {
		ReportsRPC.openTransactionView(getType(paymentsList), paymentsList
				.getTransactionId());
	}

	/* This method returns the Transaction type type basing on the PayBill Type */
	int getType(PaymentsList paymentsList) {
		if (paymentsList.getType() == 11) {
			return paymentsList.getPayBillType() == ClientPayBill.TYPE_PAYBILL ? ClientTransaction.TYPE_PAY_BILL
					: ClientTransaction.TYPE_VENDOR_PAYMENT;
		}

		return paymentsList.getType();
	}

	@Override
	protected int getCellWidth(int index) {
		if (index == 8)
			return 65;
		else if(index==3)
			return 90;
		else if(index==5)
			return 120;
		// else if (index == 10)
		// return 30;
		return super.getCellWidth(index);
	};

	@Override
	protected void onValueChange(PaymentsList obj, int col, Object value) {

	}

	@Override
	public boolean validateGrid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void executeDelete(PaymentsList object) {
		// TODO Auto-generated method stub

	}

	@Override
	protected int sort(PaymentsList obj1, PaymentsList obj2, int index) {

		switch (index) {
		case 0:
			// ClientFinanceDate date1 = obj1.getPaymentDate();
			// ClientFinanceDate date2 = obj2.getPaymentDate();
			if (obj1.getPaymentDate() != null && obj2.getPaymentDate() != null)
				return obj1.getPaymentDate().compareTo(obj2.getPaymentDate());
			break;

		case 1:
			int num1 = UIUtils.isInteger(obj1.getPaymentNumber())?Integer.parseInt(obj1.getPaymentNumber()):0;
			int num2 = UIUtils.isInteger(obj2.getPaymentNumber())?Integer.parseInt(obj2.getPaymentNumber()):0;
			if (num1!=0 && num2!=0)
				return UIUtils.compareInt(num1, num2);
			else
				return obj1.getPaymentNumber().compareTo(obj2.getPaymentNumber());

		case 2:
			String status1 = Utility
					.getStatus(obj1.getType(), obj1.getStatus()).toLowerCase();
			String status2 = Utility
					.getStatus(obj2.getType(), obj2.getStatus()).toLowerCase();
			return status1.compareTo(status2);

		case 3:
			ClientFinanceDate issuedate1 = obj1.getIssuedDate();
			ClientFinanceDate issuedate2 = obj2.getIssuedDate();
			if (issuedate1 != null && issuedate2 != null)
				return issuedate1.compareTo(issuedate2);
			break;

		case 4:
			return obj1.getName().toLowerCase().compareTo(
					obj2.getName().toLowerCase());

		case 5:
			String type1 = Utility.getTransactionName(obj1.getType())
					.toLowerCase();
			String type2 = Utility.getTransactionName(obj2.getType())
					.toLowerCase();
			return type1.compareTo(type2);

		case 6:
			return obj1.getPaymentMethodName().toLowerCase().compareTo(
					obj2.getPaymentMethodName().toLowerCase());

		case 7:
			return obj1.getAmountPaid().compareTo(obj2.getAmountPaid());
		}
		return 0;
	}

	@Override
	public void processupdateView(IAccounterCore core, int command) {
		// TODO Auto-generated method stub

	}

	public AccounterCoreType getType() {
		return null;
	}

	private String getTransactionID(PaymentsList obj) {
		return obj.getTransactionId();
	}

	public boolean isVoided(PaymentsList obj) {
		return obj.isVoided();
	}

	public AccounterCoreType getAccounterCoreType(PaymentsList obj) {

		return UIUtils.getAccounterCoreType(obj.getType());
	}

}
