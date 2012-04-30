package com.vimukti.accounter.web.server.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import com.vimukti.accounter.core.AttendanceOrProductionType;
import com.vimukti.accounter.core.AttendancePayHead;
import com.vimukti.accounter.core.ClientConvertUtil;
import com.vimukti.accounter.core.Company;
import com.vimukti.accounter.core.ComputionPayHead;
import com.vimukti.accounter.core.Employee;
import com.vimukti.accounter.core.EmployeeGroup;
import com.vimukti.accounter.core.FinanceDate;
import com.vimukti.accounter.core.FlatRatePayHead;
import com.vimukti.accounter.core.PayHead;
import com.vimukti.accounter.core.PayStructure;
import com.vimukti.accounter.core.PayStructureItem;
import com.vimukti.accounter.core.PayrollUnit;
import com.vimukti.accounter.core.ProductionPayHead;
import com.vimukti.accounter.core.UserDefinedPayHead;
import com.vimukti.accounter.utils.HibernateUtil;
import com.vimukti.accounter.web.client.core.ClientAttendanceManagementItem;
import com.vimukti.accounter.web.client.core.ClientAttendanceOrProductionType;
import com.vimukti.accounter.web.client.core.ClientAttendancePayHead;
import com.vimukti.accounter.web.client.core.ClientComputionPayHead;
import com.vimukti.accounter.web.client.core.ClientEmployee;
import com.vimukti.accounter.web.client.core.ClientEmployeeGroup;
import com.vimukti.accounter.web.client.core.ClientEmployeePayHeadComponent;
import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.core.ClientFlatRatePayHead;
import com.vimukti.accounter.web.client.core.ClientPayHead;
import com.vimukti.accounter.web.client.core.ClientPayStructure;
import com.vimukti.accounter.web.client.core.ClientPayStructureDestination;
import com.vimukti.accounter.web.client.core.ClientPayrollUnit;
import com.vimukti.accounter.web.client.core.ClientProductionPayHead;
import com.vimukti.accounter.web.client.core.ClientTransaction;
import com.vimukti.accounter.web.client.core.ClientUserDefinedPayHead;
import com.vimukti.accounter.web.client.core.PaginationList;
import com.vimukti.accounter.web.client.core.Lists.PaymentsList;
import com.vimukti.accounter.web.client.core.reports.PayHeadDetails;
import com.vimukti.accounter.web.client.core.reports.PayHeadSummary;
import com.vimukti.accounter.web.client.core.reports.PaySheet;
import com.vimukti.accounter.web.client.core.reports.PaySlipDetail;
import com.vimukti.accounter.web.client.core.reports.PaySlipSummary;
import com.vimukti.accounter.web.client.exception.AccounterException;

public class PayrollManager extends Manager {

	public PaginationList<ClientEmployee> getEmployees(int start, int lenght,
			Long companyId) throws AccounterException {
		Session session = HibernateUtil.getCurrentSession();
		Company company = getCompany(companyId);
		Query query = session.getNamedQuery("list.All.Employees").setEntity(
				"company", company);
		List<Employee> employees = query.list();
		if (employees == null) {
			return null;
		}
		int total = query.list().size();
		if (lenght != -1) {
			employees = query.setFirstResult(start).setMaxResults(lenght)
					.list();
		}
		PaginationList<ClientEmployee> clientEmployees = new PaginationList<ClientEmployee>();
		for (Employee employee : employees) {
			ClientEmployee clientEmployee;
			clientEmployee = new ClientConvertUtil().toClientObject(employee,
					ClientEmployee.class);
			clientEmployees.add(clientEmployee);
		}
		clientEmployees.setStart(start);
		clientEmployees.setTotalCount(total);
		return clientEmployees;
	}

	public PaginationList<ClientPayHead> getPayheadsList(int start, int length,
			Long companyId) throws AccounterException {
		Session session = HibernateUtil.getCurrentSession();
		Company company = getCompany(companyId);
		Query query = session.getNamedQuery("list.Payhead").setEntity(
				"company", company);
		List<PayHead> employees = query.list();
		int total = query.list().size();
		if (length != -1 && length != 0) {
			employees = query.setFirstResult(start).setMaxResults(length)
					.list();
		}
		PaginationList<ClientPayHead> clientPayHeads = new PaginationList<ClientPayHead>();
		if (employees != null) {
			for (PayHead payHead : employees) {
				ClientPayHead clientPayHead = null;
				if (payHead instanceof AttendancePayHead) {
					clientPayHead = new ClientConvertUtil().toClientObject(
							payHead, ClientAttendancePayHead.class);
				} else if (payHead instanceof ComputionPayHead) {
					clientPayHead = new ClientConvertUtil().toClientObject(
							payHead, ClientComputionPayHead.class);
				} else if (payHead instanceof FlatRatePayHead) {
					clientPayHead = new ClientConvertUtil().toClientObject(
							payHead, ClientFlatRatePayHead.class);
				} else if (payHead instanceof ProductionPayHead) {
					clientPayHead = new ClientConvertUtil().toClientObject(
							payHead, ClientProductionPayHead.class);
				} else if (payHead instanceof UserDefinedPayHead) {
					clientPayHead = new ClientConvertUtil().toClientObject(
							payHead, ClientUserDefinedPayHead.class);
				}
				if (clientPayHead != null) {
					clientPayHeads.add(clientPayHead);
				}
			}
		}
		clientPayHeads.setTotalCount(total);
		clientPayHeads.setStart(start);
		return clientPayHeads;
	}

	public PaginationList<ClientPayrollUnit> getPayrollUnitsList(int start,
			int length, Long companyId) throws AccounterException {
		Session session = HibernateUtil.getCurrentSession();
		Company company = getCompany(companyId);
		Query query = session.getNamedQuery("list.PayrollUnit").setEntity(
				"company", company);
		List<PayrollUnit> units = query.list();
		if (units == null) {
			return null;
		}

		int total = query.list().size();

		if (length != -1) {
			units = query.setFirstResult(start).setMaxResults(length).list();
		}

		PaginationList<ClientPayrollUnit> clientPayrollUnits = new PaginationList<ClientPayrollUnit>();
		for (PayrollUnit unit : units) {
			ClientPayrollUnit clientPayrollUnit;
			clientPayrollUnit = new ClientConvertUtil().toClientObject(unit,
					ClientPayrollUnit.class);
			clientPayrollUnits.add(clientPayrollUnit);
		}
		clientPayrollUnits.setTotalCount(total);
		clientPayrollUnits.setStart(start);
		return clientPayrollUnits;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<ClientEmployeePayHeadComponent> getEmployeeGroupPayHeadComponents(
			FinanceDate startDate, FinanceDate endDate,
			ClientPayStructureDestination selectItem, Long companyId,
			List<ClientAttendanceManagementItem> attendanceItems)
			throws AccounterException {
		Session session = HibernateUtil.getCurrentSession();
		List<Employee> employees = session.getNamedQuery("getEmployeesByGroup")
				.setParameter("groupId", selectItem.getID()).list();
		ArrayList<ClientEmployeePayHeadComponent> groupComponents = new ArrayList<ClientEmployeePayHeadComponent>();
		for (Employee employee : employees) {
			ArrayList<ClientEmployeePayHeadComponent> employeePayHeadComponents = getEmployeePayHeadComponents(
					startDate, endDate, employee, companyId,
					getEmployeeAttendanceItems(employee, attendanceItems));
			groupComponents.addAll(employeePayHeadComponents);
		}
		return new ArrayList<ClientEmployeePayHeadComponent>(groupComponents);
	}

	private List<ClientAttendanceManagementItem> getEmployeeAttendanceItems(
			Employee employee,
			List<ClientAttendanceManagementItem> attendanceItems) {
		List<ClientAttendanceManagementItem> empItems = new ArrayList<ClientAttendanceManagementItem>();
		for (ClientAttendanceManagementItem item : attendanceItems) {
			if (item.getEmployee().getID() == employee.getID()) {
				empItems.add(item);
			}
		}
		return empItems;
	}

	public ArrayList<ClientEmployeePayHeadComponent> getEmployeePayHeadComponents(
			FinanceDate startDate, FinanceDate endDate, Employee selectItem,
			Long companyId, List<ClientAttendanceManagementItem> attendanceItems)
			throws AccounterException {
		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getPayStructureItem.by.employee")
				.setParameter("employee", selectItem)
				.setParameter("group", selectItem.getGroup())
				.setParameter("company", getCompany(companyId))
				.setParameter("start", startDate).setParameter("end", endDate);
		List<PayStructureItem> list = query.list();

		if (list == null) {
			return null;
		}

		double earnings = 0.0;
		double deductions = 0.0;

		long[] attendance = { 0, 0, 0 };

		if (attendanceItems != null && attendanceItems.size() > 0) {
			for (ClientAttendanceManagementItem attendanceManagementItem : attendanceItems) {
				if (attendanceManagementItem.getAttendanceType().getType() == AttendanceOrProductionType.TYPE_LEAVE_WITH_PAY) {
					attendance[0] += attendanceManagementItem.getNumber();
				} else if (attendanceManagementItem.getAttendanceType()
						.getType() == AttendanceOrProductionType.TYPE_LEAVE_WITHOUT_PAY) {
					attendance[1] += attendanceManagementItem.getNumber();
				} else if (attendanceManagementItem.getAttendanceType()
						.getType() == AttendanceOrProductionType.TYPE_USER_DEFINED_CALENDAR) {
					attendance[2] += attendanceManagementItem.getNumber();
				}
			}
		}

		ArrayList<ClientEmployeePayHeadComponent> clientEmployeePayHeadComponents = new ArrayList<ClientEmployeePayHeadComponent>();

		for (PayStructureItem payStructureItem : list) {
			int calcType = payStructureItem.getPayHead().getCalculationType();
			PayHead payHead = payStructureItem.getPayHead();

			ClientPayHead clientPayHead = null;
			if (calcType == ClientPayHead.CALCULATION_TYPE_ON_ATTENDANCE) {
				clientPayHead = new ClientConvertUtil().toClientObject(payHead,
						ClientAttendancePayHead.class);
			} else if (calcType == ClientPayHead.CALCULATION_TYPE_AS_COMPUTED_VALUE) {
				clientPayHead = new ClientConvertUtil().toClientObject(payHead,
						ClientComputionPayHead.class);
			} else if (calcType == ClientPayHead.CALCULATION_TYPE_FLAT_RATE) {
				clientPayHead = new ClientConvertUtil().toClientObject(payHead,
						ClientFlatRatePayHead.class);
			} else if (calcType == ClientPayHead.CALCULATION_TYPE_ON_PRODUCTION) {
				clientPayHead = new ClientConvertUtil().toClientObject(payHead,
						ClientProductionPayHead.class);
			} else if (calcType == ClientPayHead.CALCULATION_TYPE_AS_USER_DEFINED) {
				clientPayHead = new ClientConvertUtil().toClientObject(payHead,
						ClientUserDefinedPayHead.class);
			}

			ClientEmployeePayHeadComponent component = new ClientEmployeePayHeadComponent();
			component.setPayHead(clientPayHead);
			payStructureItem.setStartDate(startDate);
			payStructureItem.setEndDate(endDate);
			payStructureItem.setAttendance(attendance);
			double calculatedAmount = payHead.calculatePayment(
					payStructureItem, deductions, earnings);
			if (payHead.isEarning()) {
				earnings += calculatedAmount;
			} else {
				deductions += calculatedAmount;
			}

			component.setRate(calculatedAmount);
			component.setEmployee(selectItem.getName());
			clientEmployeePayHeadComponents.add(component);
		}
		return clientEmployeePayHeadComponents;
	}

	public PaginationList<ClientPayStructure> getPayrollStructuresList(
			int start, int length, Long companyId) throws AccounterException {
		Session session = HibernateUtil.getCurrentSession();
		Company company = getCompany(companyId);
		Query query = session.getNamedQuery("list.PayStructure").setEntity(
				"company", company);
		List<PayStructure> paystructures = query.list();
		if (paystructures == null) {
			return null;
		}
		int total = query.list().size();
		paystructures = query.setFirstResult(start).setMaxResults(length)
				.list();
		PaginationList<ClientPayStructure> clientPayStructures = new PaginationList<ClientPayStructure>();
		for (PayStructure payStructure : paystructures) {
			ClientPayStructure clientPayStructure;
			clientPayStructure = new ClientConvertUtil().toClientObject(
					payStructure, ClientPayStructure.class);
			clientPayStructures.add(clientPayStructure);
		}
		clientPayStructures.setStart(start);
		clientPayStructures.setTotalCount(total);
		return clientPayStructures;
	}

	public PaginationList<ClientAttendanceOrProductionType> getAttendanceProductionTypes(
			int start, int length, Long companyId) throws AccounterException {
		Session session = HibernateUtil.getCurrentSession();
		int total = 0;
		Company company = getCompany(companyId);
		Query query = session.getNamedQuery("list.AttendanceProductionType")
				.setEntity("company", company);
		List<AttendanceOrProductionType> types = query.list();
		if (types == null) {
			return null;
		}
		if (length == -1) {
			types = query.list();
		} else {
			total = query.list().size();
			types = query.setFirstResult(start).setMaxResults(length).list();
		}
		PaginationList<ClientAttendanceOrProductionType> list = new PaginationList<ClientAttendanceOrProductionType>();
		for (AttendanceOrProductionType type : types) {
			ClientAttendanceOrProductionType clientType;
			clientType = new ClientConvertUtil().toClientObject(type,
					ClientAttendanceOrProductionType.class);
			list.add(clientType);
		}
		list.setTotalCount(total);
		list.setStart(start);
		return list;
	}

	public ArrayList<ClientEmployeeGroup> getEmployeeGroups(Long companyId)
			throws AccounterException {
		Session session = HibernateUtil.getCurrentSession();
		Company company = getCompany(companyId);
		Query query = session.getNamedQuery("list.EmployeeGroups").setEntity(
				"company", company);
		List<EmployeeGroup> groups = query.list();
		if (groups == null) {
			return null;
		}
		ArrayList<ClientEmployeeGroup> clientGroups = new ArrayList<ClientEmployeeGroup>();
		for (EmployeeGroup group : groups) {
			List<Employee> employees = session
					.getNamedQuery("getEmployeesByGroup")
					.setParameter("groupId", group.getID()).list();
			group.setEmployees(employees);
			ClientEmployeeGroup clientGroup = new ClientConvertUtil()
					.toClientObject(group, ClientEmployeeGroup.class);
			clientGroups.add(clientGroup);
		}
		return clientGroups;
	}

	public List<ClientPayStructureDestination> getEmployeesAndGroups(
			Long companyId) throws AccounterException {
		ArrayList<ClientPayStructureDestination> arrayList = new ArrayList<ClientPayStructureDestination>();
		PaginationList<ClientEmployee> employees = getEmployees(0, -1,
				companyId);
		arrayList.addAll(employees);
		ArrayList<ClientEmployeeGroup> employeeGroups = getEmployeeGroups(companyId);
		arrayList.addAll(employeeGroups);

		return arrayList;
	}

	public long getEmployeeAttendanceCurrentBal(long employee,
			long attendanceType, long companyId) {
		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("EmployeeAttendanceCurrentBal")
				.setParameter("company", companyId)
				.setParameter("employee", employee)
				.setParameter("attendanceType", attendanceType);
		Object result = query.uniqueResult();
		if (result == null) {
			return 0;
		}
		return (Long) result;
	}

	public ArrayList<ClientEmployee> getEmployeesByGroup(
			ClientPayStructureDestination group, Long companyId)
			throws AccounterException {
		ArrayList<ClientEmployee> list = new ArrayList<ClientEmployee>();
		Session session = HibernateUtil.getCurrentSession();
		List<Employee> employees = session.getNamedQuery("getEmployeesByGroup")
				.setParameter("groupId", group.getID()).list();
		for (Employee employee : employees) {
			ClientEmployee clientEmployee = new ClientConvertUtil()
					.toClientObject(employee, ClientEmployee.class);
			list.add(clientEmployee);
		}
		return list;
	}

	public ArrayList<PayHeadSummary> getPayHeadSummaryReport(long payHeadId,
			FinanceDate startDate, FinanceDate endDate, long companyId)
			throws AccounterException {
		ArrayList<PayHeadSummary> payHeadSummaryList = new ArrayList<PayHeadSummary>();
		Session currentSession = HibernateUtil.getCurrentSession();
		Query query = currentSession
				.getNamedQuery("getPayHeadSummaryReportList")
				.setParameter("payHeadId", payHeadId)
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate())
				.setParameter("companyId", companyId);
		List list = query.list();

		Iterator iterator = list.iterator();
		while ((iterator).hasNext()) {
			Object[] object = (Object[]) iterator.next();
			PayHeadSummary headSummary = new PayHeadSummary();
			headSummary.setEmployeeId(object[0] == null ? 0 : (Long) object[0]);
			headSummary.setEmployeeName(object[1] == null ? ""
					: (String) object[1]);
			headSummary.setPayHead(object[2] == null ? 0 : (Long) object[2]);
			headSummary.setPayHeadName(object[3] == null ? ""
					: (String) object[3]);
			headSummary.setPayHeadAmount(object[4] == null ? 0
					: (Double) object[4]);
			payHeadSummaryList.add(headSummary);
		}
		return payHeadSummaryList;
	}

	public ArrayList<PaySlipSummary> getPaySlipSummary(FinanceDate dates,
			FinanceDate dates2, Long companyId) {
		ArrayList<PaySlipSummary> result = new ArrayList<PaySlipSummary>();

		Session session = HibernateUtil.getCurrentSession();
		Query query = session.getNamedQuery("getPaySlipSummary")
				.setParameter("companyId", companyId)
				.setParameter("start", dates.getDate())
				.setParameter("end", dates2.getDate());

		List<Object[]> list = query.list();
		Iterator<Object[]> iterator = list.iterator();

		while (iterator.hasNext()) {
			Object[] type = iterator.next();
			PaySlipSummary summary = new PaySlipSummary();

			summary.setName((String) type[0]);
			summary.setNumber((String) type[1]);
			summary.setAccountNo((String) type[2]);
			summary.setBankName((String) type[3]);
			summary.setBranch((String) type[4]);
			Object object = type[5];
			if (object != null) {
				summary.setAmount((Double) type[5]);
			}
			summary.setEmail((String) type[6]);
			summary.setEmployeeId((Long) type[7]);

			result.add(summary);
		}
		return result;
	}

	public ArrayList<PaySheet> getPaySheet(FinanceDate dates,
			FinanceDate dates2, Long companyId) {
		Session session = HibernateUtil.getCurrentSession();

		Map<Long, Long> inneequeryMap = new HashMap<Long, Long>();
		List l;
		List inerlist = null;

		l = session.getNamedQuery("getPaySheet")
				.setParameter("companyId", companyId)
				.setParameter("start", dates.getDate())
				.setParameter("end", dates2.getDate()).list();

		Object[] object = null;
		Iterator iterator = l.iterator();
		List<PaySheet> queryResult = new ArrayList<PaySheet>();
		long previousEmployeeID = 0;
		while (iterator.hasNext()) {
			object = (Object[]) iterator.next();
			long employeeId = ((Long) object[0]).longValue();
			if (previousEmployeeID == 0 || previousEmployeeID != employeeId) {
				previousEmployeeID = employeeId;
				PaySheet record = new PaySheet();
				record.setEmployeeId(employeeId == 0 ? 0 : employeeId);
				record.setEmployee(object[1] == null ? null
						: (String) object[1]);
				record.setPayheadId((Long) object[5]);
				long transactionID = (object[4] == null ? 0
						: ((Long) object[4]).longValue());
				long location;
				if (object[2] == null) {
					location = inneequeryMap.get(transactionID);
				} else {
					location = object[2] == null ? 0 : ((Long) object[2])
							.longValue();
				}

				double amount = object[3] == null ? 0 : (Double) object[3];
				record.getMap().put(location, amount);

				queryResult.add(record);
			} else {
				PaySheet record = queryResult.get(queryResult.size() - 1);
				long transactionID = (object[4] == null ? 0
						: ((Long) object[4]).longValue());
				long location;

				if (object[4] == null) {
					location = inneequeryMap.get(transactionID);
				} else {
					location = object[2] == null ? 0 : ((Long) object[2])
							.longValue();
				}
				double amount = object[3] == null ? 0 : (Double) object[3];

				record.getMap().get(location);
				record.getMap().put(location, amount);
				record.setEmployeeId(location);
				record.setPayheadId((Long) object[5]);
			}
		}
		return new ArrayList<PaySheet>(queryResult);
	}

	public ArrayList<PayHeadDetails> getpayHeadDetailsList(long employeeId,
			long payHeadId, FinanceDate startDate, FinanceDate endDate,
			Long companyId) throws AccounterException {
		ArrayList<PayHeadDetails> payHeadDetails = new ArrayList<PayHeadDetails>();
		Session currentSession = HibernateUtil.getCurrentSession();
		Query query = currentSession.getNamedQuery("getpayHeadDetailsList")
				.setParameter("payHeadId", payHeadId)
				.setParameter("employee", employeeId)
				.setParameter("startDate", startDate.getDate())
				.setParameter("endDate", endDate.getDate())
				.setParameter("companyId", companyId);
		List list = query.list();

		Iterator iterator = list.iterator();
		while ((iterator).hasNext()) {
			Object[] object = (Object[]) iterator.next();
			PayHeadDetails payHeadDetail = new PayHeadDetails();
			payHeadDetail.setAmount(object[0] == null ? 0.0
					: (Double) object[0]);
			payHeadDetail.setEmployee(object[1] == null ? ""
					: (String) object[1]);
			payHeadDetail.setPayHead(object[2] == null ? ""
					: (String) object[2]);
			payHeadDetail.setPayHeadType(object[3] == null ? 0
					: (Integer) object[3]);
			payHeadDetail.setTransactionNumber(object[4] == null ? ""
					: (String) object[4]);
			payHeadDetail.setPeriodEndDate(object[5] == null ? 0
					: (Long) object[5]);
			payHeadDetail.setTransactionId(object[6] == null ? 0
					: (Long) object[6]);
			payHeadDetails.add(payHeadDetail);
		}
		return payHeadDetails;
	}

	public PaginationList<PaymentsList> getPayRunsList(Long companyId,
			long startDate, long endDate, int type, int start, int length) {
		PaginationList<PaymentsList> payrunsList = new PaginationList<PaymentsList>();
		Session currentSession = HibernateUtil.getCurrentSession();
		Query query = currentSession.getNamedQuery("getPayrunsList")
				.setParameter("companyId", companyId)
				.setParameter("type", type)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate);
		List list = query.list();
		int total = 0;
		if (length == -1) {
			list = query.list();
		} else {
			total = query.list().size();
			list = query.setFirstResult(start).setMaxResults(length).list();
		}
		if (list != null && !list.isEmpty()) {
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				Object[] object = (Object[]) iterator.next();
				PaymentsList paymentsList = new PaymentsList();
				paymentsList.setName((String) object[0]);
				paymentsList.setTransactionId((Long) object[1]);
				paymentsList.setPaymentNumber((String) object[2]);
				paymentsList.setPaymentDate(new ClientFinanceDate(
						(Long) object[3]));
				paymentsList.setAmountPaid((Double) object[4]);
				paymentsList.setVoided((Boolean) object[5]);
				paymentsList.setType(ClientTransaction.TYPE_PAY_RUN);
				payrunsList.add(paymentsList);
			}
		}
		payrunsList.setTotalCount(total);
		payrunsList.setStart(start);
		return payrunsList;
	}

	public ArrayList<PaySlipDetail> getPaySlipDetail(long employeeId,
			FinanceDate start, FinanceDate end, Long companyId) {
		Session session = HibernateUtil.getCurrentSession();

		Query attQuery = session.getNamedQuery("getAttendanceDetials")
				.setParameter("companyId", companyId)
				.setParameter("employeeId", employeeId)
				.setParameter("start", start.getDate())
				.setParameter("end", end.getDate());

		List list = attQuery.list();

		Iterator iterator = list.iterator();
		ArrayList<PaySlipDetail> queryResult = new ArrayList<PaySlipDetail>();

		while (iterator.hasNext()) {
			PaySlipDetail paySlipDetail = new PaySlipDetail();
			Object[] object = (Object[]) iterator.next();

			paySlipDetail.setId((Long) object[0]);
			paySlipDetail.setName((String) object[1]);
			paySlipDetail.setAmount((Double) object[2]);
			paySlipDetail.setPeriodType((Integer) object[3]);
			paySlipDetail.setType((Integer) object[4]);
			paySlipDetail.setEmployeeId((Long) object[5]);
			queryResult.add(paySlipDetail);
		}

		Query payheadQuery = session.getNamedQuery("getPayHeadDetials")
				.setParameter("companyId", companyId)
				.setParameter("employeeId", employeeId)
				.setParameter("start", start.getDate())
				.setParameter("end", end.getDate());

		list = payheadQuery.list();

		iterator = list.iterator();

		while (iterator.hasNext()) {
			PaySlipDetail paySlipDetail = new PaySlipDetail();
			Object[] object = (Object[]) iterator.next();

			paySlipDetail.setId((Long) object[0]);
			paySlipDetail.setName((String) object[1]);
			paySlipDetail.setAmount((Double) object[2]);
			paySlipDetail.setType((Integer) object[3]);

			queryResult.add(paySlipDetail);
		}

		return queryResult;
	}
}