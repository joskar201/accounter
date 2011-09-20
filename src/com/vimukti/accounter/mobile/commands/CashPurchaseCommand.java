package com.vimukti.accounter.mobile.commands;

import java.util.Date;
import java.util.List;

import com.vimukti.accounter.core.Account;
import com.vimukti.accounter.core.TransactionItem;
import com.vimukti.accounter.core.Vendor;
import com.vimukti.accounter.mobile.ActionNames;
import com.vimukti.accounter.mobile.Context;
import com.vimukti.accounter.mobile.ObjectListRequirement;
import com.vimukti.accounter.mobile.Record;
import com.vimukti.accounter.mobile.Requirement;
import com.vimukti.accounter.mobile.Result;
import com.vimukti.accounter.mobile.ResultList;

public class CashPurchaseCommand extends AbstractTransactionCommand {

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void addRequirements(List<Requirement> list) {
		list.add(new Requirement("supplier", false, true));
		list.add(new ObjectListRequirement("items", false, true) {

			@Override
			public void addRequirements(List<Requirement> list) {
				list.add(new Requirement("name", false, true));
				list.add(new Requirement("desc", true, true));
				list.add(new Requirement("quantity", true, true));
				list.add(new Requirement("price", true, true));
				list.add(new Requirement("vatCode", true, true));
			}
		});
		list.add(new Requirement("paymentMethod", false, true));
		list.add(new Requirement("date", true, true));
		list.add(new Requirement("number", true, false));
		list.add(new Requirement("contact", true, true));
		list.add(new Requirement("billTo", true, true));
		list.add(new Requirement("phone", true, true));
		list.add(new Requirement("memo", true, true));
		list.add(new Requirement("depositOrTransferTo", false, true));
		list.add(new Requirement("chequeNo", true, true));
		list.add(new Requirement("deliveryDate", true, true));
	}

	@Override
	public Result run(Context context) {
		Result result = context.makeResult();
		result = createSupplierRequirement(context);
		if (result != null) {
			return result;
		}
		result = itemsRequirement(context);
		if (result != null) {
			return result;
		}

		result = paymentMethodRequirement(context);
		if (result != null) {
			return result;
		}

		result = depositeOrTransferTo(context);
		if (result != null) {
			return result;
		}
		result = createOptionalResult(context);
		return null;
	}

	private Result createOptionalResult(Context context) {
		context.setAttribute(INPUT_ATTR, "optional");

		Object selection = context.getSelection(ACTIONS);
		if (selection != null) {
			ActionNames actionName = (ActionNames) selection;
			switch (actionName) {
			case ADD_MORE_ITEMS:
				return items(context);
			case FINISH:
				context.removeAttribute(INPUT_ATTR);
				return null;
			default:
				break;
			}
		}
		Requirement itemsReq = get("items");
		List<TransactionItem> transItems = itemsReq.getValue();

		selection = context.getSelection("transactionItems");
		if (selection != null) {
			Result result = transactionItem(context,
					(TransactionItem) selection);
			if (result != null) {
				return result;
			}
		}
		selection = context.getSelection("values");
		ResultList list = new ResultList("values");

		Requirement transferTo = get("depositOrTransferTo");
		Account account = transferTo.getValue();
		Record accountRec = new Record(account);
		accountRec.add("Number", "Account No");
		accountRec.add("value", account.getNumber());
		accountRec.add("Account name", "Account Name");
		accountRec.add("value", account.getNumber());
		accountRec.add("Account type", "Account Type");
		accountRec.add("Account Type", getAccountTypeString(account.getType()));
		list.add(accountRec);

		Requirement supplierReq = get("supplier");
		Vendor supplier = (Vendor) supplierReq.getValue();

		selection = context.getSelection("values");
		if (supplier == selection) {
			return createSupplierRequirement(context);
		}

		// ResultList list = new ResultList("values");

		Record supplierRecord = new Record(supplier);
		supplierRecord.add("Name", "Customer");
		supplierRecord.add("Value", supplier.getName());

		list.add(supplierRecord);

		Result result = dateRequirement(context, list, selection,
				"deliveryDate");
		if (result != null) {
			return result;
		}
		result = dateRequirement(context, list, selection, "date");
		if (result != null) {
			return result;
		}
		result = contactRequirement(context, list, selection, supplier);
		if (result != null) {
			return result;
		}

		result = cashPurchaseNoRequirement(context, list, selection);
		if (result != null) {
			return result;
		}

		// result = billToRequirement(context, list, selection);
		// if (result != null) {
		// return result;
		// }

		result = phoneRequirement(context, list, (String) selection);
		if (result != null) {
			return result;
		}

		result = chequeNoRequirement(context, list, selection);
		if (result != null) {
			return result;
		}

		result = memoRequirement(context, list, selection);
		if (result != null) {
			return result;
		}

		result = context.makeResult();
		result.add("CashPurchase is ready to create with following values.");
		result.add(list);
		result.add("Items:-");
		ResultList items = new ResultList("transactionItems");
		for (TransactionItem item : transItems) {
			Record itemRec = new Record(item);
			itemRec.add("Name", item.getItem().getName());
			itemRec.add("Total", item.getLineTotal());
			itemRec.add("VatCode", item.getVATfraction());
		}
		result.add(items);

		ResultList actions = new ResultList(ACTIONS);
		Record moreItems = new Record(ActionNames.ADD_MORE_ITEMS);
		moreItems.add("", "Add more items");
		actions.add(moreItems);
		Record finish = new Record(ActionNames.FINISH);
		finish.add("", "Finish to create CashPurchase.");
		actions.add(finish);
		result.add(actions);

		return result;
	}

	private Result chequeNoRequirement(Context context, ResultList list,
			Object selection) {
		Requirement requirement = get("paymentMethod");
		if (requirement != null) {
			String paymentMethod = (String) requirement.getValue();
			if (paymentMethod.equals("Check")) {

				Requirement req = get("chequeNo");
				String invoiceNo = (String) req.getValue();

				String attribute = (String) context.getAttribute(INPUT_ATTR);
				if (attribute.equals("chequeNo")) {
					String order = context.getSelection(NUMBER);
					if (order == null) {
						order = context.getString();
					}
					invoiceNo = order;
					req.setValue(invoiceNo);
				}

				if (selection == invoiceNo) {
					context.setAttribute(INPUT_ATTR, "chequeNo");
					return number(context, "Enter Cheque number", invoiceNo);
				}

				Record invoiceNoRec = new Record(invoiceNo);
				invoiceNoRec.add("Name", "Cheque Number");
				invoiceNoRec.add("Value", invoiceNo);
				list.add(invoiceNoRec);
			}
		}

		return null;
	}

	private Result cashPurchaseNoRequirement(Context context, ResultList list,
			Object selection) {
		Requirement req = get("number");
		String invoiceNo = (String) req.getValue();

		String attribute = (String) context.getAttribute(INPUT_ATTR);
		if (attribute.equals("invoiceNo")) {
			String order = context.getSelection(NUMBER);
			if (order == null) {
				order = context.getString();
			}
			invoiceNo = order;
			req.setValue(invoiceNo);
		}

		if (selection == invoiceNo) {
			context.setAttribute(INPUT_ATTR, "invoiceNo");
			return number(context, "Enter Cash Purcase number", invoiceNo);
		}

		Record invoiceNoRec = new Record(invoiceNo);
		invoiceNoRec.add("Name", "CashPurcase Number");
		invoiceNoRec.add("Value", invoiceNo);
		list.add(invoiceNoRec);
		return null;
	}

	private Result dateRequirement(Context context, ResultList list,
			Object selection, String requirement) {
		Requirement req = get(requirement);
		Date dueDate = (Date) req.getValue();

		String attribute = (String) context.getAttribute(INPUT_ATTR);
		if (attribute.equals(requirement)) {
			Date date = context.getSelection(DATE);
			if (date == null) {
				date = context.getDate();
			}
			dueDate = date;
			req.setValue(dueDate);
		}
		if (selection == dueDate) {
			context.setAttribute(INPUT_ATTR, requirement);
			return date(context, "Enter Date", dueDate);
		}

		Record dueDateRecord = new Record(dueDate);
		dueDateRecord.add("Name", requirement);
		dueDateRecord.add("Value", dueDate.toString());
		list.add(dueDateRecord);
		return null;
	}

}
