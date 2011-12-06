package com.vimukti.accounter.mobile.commands.preferences;

import java.util.ArrayList;
import java.util.List;

import com.vimukti.accounter.mobile.Context;
import com.vimukti.accounter.mobile.Requirement;
import com.vimukti.accounter.mobile.Result;
import com.vimukti.accounter.mobile.ResultList;
import com.vimukti.accounter.mobile.requirements.BooleanRequirement;
import com.vimukti.accounter.mobile.requirements.StringListRequirement;
import com.vimukti.accounter.web.client.Global;
import com.vimukti.accounter.web.client.core.ClientCompanyPreferences;

public class CategoriesPreferencesCommand extends
		AbstractCompanyPreferencesCommand {

	private static final String LOCATION_TRACKING = "locationtrackin";
	private static final String CLASS_TRACKING = "classtracking";
	private static final String CLASS_WARNING = "classwarning";

	@Override
	protected void addRequirements(List<Requirement> list) {

		list.add(new StringListRequirement(
				LOCATION_TRACKING,
				getMessages().useTerminologyFor(Global.get().Location() + " :"),
				getMessages().useTerminologyFor(Global.get().Location() + " :"),
				true, true, null) {
			@Override
			protected String getSelectString() {
				return getMessages().pleaseSelect(getMessages().location());
			}

			@Override
			protected List<String> getLists(Context context) {
				return getLocationsList();
			}

			@Override
			protected String getSetMessage() {
				return null;
			}

			@Override
			protected String getEmptyString() {
				return null;
			}
		});

		list.add(new BooleanRequirement(CLASS_TRACKING, true) {

			@Override
			protected String getTrueString() {
				return getMessages().classTracking() + " "
						+ getMessages().enabled();

			}

			@Override
			protected String getFalseString() {
				return getMessages().classTracking() + " "
						+ getMessages().disabled();
			}
		});

		list.add(new BooleanRequirement(CLASS_WARNING, true) {
			@Override
			public Result run(Context context, Result makeResult,
					ResultList list, ResultList actions) {
				boolean inventoryTracking = get(CLASS_TRACKING).getValue();
				if (inventoryTracking) {
					return super.run(context, makeResult, list, actions);
				}
				return null;
			}

			@Override
			protected String getTrueString() {
				return getMessages().classWarning() + " "
						+ getMessages().enabled();

			}

			@Override
			protected String getFalseString() {
				return getMessages().classWarning() + " "
						+ getMessages().disabled();

			}
		});
	}

	private List<String> getLocationsList() {
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add(getMessages().location());
		arrayList.add(getMessages().buisiness());
		arrayList.add(getMessages().department());
		arrayList.add(getMessages().division());
		arrayList.add(getMessages().property());
		arrayList.add(getMessages().store());
		arrayList.add(getMessages().territory());
		arrayList.add(getMessages().doNottrackingLocation());
		return arrayList;

	}

	@Override
	protected String initObject(Context context, boolean isUpdate) {

		ClientCompanyPreferences preferences = context.getPreferences();

		String string = null;
		if (!preferences.isLocationTrackingEnabled()) {
			string = getMessages().doNottrackingLocation();
		}
		if (preferences.getLocationTrackingId() == 0) {
			string = getMessages().buisiness();
		} else if (preferences.getLocationTrackingId() == 1) {
			string = getMessages().location();
		} else if (preferences.getLocationTrackingId() == 2) {
			string = getMessages().department();
		} else if (preferences.getLocationTrackingId() == 3) {
			string = getMessages().division();
		} else if (preferences.getLocationTrackingId() == 4) {
			string = getMessages().property();
		} else if (preferences.getLocationTrackingId() == 5) {
			string = getMessages().store();
		} else {
			string = getMessages().territory();
		}

		get(LOCATION_TRACKING).setValue(string);

		get(CLASS_TRACKING).setValue(preferences.isClassTrackingEnabled());
		get(CLASS_WARNING).setValue(preferences.isWarnOnEmptyClass());

		return null;

	}

	@Override
	protected Result onCompleteProcess(Context context) {
		ClientCompanyPreferences preferences = context.getPreferences();

		String locTrack = get(LOCATION_TRACKING).getValue();
		Integer incluestimate = getLocationsList().indexOf(locTrack);

		if (incluestimate == 0) {// location
			preferences.setLocationTrackingId(0);
			preferences.setLocationTrackingEnabled(true);
		} else if (incluestimate == 1) {// buisiness
			preferences.setLocationTrackingId(1);
			preferences.setLocationTrackingEnabled(true);
		} else if (incluestimate == 2) {// department
			preferences.setLocationTrackingId(2);
			preferences.setLocationTrackingEnabled(true);
		} else if (incluestimate == 3) {// division
			preferences.setLocationTrackingId(3);
			preferences.setLocationTrackingEnabled(true);
		} else if (incluestimate == 4) {// property
			preferences.setLocationTrackingId(4);
			preferences.setLocationTrackingEnabled(true);
		} else if (incluestimate == 5) {// store
			preferences.setLocationTrackingId(5);
			preferences.setLocationTrackingEnabled(true);
		} else if (incluestimate == 6) {// territory
			preferences.setLocationTrackingId(6);
			preferences.setLocationTrackingEnabled(true);
		} else {
			preferences.setIncludePendingAcceptedEstimates(false);
			preferences.setDontIncludeEstimates(false);
			preferences.setIncludeAcceptedEstimates(false);
			preferences.setDoyouwantEstimates(false);
		}

		boolean classreack = get(CLASS_TRACKING).getValue();
		boolean classwarn = get(CLASS_WARNING).getValue();

		preferences.setClassTrackingEnabled(classreack);
		preferences.setWarnOnEmptyClass(classwarn);

		savePreferences(context, preferences);
		return null;
	}

	@Override
	protected String getWelcomeMessage() {
		return "Updating Preferences";
	}

	@Override
	protected String getDetailsMessage() {
		return getMessages().readyToUpdate(getMessages().companyPreferences());
	}

	@Override
	protected void setDefaultValues(Context context) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSuccessMessage() {
		return getMessages().updateSuccessfully(
				getMessages().companyPreferences());
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

}
