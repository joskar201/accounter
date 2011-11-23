package com.vimukti.accounter.mobile.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.vimukti.accounter.core.ItemGroup;
import com.vimukti.accounter.mobile.CommandList;
import com.vimukti.accounter.mobile.Context;
import com.vimukti.accounter.mobile.Record;
import com.vimukti.accounter.mobile.Requirement;
import com.vimukti.accounter.mobile.requirements.ShowListRequirement;

public class ItemGroupsListCommand extends NewAbstractCommand {

	@Override
	protected String initObject(Context context, boolean isUpdate) {
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

	}

	@Override
	public String getSuccessMessage() {
		return "Success";
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	protected void addRequirements(List<Requirement> list) {

		list.add(new ShowListRequirement<ItemGroup>(getMessages().itemGroup(),
				"", 10) {

			@Override
			protected String onSelection(ItemGroup value) {
				return "update ItemGroup " + value.getName();
			}

			@Override
			protected String getShowMessage() {

				return getMessages().payeeList(getMessages().itemGroup());
			}

			@Override
			protected String getEmptyString() {

				return getMessages().youDontHaveAny(getMessages().itemGroup());

			}

			@Override
			protected Record createRecord(ItemGroup value) {
				Record itemGroupRec = new Record(value);
				itemGroupRec.add(getMessages().name(), value.getName());
				return itemGroupRec;

			}

			@Override
			protected void setCreateCommand(CommandList list) {
				list.add(getMessages().create(getMessages().itemGroup()));

			}

			@Override
			protected boolean filter(ItemGroup e, String name) {
				return false;

			}

			@Override
			protected List<ItemGroup> getLists(Context context) {
				return getItemGroups(context);
			}

		});

	}

	private List<ItemGroup> getItemGroups(Context context) {
		Set<ItemGroup> itemGroups = context.getCompany().getItemGroups();
		List<ItemGroup> result = new ArrayList<ItemGroup>(itemGroups);
		return result;
	}
}