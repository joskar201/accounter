package com.vimukti.accounter.web.client.ui.payroll;

import com.vimukti.accounter.web.client.core.ClientEmployeeCategory;
import com.vimukti.accounter.web.client.core.ValidationResult;
import com.vimukti.accounter.web.client.ui.core.BaseDialog;
import com.vimukti.accounter.web.client.ui.forms.DynamicForm;
import com.vimukti.accounter.web.client.ui.forms.TextItem;

public class NewEmployeeCategoryDialog extends
		BaseDialog<ClientEmployeeCategory> {

	private TextItem nameItem;
	private DynamicForm form;

	public NewEmployeeCategoryDialog(String title) {
		super(title);
		createControls();
	}

	public void createControls() {
		nameItem = new TextItem(messages.employeeCategory(), "nameItem");
		form = new DynamicForm("form");
		form.add(nameItem);
		bodyLayout.add(form);

	}

	@Override
	protected boolean onOK() {
		updateData();
		return true;
	}

	@Override
	protected ValidationResult validate() {
		ValidationResult result = new ValidationResult();
		result.add(form.validate());
		return super.validate();
	}

	private void updateData() {
		ClientEmployeeCategory category = new ClientEmployeeCategory();
		category.setName(nameItem.getValue());
		saveOrUpdate(category);
	}

	@Override
	public void setFocus() {
		nameItem.setFocus();
	}

}
