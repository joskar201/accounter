package com.vimukti.accounter.mobile.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.vimukti.accounter.core.TAXAgency;
import com.vimukti.accounter.mobile.CommandList;
import com.vimukti.accounter.mobile.Context;
import com.vimukti.accounter.mobile.Record;
import com.vimukti.accounter.mobile.Requirement;
import com.vimukti.accounter.mobile.requirements.ActionRequirement;
import com.vimukti.accounter.mobile.requirements.ShowListRequirement;

public class TaxAgenciesListCommand extends NewAbstractCommand {

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
		get(VIEW_BY).setDefaultValue(getMessages().active());

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
		list.add(new ActionRequirement(VIEW_BY, null) {

			@Override
			protected List<String> getList() {
				List<String> list = new ArrayList<String>();
				list.add(getMessages().active());
				list.add(getMessages().inActive());
				return list;
			}
		});

		list.add(new ShowListRequirement<TAXAgency>(getMessages().taxAgency(),
				"", 10) {

			@Override
			protected String onSelection(TAXAgency value) {
				return "update Tax Agency " + value.getName();
			}

			@Override
			protected String getShowMessage() {

				return getMessages().payeeList(getMessages().taxAgency());
			}

			@Override
			protected String getEmptyString() {

				return getMessages().youDontHaveAny(getMessages().taxAgency());

			}

			@Override
			protected Record createRecord(TAXAgency value) {
				Record taxAgencyrec = new Record(value);
				taxAgencyrec.add(getMessages().name(), value.getName());
				// TODO balance by dates
				taxAgencyrec.add(getMessages().balanceAsOf(),
						value.getBalance());
				return taxAgencyrec;

			}

			@Override
			protected void setCreateCommand(CommandList list) {
				list.add(getMessages().create(getMessages().taxAgency()));

			}

			@Override
			protected boolean filter(TAXAgency e, String name) {
				return e.getName().toLowerCase().startsWith(name.toLowerCase());
			}

			@Override
			protected List<TAXAgency> getLists(Context context) {

				return getTaxAgencies(context);
			}

		});

	}

	private List<TAXAgency> getTaxAgencies(Context context) {
		String isActive = get(VIEW_BY).getValue();
		Set<TAXAgency> tAXAgencys = context.getCompany().getTaxAgencies();
		List<TAXAgency> result = new ArrayList<TAXAgency>();
		for (TAXAgency taxagency : tAXAgencys) {
			if (isActive.equals(getMessages().active())) {
				if (taxagency.isActive()) {
					result.add(taxagency);
				}

			} else {
				if (!taxagency.isActive()) {
					result.add(taxagency);
				}

			}
		}

		return result;
	}
}