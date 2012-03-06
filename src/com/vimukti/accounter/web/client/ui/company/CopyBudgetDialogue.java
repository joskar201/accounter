package com.vimukti.accounter.web.client.ui.company;

import java.util.List;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vimukti.accounter.web.client.core.ClientBudget;
import com.vimukti.accounter.web.client.ui.UIUtils;
import com.vimukti.accounter.web.client.ui.combo.IAccounterComboSelectionChangeHandler;
import com.vimukti.accounter.web.client.ui.combo.SelectCombo;
import com.vimukti.accounter.web.client.ui.core.BaseDialog;
import com.vimukti.accounter.web.client.ui.forms.DynamicForm;

public class CopyBudgetDialogue extends BaseDialog {

	SelectCombo selectBudget;
	List<ClientBudget> budgetList;

	ClientBudget budgetName;

	public CopyBudgetDialogue(String title, String desc) {
		super(title, desc);
		createControls();
	}

	public CopyBudgetDialogue(String budgetTitle, String string,
			List<ClientBudget> listData) {
		super(budgetTitle, string);
		budgetList = listData;
		createControls();
	}

	private void createControls() {
		VerticalPanel verticalPanel = new VerticalPanel();
		setWidth("400px");

		selectBudget = new SelectCombo(messages.CopyfromExistingBudget());

		for (ClientBudget budget : budgetList) {
			selectBudget.addComboItem(budget.getBudgetName());
		}

		selectBudget
				.addSelectionChangeHandler(new IAccounterComboSelectionChangeHandler<String>() {

					@Override
					public void selectedComboBoxItem(String selectItem) {
						for (ClientBudget budget : budgetList) {
							if (selectBudget.getSelectedValue().equals(
									budget.getBudgetName())) {
								budgetName = budget;
								break;
							}
						}

					}

				});

		DynamicForm budgetInfoForm = UIUtils.form(messages
				.chartOfAccountsInformation());
		budgetInfoForm.setWidth("100%");

		budgetInfoForm.add(selectBudget);

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(budgetInfoForm);

		verticalPanel.add(horizontalPanel);

		setBodyLayout(verticalPanel);
		center();

	}

	@Override
	protected boolean onCancel() {
		return true;
	}

	@Override
	protected boolean onOK() {

		if (budgetName != null)
			getCallback().actionResult(budgetName);
		return true;
	}

	@Override
	public void setFocus() {
		selectBudget.setFocus();

	}
}
