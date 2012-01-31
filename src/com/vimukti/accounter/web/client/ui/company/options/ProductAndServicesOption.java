/**
 * 
 */
package com.vimukti.accounter.web.client.ui.company.options;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author vimukti36
 * 
 */
public class ProductAndServicesOption extends AbstractPreferenceOption {

	private static ProductAndServicesOptionUiBinder uiBinder = GWT
			.create(ProductAndServicesOptionUiBinder.class);
	@UiField
	VerticalPanel viewPanel;
	@UiField
	RadioButton servicesOnly;
	@UiField
	Label servicesOnlyText;
	@UiField
	Label headerLabel;
	@UiField
	RadioButton productsOnly;
	@UiField
	Label productsOnlyText;
	@UiField
	RadioButton both;
	@UiField
	Label bothText;
	@UiField
	VerticalPanel sell;
	@UiField
	CheckBox warehousesCheckBox;
	@UiField
	CheckBox inventoryCheckBox;
	@UiField
	CheckBox unitsCheckBox;
	@UiField
	VerticalPanel hiddenPanel;
	@UiField
	VerticalPanel totalPanel;
	@UiField
	VerticalPanel subpanel;

	interface ProductAndServicesOptionUiBinder extends
			UiBinder<Widget, ProductAndServicesOption> {
	}

	/**
	 * Because this class has a default constructor, it can be used as a binder
	 * template. In other words, it can be used in other *.ui.xml files as
	 * follows: <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 * xmlns:g="urn:import:**user's package**">
	 * <g:**UserClassName**>Hello!</g:**UserClassName> </ui:UiBinder> Note that
	 * depending on the widget that is used, it may be necessary to implement
	 * HasHTML instead of HasText.
	 */
	public ProductAndServicesOption() {
		initWidget(uiBinder.createAndBindUi(this));
		createControls();
		initData();
	}

	public ProductAndServicesOption(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));

	}

	public void createControls() {

		headerLabel.setText(messages.whatDoYouSell());
		// servicesOnlyText.setText(constants.whatDoYouSell());
		servicesOnly.setText(messages.services_labelonly());
		servicesOnlyText.setText(messages.servicesOnly());
		productsOnly.setText(messages.products_labelonly());

		productsOnlyText.setText(messages.productsOnly());
		both.setText(messages.bothservicesandProduct_labelonly());
		bothText.setText(messages.bothServicesandProducts());

		inventoryCheckBox.setText(messages.inventoryTracking());
		warehousesCheckBox.setText(messages.haveMultipleWarehouses());
		unitsCheckBox.setText(messages.units());

		servicesOnly.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				inventoryCheckBox.setValue(false);
				warehousesCheckBox.setValue(false);
				unitsCheckBox.setVisible(false);
				totalPanel.setVisible(false);

			}
		});
		productsOnly.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				totalPanel.setVisible(productsOnly.getValue());

			}
		});
		both.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				totalPanel.setVisible(both.getValue());

			}
		});

		inventoryCheckBox.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hiddenPanel.setVisible(inventoryCheckBox.getValue());
			}
		});
	}

	@Override
	public String getTitle() {
		return messages.productAndServices();
	}

	@Override
	public void onSave() {

		if (servicesOnly.getValue()) {
			getCompanyPreferences().setSellServices(true);
			getCompanyPreferences().setSellProducts(false);
		}
		if (productsOnly.getValue()) {
			getCompanyPreferences().setSellProducts(true);
			getCompanyPreferences().setSellServices(false);
		}
		if (both.getValue()) {
			getCompanyPreferences().setSellServices(true);
			getCompanyPreferences().setSellProducts(true);
		}
		getCompanyPreferences().setInventoryEnabled(
				inventoryCheckBox.getValue());
		if (inventoryCheckBox.getValue()) {
			getCompanyPreferences().setwareHouseEnabled(
					warehousesCheckBox.getValue());
			getCompanyPreferences().setUnitsEnabled(unitsCheckBox.getValue());
		}
	}

	@Override
	public String getAnchor() {
		return messages.productAndServices();
	}

	@Override
	public void initData() {

		boolean sellServices = getCompanyPreferences().isSellServices();
		if (sellServices)
			servicesOnly.setValue(true);
		boolean sellProducts = getCompanyPreferences().isSellProducts();
		if (sellProducts)
			productsOnly.setValue(true);
		if (sellServices && sellProducts)
			both.setValue(true);

		if (sellServices) {
			totalPanel.setVisible(false);
		}
		if ((sellProducts && sellServices) || sellProducts) {
			totalPanel.setVisible(true);
		}
		if (getCompanyPreferences().isInventoryEnabled()) {
			inventoryCheckBox.setValue(true);
			hiddenPanel.setVisible(true);
			warehousesCheckBox.setValue(getCompanyPreferences()
					.iswareHouseEnabled());
			unitsCheckBox.setValue(getCompanyPreferences().isUnitsEnabled());
		} else {
			hiddenPanel.setVisible(false);
		}
	}
}
