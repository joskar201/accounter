package com.vimukti.accounter.migration;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.json.JSONException;
import org.json.JSONObject;

import com.vimukti.accounter.core.Address;
import com.vimukti.accounter.core.CashPurchase;
import com.vimukti.accounter.core.Contact;
import com.vimukti.accounter.core.Vendor;
import com.vimukti.accounter.web.client.core.ClientTransaction;

public class CashExpenseMigrator extends TransactionMigrator<CashPurchase> {

	@Override
	public JSONObject migrate(CashPurchase obj, MigratorContext context)
			throws JSONException {
		JSONObject cashPurchase = super.migrate(obj, context);
		Vendor vendor = obj.getVendor();
		cashPurchase.put("phone", obj.getPhone());
		if (vendor != null) {
			cashPurchase.put("payee", context.get("Vendor", vendor.getID()));
		}
		Contact contact = obj.getContact();
		if (contact != null) {
			cashPurchase
					.put("contact", context.get("Contact", contact.getID()));
		}
		Address vendorAddress = obj.getVendorAddress();
		if (vendorAddress != null) {
			JSONObject addressJSON = new JSONObject();
			addressJSON.put("street", vendorAddress.getStreet());
			addressJSON.put("city", vendorAddress.getCity());
			addressJSON.put("stateOrProvince",
					vendorAddress.getStateOrProvinence());
			addressJSON.put("zipOrPostalCode",
					vendorAddress.getZipOrPostalCode());
			addressJSON.put("country", vendorAddress.getCountryOrRegion());
			cashPurchase.put("billTo", vendorAddress);
		}
		cashPurchase.put("paymentMethod", obj.getPaymentMethod());
		cashPurchase.put("account",
				context.get("Account", obj.getPayFrom().getID()));
		cashPurchase.put("deliveryDate", obj.getDeliveryDate()
				.getAsDateObject().getTime());
		cashPurchase.put("memo", obj.getMemo());
		return cashPurchase;
	}

	public void addRestrictions(Criteria criteria) {
		criteria.add(Restrictions.eq("type",
				ClientTransaction.TYPE_CASH_EXPENSE));
	}
}