package com.vimukti.accounter.mobile.commands;

import java.util.ArrayList;
import java.util.List;

import com.vimukti.accounter.core.Warehouse;
import com.vimukti.accounter.mobile.CommandList;
import com.vimukti.accounter.mobile.Context;
import com.vimukti.accounter.mobile.Record;
import com.vimukti.accounter.mobile.Requirement;
import com.vimukti.accounter.mobile.requirements.ShowListRequirement;
import com.vimukti.accounter.mobile.utils.CommandUtils;
import com.vimukti.accounter.web.client.core.AccounterCoreType;
import com.vimukti.accounter.web.client.core.ClientStockTransfer;
import com.vimukti.accounter.web.client.exception.AccounterException;
import com.vimukti.accounter.web.server.FinanceTool;

public class WareHouseTransferListCommand extends NewAbstractCommand {

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
		list.add(new ShowListRequirement<ClientStockTransfer>(
				"warehousetransfers", getMessages().pleaseSelect(
						"any " + getMessages().wareHouseTransfer()
								+ " to view details"), 40) {

			@Override
			protected String onSelection(ClientStockTransfer value) {
				return "updateWarehouseTransfer " + value.getID();
			}

			@Override
			protected String getShowMessage() {
				return getMessages().warehouseTransferList();
			}

			@Override
			protected String getEmptyString() {
				return getMessages().noRecordsToShow();
			}

			@Override
			protected Record createRecord(ClientStockTransfer value) {
				Record record = new Record(value);
				Warehouse fromWarehouse = (Warehouse) CommandUtils
						.getServerObjectById(value.getFromWarehouse(),
								AccounterCoreType.WAREHOUSE);
				record.add(getMessages().fromWarehouse(),
						fromWarehouse.getName());
				fromWarehouse = (Warehouse) CommandUtils.getServerObjectById(
						value.getToWarehouse(), AccounterCoreType.WAREHOUSE);
				record.add(getMessages().fromWarehouse(),
						fromWarehouse.getName());
				record.add(getMessages().comment(), value.getMemo());
				return record;
			}

			@Override
			protected void setCreateCommand(CommandList list) {
				list.add("createWarehouseTransfer");
			}

			@Override
			protected boolean filter(ClientStockTransfer e, String name) {
				return false;
			}

			@Override
			protected ArrayList<ClientStockTransfer> getLists(Context context) {
				try {
					return new FinanceTool().getInventoryManager()
							.getWarehouseTransfersList(getCompanyId());
				} catch (AccounterException e) {
					e.printStackTrace();
				}
				return new ArrayList<ClientStockTransfer>();
			}
		});
	}

}
