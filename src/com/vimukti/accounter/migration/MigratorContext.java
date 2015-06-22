package com.vimukti.accounter.migration;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.vimukti.accounter.core.Company;

public class MigratorContext {

	private Map<String, Long> ids = new HashMap<String, Long>();

	private PickListTypeContext pickListTypeContext;
	private Company company;

	public void put(String name, Map<Long, Long> migrateAccounts) {
		for (Entry<Long, Long> oldId : migrateAccounts.entrySet()) {
			ids.put(name + oldId.getKey(), oldId.getValue());
		}
	}

	public Long get(String name, long id) {
		return ids.get(name + id);
	}

	public PickListTypeContext getPickListContext() {
		return pickListTypeContext;
	}

	public void setPickListContext(PickListTypeContext pickListTypeContext) {
		this.pickListTypeContext = pickListTypeContext;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
}