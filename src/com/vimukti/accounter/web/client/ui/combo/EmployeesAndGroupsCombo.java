package com.vimukti.accounter.web.client.ui.combo;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.vimukti.accounter.web.client.core.ClientPayStructureDestination;
import com.vimukti.accounter.web.client.ui.Accounter;

public class EmployeesAndGroupsCombo extends
		CustomCombo<ClientPayStructureDestination> {

	private long empGroup;
	private boolean isItemsAdded;

	public EmployeesAndGroupsCombo(String title, String styleName) {
		super(title, false, 1, styleName);
		initList();
	}

	private void initList() {
		isItemsAdded = false;
		Accounter.createPayrollService().getEmployeesAndGroups(
				new AsyncCallback<List<ClientPayStructureDestination>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(
							List<ClientPayStructureDestination> result) {
						initCombo(result);
						EmployeesAndGroupsCombo.this.isItemsAdded = true;
						selectCombo();
					}
				});
	}

	@Override
	protected String getDisplayName(ClientPayStructureDestination object) {
		return object.getDisplayName();
	}

	@Override
	protected String getColumnData(ClientPayStructureDestination object, int col) {
		if (col == 0) {
			return object.getDisplayName();
		}
		return null;
	}

	@Override
	public String getDefaultAddNewCaption() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onAddNew() {
		// TODO Auto-generated method stub

	}

	public void setEmpGroup(long employeeorEGroup) {
		this.empGroup = employeeorEGroup;
		if (this.isItemsAdded) {
			selectCombo();
		}
	}

	private void selectCombo() {
		if (this.empGroup != 0) {
			for (ClientPayStructureDestination item : getComboItems()) {
				if (item.getID() == empGroup) {
					setComboItem(item);
					this.empGroup = 0;
					break;
				}
			}
		}
	}

}