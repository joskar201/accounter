package com.vimukti.accounter.web.client.ui.vat;

import java.util.ArrayList;
import java.util.List;

import com.vimukti.accounter.web.client.AccounterAsyncCallback;
import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.core.ClientTAXReturn;
import com.vimukti.accounter.web.client.core.ClientTAXReturnEntry;
import com.vimukti.accounter.web.client.exception.AccounterException;
import com.vimukti.accounter.web.client.exception.AccounterExceptions;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.core.ActionFactory;
import com.vimukti.accounter.web.client.ui.grids.FileTAXGrid;
import com.vimukti.accounter.web.client.ui.grids.ListGrid;
import com.vimukti.accounter.web.client.ui.reports.AbstractReportView;
import com.vimukti.accounter.web.client.ui.reports.TAXItemDetail;
import com.vimukti.accounter.web.client.ui.reports.TaxItemDetailReportView;

public class FileTAXView extends AbstractFileTAXView {

	private FileTAXGrid grid;

	@Override
	protected void reloadGrid() {
		canSaveFileVat = true;
		grid.removeAllRecords();
		grid.addLoadingImagePanel();
		rpcGetService.getTAXReturnEntries(selectedTaxAgency.getID(), fromDate
				.getDate().getDate(), toDate.getDate().getDate(),
				new AccounterAsyncCallback<List<ClientTAXReturnEntry>>() {

					@Override
					public void onException(AccounterException exception) {
						String errorString = AccounterExceptions
								.getErrorString(exception.getErrorCode());
						Accounter.showError(errorString);
						disableprintButton();
					}

					@Override
					public void onResultSuccess(
							List<ClientTAXReturnEntry> result) {
						grid.removeLoadingImage();
						if (result != null && !result.isEmpty()) {
							grid.setRecords(result);
							enableprintButton();
						} else {
							grid.addEmptyMessage(Accounter.constants()
									.selectTAXAgency());
						}
					}

				});
	}

	@Override
	protected void printTaxReturn() {
		AbstractReportView<TAXItemDetail> report = new TaxItemDetailReportView() {
			private boolean isSecondReuqest = false;

			@Override
			public void onSuccess(ArrayList<TAXItemDetail> result) {
				super.onSuccess(result);
				print();
			}

			@Override
			public void makeReportRequest(long vatAgency,
					ClientFinanceDate startDate, ClientFinanceDate endDate) {
				if (isSecondReuqest) {
					this.startDate = startDate;
					this.endDate = endDate;
					super.makeReportRequest(vatAgency, startDate, endDate);
				} else {
					isSecondReuqest = true;
				}
			}
		};
		report.setAction(ActionFactory.getTaxItemDetailReportAction());
		report.init();
		report.initData();
		report.makeReportRequest(selectedTaxAgency.getID(),
				fromDate.getEnteredDate(), toDate.getEnteredDate());
	}

	@Override
	protected ListGrid getGrid() {
		if (grid == null) {
			this.grid = new FileTAXGrid(false);
		}
		return grid;
	}

	@Override
	public void saveAndUpdateView() {
		updateTransaction();
		saveOrUpdate(getData());
	}

	private void updateTransaction() {
		ClientTAXReturn taxReturn = new ClientTAXReturn();
		taxReturn.setTAXAgency(selectedTaxAgency.getID());
		taxReturn.setTaxReturnEntries(grid.getRecords());
		taxReturn.setTransactionDate(new ClientFinanceDate().getDate());
		taxReturn.setPeriodStartDate(fromDate.getDate().getDate());
		taxReturn.setPeriodEndDate(toDate.getDate().getDate());
		setData(taxReturn);
	}

}
