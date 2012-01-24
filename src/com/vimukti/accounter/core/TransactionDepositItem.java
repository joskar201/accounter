package com.vimukti.accounter.core;

import java.io.Serializable;

import org.hibernate.CallbackException;
import org.hibernate.Session;
import org.hibernate.classic.Lifecycle;
import org.json.JSONException;

import com.vimukti.accounter.web.client.Global;
import com.vimukti.accounter.web.client.exception.AccounterException;
import com.vimukti.accounter.web.client.externalization.AccounterMessages;

public class TransactionDepositItem implements IAccounterServerCore, Lifecycle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ReffereredObject
	private Payee receivedFrom;

	@ReffereredObject
	private Account account;
	private Double total;
	private int version;
	private String refNo;
	private long id;
	private boolean isBillable;

	@ReffereredObject
	private Customer customer;
	private String description;
	private String paymentMethod;
	private AccounterClass accounterClass;

	@ReffereredObject
	private Transaction transaction;

	private boolean isOnSaveProccessed;

	public Payee getReceivedFrom() {
		return receivedFrom;
	}

	public void setReceivedFrom(Payee receivedFrom) {
		this.receivedFrom = receivedFrom;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public String getName() {
		return refNo;
	}

	public void setName(String name) {
		this.refNo = name;
	}

	public boolean isBillable() {
		return isBillable;
	}

	public void setBillable(boolean isBillable) {
		this.isBillable = isBillable;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public AccounterClass getAccounterClass() {
		return accounterClass;
	}

	public void setAccounterClass(AccounterClass accounterClass) {
		this.accounterClass = accounterClass;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	@Override
	public int getVersion() {
		return version;
	}

	@Override
	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public long getID() {
		return getId();
	}

	@Override
	public boolean canEdit(IAccounterServerCore clientObject)
			throws AccounterException {
		if (this.account == null) {
			throw new AccounterException(
					AccounterException.ERROR_TRANSACTION_ITEM_NULL);
		}
		if (this.total < 0) {
			throw new AccounterException(
					AccounterException.ERROR_TRANSACTION_ITEM_TOTAL_0);
		}
		return true;
	}

	@Override
	public void writeAudit(AuditWriter w) throws JSONException {
		AccounterMessages messages = Global.get().messages();

		if (this.account != null) {
			w.put(messages.account(), this.account.name);
		}

		if (this.receivedFrom != null) {
			w.put(messages.receivedFrom(), this.receivedFrom.name);
		}

		w.put(messages.description(), this.description);

		w.put(messages.total(), this.total);

		if (this.refNo != null) {
			w.put(messages.referenceNo(), this.refNo);
		}

		if (this.customer != null) {
			w.put(messages.customer(), this.customer.name);
		}

		if (this.transaction.getLocation() != null) {
			w.put(messages.location(), this.transaction.getLocation().getName());
		}

		w.put(messages.isBillable(), this.isBillable);

		if (this.paymentMethod != null) {
			w.put(messages.paymentMethod(), this.paymentMethod);
		}

	}

	public boolean isOnSaveProccessed() {
		return isOnSaveProccessed;
	}

	public void setOnSaveProccessed(boolean isOnSaveProccessed) {
		this.isOnSaveProccessed = isOnSaveProccessed;
	}

	@Override
	public boolean onSave(Session session) throws CallbackException {
		checkNullValues();
		if (this.isOnSaveProccessed())
			return true;

		this.setOnSaveProccessed(true);

		if (this.transaction.type == Transaction.TYPE_EMPLOYEE_EXPENSE
				&& ((CashPurchase) this.transaction).expenseStatus != CashPurchase.EMPLOYEE_EXPENSE_STATUS_APPROVED)
			return false;

		if (!transaction.isDraftOrTemplate() && !transaction.isVoid()) {
			doCreateEffect(session);
		}
		return false;
	}

	private void checkNullValues() {
		if (this.total == null) {
			this.setTotal(new Double(0));
		}
	}

	private void doCreateEffect(Session session) {

		/**
		 * First take the Back up of the TransactionItem information
		 */
		Double amount = (transaction.isPositiveTransaction() ? 1d : -1d)
				* (this.total);
		// this.setUpdateAmount(amount);
		Account effectingAccount = this.account;
		if (effectingAccount != null) {
			effectingAccount.updateCurrentBalance(this.transaction, -amount,
					transaction.currencyFactor);
			session.saveOrUpdate(effectingAccount);
			effectingAccount.onUpdate(session);
		}
	}

	@Override
	public boolean onUpdate(Session s) throws CallbackException {
		return false;
	}

	@Override
	public boolean onDelete(Session session) throws CallbackException {

		if (transaction.isVoid()) {
			return false;
		}

		if (this.transaction.type == Transaction.TYPE_EMPLOYEE_EXPENSE
				|| transaction.isDraftOrTemplate())
			return false;

		Double amount = (transaction.isPositiveTransaction() ? 1d : -1d)
				* (this.total);

		Account effectingAccount = this.account;
		if (effectingAccount != null) {
			effectingAccount.updateCurrentBalance(this.transaction, amount,
					transaction.previousCurrencyFactor);

			session.saveOrUpdate(effectingAccount);
			effectingAccount.onUpdate(session);
		}

		return false;
	}

	@Override
	public void onLoad(Session s, Serializable id) {

	}

	public void doReverseEffect(Session session) {

		Account effectingAccount = this.getAccount();
		Double amount = (transaction.isPositiveTransaction() ? 1d : -1d)
				* (this.total);
		if (effectingAccount != null) {
			effectingAccount.updateCurrentBalance(this.transaction, amount,
					transaction.currencyFactor);
			effectingAccount.onUpdate(session);
			session.saveOrUpdate(effectingAccount);
		}

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}