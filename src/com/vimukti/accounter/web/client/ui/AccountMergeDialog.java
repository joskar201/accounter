package com.vimukti.accounter.web.client.ui;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.vimukti.accounter.web.client.AccounterAsyncCallback;
import com.vimukti.accounter.web.client.core.ClientAccount;
import com.vimukti.accounter.web.client.core.ValidationResult;
import com.vimukti.accounter.web.client.exception.AccounterException;
import com.vimukti.accounter.web.client.ui.combo.IAccounterComboSelectionChangeHandler;
import com.vimukti.accounter.web.client.ui.combo.OtherAccountsCombo;
import com.vimukti.accounter.web.client.ui.core.BaseDialog;
import com.vimukti.accounter.web.client.ui.forms.DynamicForm;
import com.vimukti.accounter.web.client.ui.forms.TextItem;

/**
 * 
 * @author Sai Prasad N
 * 
 */
public class AccountMergeDialog extends BaseDialog implements
		AsyncCallback<Void> {

	private DynamicForm form;
	private DynamicForm form1;

	private OtherAccountsCombo accountCombo;
	private OtherAccountsCombo accountCombo1;
	private TextItem accountNumberTextItem;
	private TextItem accountNumberTextItem1;
	private TextItem name;
	private TextItem name1;
	private TextItem balanceTextItem1;
	private TextItem balanceTextItem;
	private ClientAccount fromAccount;
	private ClientAccount toAccount;

	public AccountMergeDialog(String title, String descript) {
		super(title, descript);
		this.getElement().setId("AccountMergeDialog");
		okbtn.setText(messages.merge());
		createControls();
		center();
	}

	private void createControls() {
		form = new DynamicForm("form");
		form1 = new DynamicForm("form1");
		StyledPanel layout = new StyledPanel("layout");
		StyledPanel layout1 = new StyledPanel("layout1");
		StyledPanel horizontalPanel = new StyledPanel("horizontalPanel");
		accountCombo = createAccountCombo();
		accountCombo1 = createAccountCombo1();

		accountNumberTextItem = new TextItem(messages.payeeNumber(messages
				.Account()),"accountNumberTextItem");

		accountNumberTextItem1 = new TextItem(messages.payeeNumber(messages
				.Account()),"accountNumberTextItem1");

		name = new TextItem(messages.accountName(),"name");
		name1 = new TextItem(messages.accountName(),"name1");

		balanceTextItem = new TextItem(messages.balance(),"balanceTextItem");

		balanceTextItem1 = new TextItem(messages.balance(),"balanceTextItem1");

		form.add(accountCombo, accountNumberTextItem, name,
				balanceTextItem);

		form1.add(accountCombo1, accountNumberTextItem1, name1,
				balanceTextItem1);
		// form.setItems(getTextItems());
		layout.add(form);
		layout1.add(form1);
		horizontalPanel.add(layout);
		horizontalPanel.add(layout1);
		setBodyLayout(horizontalPanel);

	}

	private OtherAccountsCombo createAccountCombo1() {
		accountCombo1 = new OtherAccountsCombo(messages.payeeTo(messages
				.Account()), false);
		accountCombo1.setRequired(true);
		accountCombo1
				.addSelectionChangeHandler(new IAccounterComboSelectionChangeHandler<ClientAccount>() {

					@Override
					public void selectedComboBoxItem(ClientAccount selectItem) {
						toAccount = selectItem;
						customerSelected1(selectItem);

					}

				});

		return accountCombo1;
	}

	private OtherAccountsCombo createAccountCombo() {
		accountCombo = new OtherAccountsCombo(messages.payeeFrom(messages
				.Account()), false);
		accountCombo.setRequired(true);

		accountCombo
				.addSelectionChangeHandler(new IAccounterComboSelectionChangeHandler<ClientAccount>() {

					@Override
					public void selectedComboBoxItem(ClientAccount selectItem) {
						fromAccount = selectItem;
						customerSelected(selectItem);

					}

				});

		return accountCombo;
	}

	private void customerSelected(ClientAccount selectItem) {

		accountNumberTextItem.setValue(String.valueOf(selectItem.getNumber()));
		balanceTextItem.setValue(DataUtils
				.getAmountAsStringInPrimaryCurrency(selectItem
						.getOpeningBalance()));

		name.setValue(selectItem.getName());

	}

	private void customerSelected1(ClientAccount selectItem) {
		accountNumberTextItem1.setValue(String.valueOf(selectItem.getNumber()));
		balanceTextItem1.setValue(DataUtils
				.getAmountAsStringInPrimaryCurrency(selectItem
						.getOpeningBalance()));

		name1.setValue(selectItem.getName());

	}

	@Override
	protected ValidationResult validate() {
		ValidationResult result = form.validate();
		if (toAccount != null && fromAccount != null) {
			if ((toAccount.getID() == fromAccount.getID())) {
				result.addError(fromAccount, messages.notMoveAccount());
				return result;
			}
			if (toAccount.getType() != fromAccount.getType()) {
				result.addError(fromAccount, messages.notMoveDiffTypeAccount());
				return result;
			}
			if (fromAccount.getID() == toAccount.getID()) {
				result.addError(fromAccount, "Accounts must be different");
				return result;
			}

			if (fromAccount.getCurrency() != toAccount.getCurrency()) {
				result.addError(fromAccount,
						"Currencies of the both Accounts must be same ");
				return result;
			}

			return result;

		}

		result.add(form1.validate());

		return result;
	}

	@Override
	protected boolean onOK() {

		Accounter.createHomeService().mergeAccount(fromAccount, toAccount,
				new AccounterAsyncCallback<ClientAccount>() {

					@Override
					public void onException(AccounterException exception) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onResultSuccess(ClientAccount result) {
						getCompany().deleteAccount(fromAccount.getID());
						getCompany().processUpdateOrCreateObject(result);
						com.google.gwt.user.client.History.back();
					}

				});
		return true;
	}

	@Override
	public void onFailure(Throwable caught) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSuccess(Void result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus() {
		accountCombo.setFocus();

	}

}
