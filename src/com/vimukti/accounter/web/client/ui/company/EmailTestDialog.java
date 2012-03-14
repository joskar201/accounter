package com.vimukti.accounter.web.client.ui.company;

import com.vimukti.accounter.web.client.AccounterAsyncCallback;
import com.vimukti.accounter.web.client.core.ClientEmailAccount;
import com.vimukti.accounter.web.client.core.ValidationResult;
import com.vimukti.accounter.web.client.exception.AccounterException;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.UIUtils;
import com.vimukti.accounter.web.client.ui.core.BaseDialog;
import com.vimukti.accounter.web.client.ui.core.EmailField;
import com.vimukti.accounter.web.client.ui.forms.DynamicForm;

public class EmailTestDialog extends BaseDialog {

	EmailField emailField;

	ClientEmailAccount emailAccount;

	public EmailTestDialog(ClientEmailAccount emailAccount) {
		super(messages.emailAddress());
		this.getElement().setId("EmailTestDialog");
		createControls();
		this.emailAccount = emailAccount;
	}

	private void createControls() {

		DynamicForm form = new DynamicForm("form");

		emailField = new EmailField(messages.emailAddress());
		emailField.setRequired(true);

		form.add(emailField);

		setBodyLayout(form);
	}

	@Override
	protected ValidationResult validate() {
		ValidationResult result = new ValidationResult();
		if (emailField.getValue() != null
				&& !UIUtils.isValidEmail(emailField.getValue())) {
			result.addError(emailField, messages.pleaseEnterValidEmailId());
		}
		return result;
	}

	@Override
	protected boolean onOK() {
		Accounter.createHomeService().sendTestMail(emailAccount,
				emailField.getValue(), new AccounterAsyncCallback<Boolean>() {

					@Override
					public void onException(AccounterException exception) {
						Accounter.showError(messages.messagesSendingFailed());
					}

					@Override
					public void onResultSuccess(Boolean result) {
						sendSuccess();
					}
				});
		return false;
	}

	private void sendSuccess() {
		this.removeFromParent();

		Accounter
				.showInformation(messages
						.ATestEmailIsSentToGivenEmailIdKindlyCheckYourEmailAndProceedIfYouGot());
	}

	@Override
	protected boolean onCancel() {
		return true;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
