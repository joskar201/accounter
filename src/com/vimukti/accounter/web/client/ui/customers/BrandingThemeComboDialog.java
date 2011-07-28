package com.vimukti.accounter.web.client.ui.customers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vimukti.accounter.web.client.core.ClientBrandingTheme;
import com.vimukti.accounter.web.client.core.ClientInvoice;
import com.vimukti.accounter.web.client.core.ClientTransaction;
import com.vimukti.accounter.web.client.core.IAccounterCore;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.UIUtils;
import com.vimukti.accounter.web.client.ui.combo.BrandingThemeCombo;
import com.vimukti.accounter.web.client.ui.combo.IAccounterComboSelectionChangeHandler;
import com.vimukti.accounter.web.client.ui.core.BaseDialog;
import com.vimukti.accounter.web.client.ui.forms.DynamicForm;

@SuppressWarnings("unchecked")
public class BrandingThemeComboDialog extends BaseDialog {
	private BrandingThemeCombo brandingThemeTypeCombo;
	private ClientTransaction clientTransaction;
	private ClientBrandingTheme brandingTheme;

	public BrandingThemeComboDialog(String title, String desc,
			ClientTransaction clientTransaction) {
		super(title, desc);
		this.clientTransaction = clientTransaction;
		createControls();
	}

	public BrandingThemeComboDialog(String title, String desc) {
		super(title, desc);
	}

	@Override
	public void processupdateView(IAccounterCore core, int command) {

	}

	private void createControls() {
		brandingThemeTypeCombo = new BrandingThemeCombo(Accounter
				.constants().selectTheme());
		brandingTheme = new ClientBrandingTheme();
		brandingThemeTypeCombo
				.addSelectionChangeHandler(new IAccounterComboSelectionChangeHandler<ClientBrandingTheme>() {

					@Override
					public void selectedComboBoxItem(
							ClientBrandingTheme selectItem) {
						brandingTheme = selectItem;
					}
				});

		okbtn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (brandingThemeTypeCombo.getSelectedValue().equals(null)) {
					brandingThemeTypeCombo.setSelected(Accounter
							.constants().standardTheme());
				}
				print();
				hide();
			}
		});
		cancelBtn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		DynamicForm dynamicForm = new DynamicForm();
		VerticalPanel comboPanel = new VerticalPanel();
		HorizontalPanel buttonPanel = new HorizontalPanel();

		dynamicForm.setFields(brandingThemeTypeCombo);

		comboPanel.add(dynamicForm);
		comboPanel.add(buttonPanel);

		setBodyLayout(comboPanel);
	}

	private void print() {
		UIUtils.downloadAttachment(((ClientInvoice) clientTransaction).getID(),
				ClientTransaction.TYPE_INVOICE, brandingTheme.getID());
	}

	@Override
	protected String getViewTitle() {
		return Accounter.constants().selectThemes();
	}
}
