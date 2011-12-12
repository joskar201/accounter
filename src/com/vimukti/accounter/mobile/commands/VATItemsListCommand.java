package com.vimukti.accounter.mobile.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.vimukti.accounter.core.TAXItem;
import com.vimukti.accounter.mobile.CommandList;
import com.vimukti.accounter.mobile.Context;
import com.vimukti.accounter.mobile.Record;
import com.vimukti.accounter.mobile.Requirement;
import com.vimukti.accounter.mobile.requirements.CommandsRequirement;
import com.vimukti.accounter.mobile.requirements.ShowListRequirement;

public class VATItemsListCommand extends NewAbstractCommand {

	private static final String CURRENT_VIEW = "currentView";

	@Override
	public String getId() {
		return null;
	}

	@Override
	protected void addRequirements(List<Requirement> list) {

		list.add(new CommandsRequirement(CURRENT_VIEW) {

			@Override
			protected List<String> getList() {
				List<String> list = new ArrayList<String>();
				list.add(getMessages().active());
				list.add(getMessages().inActive());
				return list;
			}
		});

		list.add(new ShowListRequirement<TAXItem>("taxItemsList", getMessages()
				.pleaseSelect(getMessages().taxItem()), 20) {
			@Override
			protected Record createRecord(TAXItem value) {
				Record record = new Record(value);
				record.add(getMessages().name(), value.getName());
				record.add(getMessages().description(), value.getDescription());
				return record;
			}

			@Override
			protected String onSelection(TAXItem value) {
				return "updateTaxItem " + value.getID();
			}

			@Override
			protected void setCreateCommand(CommandList list) {
				list.add(getMessages().create(getMessages().taxItem()));
			}

			@Override
			protected boolean filter(TAXItem e, String name) {
				return e.getName().startsWith(name);
			}

			@Override
			protected List<TAXItem> getLists(Context context) {
				Set<TAXItem> completeList = vatItemssList(context);
				List<TAXItem> result = new ArrayList<TAXItem>();

				String type = VATItemsListCommand.this.get(CURRENT_VIEW)
						.getValue();

				for (TAXItem taxItem : completeList) {

					if (type.equals("Active")) {
						if (taxItem.isActive())

							result.add(taxItem);
					}
					if (type.equals("In-Active")) {
						if (!taxItem.isActive())
							result.add(taxItem);
					}

				}
				return result;
			}

			@Override
			protected String getShowMessage() {
				return getMessages().vatItemList();
			}

			@Override
			protected String getEmptyString() {
				return getMessages().noRecordsToShow();
			}

		});
	}

	private Set<TAXItem> vatItemssList(Context context) {
		return context.getCompany().getTaxItems();
	}

	@Override
	protected String initObject(Context context, boolean isUpdate) {
		if (!context.getPreferences().isTrackTax()) {
			addFirstMessage(context, "You dnt have permission to do this.");
			return "cancel";
		}
		return null;
	}

	@Override
	protected String getWelcomeMessage() {
		return null;
	}

	@Override
	protected String getDetailsMessage() {
		return null;
	}

	@Override
	protected void setDefaultValues(Context context) {
		get(CURRENT_VIEW).setDefaultValue(getMessages().active());
	}

	@Override
	public String getSuccessMessage() {
		return "Success";
	}

}
