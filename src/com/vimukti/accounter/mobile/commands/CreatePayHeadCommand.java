package com.vimukti.accounter.mobile.commands;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.vimukti.accounter.core.Account;
import com.vimukti.accounter.core.AttendanceOrProductionType;
import com.vimukti.accounter.core.PayHead;
import com.vimukti.accounter.mobile.Context;
import com.vimukti.accounter.mobile.Requirement;
import com.vimukti.accounter.mobile.Result;
import com.vimukti.accounter.mobile.ResultList;
import com.vimukti.accounter.mobile.requirements.AccountRequirement;
import com.vimukti.accounter.mobile.requirements.AttendanceOrProductionTypeRequirement;
import com.vimukti.accounter.mobile.requirements.BooleanRequirement;
import com.vimukti.accounter.mobile.requirements.NameRequirement;
import com.vimukti.accounter.mobile.requirements.PayHeadRequirement;
import com.vimukti.accounter.mobile.requirements.StringListRequirement;
import com.vimukti.accounter.mobile.utils.CommandUtils;
import com.vimukti.accounter.utils.HibernateUtil;
import com.vimukti.accounter.web.client.core.AccounterCoreType;
import com.vimukti.accounter.web.client.core.ClientAttendancePayHead;
import com.vimukti.accounter.web.client.core.ClientComputionPayHead;
import com.vimukti.accounter.web.client.core.ClientFlatRatePayHead;
import com.vimukti.accounter.web.client.core.ClientPayHead;
import com.vimukti.accounter.web.client.core.ListFilter;

public class CreatePayHeadCommand extends AbstractCommand {

	private static final String PAY_HEAD_NAME = "payHeadName";
	private static final String PAY_HEAD_TYPE = "payHeadType";
	private static final String PAY_HEAD_SLIPNAME = "payHeadSlipName";
	private static final String EXPENSE_ACCOUNT = "expenseAccount";
	private static final String AFFECT_NET_SALARY = "affectNetSalary";
	private static final String CALCULATION_TYPE = "calculationType";
	private static final String EARNING_DEDUCTION_ON = "earningDeduction";
	private static final String PAY_HEADS = "payheads";
	private static final String PER_DAY_CALCULATION_BASIS = "perdayCalculationBasis";
	private static final String CALCULATION_PERIOD = "calculationPeriod";
	private static final String PRODUCTION_TYPE = "productionType";
	private static final String COMPUTE_ON = "computeOn";

	private String[] types = { getMessages().earningsForEmployees(),
			getMessages().deductionsForEmployees(),
			getMessages().employeesStatutoryDeductions(),
			getMessages().employeesStatutoryContributions(),
			getMessages().employeesOtherCharges(), getMessages().bonus(),
			getMessages().loansAndAdvances(),
			getMessages().reimbursmentsToEmployees() };

	private String[] calType = { getMessages().attendance(),
			getMessages().asComputedValue(), getMessages().flatRate(),
			getMessages().production() };

	private String[] calPeriod = { getMessages().days(), getMessages().weeks(),
			getMessages().months() };

	private String[] perDayCalculations = {
			getMessages().asPerCalendarPeriod(), getMessages().days30(),
			getMessages().userDefinedCalendar() };

	private String[] computationTypes = { getMessages().onDeductionTotal(),
			getMessages().onEarningTotal(), getMessages().onSubTotal() };

	String[] attendanceTypes = { getMessages().otherPayhead(),
			getMessages().onEarningTotal(), getMessages().onSubTotal(),
			getMessages().rate() };

	private ClientPayHead payHead;
	private List<String> payHeadTypesList = new ArrayList<String>();
	private List<String> calculationTypesList = new ArrayList<String>();
	private List<String> attendanceTypesList = new ArrayList<String>();
	private List<String> perDayCalTypesList = new ArrayList<String>();
	private List<String> calPeriodTypesList = new ArrayList<String>();
	private List<String> computationTypeList = new ArrayList<String>();

	@Override
	protected void addRequirements(List<Requirement> list) {
		initEarningDeductionList();
		initPayHeads();
		initCalculationTypes();
		for (int i = 0; i < computationTypes.length; i++) {
			computationTypeList.add(computationTypes[i]);
		}

		list.add(new NameRequirement(PAY_HEAD_NAME, getMessages().pleaseEnter(
				getMessages().payhead()), getMessages().payhead(), false, true) {
			@Override
			public void setValue(Object value) {
				if (CreatePayHeadCommand.this.isPayHeadExists((String) value)) {
					addFirstMessage(getMessages().alreadyExist());
					return;
				}
				addFirstMessage(getMessages().pleaseEnter(
						getMessages().payhead()));
				super.setValue(value);
			}
		});

		list.add(new NameRequirement(PAY_HEAD_SLIPNAME, getMessages()
				.pleaseEnter(getMessages().paySlipName()), getMessages()
				.paySlipName(), false, true));

		list.add(new StringListRequirement(PAY_HEAD_TYPE, getMessages()
				.pleaseEnter(getMessages().payHeadType()), getMessages()
				.payHeadType(), true, true, null) {

			@Override
			protected String getSetMessage() {
				return getMessages().hasSelected(getMessages().payHeadType());
			}

			@Override
			protected String getSelectString() {
				return getMessages().pleaseSelect(getMessages().payHeadType());
			}

			@Override
			protected List<String> getLists(Context context) {
				return payHeadTypesList;
			}

			@Override
			protected String getEmptyString() {
				return null;
			}
		});

		list.add(new BooleanRequirement(AFFECT_NET_SALARY, true) {

			@Override
			protected String getTrueString() {
				return getMessages().affectNetSalary() + " "
						+ getMessages().YES();
			}

			@Override
			protected String getFalseString() {
				return getMessages().affectNetSalary() + " "
						+ getMessages().NO();
			}
		});
		list.add(new AccountRequirement(EXPENSE_ACCOUNT, getMessages()
				.pleaseEnterNameOrNumber(getMessages().expenseAccount()),
				getMessages().expenseAccount(), false, true, null) {

			@Override
			protected String getSetMessage() {
				return null;
			}

			@Override
			protected List<Account> getLists(Context context) {
				List<Account> filteredList = new ArrayList<Account>();
				for (Account obj : context.getCompany().getAccounts()) {
					if (new ListFilter<Account>() {

						@Override
						public boolean filter(Account acc) {
							return acc.getIsActive()
									&& acc.getType() != Account.TYPE_EXPENSE;
						}
					}.filter(obj)) {
						filteredList.add(obj);
					}
				}
				return filteredList;
			}

			@Override
			protected String getEmptyString() {
				return null;
			}
		});

		list.add(new StringListRequirement(CALCULATION_TYPE, getMessages()
				.pleaseEnter(getMessages().calculationType()), getMessages()
				.calculationType(), true, true, null) {

			@Override
			protected String getSetMessage() {
				return getMessages().hasSelected(
						getMessages().calculationType());
			}

			@Override
			protected String getSelectString() {
				return getMessages().pleaseSelect(
						getMessages().calculationType());
			}

			@Override
			protected List<String> getLists(Context context) {
				return calculationTypesList;
			}

			@Override
			protected String getEmptyString() {
				return null;
			}
		});

		list.add(new StringListRequirement(EARNING_DEDUCTION_ON, getMessages()
				.pleaseEnter(getMessages().deductionOn()), getMessages()
				.deductionOn(), true, true, null) {

			@Override
			public Result run(Context context, Result makeResult,
					ResultList list, ResultList actions) {
				String value = get(CALCULATION_TYPE).getValue();
				if (value.equals(getMessages().production())
						|| value.equals(getMessages().attendance())) {
					return super.run(context, makeResult, list, actions);
				} else {
					return null;
				}

			}

			@Override
			protected String getSetMessage() {
				return getMessages().hasSelected(getMessages().deductionOn());
			}

			@Override
			protected String getSelectString() {
				return getMessages().pleaseSelect(getMessages().deductionOn());
			}

			@Override
			protected List<String> getLists(Context context) {
				return attendanceTypesList;
			}

			@Override
			protected String getEmptyString() {
				return null;
			}
		});

		list.add(new PayHeadRequirement(PAY_HEADS) {

			@Override
			public Result run(Context context, Result makeResult,
					ResultList list, ResultList actions) {
				String value = get(EARNING_DEDUCTION_ON).getValue();
				if (value.equals(getMessages().otherPayhead())) {
					return super.run(context, makeResult, list, actions);
				} else {
					return null;
				}

			}

			@Override
			protected String getSetMessage() {
				return getMessages().hasSelected(getMessages().deductionOn());
			}

			@Override
			protected String getSelectString() {
				return getMessages().pleaseSelect(getMessages().deductionOn());
			}

			@Override
			protected List<PayHead> getLists(Context context) {
				return getpayHeadslist();
			}

			@Override
			protected String getEmptyString() {
				return null;
			}
		});

		list.add(new StringListRequirement(PER_DAY_CALCULATION_BASIS,
				getMessages().pleaseEnter(
						getMessages().perDayCalculationBasis()), getMessages()
						.perDayCalculationBasis(), true, false, null) {

			@Override
			public Result run(Context context, Result makeResult,
					ResultList list, ResultList actions) {
				String value = get(CALCULATION_TYPE).getValue();
				if (value.equals(getMessages().attendance())) {
					return super.run(context, makeResult, list, actions);
				} else {
					return null;
				}
			}

			@Override
			protected String getSetMessage() {
				return getMessages().hasSelected(
						getMessages().perDayCalculationBasis());
			}

			@Override
			protected String getSelectString() {
				return getMessages().pleaseSelect(
						getMessages().perDayCalculationBasis());
			}

			@Override
			protected List<String> getLists(Context context) {
				return getPerDayCalculationTypes();
			}

			@Override
			protected String getEmptyString() {
				return null;
			}
		});

		list.add(new StringListRequirement(CALCULATION_PERIOD, getMessages()
				.pleaseEnter(getMessages().calculationPeriod()), getMessages()
				.calculationPeriod(), true, true, null) {

			@Override
			public Result run(Context context, Result makeResult,
					ResultList list, ResultList actions) {
				String value = get(CALCULATION_TYPE).getValue();
				if (value.equals(getMessages().asComputedValue())
						|| value.equals(getMessages().flatRate())) {
					return super.run(context, makeResult, list, actions);
				} else {
					return null;
				}
			}

			@Override
			protected String getSetMessage() {
				return getMessages().hasSelected(
						getMessages().calculationPeriod());
			}

			@Override
			protected String getSelectString() {
				return getMessages().pleaseSelect(
						getMessages().calculationPeriod());
			}

			@Override
			protected List<String> getLists(Context context) {
				return getCalculationPeriodTypes();
			}

			@Override
			protected String getEmptyString() {
				return null;
			}
		});

		list.add(new AttendanceOrProductionTypeRequirement(PRODUCTION_TYPE,
				getMessages().pleaseSelect(getMessages().productionType()),
				getMessages().productionType()) {

			@Override
			public Result run(Context context, Result makeResult,
					ResultList list, ResultList actions) {
				String value = get(CALCULATION_TYPE).getValue();
				if (value.equals(getMessages().production())) {
					return super.run(context, makeResult, list, actions);
				} else {
					return null;
				}
			}

			@Override
			protected String getSetMessage() {
				return getMessages()
						.hasSelected(getMessages().productionType());
			}

			@Override
			protected String getSelectString() {
				return getMessages().pleaseSelect(
						getMessages().productionType());
			}
		});

		list.add(new StringListRequirement(COMPUTE_ON, getMessages()
				.pleaseEnter(getMessages().computedOn()), getMessages()
				.computedOn(), true, true, null) {

			@Override
			public Result run(Context context, Result makeResult,
					ResultList list, ResultList actions) {
				String value = get(CALCULATION_TYPE).getValue();
				if (value.equals(getMessages().asComputedValue())) {
					return super.run(context, makeResult, list, actions);
				} else {
					return null;
				}
			}

			@Override
			protected String getSetMessage() {
				return getMessages().hasSelected(getMessages().computedOn());
			}

			@Override
			protected String getSelectString() {
				return getMessages().pleaseSelect(getMessages().computedOn());
			}

			@Override
			protected List<String> getLists(Context context) {
				return computationTypeList;
			}

			@Override
			protected String getEmptyString() {
				return null;
			}
		});

	}

	protected List<String> getCalculationPeriodTypes() {
		calPeriodTypesList = new ArrayList<String>();
		for (String string : calPeriod) {
			calPeriodTypesList.add(string);
		}
		return calPeriodTypesList;
	}

	protected List<String> getPerDayCalculationTypes() {
		perDayCalTypesList = new ArrayList<String>();
		for (String string : perDayCalculations) {
			perDayCalTypesList.add(string);
		}
		return perDayCalTypesList;
	}

	protected List<PayHead> getpayHeadslist() {
		List<PayHead> payheadsList = new ArrayList<PayHead>();
		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("list.Payhead").setEntity(
				"company", getCompany());
		payheadsList = query.list();
		return payheadsList;
	}

	private void initEarningDeductionList() {
		attendanceTypesList = new ArrayList<String>();
		for (String string : attendanceTypes) {
			attendanceTypesList.add(string);
		}
	}

	private void initCalculationTypes() {
		calculationTypesList = new ArrayList<String>();
		for (String string : calType) {
			calculationTypesList.add(string);
		}
	}

	private void initPayHeads() {
		payHeadTypesList = new ArrayList<String>();
		for (String string : types) {
			payHeadTypesList.add(string);
		}
	}

	protected boolean isPayHeadExists(String value) {
		List<PayHead> getpayHeadslist = getpayHeadslist();
		for (PayHead payHead : getpayHeadslist) {
			if (this.payHead.getID() != payHead.getID()
					&& payHead.getName().equals(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected String initObject(Context context, boolean isUpdate) {
		if (isUpdate) {
			String string = context.getString();
			if (string.isEmpty()) {
				addFirstMessage(context, getMessages()
						.selectATransactionToUpdate(getMessages().invoice()));
				return "payheadList";
			}
			long numberFromString = getNumberFromString(string);
			payHead = (ClientPayHead) CommandUtils.getServerObjectById(
					numberFromString, AccounterCoreType.PAY_HEAD);
			if (payHead == null) {
				return "payheadList" + string;
			}
			setValues();
		} else {
			payHead = new ClientPayHead();
		}
		return null;
	}

	private void setValues() {
		get(PAY_HEAD_NAME).setValue(payHead.getName());
		get(PAY_HEAD_TYPE).setValue(
				ClientPayHead.getPayHeadType(payHead.getType()));
		get(PAY_HEAD_SLIPNAME).setValue(payHead.getNameToAppearInPaySlip());
		get(EXPENSE_ACCOUNT).setValue(
				getServerObject(Account.class, payHead.getExpenseAccount()));
		get(AFFECT_NET_SALARY).setValue(payHead.isAffectNetSalary());
		get(CALCULATION_TYPE).setValue(
				ClientPayHead.getCalculationType(payHead.getCalculationType()));

		String calType = ClientPayHead.getCalculationType(payHead
				.getCalculationType());
		if (calType.equals(getMessages().attendance())
				|| calType.equals(getMessages().production())) {
			ClientAttendancePayHead attendance = (ClientAttendancePayHead) payHead;
			get(PRODUCTION_TYPE).setValue(
					getServerObject(AttendanceOrProductionType.class,
							attendance.getProductionType()));
			String attendanceType = ClientAttendancePayHead
					.getAttendanceType(attendance.getAttendanceType());
			get(EARNING_DEDUCTION_ON).setValue(attendanceType);

			if (attendance.getAttendanceType() == ClientAttendancePayHead.PAY_HEAD) {
				get(PAY_HEADS)
						.setValue(
								getServerObject(PayHead.class,
										attendance.getPayhead()));
			}

			get(CALCULATION_PERIOD).setValue(
					getCalculationPeriod(attendance.getCalculationPeriod()));
			get(PER_DAY_CALCULATION_BASIS).setValue(
					getPerdayCalculationBasis(attendance
							.getPerDayCalculationBasis()));

		} else if (calType.equals(getMessages().asComputedValue())) {
			ClientComputionPayHead computation = (ClientComputionPayHead) payHead;

			get(CALCULATION_PERIOD).setValue(
					getCalculationPeriod(computation.getCalculationPeriod()));
			String compType = computationTypeList.get(computation
					.getComputationType() - 1);
			get(COMPUTE_ON).setValue(compType);
			if (compType.equals(getMessages().onSpecifiedFormula())) {
				// TODO
			}

		} else if (calType.equals(getMessages().flatRate())) {
			ClientFlatRatePayHead flatrate = (ClientFlatRatePayHead) payHead;

			get(CALCULATION_PERIOD).setValue(
					getCalculationPeriod(flatrate.getCalculationPeriod()));
		}
	}

	private String getPerdayCalculationBasis(int perDayCalculationBasis) {
		switch (perDayCalculationBasis) {
		case ClientAttendancePayHead.PER_DAY_CALCULATION_AS_PER_CALANDAR_PERIOD:
			return getMessages().asPerCalendarPeriod();

		case ClientAttendancePayHead.PER_DAY_CALCULATION_30_DAYS:
			return getMessages().days30();

		case ClientAttendancePayHead.PER_DAY_CALCULATION_USER_DEFINED_CALANDAR:
			return getMessages().userDefindCalendar();

		default:
			return null;
		}
	}

	private String getCalculationPeriod(int calculationPeriod) {
		switch (calculationPeriod) {
		case ClientPayHead.CALCULATION_PERIOD_DAYS:
			return getMessages().days();

		case ClientPayHead.CALCULATION_PERIOD_MONTHS:
			return getMessages().months();

		case ClientPayHead.CALCULATION_PERIOD_WEEKS:
			return getMessages().weeks();

		default:
			return null;
		}
	}

	@Override
	protected String getWelcomeMessage() {
		return payHead.getID() == 0 ? getMessages().creating(
				getMessages().payhead()) : getMessages().updating(
				getMessages().payhead());
	}

	@Override
	protected String getDetailsMessage() {
		return payHead.getID() == 0 ? getMessages().readyToCreate(
				getMessages().payhead()) : getMessages().readyToUpdate(
				getMessages().payhead());
	}

	@Override
	protected void setDefaultValues(Context context) {
		get(CALCULATION_TYPE).setDefaultValue(getMessages().attendance());
		get(EARNING_DEDUCTION_ON).setDefaultValue(getMessages().otherPayhead());
	}

	@Override
	public String getSuccessMessage() {
		return payHead.getID() == 0 ? getMessages().createSuccessfully(
				getMessages().payhead()) : getMessages().updateSuccessfully(
				getMessages().payhead());
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	protected Result onCompleteProcess(Context context) {
		String selectedValue = get(CALCULATION_TYPE).getValue();
		if (selectedValue == null) {
			return new Result(getMessages().pleaseSelect(
					getMessages().calculationType()));
		}
		if (selectedValue.equals(getMessages().attendance())
				|| selectedValue.equals(getMessages().production())) {
			ClientAttendancePayHead payhead = new ClientAttendancePayHead();
			payhead.setAttendanceType(attendanceTypesList.indexOf(get(
					EARNING_DEDUCTION_ON).getValue()) + 1);
			String attendanceType = ClientAttendancePayHead
					.getAttendanceType(payhead.getAttendanceType());

			if (attendanceType.equals(getMessages().otherPayhead())) {
				payhead.setPayhead(((PayHead) get(PAY_HEADS).getValue())
						.getID());
			}
			payhead.setID(payhead.getID());
			if (selectedValue.equals(getMessages().attendance())) {
				String period = get(CALCULATION_PERIOD).getValue();
				payhead.setCalculationPeriod(calPeriodTypesList.indexOf(period) + 1);
				String perDayBasis = get(PER_DAY_CALCULATION_BASIS).getValue();
				payhead.setPerDayCalculationBasis(perDayCalTypesList
						.indexOf(perDayBasis) + 1);
			}

			if (selectedValue.equals(getMessages().production())) {
				AttendanceOrProductionType productionType = get(PRODUCTION_TYPE)
						.getValue();
				payhead.setProductionType(productionType.getID());
				payhead.setPerDayCalculationBasis(ClientAttendancePayHead.PER_DAY_CALCULATION_AS_PER_CALANDAR_PERIOD);
			}
			this.payHead = payhead;

		} else if (selectedValue.equals(getMessages().asComputedValue())) {
			ClientComputionPayHead payhead = new ClientComputionPayHead();
			payhead.setID(this.payHead.getID());
			String period = get(CALCULATION_PERIOD).getValue();
			payhead.setCalculationPeriod(calPeriodTypesList.indexOf(period) + 1);
			String computeOn = get(COMPUTE_ON).getValue();
			payhead.setComputationType(computationTypeList.indexOf(computeOn) + 1);
			this.payHead = payhead;
		} else if (selectedValue.equals(getMessages().flatRate())) {
			ClientFlatRatePayHead payHead = new ClientFlatRatePayHead();
			payHead.setID(this.payHead.getID());
			String period = get(CALCULATION_PERIOD).getValue();
			payHead.setCalculationPeriod(calPeriodTypesList.indexOf(period) + 1);
			this.payHead = payHead;
		}

		payHead.setName((String) get(PAY_HEAD_NAME).getValue());
		String payHeadType = get(PAY_HEAD_TYPE).getValue();
		payHead.setType(payHeadTypesList.indexOf(payHeadType) + 1);
		String calType = get(CALCULATION_TYPE).getValue();
		payHead.setCalculationType(calculationTypesList.indexOf(calType) + 1);
		payHead.setRoundingMethod(1);
		payHead.setNameToAppearInPaySlip((String) get(PAY_HEAD_SLIPNAME)
				.getValue());
		Boolean isEffect = get(AFFECT_NET_SALARY).getValue();
		payHead.setAffectNetSalary(isEffect);
		Account account = get(EXPENSE_ACCOUNT).getValue();
		payHead.setExpenseAccount(account.getID());
		create(payHead, context);
		return null;
	}
}
