package com.vimukti.accounter.web.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import com.vimukti.accounter.core.Company;
import com.vimukti.accounter.core.Estimate;
import com.vimukti.accounter.core.FinanceDate;
import com.vimukti.accounter.core.FixedAsset;
import com.vimukti.accounter.core.Item;
import com.vimukti.accounter.core.JournalEntry;
import com.vimukti.accounter.core.SalesPerson;
import com.vimukti.accounter.core.TAXAgency;
import com.vimukti.accounter.core.TAXCode;
import com.vimukti.accounter.core.TAXItem;
import com.vimukti.accounter.core.Unit;
import com.vimukti.accounter.core.Utility;
import com.vimukti.accounter.core.Warehouse;
import com.vimukti.accounter.utils.CSVExporter;
import com.vimukti.accounter.utils.HibernateUtil;
import com.vimukti.accounter.utils.ICSVExportRunner;
import com.vimukti.accounter.web.client.Global;
import com.vimukti.accounter.web.client.IAccounterExportCSVService;
import com.vimukti.accounter.web.client.core.ClientAccount;
import com.vimukti.accounter.web.client.core.ClientActivity;
import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.core.ClientMeasurement;
import com.vimukti.accounter.web.client.core.ClientRecurringTransaction;
import com.vimukti.accounter.web.client.core.ClientReminder;
import com.vimukti.accounter.web.client.core.ClientStockTransfer;
import com.vimukti.accounter.web.client.core.ClientStockTransferItem;
import com.vimukti.accounter.web.client.core.ClientTransaction;
import com.vimukti.accounter.web.client.core.ClientUnit;
import com.vimukti.accounter.web.client.core.ClientWarehouse;
import com.vimukti.accounter.web.client.core.PaginationList;
import com.vimukti.accounter.web.client.core.Lists.BillsList;
import com.vimukti.accounter.web.client.core.Lists.CustomerRefundsList;
import com.vimukti.accounter.web.client.core.Lists.InvoicesList;
import com.vimukti.accounter.web.client.core.Lists.PayeeList;
import com.vimukti.accounter.web.client.core.Lists.PaymentsList;
import com.vimukti.accounter.web.client.core.Lists.ReceivePaymentsList;
import com.vimukti.accounter.web.client.externalization.AccounterMessages;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.settings.StockAdjustmentList;

/**
 * for exporting the the list as CSV file get
 * 
 * @author Lingarao.R
 * 
 */
public class AccounterExportCSVImpl extends AccounterRPCBaseServiceImpl
		implements IAccounterExportCSVService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	AccounterMessages messages = Global.get().messages();

	public AccounterExportCSVImpl() {
		super();
	}

	// From here export csv for list
	@Override
	public String getPayeeListExportCsv(int transactionCategory,
			boolean isActive) {
		try {
			PaginationList<PayeeList> payeeList = getFinanceTool()
					.getPayeeList(transactionCategory, isActive, 0, -1,
							getCompanyId());
			ICSVExportRunner<PayeeList> icsvExportRunner = new ICSVExportRunner<PayeeList>() {

				@Override
				public String[] getColumns() {
					return new String[] {
							messages.payeeName(Global.get().Customer()),
							messages.currentMonth(), messages.yearToDate(),
							messages.balance() };
				}

				@Override
				public String getColumnValue(PayeeList obj, int index) {
					String columnValue = null;
					switch (index) {
					case 0:
						columnValue = obj.getPayeeName();
						break;
					case 1:
						columnValue = obj.getCurrentMonth() != 0.0 ? String
								.valueOf(obj.getCurrentMonth()) : "0.0";
						break;
					case 2:
						columnValue = obj.getYearToDate() != 0.0 ? String
								.valueOf(obj.getYearToDate()) : "0.0";
						break;
					case 3:
						columnValue = obj.getBalance() != 0.0 ? String
								.valueOf(obj.getBalance()) : "0.0";
						break;
					}
					return columnValue;
				}
			};
			CSVExporter<PayeeList> csvExporter = new CSVExporter<PayeeList>(
					icsvExportRunner);
			return csvExporter.export(payeeList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getInvoiceListExportCsv(long fromDate, long toDate,
			int invoicesType, int viewType) {

		try {
			FinanceDate[] dates = getMinimumAndMaximumDates(
					new ClientFinanceDate(fromDate), new ClientFinanceDate(
							toDate), getCompanyId());
			PaginationList<InvoicesList> invoiceList = getFinanceTool()
					.getInventoryManager().getInvoiceList(getCompanyId(),
							dates[0].getDate(), dates[1].getDate(),
							invoicesType, viewType, 0, -1);
			ICSVExportRunner<InvoicesList> icsvExportRunner = new ICSVExportRunner<InvoicesList>() {

				@Override
				public String[] getColumns() {

					return new String[] { messages.type(), messages.date(),
							messages.no(),
							messages.payeeName(Global.get().customer()),
							messages.dueDate(), messages.netPrice(),
							messages.totalPrice() };
				}

				@Override
				public String getColumnValue(InvoicesList obj, int index) {
					String columnValue = null;
					switch (index) {
					case 0:
						columnValue = Utility.getTransactionName(obj.getType());
						break;
					case 1:
						columnValue = obj.getDate() != null ? Utility
								.getDateInSelectedFormat(new FinanceDate(obj
										.getDate().getDate())) : "";
						break;
					case 2:
						columnValue = obj.getNumber() != null ? obj.getNumber()
								: " ";
						break;
					case 3:
						columnValue = obj.getCustomerName();
						break;
					case 4:
						columnValue = obj.getDueDate() != null ? Utility
								.getDateInSelectedFormat(new FinanceDate(obj
										.getDueDate().getDate())) : " ";
						break;
					case 5:
						columnValue = obj.getNetAmount() != 0 ? String
								.valueOf(obj.getNetAmount()) : "";
						break;
					case 6:
						columnValue = obj.getTotalPrice() != 0 ? String
								.valueOf(obj.getTotalPrice()) : "";
						break;
					}
					return columnValue;
				}
			};
			CSVExporter<InvoicesList> csvExporter = new CSVExporter<InvoicesList>(
					icsvExportRunner);
			return csvExporter.export(invoiceList);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public String getEstimatesExportCsv(int type, int status, long fromDate,
			long toDate) {
		try {
			FinanceDate[] dates = getMinimumAndMaximumDates(
					new ClientFinanceDate(fromDate), new ClientFinanceDate(
							toDate), getCompanyId());
			ArrayList<Estimate> estimates = getFinanceTool()
					.getCustomerManager().getEstimates(getCompanyId(), type,
							status, dates[0], dates[1], 0, -1);
			ICSVExportRunner<Estimate> icsvExportRunner = new ICSVExportRunner<Estimate>() {

				@Override
				public String[] getColumns() {
					return new String[] { messages.date(),
							messages.payeeName(Global.get().Customer()),
							messages.no(), messages.phone(),
							messages.expirationDate(), messages.deliveryDate(),
							messages.totalPrice() };
				}

				@Override
				public String getColumnValue(Estimate obj, int index) {
					String columnValue = null;
					switch (index) {
					case 0:
						columnValue = Utility
								.getDateInSelectedFormat(new FinanceDate(obj
										.getDate().getDate()));
						break;
					case 1:
						columnValue = obj.getPayee().getName();
						break;
					case 2:
						columnValue = obj.getNumber() != null ? obj.getNumber()
								: "";
						break;
					case 3:
						columnValue = obj.getPhone() != null ? obj.getPhone()
								: "";
						break;
					case 4:
						columnValue = obj.getExpirationDate() != null ? Utility
								.getDateInSelectedFormat(new FinanceDate(obj
										.getExpirationDate().getDate())) : "";
						break;
					case 5:
						columnValue = obj.getDeliveryDate() != null ? Utility
								.getDateInSelectedFormat(new FinanceDate(obj
										.getDeliveryDate().getDate())) : "";
						break;
					case 6:
						columnValue = obj.getTotal() != 0 ? String.valueOf(obj
								.getTotal()) : "";
						break;
					}
					return columnValue;
				}
			};
			CSVExporter<Estimate> csvExporter = new CSVExporter<Estimate>(
					icsvExportRunner);
			return csvExporter.export(estimates);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getReceivePaymentsListExportCsv(long fromDate, long toDate,
			int transactionType, int viewType) {
		try {
			FinanceDate[] dates = getMinimumAndMaximumDates(
					new ClientFinanceDate(fromDate), new ClientFinanceDate(
							toDate), getCompanyId());
			PaginationList<ReceivePaymentsList> receivePaymentsList = getFinanceTool()
					.getCustomerManager().getReceivePaymentsList(
							getCompanyId(), dates[0].getDate(),
							dates[1].getDate(), transactionType, 0, -1,
							viewType);
			ICSVExportRunner<ReceivePaymentsList> icsvExportRunner = new ICSVExportRunner<ReceivePaymentsList>() {

				@Override
				public String[] getColumns() {
					return new String[] { messages.type(),
							messages.paymentDate(), messages.no(),
							messages.payeeName(Global.get().Customer()),
							messages.payMethod(), messages.checkNo(),
							messages.amountPaid() };
				}

				@Override
				public String getColumnValue(ReceivePaymentsList obj, int index) {
					String columnValue = null;
					switch (index) {
					case 0:
						columnValue = Utility.getTransactionName(obj.getType());
						break;
					case 1:
						columnValue = Utility
								.getDateInSelectedFormat(new FinanceDate(obj
										.getPaymentDate().getDate()));
						break;
					case 2:
						columnValue = obj.getNumber() != null ? obj.getNumber()
								: "";
						break;
					case 3:
						columnValue = obj.getCustomerName();
						break;
					case 4:
						columnValue = obj.getPaymentMethodName();
						break;
					case 5:
						columnValue = obj.getCheckNumber() != null ? obj
								.getCheckNumber() : "";
						break;
					case 6:
						columnValue = obj.getAmountPaid() != 0 ? String
								.valueOf(obj.getAmountPaid()) : "";
						break;
					}
					return columnValue;
				}
			};
			CSVExporter<ReceivePaymentsList> csvExporter = new CSVExporter<ReceivePaymentsList>(
					icsvExportRunner);
			return csvExporter.export(receivePaymentsList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getCustomerRefundsListExportCsv(long fromDate, long toDate) {
		try {
			FinanceDate[] dates = getMinimumAndMaximumDates(
					new ClientFinanceDate(fromDate), new ClientFinanceDate(
							toDate), getCompanyId());
			PaginationList<CustomerRefundsList> customerRefundsList = getFinanceTool()
					.getCustomerManager().getCustomerRefundsList(
							getCompanyId(), dates[0], dates[1]);
			ICSVExportRunner<CustomerRefundsList> icsvExportRunner = new ICSVExportRunner<CustomerRefundsList>() {

				@Override
				public String[] getColumns() {

					return new String[] { messages.paymentDate(),
							messages.paymentNo(), messages.status(),
							messages.issueDate(), messages.name(),
							messages.paymentMethod(), messages.amountPaid() };
				}

				@Override
				public String getColumnValue(CustomerRefundsList obj, int index) {
					String columnValue = null;
					switch (index) {
					case 0:
						columnValue = Utility
								.getDateInSelectedFormat(new FinanceDate(obj
										.getPaymentDate()));
						break;
					case 1:
						columnValue = obj.getPaymentNumber();
						break;
					case 2:
						columnValue = (obj.getStatus() == ClientTransaction.STATUS_PAID_OR_APPLIED_OR_ISSUED && obj
								.getSaveStatus() == ClientTransaction.STATUS_DRAFT) ? "Draft"
								: Utility
										.getStatus(
												ClientTransaction.TYPE_CUSTOMER_REFUNDS,
												obj.getStatus());
						break;
					case 3:
						columnValue = Utility
								.getDateInSelectedFormat(new FinanceDate(obj
										.getIssueDate()));
						break;
					case 4:
						columnValue = obj.getName() == null ? "" : obj
								.getName();
						break;
					case 5:
						columnValue = obj.getPaymentMethod();
						break;
					case 6:
						columnValue = String.valueOf(obj.getAmountPaid());
						break;
					}
					return columnValue;
				}
			};
			CSVExporter<CustomerRefundsList> csvExporter = new CSVExporter<CustomerRefundsList>(
					icsvExportRunner);
			return csvExporter.export(customerRefundsList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public String getBillsAndItemReceiptListExportCsv(boolean isExpensesList,
			int transactionType, long fromDate, long toDate, int viewType) {
		try {
			FinanceDate[] dates = getMinimumAndMaximumDates(
					new ClientFinanceDate(fromDate), new ClientFinanceDate(
							toDate), getCompanyId());
			PaginationList<BillsList> billsList = getFinanceTool()
					.getVendorManager().getBillsList(isExpensesList,
							getCompanyId(), transactionType,
							dates[0].getDate(), dates[1].getDate(), 0, -1,
							viewType);
			ICSVExportRunner<BillsList> icsvExportRunner = new ICSVExportRunner<BillsList>() {

				@Override
				public String[] getColumns() {

					return new String[] {
							messages.type(),
							messages.date(),
							messages.no(),
							Global.get().messages()
									.payeeName(Global.get().Vendor()),
							messages.originalAmount(), messages.balance() };
				}

				@Override
				public String getColumnValue(BillsList obj, int index) {
					String columnValue = null;
					switch (index) {
					case 0:
						columnValue = Utility.getTransactionName(obj.getType());
						break;
					case 1:
						columnValue = Utility
								.getDateInSelectedFormat(new FinanceDate(obj
										.getDate()));
						break;
					case 2:
						columnValue = obj.getNumber() != null ? obj.getNumber()
								: "";
						break;
					case 3:
						columnValue = obj.getVendorName();

						break;
					case 4:
						columnValue = obj.getOriginalAmount() != 0 ? String
								.valueOf(obj.getOriginalAmount()) : "";
						break;
					case 5:
						columnValue = obj.getBalance() != 0 ? String
								.valueOf(obj.getBalance()) : "";
						break;

					}
					return columnValue;
				}
			};
			CSVExporter<BillsList> csvExporter = new CSVExporter<BillsList>(
					icsvExportRunner);
			return csvExporter.export(billsList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getVendorPaymentsListExportCsv(long fromDate, long toDate,
			int viewType) {
		try {
			FinanceDate[] dates = getMinimumAndMaximumDates(
					new ClientFinanceDate(fromDate), new ClientFinanceDate(
							toDate), getCompanyId());
			PaginationList<PaymentsList> vendorPaymentsList = getFinanceTool()
					.getVendorManager().getVendorPaymentsList(getCompanyId(),
							dates[0].getDate(), dates[1].getDate(), 0, -1,
							viewType);
			ICSVExportRunner<PaymentsList> icsvExportRunner = new ICSVExportRunner<PaymentsList>() {

				@Override
				public String[] getColumns() {
					return new String[] { messages.payDate(), messages.payNo(),
							messages.status(), messages.issueDate(),
							messages.name(), messages.type(),
							messages.payMethod(), messages.checkNo(),
							messages.amountPaid() };
				}

				@Override
				public String getColumnValue(PaymentsList obj, int index) {
					String columnValue = null;
					switch (index) {
					case 0:
						columnValue = Utility
								.getDateInSelectedFormat(new FinanceDate(obj
										.getPaymentDate()));
						break;
					case 1:
						columnValue = obj.getPaymentNumber();
						break;
					case 2:
						columnValue = Utility.getStatus(obj.getType(),
								obj.getStatus());
						break;
					case 3:
						columnValue = Utility
								.getDateInSelectedFormat(new FinanceDate(obj
										.getIssuedDate()));
						break;
					case 4:
						columnValue = obj.getName();
						break;
					case 5:
						columnValue = Utility.getTransactionName(obj.getType());
						break;
					case 6:
						columnValue = obj.getPaymentMethodName();
						break;
					case 7:
						columnValue = obj.getCheckNumber();
						break;
					case 8:
						columnValue = String.valueOf(obj.getAmountPaid());
						break;
					}

					return columnValue;
				}
			};
			CSVExporter<PaymentsList> csvExporter = new CSVExporter<PaymentsList>(
					icsvExportRunner);
			return csvExporter.export(vendorPaymentsList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getPaymentsListExportCsv(long fromDate, long toDate,
			int viewType) {
		try {
			FinanceDate[] dates = getMinimumAndMaximumDates(
					new ClientFinanceDate(fromDate), new ClientFinanceDate(
							toDate), getCompanyId());
			PaginationList<PaymentsList> paymentsList = getFinanceTool()
					.getCustomerManager().getPaymentsList(getCompanyId(),
							dates[0].getDate(), dates[1].getDate(), 0, -1,
							viewType);
			ICSVExportRunner<PaymentsList> icsvExportRunner = new ICSVExportRunner<PaymentsList>() {

				@Override
				public String[] getColumns() {
					return new String[] { messages.payDate(), messages.payNo(),
							messages.status(), messages.issueDate(),
							messages.name(), messages.type(),
							messages.payMethod(), messages.checkNo(),
							messages.amountPaid() };
				}

				@Override
				public String getColumnValue(PaymentsList obj, int index) {
					String columnValue = null;
					switch (index) {
					case 0:
						columnValue = Utility
								.getDateInSelectedFormat(new FinanceDate(obj
										.getPaymentDate()));
						break;
					case 1:
						columnValue = obj.getPaymentNumber();
						break;
					case 2:
						columnValue = Utility.getStatus(obj.getType(),
								obj.getStatus());
						break;
					case 3:
						columnValue = Utility
								.getDateInSelectedFormat(new FinanceDate(obj
										.getIssuedDate()));
						break;
					case 4:
						columnValue = obj.getName();
						break;
					case 5:
						columnValue = Utility.getTransactionName(obj.getType());
						break;
					case 6:
						columnValue = obj.getPaymentMethodName();
						break;
					case 7:
						columnValue = obj.getCheckNumber();
						break;
					case 8:
						columnValue = String.valueOf(obj.getAmountPaid());
						break;
					}

					return columnValue;
				}
			};
			CSVExporter<PaymentsList> csvExporter = new CSVExporter<PaymentsList>(
					icsvExportRunner);
			return csvExporter.export(paymentsList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getPayeeChecksExportCsv(boolean isCustomerChecks,
			long fromDate, long toDate, int viewType) {
		try {
			FinanceDate[] dates = getMinimumAndMaximumDates(
					new ClientFinanceDate(fromDate), new ClientFinanceDate(
							toDate), getCompanyId());
			PaginationList<PaymentsList> payeeChecks = getFinanceTool()
					.getVendorManager().getPayeeChecks(getCompanyId(),
							isCustomerChecks, dates[0], dates[1], viewType, 0,
							-1);
			ICSVExportRunner<PaymentsList> icsvExportRunner = new ICSVExportRunner<PaymentsList>() {

				@Override
				public String[] getColumns() {
					return new String[] { messages.payDate(), messages.payNo(),
							messages.status(), messages.issueDate(),
							messages.name(), messages.type(),
							messages.payMethod(), messages.checkNo(),
							messages.amountPaid() };
				}

				@Override
				public String getColumnValue(PaymentsList obj, int index) {
					String columnValue = null;
					switch (index) {
					case 0:
						columnValue = Utility
								.getDateInSelectedFormat(new FinanceDate(obj
										.getPaymentDate()));
						break;
					case 1:
						columnValue = obj.getPaymentNumber();
						break;
					case 2:
						columnValue = Utility.getStatus(obj.getType(),
								obj.getStatus());
						break;
					case 3:
						columnValue = Utility
								.getDateInSelectedFormat(new FinanceDate(obj
										.getIssuedDate()));
						break;
					case 4:
						columnValue = obj.getName();
						break;
					case 5:
						columnValue = Utility.getTransactionName(obj.getType());
						break;
					case 6:
						columnValue = obj.getPaymentMethodName();
						break;
					case 7:
						columnValue = obj.getCheckNumber();
						break;
					case 8:
						columnValue = String.valueOf(obj.getAmountPaid());
						break;
					}

					return columnValue;
				}
			};
			CSVExporter<PaymentsList> csvExporter = new CSVExporter<PaymentsList>(
					icsvExportRunner);
			return csvExporter.export(payeeChecks);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getFixedAssetListExportCsv(int status) {
		try {
			ArrayList<FixedAsset> fixedAssets = getFinanceTool()
					.getFixedAssetManager().getFixedAssets(status,
							getCompanyId());
			ICSVExportRunner<FixedAsset> icsvExportRunner = new ICSVExportRunner<FixedAsset>() {

				@Override
				public String[] getColumns() {
					return new String[] { messages.item(),
							messages.assetNumber(), messages.Account(),
							messages.purchaseDate(), messages.purchasePrice() };
				}

				@Override
				public String getColumnValue(FixedAsset obj, int index) {
					String columnValue = null;
					switch (index) {
					case 0:
						columnValue = obj.getName() != null ? obj.getName()
								: "";
						break;
					case 1:
						columnValue = obj.getAssetNumber() != null ? obj
								.getAssetNumber() : "";
						break;
					case 2:
						columnValue = obj.getAssetAccount() != null ? obj
								.getAssetAccount().getName() : "";
						break;
					case 3:
						columnValue = obj.getPurchaseDate() != null ? Utility
								.getDateInSelectedFormat(obj.getPurchaseDate())
								: "";
						break;
					case 4:
						columnValue = obj.getPurchasePrice() != 0 ? String
								.valueOf(obj.getPurchasePrice()) : "";
						break;
					}
					return columnValue;
				}
			};
			CSVExporter<FixedAsset> csvExporter = new CSVExporter<FixedAsset>(
					icsvExportRunner);
			return csvExporter.export(fixedAssets);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getAccountsExportCsv(int typeOfAccount,
			boolean isActiveAccount) {
		try {
			PaginationList<ClientAccount> accounts = new PaginationList<ClientAccount>();
			if (typeOfAccount == ClientAccount.TYPE_BANK) {
				accounts = getFinanceTool()
						.getCompanyManager()
						.getBankAccounts(getCompanyId(), isActiveAccount, 0, -1);
			} else {
				accounts = getFinanceTool().getCompanyManager().getAccounts(
						typeOfAccount, isActiveAccount, 0, -1, getCompanyId());
			}
			ICSVExportRunner<ClientAccount> icsvExportRunner = new ICSVExportRunner<ClientAccount>() {

				@Override
				public String[] getColumns() {

					return new String[] { messages.no(), messages.name(),
							messages.type(), messages.balance() };
				}

				@Override
				public String getColumnValue(ClientAccount obj, int index) {
					switch (index) {
					case 0:
						return obj.getNumber() != null ? obj.getNumber() : "";
					case 1:
						return obj.getName() != null ? obj.getName() : "";
					case 2:
						return obj.getType() != 0 ? Utility
								.getAccountTypeString(obj.getType()) : " ";
					case 3:
						return String.valueOf(obj
								.getTotalBalanceInAccountCurrency());
					}
					return null;
				}
			};

			CSVExporter<ClientAccount> csvExporter = new CSVExporter<ClientAccount>(
					icsvExportRunner);
			return csvExporter.export(accounts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getJournalEntriesExportCsv(long fromDate, long toDate) {

		try {
			FinanceDate[] dates = getMinimumAndMaximumDates(
					new ClientFinanceDate(fromDate), new ClientFinanceDate(
							toDate), getCompanyId());
			PaginationList<JournalEntry> journalEntries = getFinanceTool()
					.getJournalEntries(getCompanyId(), dates[0], dates[1], 0,
							-1);
			ICSVExportRunner<JournalEntry> icsvExportRunner = new ICSVExportRunner<JournalEntry>() {

				@Override
				public String[] getColumns() {
					return new String[] { messages.no(), messages.date(),
							messages.memo(), messages.amount() };
				}

				@Override
				public String getColumnValue(JournalEntry obj, int index) {
					String columnValue = null;
					switch (index) {
					case 0:
						columnValue = obj.getNumber();
						break;
					case 1:
						columnValue = obj.getDate() != null ? Utility
								.getDateInSelectedFormat(obj.getDate()) : "";
						break;
					case 2:
						columnValue = obj.getMemo();
						break;
					case 3:
						columnValue = obj.getTotal() != 0 ? String.valueOf(obj
								.getTotal()) : "";
						break;
					}
					return columnValue;
				}
			};
			CSVExporter<JournalEntry> csvExporter = new CSVExporter<JournalEntry>(
					icsvExportRunner);
			return csvExporter.export(journalEntries);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getUsersActivityLogExportCsv(ClientFinanceDate startDate,
			ClientFinanceDate endDate, long value) {
		try {
			final Company company = getFinanceTool().getCompany(getCompanyId());
			PaginationList<ClientActivity> usersActivityLog = getFinanceTool()
					.getUserManager().getUsersActivityLog(startDate, endDate,
							0, -1, getCompanyId(), value);
			ICSVExportRunner<ClientActivity> icsvExportRunner = new ICSVExportRunner<ClientActivity>() {

				@Override
				public String[] getColumns() {
					return new String[] { messages.modifiedTime(),
							messages.userName(), messages.activity(),
							messages.name(), messages.date(), messages.amount() };
				}

				@Override
				public String getColumnValue(ClientActivity obj, int index) {
					String columnValue = null;
					switch (index) {
					case 0:
						String dateInSelectedFormat = Utility
								.getDateInSelectedFormat(new FinanceDate(
										new Date(obj.getTime())));
						columnValue = dateInSelectedFormat;

						break;
					case 1:
						columnValue = obj.getUserName() != null ? obj
								.getUserName() : " ";
						break;
					case 2:
						columnValue = Utility.getActivityType(obj
								.getActivityType());
						break;
					case 3:
						columnValue = obj.getName() != null ? obj.getName()
								: " ";
						break;
					case 4:
						columnValue = obj.getDate() != null ? Utility
								.getDateInSelectedFormat(new FinanceDate(obj
										.getDate())) : "";
						break;
					case 5:
						columnValue = obj.getAmount() != null ? String
								.valueOf(obj.getAmount()) : "";
						break;
					}
					return columnValue;
				}
			};
			CSVExporter<ClientActivity> csvExporter = new CSVExporter<ClientActivity>(
					icsvExportRunner);
			return csvExporter.export(usersActivityLog);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getRecurringsListExportCsv(long fromDate, long toDate) {
		try {
			FinanceDate[] dates = getMinimumAndMaximumDates(
					new ClientFinanceDate(fromDate), new ClientFinanceDate(
							toDate), getCompanyId());
			PaginationList<ClientRecurringTransaction> allRecurringTransactions = getFinanceTool()
					.getAllRecurringTransactions(getCompanyId(), dates[0],
							dates[1]);
			ICSVExportRunner<ClientRecurringTransaction> icsvExportRunner = new ICSVExportRunner<ClientRecurringTransaction>() {

				@Override
				public String[] getColumns() {
					return new String[] { messages.name(), messages.type(),
							messages.transactionType(), messages.interval(),
							messages.prevSchedule(), messages.nextSchedule(),
							messages.amount() };
				}

				@Override
				public String getColumnValue(ClientRecurringTransaction obj,
						int index) {
					String columnValue = " ";
					switch (index) {
					case 0:// Name
						columnValue = obj.getName();
						break;
					case 1: // Recurring Type
						switch (obj.getType()) {
						case 0:
							columnValue = messages.scheduled();
						case 1:
							columnValue = messages.reminder();
						case 2:
							columnValue = messages.unScheduled();
						}
						break;
					case 2: // Transaction Type
						columnValue = obj.getTransaction() != null ? Utility
								.getTransactionName(obj.getTransaction()
										.getType()) : "";
						break;
					case 3:
						if (obj.getType() != 0)
							if (obj.getType() != ClientRecurringTransaction.RECURRING_UNSCHEDULED) {
								columnValue = obj.getFrequencyString();
							} else {
								columnValue = "";
							}
						break;
					case 4: // prevScheduleOn
						columnValue = Utility.getDateInSelectedFormat(obj
								.getPrevScheduleOn() == 0 ? null
								: new FinanceDate(obj.getPrevScheduleOn()));
						break;
					case 5: // nextScheduleOn
						columnValue = Utility.getDateInSelectedFormat(obj
								.getNextScheduleOn() == 0 ? null
								: new FinanceDate(obj.getNextScheduleOn()));
						break;
					case 6: // transaction amount
						columnValue = obj.getTransaction() != null ? String
								.valueOf(obj.getTransaction().getTotal()) : "";
						break;
					}
					return columnValue;
				}
			};
			CSVExporter<ClientRecurringTransaction> csvExporter = new CSVExporter<ClientRecurringTransaction>(
					icsvExportRunner);
			return csvExporter.export(allRecurringTransactions);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public String getRemindersListExportCsv() {
		try {
			PaginationList<ClientReminder> remindersList = getFinanceTool()
					.getCompanyManager().getRemindersList(getCompanyId());
			ICSVExportRunner<ClientReminder> icsvExportRunner = new ICSVExportRunner<ClientReminder>() {

				@Override
				public String[] getColumns() {
					return new String[] { messages.name(), " ",
							messages.transactionDate(),
							messages.transactionType(), messages.amount() };
				}

				@Override
				public String getColumnValue(ClientReminder obj, int index) {
					String columnValue = null;
					switch (index) {
					case 0:
						columnValue = obj.getName();
						break;
					case 1:
						columnValue = messages.editAndCreate();
						break;
					case 2:
						columnValue = obj.getTransactionDate() != 0.0 ? Utility
								.getDateInSelectedFormat(new FinanceDate(obj
										.getTransactionDate())) : "";
						break;
					case 3:
						columnValue = obj.getRecurringTransaction() != null ? Utility
								.getTransactionName(obj
										.getRecurringTransaction()
										.getTransaction().getType()) : "";
						break;
					case 4:
						columnValue = obj.getRecurringTransaction() != null ? String
								.valueOf(obj.getRecurringTransaction()
										.getTransaction().getTotal()) : "";
						break;
					}
					return columnValue;
				}
			};
			CSVExporter<ClientReminder> csvExporter = new CSVExporter<ClientReminder>(
					icsvExportRunner);
			return csvExporter.export(remindersList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getWarehousesExportCsv() {
		try {
			PaginationList<ClientWarehouse> warehouses = getFinanceTool()
					.getInventoryManager().getWarehouses(getCompanyId());
			ICSVExportRunner<ClientWarehouse> icsvExportRunner = new ICSVExportRunner<ClientWarehouse>() {

				@Override
				public String[] getColumns() {
					return new String[] { messages.warehouseCode(),
							messages.warehouseName(), messages.ddiNumber() };
				}

				@Override
				public String getColumnValue(ClientWarehouse obj, int index) {
					String columnValue = null;
					switch (index) {
					case 0:
						columnValue = obj.getWarehouseCode() != null ? obj
								.getWarehouseCode() : " ";
						break;
					case 1:
						columnValue = obj.getName() != null ? obj.getName()
								: "";
						break;
					case 2:
						columnValue = obj.getDDINumber() != null ? obj
								.getDDINumber() : " ";
						break;
					}
					return columnValue;
				}
			};
			CSVExporter<ClientWarehouse> csvExporter = new CSVExporter<ClientWarehouse>(
					icsvExportRunner);
			return csvExporter.export(warehouses);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getWarehouseTransfersListExportCsv() {
		try {
			ArrayList<ClientStockTransfer> warehouseTransfersList = getFinanceTool()
					.getInventoryManager().getWarehouseTransfersList(
							getCompanyId());
			final Company company = getFinanceTool().getCompany(getCompanyId());
			ICSVExportRunner<ClientStockTransfer> icsvExportRunner = new ICSVExportRunner<ClientStockTransfer>() {
				@Override
				public String[] getColumns() {
					return new String[] { messages.fromWarehouse(),
							messages.toWarehouse(), messages.itemStatus() };
				}

				@Override
				public String getColumnValue(ClientStockTransfer obj, int index) {

					String columnValue = null;
					switch (index) {
					case 0:
						if (obj.getFromWarehouse() != 0)
							for (Warehouse warehouse : company.getWarehouses()) {
								if (warehouse.getID() == obj.getFromWarehouse()) {
									columnValue = warehouse.getName();
								}
							}
						break;
					case 1:
						if (obj.getToWarehouse() != 0)
							for (Warehouse warehouse : company.getWarehouses()) {
								if (warehouse.getID() == obj.getToWarehouse()) {
									columnValue = warehouse.getName();
								}
							}
						break;
					case 2:
						StringBuffer result = new StringBuffer();
						if (!obj.getStockTransferItems().isEmpty()) {
							for (ClientStockTransferItem item : obj
									.getStockTransferItems()) {
								result.append(" ");
								result.append(Accounter.getCompany()
										.getItem(item.getItem()).getName());
								result.append(" : ");
								result.append(item.getQuantity().getValue());
								result.append(" ");
								result.append(Accounter
										.getCompany()
										.getUnitById(
												item.getQuantity().getUnit())
										.getName());
								result.append(",");
							}
							columnValue = result.toString();
						} else {
							columnValue = " ";
						}
						break;

					}
					return columnValue;
				}
			};
			CSVExporter<ClientStockTransfer> csvExporter = new CSVExporter<ClientStockTransfer>(
					icsvExportRunner);
			return csvExporter.export(warehouseTransfersList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getStockAdjustmentsExportCsv() {
		try {
			ArrayList<StockAdjustmentList> stockAdjustments = getFinanceTool()
					.getInventoryManager().getStockAdjustments(getCompanyId());
			final Company company = getFinanceTool().getCompany(getCompanyId());

			ICSVExportRunner<StockAdjustmentList> icsvExportRunner = new ICSVExportRunner<StockAdjustmentList>() {
				@Override
				public String[] getColumns() {
					return new String[] { messages.wareHouse(),
							messages.itemName(), messages.adjustmentQty() };
				}

				@Override
				public String getColumnValue(StockAdjustmentList obj, int index) {
					String columnValue = null;
					switch (index) {
					case 0:
						columnValue = obj.getWareHouse() != null ? obj
								.getWareHouse() : "";
						break;
					case 1:
						columnValue = obj.getItem() != null ? obj.getItem()
								: "";
						break;
					case 2:
						if (obj.getQuantity().getUnit() != 0) {
							Unit unit = (Unit) HibernateUtil
									.getCurrentSession().get(Unit.class,
											obj.getQuantity().getUnit());
							StringBuffer result = new StringBuffer();
							result.append(obj.getQuantity().getValue());
							result.append(" ");
							result.append(unit.getMeasurement().getName());
							columnValue = result.toString();
						} else {
							columnValue = " ";
						}
						break;
					}
					return columnValue;
				}
			};
			CSVExporter<StockAdjustmentList> csvExporter = new CSVExporter<StockAdjustmentList>(
					icsvExportRunner);
			return csvExporter.export(stockAdjustments);
		} catch (Exception e) {

		}
		return null;
	}

	@Override
	public String getAllUnitsExportCsv() {
		try {
			PaginationList<ClientMeasurement> allUnits = getFinanceTool()
					.getInventoryManager().getAllUnits(getCompanyId());
			ICSVExportRunner<ClientMeasurement> icsvExportRunner = new ICSVExportRunner<ClientMeasurement>() {

				@Override
				public String[] getColumns() {
					return new String[] { messages.measurementName(),
							messages.measurementDescription(),
							messages.unitName(), messages.factor() };
				}

				@Override
				public String getColumnValue(ClientMeasurement obj, int index) {
					String columnValue = null;
					switch (index) {
					case 0:
						columnValue = obj.getName();
						break;
					case 1:
						columnValue = obj.getDesctiption();
						break;
					case 2:
						for (ClientUnit unit : obj.getUnits()) {
							if (unit.isDefault()) {
								columnValue = String.valueOf(unit.getType());
							}
						}
						break;
					case 3:
						for (ClientUnit unit : obj.getUnits()) {
							if (unit.isDefault()) {
								columnValue = String.valueOf(unit.getFactor());
							}
							break;
						}
					}
					return columnValue;
				}
			};
			CSVExporter<ClientMeasurement> csvExporter = new CSVExporter<ClientMeasurement>(
					icsvExportRunner);
			return csvExporter.export(allUnits);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param companyId
	 * @return
	 */
	public ArrayList<ClientFinanceDate> getMinimumAndMaximumTransactionDate(
			long companyId) {
		List<ClientFinanceDate> transactionDates = new ArrayList<ClientFinanceDate>();
		try {

			ClientFinanceDate[] dates = getFinanceTool().getManager()
					.getMinimumAndMaximumTransactionDate(getCompanyId());
			transactionDates.add(dates[0]);
			transactionDates.add(dates[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<ClientFinanceDate>(transactionDates);
	}

	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @param companyId
	 * @return
	 */
	private FinanceDate[] getMinimumAndMaximumDates(
			ClientFinanceDate startDate, ClientFinanceDate endDate,
			long companyId) {

		List<ClientFinanceDate> dates = getMinimumAndMaximumTransactionDate(companyId);
		ClientFinanceDate startDate1 = dates.get(0) == null ? new ClientFinanceDate()
				: dates.get(0);
		ClientFinanceDate endDate2 = dates.get(1) == null ? new ClientFinanceDate()
				: dates.get(1);

		FinanceDate transtartDate;
		if (startDate == null || startDate.isEmpty())
			transtartDate = new FinanceDate(startDate1);
		else
			transtartDate = new FinanceDate(startDate.getDate());
		FinanceDate tranendDate;
		if (endDate == null || endDate.isEmpty())
			tranendDate = new FinanceDate(endDate2);
		else
			tranendDate = new FinanceDate(endDate.getDate());

		return new FinanceDate[] { transtartDate, tranendDate };
	}

	@Override
	public String getItemsExportCsv(final boolean isPurchaseType,
			final boolean isSalesType, String viewType) {

		try {
			List<Item> allItems = null;

			Session session = HibernateUtil.getCurrentSession();

			List<Item> items = session.getNamedQuery("getAllItems")
					.setParameter("companyId", getCompanyId()).list();

			if (isSalesType && isPurchaseType) {
				allItems = getItems(items, viewType);
			} else if (isPurchaseType) {
				allItems = getPurchaseItems(items, viewType);
			} else if (isSalesType) {
				allItems = getSalesItems(items, viewType);
			}

			ICSVExportRunner<Item> icsvExportRunner = new ICSVExportRunner<Item>() {

				@Override
				public String[] getColumns() {

					if (isPurchaseType && isSalesType) {
						return new String[] { messages.itemName(),
								messages.description(), messages.type(),
								messages.salesPrice(), messages.purchasePrice() };
					} else if (isPurchaseType) {
						return new String[] { messages.itemName(),
								messages.description(), messages.type(),
								messages.purchasePrice() };
					} else {
						return new String[] { messages.itemName(),
								messages.description(), messages.type(),
								messages.salesPrice() };
					}
				}

				@Override
				public String getColumnValue(Item obj, int index) {
					switch (index) {
					case 0:
						return obj.getName() != null ? obj.getName() : "";
					case 1:
						if (!isPurchaseType) {
							return obj.getSalesDescription() != null ? obj
									.getSalesDescription() : "";
						} else
							return obj.getPurchaseDescription() != null ? obj
									.getPurchaseDescription() : "";

					case 2:
						return Utility.getItemTypeText(obj) != null ? Utility
								.getItemTypeText(obj) : "";
					case 3:
						if (!(isPurchaseType && isSalesType)) {
							if (obj.isISellThisItem()) {
								return String.valueOf(obj.getSalesPrice());
							} else {
								return String.valueOf(obj.getPurchasePrice());
							}
						}
						if (isSalesType) {
							return String.valueOf(obj.getSalesPrice());
						} else
							return String.valueOf(obj.getPurchasePrice());

					case 4:
						if (isPurchaseType && isSalesType) {
							return String.valueOf(obj.getPurchasePrice());
						}
					}
					return null;
				}
			};
			CSVExporter<Item> csvExporter = new CSVExporter<Item>(
					icsvExportRunner);
			return csvExporter.export(allItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param allItems
	 * @param viewType
	 * @return
	 */
	private List<Item> getSalesItems(List<Item> allItems, String viewType) {
		List<Item> items = new ArrayList<Item>();
		for (Item item : allItems) {
			if (item.getType() == Item.TYPE_SERVICE)
				if (viewType.equalsIgnoreCase(messages.active())) {
					if (item.isActive() == true) {
						items.add(item);
					}
				} else {
					if (item.isActive() == false) {
						items.add(item);
					}
				}
		}
		return items;

	}

	/**
	 * 
	 * @param allItems
	 * @param viewType
	 * @return
	 */
	private List<Item> getPurchaseItems(List<Item> allItems, String viewType) {
		List<Item> items = new ArrayList<Item>();
		for (Item item : allItems) {
			if (item.getType() == Item.TYPE_NON_INVENTORY_PART)
				if (viewType.equalsIgnoreCase(messages.active())) {
					if (item.isActive() == true) {
						items.add(item);
					}
				} else {
					if (item.isActive() == false) {
						items.add(item);
					}
				}
		}
		return items;

	}

	/**
	 * filter the active or in active.
	 * 
	 * @param set
	 * @param viewType
	 * @return
	 */
	private List<Item> getItems(List<Item> allItems, String viewType) {
		List<Item> items = new ArrayList<Item>();
		for (Item item : allItems) {
			if (viewType.equalsIgnoreCase(messages.active())) {
				if (item.isActive() == true) {
					items.add(item);
				}
			} else {
				if (item.isActive() == false) {
					items.add(item);
				}
			}
		}
		return items;

	}

	@Override
	public String getTaxItemsListExportCsv(String viewType) {
		List<TAXItem> taxItems = new ArrayList<TAXItem>();
		Session session = HibernateUtil.getCurrentSession();

		List<TAXItem> list = session.getNamedQuery("getTaxItems")
				.setParameter("companyId", getCompanyId()).list();
		try {
			final Company company = getFinanceTool().getCompany(getCompanyId());
			for (TAXItem taxItem : list) {
				if (viewType.equalsIgnoreCase(messages.active())) {
					if (taxItem.isActive() == true) {
						taxItems.add(taxItem);
					}
				} else {
					if (taxItem.isActive() == false) {
						taxItems.add(taxItem);
					}
				}
			}
			ICSVExportRunner<TAXItem> icsvExportRunner = new ICSVExportRunner<TAXItem>() {

				@Override
				public String[] getColumns() {
					return new String[] { messages.product(),
							messages.vatAgency(), messages.description(),
							messages.rate() };
				}

				@Override
				public String getColumnValue(TAXItem item, int index) {
					switch (index) {

					case 0:
						return item.getName() != null ? item.getName() : "";
					case 1:
						TAXAgency agency = null;
						if (item.getTaxAgency() != null) {
							agency = item.getTaxAgency();
						}
						return agency != null ? agency.getName() : "";
					case 2:
						return item.getDescription() != null ? item
								.getDescription() : "";
					case 3:
						if (item.isPercentage())
							return item.getTaxRate() + "%";
						else
							return String.valueOf(item.getTaxRate());
					}
					return null;
				}
			};
			CSVExporter<TAXItem> csvExporter = new CSVExporter<TAXItem>(
					icsvExportRunner);
			return csvExporter.export(taxItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getTaxCodesListExportCsv(String viewType) {
		List<TAXCode> taxCodes = new ArrayList<TAXCode>();
		Session session = HibernateUtil.getCurrentSession();
		List<TAXCode> list = session.getNamedQuery("getTaxcodes")
				.setParameter("companyId", getCompanyId()).list();
		try {
			for (TAXCode taxCode : list) {
				if (viewType.equalsIgnoreCase(messages.active())) {
					if (taxCode.isActive() == true) {
						taxCodes.add(taxCode);
					}
				} else {
					if (taxCode.isActive() == false) {
						taxCodes.add(taxCode);
					}
				}
			}
			ICSVExportRunner<TAXCode> icsvExportRunner = new ICSVExportRunner<TAXCode>() {

				@Override
				public String[] getColumns() {
					return new String[] { messages.code(),
							messages.description(), };
				}

				@Override
				public String getColumnValue(TAXCode taxCode, int index) {
					switch (index) {
					case 0:
						return taxCode.getName() != null ? taxCode.getName()
								: "";
					case 1:
						return taxCode.getDescription() != null ? taxCode
								.getDescription() : "";
					}
					return null;
				}
			};
			CSVExporter<TAXCode> csvExporter = new CSVExporter<TAXCode>(
					icsvExportRunner);
			return csvExporter.export(taxCodes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getSalesPersonsListExportCsv(String viewType) {
		ArrayList<SalesPerson> salesPersons = new ArrayList<SalesPerson>();
		Session session = HibernateUtil.getCurrentSession();
		List<SalesPerson> list = session.getNamedQuery("getSalesPersons")
				.setParameter("companyId", getCompanyId()).list();
		for (SalesPerson salesPerson : list) {
			if (viewType.equalsIgnoreCase(messages.active())) {
				if (salesPerson.isActive() == true) {
					salesPersons.add(salesPerson);
				}
			} else {
				if (salesPerson.isActive() == false) {
					salesPersons.add(salesPerson);
				}
			}
		}
		ICSVExportRunner<SalesPerson> icsvExportRunner = new ICSVExportRunner<SalesPerson>() {

			@Override
			public String[] getColumns() {
				return new String[] { messages.salesPerson(),
						messages.address(), messages.city(), messages.state(),
						messages.zipCode(), messages.phone(), messages.fax() };
			}

			@Override
			public String getColumnValue(SalesPerson salesPerson, int index) {
				switch (index) {
				case 0:
					return salesPerson.getFirstName();
				case 1:
					return salesPerson.getAddress() != null ? salesPerson
							.getAddress().getAddress1() : "";
				case 2:
					return salesPerson.getAddress() != null ? salesPerson
							.getAddress().getCity() : "";
				case 3:
					return salesPerson.getAddress() != null ? salesPerson
							.getAddress().getStateOrProvinence() : "";

				case 4:
					return salesPerson.getAddress() != null ? salesPerson
							.getAddress().getZipOrPostalCode() : "";
				case 5:
					return salesPerson.getPhoneNo();

				case 6:
					return salesPerson.getFaxNo();
				}
				return null;
			}
		};
		CSVExporter<SalesPerson> csvExporter = new CSVExporter<SalesPerson>(
				icsvExportRunner);
		try {
			return csvExporter.export(salesPersons);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
