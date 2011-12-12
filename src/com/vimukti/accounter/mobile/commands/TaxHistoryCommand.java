package com.vimukti.accounter.mobile.commands;

import java.util.ArrayList;
import java.util.List;

import com.vimukti.accounter.core.TAXReturn;
import com.vimukti.accounter.mobile.CommandList;
import com.vimukti.accounter.mobile.Context;
import com.vimukti.accounter.mobile.Record;
import com.vimukti.accounter.mobile.Requirement;
import com.vimukti.accounter.mobile.requirements.CommandsRequirement;
import com.vimukti.accounter.mobile.requirements.ShowListRequirement;
import com.vimukti.accounter.web.client.exception.AccounterException;
import com.vimukti.accounter.web.server.managers.TaxManager;

public class TaxHistoryCommand extends NewAbstractCommand {

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
		get(VIEW_BY).setDefaultValue(getMessages().open());
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

		list.add(new CommandsRequirement(VIEW_BY) {

			@Override
			protected List<String> getList() {
				List<String> list = new ArrayList<String>();
				list.add(getMessages().all());
				list.add(getMessages().paid());
				list.add(getMessages().unPaid());
				return list;
			}
		});

		list.add(new ShowListRequirement<TAXReturn>(getMessages().taxHistory(),
				getMessages().taxHistory(), 20) {

			@Override
			protected String onSelection(TAXReturn value) {
				return "updatetaxhistory " + value.getTaxAgency().getName();
			}

			@Override
			protected String getShowMessage() {
				return getMessages().taxHistory();
			}

			@Override
			protected String getEmptyString() {

				return getMessages().youDontHaveAny(getMessages().taxHistory());

			}

			@Override
			protected Record createRecord(TAXReturn value) {
				Record taxHistory = new Record(value);
				taxHistory.add(getMessages().name(), value.getTaxAgency()
						.getName());
				taxHistory.add(getMessages().periodStartDate(),
						value.getPeriodStartDate());
				taxHistory.add(getMessages().periodEndDate(),
						value.getPeriodEndDate());
				taxHistory.add(getMessages().taxAmount(),
						value.getTotalTAXAmount());
				taxHistory.add(getMessages().totalPaymentMade(),
						(value.getTotal() - value.getBalance()));

				return taxHistory;

			}

			@Override
			protected void setCreateCommand(CommandList list) {
				list.add(getMessages().taxHistory());
			}

			@Override
			protected boolean filter(TAXReturn e, String name) {
				return false;

			}

			@Override
			protected List<TAXReturn> getLists(Context context) {
				return gettaxReturnEntries(context);
			}

		});

	}

	private List<TAXReturn> gettaxReturnEntries(Context context) {
		TaxManager manager = new TaxManager();
		List<TAXReturn> allTAXReturns = new ArrayList<TAXReturn>();
		List<TAXReturn> result = new ArrayList<TAXReturn>();
		try {
			allTAXReturns = manager.getAllTAXReturnsFromDB(getCompanyId());
		} catch (AccounterException e) {
			e.printStackTrace();
		}
		String viewType = get(VIEW_BY).getValue();
		for (TAXReturn tAXReturn : allTAXReturns) {
			if (viewType.equals(getMessages().paid())) {
				if (tAXReturn.getBalance() <= 0) {
					result.add(tAXReturn);
				}
			} else if (viewType.equals(getMessages().unPaid())) {
				if (tAXReturn.getBalance() > 0) {
					result.add(tAXReturn);
				}
			} else {
				result = allTAXReturns;

			}
		}
		return result;
	}
}
