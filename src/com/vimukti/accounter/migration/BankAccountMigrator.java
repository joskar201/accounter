package com.vimukti.accounter.migration;

import org.json.JSONObject;

import com.vimukti.accounter.core.BankAccount;

public class BankAccountMigrator implements IMigrator<BankAccount> { 
	
	@Override
	public JSONObject migrate(BankAccount bankAccount, MigratorContext context) throws JSONException {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("bankName",bankAccount.getBank().getName());
		jsonObject.put("bankAccountType",bankAccount.getBankAccountType());
		jsonObject.put("bankAccountNumber",bankAccount.getBankAccountNumber());
		return jsonObject;
	}
}