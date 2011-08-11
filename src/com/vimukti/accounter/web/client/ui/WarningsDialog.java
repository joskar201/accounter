/**
 * 
 */
package com.vimukti.accounter.web.client.ui;

import java.util.Iterator;
import java.util.List;

import com.vimukti.accounter.web.client.core.ValidationResult.Warning;
import com.vimukti.accounter.web.client.ui.Accounter.AccounterType;
import com.vimukti.accounter.web.client.ui.core.AccounterDialog;
import com.vimukti.accounter.web.client.ui.core.ErrorDialogHandler;

/**
 * @author Prasanna Kumar G
 * 
 */
public class WarningsDialog extends AccounterDialog implements
		ErrorDialogHandler {

	private ErrorDialogHandler warningHandler;
	private List<Warning> warnings;

	/**
	 * Creates new Instance
	 */
	public WarningsDialog(List<Warning> warnings,
			ErrorDialogHandler warningHandler) {
		super(warnings.get(0).getMessage(), AccounterType.WARNING);
		this.warnings = warnings;
		this.warningHandler = warningHandler;
		setDialogHandler(this);
	}

	private boolean showNext() {
		int size = warnings.size();
		int index = getIndexByMessage(message) + 1;

		if (index == size) {
			warningHandler.onYesClick();
			return true;
		}

		message = warnings.get(index + 1).getMessage();
		super.show();

		return false;

	}

	private int getIndexByMessage(String message) {
		Iterator<Warning> iterator = warnings.iterator();
		while (iterator.hasNext()) {
			Warning next = iterator.next();
			if (next.getMessage().equals(message)) {
				return warnings.indexOf(next);
			}
		}
		return -1;
	}

	@Override
	public boolean onYesClick() {
		return showNext();
	}

	@Override
	public boolean onNoClick() {
		warningHandler.onNoClick();
		return true;
	}

	@Override
	public boolean onCancelClick() {
		warningHandler.onCancelClick();
		return true;
	}

}
