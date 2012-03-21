package com.vimukti.accounter.web.client.imports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.vimukti.accounter.web.client.AccounterAsyncCallback;
import com.vimukti.accounter.web.client.core.IAccounterCore;
import com.vimukti.accounter.web.client.core.ImportField;
import com.vimukti.accounter.web.client.exception.AccounterException;
import com.vimukti.accounter.web.client.exception.AccounterExceptions;
import com.vimukti.accounter.web.client.ui.AbstractBaseView;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.Accounter.AccounterType;
import com.vimukti.accounter.web.client.ui.core.AccounterDialog;
import com.vimukti.accounter.web.client.ui.core.CancelButton;
import com.vimukti.accounter.web.client.ui.core.ErrorDialogHandler;

public class ImportView extends AbstractBaseView {

	/**
	 * Contains Map<Column,AllValues>
	 */
	private Map<String, List<String>> importData = new HashMap<String, List<String>>();
	private int importType;
	/**
	 * All Fields of the CurrentImporter
	 */
	private Map<String, ImportField> fields = new HashMap<String, ImportField>();

	private FlexTable mappingTable, previewTable, mainTable;
	// contains accounter names and fields..
	private Map<String, ImportField> listBoxMap;

	private int currentLine = 0;
	private String fileID;
	private double noOfRecords;
	Button backButton;
	Button nextButton;
	private Label dateFormat_Label;
	private ListBox dateFormatList;
	private String selectedDateFormate;

	public ImportView(int importType, String fileID,
			List<ImportField> importerFields, Map<String, List<String>> data,
			double recordCount) {
		this.importType = importType;
		this.importData = data;
		this.fileID = fileID;
		this.noOfRecords = recordCount;
		for (ImportField field : importerFields) {
			fields.put(field.getDesplayName(), field);
		}

	}

	@Override
	public void init() {
		super.init();
		createControls();
	}

	private void createControls() {
		mappingTable = new FlexTable();
		mainTable = new FlexTable();
		previewTable = new FlexTable();
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.add(previewTable);
		scrollPanel.setHeight("300px");
		mappingTable.addStyleName("import-match-content");
		// previewTable.addStyleName("import-match-content");
		// importerMatchingPanel.addStyleName("import-mapping-panel");
		FlowPanel buttonPanel = new FlowPanel();

		Label header = new Label();

		backButton = new Button(messages.back());
		nextButton = new Button(messages.next());
		backButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				currentLine--;
				updateColumnValues();
			}
		});

		nextButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				currentLine++;
				updateColumnValues();
			}
		});
		backButton.addStyleName("prev_button");
		nextButton.addStyleName("next_button");
		buttonPanel.add(backButton);
		buttonPanel.add(nextButton);
		buttonPanel.addStyleName("import-button-panel");

		HorizontalPanel datePanel = new HorizontalPanel();
		dateFormat_Label = new Label(messages.DateFormat());
		dateFormatList = new ListBox(false);
		String[] dateFormates = new String[] { "ddMMyy", "MM/dd/yy",
				"dd/MM/yy", "ddMMyyyy", "MMddyyyy", "MMM-dd-yy", "MMMddyyyy",
				"dd/MM/yyyy", "MM/dd/yyyy", "dd/MMMM/yyyy", "MMMMddyyyy",
				"dd-MM-yyyy", "MM-dd-yyyy", "dd/MMM/yyyy", "MMM/dd/yyyy", };

		for (int i = 0; i < dateFormates.length; i++) {
			dateFormatList.addItem(dateFormates[i]);
		}

		dateFormatList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				selectedDateFormate = getSelectedDateFormat(dateFormatList
						.getSelectedIndex());
			}
		});
		dateFormatList.setSelectedIndex(2);
		selectedDateFormate = getSelectedDateFormat(2);
		datePanel.add(dateFormat_Label);
		datePanel.add(dateFormatList);
		FlowPanel mappingTablePanel = new FlowPanel();
		mappingTablePanel.add(mappingTable);
		mappingTablePanel.add(datePanel);

		FlowPanel previewPanel = new FlowPanel();
		Label previewHeader = new Label(messages.mappingPreview());
		previewHeader.addStyleName("preview-header");
		previewPanel.add(previewHeader);
		previewPanel.add(scrollPanel);
		previewPanel.addStyleName("import-match-content");
		// Save & Import buttons
		FlowPanel buttonPanel2 = new FlowPanel();
		Button saveButton = new Button(messages.save());
		saveButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (listBoxMap.size() > 0) {
					saveAndUpdateView();
				} else {
					Accounter.showError(messages.pleaseMapAtLeastOneField());
				}
			}
		});
		CancelButton cancelButton = new CancelButton(this);
		buttonPanel2.add(saveButton);
		buttonPanel2.add(cancelButton);
		buttonPanel2.addStyleName("import-button-panel");
		FlowPanel importHeaderPanel = new FlowPanel();
		String importerName = ImporterType.getAllSupportedImporters().get(
				new Integer(importType));
		Label importerTitle = new Label(importerName + messages.importer());
		importerTitle.addStyleName("preview-header");
		importHeaderPanel.add(importerTitle);
		mainTable.setWidget(1, 0, header);
		mainTable.setWidget(1, 0, importHeaderPanel);
		mainTable.setWidget(2, 0, buttonPanel);
		mainTable.setWidget(3, 0, mappingTablePanel);
		mainTable.setWidget(3, 1, previewPanel);
		mainTable.setWidget(4, 1, buttonPanel2);
		add(mainTable);
		mainTable.setSize("100%", "100%");
		importHeaderPanel.addStyleName("import-preview-panel");
		importHeaderPanel.getElement().setAttribute("height", "10px");
		previewPanel.addStyleName("import-preview-panel");
		previewPanel.getElement().getParentElement()
				.setAttribute("height", "100%");
		buttonPanel.getElement().getParentElement()
				.setAttribute("width", "500px");
		initMappingGUI();
		refreshPreviewGUI();
		showOrHideButtons();
	}

	protected void close() {
		this.removeFromParent();
	}

	private void updateColumnValues() {
		Map<String, String> nextLine = getCurrentLine();
		int row = 1;
		if (!nextLine.isEmpty()) {
			for (Entry<String, String> entry : nextLine.entrySet()) {
				Label label = (Label) mappingTable.getWidget(row, 1);
				label.setText(entry.getValue());
				row++;
			}
		} else {
			currentLine = 0;
		}
		refreshPreviewGUI();
		showOrHideButtons();
	}

	private void initMappingGUI() {

		listBoxMap = new HashMap<String, ImportField>();
		Label csvHeader = new Label(messages.nameInCSV());
		Label valueHeader = new Label(messages.fieldValue());
		Label accounterNameHeader = new Label(messages.accounterField());

		csvHeader.addStyleName("table-header");
		valueHeader.addStyleName("table-header");
		accounterNameHeader.addStyleName("table-header");

		mappingTable.setWidget(0, 0, csvHeader);
		mappingTable.setWidget(0, 1, valueHeader);
		mappingTable.setWidget(0, 2, accounterNameHeader);
		int objectIndex = 0;
		Map<String, String> map = getCurrentLine();
		for (final Entry<String, String> entry : map.entrySet()) {
			Label columnName = new Label(entry.getKey());
			Label columnValue = new Label(entry.getValue());
			final ListBox importerFields = new ListBox();
			importerFields.addItem(messages.unassigned());
			for (String f : fields.keySet()) {
				importerFields.addItem(f);
			}
			importerFields.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					int selectedIndex = importerFields.getSelectedIndex();
					String selectedVal = importerFields
							.getItemText(selectedIndex);
					ImportField tempField = fields.get(selectedVal);
					String columnName = entry.getKey();
					for (ImportField field : fields.values()) {
						if (field.getColumnName() != null
								&& !(field.getColumnName().isEmpty())) {

							/*
							 * In one list box, we are not allow multiple
							 * columns having same value.
							 */
							if (field.getColumnName().equals(columnName)) {
								field.setColumnName(null);
								if (listBoxMap.containsKey(columnName)) {
									listBoxMap.remove(columnName);
								}
							}

						}
					}
					// tempField.setColumnName(entry.getKey());
					validateImportSelection(selectedVal, selectedIndex,
							tempField, importerFields, entry.getValue(),
							columnName);
					refreshPreviewGUI();
				}
			});
			mappingTable.setWidget(objectIndex + 1, 0, columnName);
			mappingTable.setWidget(objectIndex + 1, 1, columnValue);
			mappingTable.setWidget(objectIndex + 1, 2, importerFields);
			objectIndex++;
		}
	}

	/**
	 * get selected date format
	 * 
	 * @param index
	 *            of combo Box
	 */
	private String getSelectedDateFormat(int i) {
		switch (i) {
		case 0:
			return "ddMMyy";

		case 1:
			return "MM/dd/yy";

		case 2:
			return "dd/MM/yy";

		case 3:
			return "ddMMyyyy";

		case 4:
			return "MMddyyyy";

		case 5:
			return "MMM-dd-yy";

		case 6:
			return "MMMddyyyy";

		case 7:
			return "dd/MM/yyyy";

		case 8:
			return "MM/dd/yyyy";

		case 9:
			return "dd/MMMM/yyyy";

		case 10:
			return "MMMMddyyyy";

		case 11:
			return "dd-MM-yyyy";

		case 12:
			return "MM-dd-yyyy";

		case 13:
			return "dd/MMM/yyyy";

		case 14:
			return "MMM/dd/yyyy";

		}
		return null;
	}

	protected void refreshListBoxes(String selectedVal, ImportField tempField,
			int selectedIndex, ListBox selectedBox) {
		for (int i = 1; i <= importData.values().size(); i++) {
			ListBox listBox = (ListBox) mappingTable.getWidget(i, 2);
			if (listBox != selectedBox
					&& listBox.getSelectedIndex() == selectedIndex) {
				listBox.setSelectedIndex(0);
			}
		}
		refreshPreviewGUI();
		selectedBox.setSelectedIndex(selectedIndex);
	}

	protected void validateImportSelection(final String fieldName,
			final int selectedIndex, final ImportField field,
			final ListBox importerFields, final String columnValue,
			final String columnName) {
		if (listBoxMap.containsKey(fieldName)) {
			Accounter.showWarning(messages.aleadyMapped(fieldName),
					AccounterType.WARNING, new ErrorDialogHandler() {

						@SuppressWarnings("unchecked")
						@Override
						public boolean onYesClick() {
							if (validateFieldValue(fieldName, field,
									columnValue, columnName)) {
								refreshListBoxes(fieldName,
										(ImportField) field, selectedIndex,
										importerFields);
							} else {
								refreshPreviewGUI();
								importerFields.setSelectedIndex(0);
							}
							return true;
						}

						@Override
						public boolean onNoClick() {
							importerFields.setSelectedIndex(0);
							return true;
						}

						@Override
						public boolean onCancelClick() {
							return false;
						}
					});
		} else {
			if (!validateFieldValue(fieldName, field, columnValue, columnName)) {
				importerFields.setSelectedIndex(0);
			}
		}
	}

	protected boolean validateFieldValue(String fieldName, ImportField field,
			String columnValue, String columnName) {
		if (field.isFinanceDate()) {
			((FinanceDateField) field).setDateFormate(this.selectedDateFormate);
		}
		if (field.validate(columnValue)) {
			refreshMapByValue(fieldName, field);
			listBoxMap.put(fieldName, field);
			ImportField localField = fields.get(fieldName);
			localField.setColumnName(columnName);
			return true;
		} else {

			Accounter.showError(messages.matchedWithWrongProprty(fieldName,
					field.getValueAsString()));
			return false;
		}
	}

	private void refreshMapByValue(String fieldName, ImportField field) {
		for (Entry<String, ImportField> entry : listBoxMap.entrySet()) {
			if (field != null && field.getColumnName() != null && entry != null
					&& entry.getValue() != null) {
				if (field.getColumnName().equals(
						entry.getValue().getColumnName())) {
					listBoxMap.remove(entry.getKey());
					return;
				}
			}

		}

	}

	private void refreshPreviewGUI() {
		int row = 0;
		Map<String, String> currentLine = getCurrentLine();
		for (ImportField field : fields.values()) {
			Label fieldNameLabel = new Label(field.getDesplayName());
			String string = currentLine.get(field.getColumnName());
			Label valLabel = new Label(string != null ? string
					: messages.unassigned());
			previewTable.setWidget(row, 0, fieldNameLabel);
			previewTable.setWidget(row, 1, valLabel);
			row++;
		}

	}

	private Map<String, String> getCurrentLine() {
		Map<String, String> map = new HashMap<String, String>();
		for (Entry<String, List<String>> entrySet : importData.entrySet()) {
			String key = entrySet.getKey();
			List<String> value = entrySet.getValue();
			map.put(key, value.size() > currentLine ? value.get(currentLine)
					: "");
		}
		return map;
	}

	@Override
	public void saveAndUpdateView() {
		if (!listBoxMap.isEmpty()) {
			Map<String, String> importMap = updateImport();
			Accounter.createHomeService().importData(this.fileID, importType,
					importMap, this.selectedDateFormate,
					new AccounterAsyncCallback<List<Object>>() {

						@Override
						public void onException(AccounterException exception) {
							if (exception.getErrorCode() == AccounterException.ERROR_NAME_CONFLICT) {
								Accounter.showError(messages
										.objAlreadyExistsWithName(ImporterType
												.getAllSupportedImporters()
												.get(importType)));
								return;
							}
							Accounter.showError(exception.getMessage());
						}

						@Override
						public void onResultSuccess(List<Object> result) {

							AccounterDialog accounterDialog = new AccounterDialog(
									getDeatailsAsString(result),
									AccounterType.INFORMATION);
							accounterDialog.show();
						}
					});
			super.saveAndUpdateView();
		} else {
			Accounter.showError(messages.pleaseMapAtLeastOneField());
		}
	}

	protected String getDeatailsAsString(List<Object> result) {
		long successCount = ((Long) result.get(0)).intValue();
		int failureCount = result.size() - 1;
		StringBuffer buffer = new StringBuffer();
		buffer.append("<li>" + messages.importSuccessCount() + successCount
				+ "</li>");
		buffer.append("<li>" + messages.importFailureCount() + failureCount
				+ "</li>");
		for (int i = 1; i < result.size(); i++) {
			AccounterException exception = (AccounterException) result.get(i);
			buffer.append("<li>"
					+ messages.failed()
					+ "("
					+ i
					+ ")"
					+ AccounterExceptions.getErrorString(exception
							.getErrorCode()) + "</li>");
		}
		return buffer.toString();
	}

	private Map<String, String> updateImport() {
		Map<String, String> map = new HashMap<String, String>();
		for (ImportField field : fields.values()) {
			map.put(field.getName(), field.getColumnName());
		}
		return map;
	}

	@Override
	public void deleteFailed(AccounterException caught) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteSuccess(IAccounterCore result) {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getViewTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	private void showOrHideButtons() {
		if (noOfRecords == 1) {
			this.nextButton.setVisible(false);
			this.backButton.setVisible(false);
		} else if (noOfRecords > 1 && currentLine == 0) {
			this.nextButton.setVisible(true);
			this.backButton.setVisible(false);
		} else if (currentLine > 0 && noOfRecords > 1
				&& currentLine + 1 == noOfRecords) {
			this.nextButton.setVisible(false);
			this.backButton.setVisible(true);
		} else {
			this.nextButton.setVisible(true);
			this.backButton.setVisible(true);
		}
	}
}
