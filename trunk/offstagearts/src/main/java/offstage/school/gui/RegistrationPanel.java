/*
OffstageArts: Enterprise Database for Arts Organizations
This file Copyright (c) 2005-2007 by Robert Fischer

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
/*
 * SchoolPanel.java
 *
 * Created on August 9, 2007, 11:41 AM
 */

package offstage.school.gui;

import citibob.jschema.*;
import citibob.swing.table.*;
import citibob.swing.typed.*;
import citibob.task.*;
import javax.swing.JOptionPane;
import offstage.*;
import citibob.sql.pgsql.*;
import citibob.sql.*;
import static citibob.swing.typed.TypedWidgetBinder.*;
import offstage.schema.*;
import citibob.wizard.*;
import javax.swing.*;
import citibob.swing.*;
import offstage.accounts.gui.*;
import java.awt.*;
import offstage.db.*;
import offstage.reports.*;
import citibob.types.*;
//import citibob.swingers.*;
import citibob.util.IntVal;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import offstage.school.tuition.RBPlan;
import offstage.school.tuition.RBPlanSet;
import offstage.school.tuition.TuitionCalc;

/**
 *
 * @author  citibob
 */
public class RegistrationPanel extends javax.swing.JPanel
{

FrontApp fapp;
SchoolModel smod;

//public JoinedSchemaBufDbModel enrolledDb;
public EnrolledDbModel enrolledDb;
//public IntKeyedDbModel actransDb;


RBPlanSet rbPlanSet;
// ====================================================
AllStudentDbModel allStudent;
AllPayerDbModel allPayer;
AllParentDbModel allParent1;
AllParentDbModel allParent2;
AllRecDbModel allRec;

// ====================================================
// ====================================================

//int schoolModel.getTermID(){
//	Integer Termid = (Integer)vTermID.getValue();
//	return (Termid == null ? -1 : Termid);
//}
/** Creates new form SchoolPanel */
public RegistrationPanel()
{
	initComponents();
}

// =====================================================
class EnrolledDbModel extends SqlBufDbModel
{
	int termID, studentID;
	public EnrolledDbModel(SqlRun str) {
		super(str, fapp,
		new String[] {"courseids", "enrollments"},
		null,
		new String[] {"enrollments"});
	}
	public String getSelectSql(boolean proto) {
		return
			" select e.*,c.name,c.dayofweek,c.tstart,c.tnext" +
			" from enrollments e, courseids c" +
			" where e.courseid = c.courseid" +
			" and c.termid = " + SqlInteger.sql(termID) + //smod.getTermID()) +
			(proto ? " and false" : " and e.entityid = " + SqlInteger.sql(studentID)) + //smod.getStudentID())) +
			" order by dayofweek, tstart, name";
	}
}
// =====================================================
class AllStudentDbModel extends MultiDbModel
{
	public AllStudentDbModel()
		{ super(smod.studentDm, smod.termregsDm, enrolledDb); }
	public void setStudentID(Integer studentid)
	{
		smod.studentDm.setKey(studentid);
		smod.termregsDm.setKey("entityid", studentid);
		enrolledDb.studentID = studentid;
	}
	public Integer getStudentID()
		{ return (Integer)smod.studentDm.getKey(); }
	public void setTermID(Integer termid)
	{
		smod.termregsDm.setKey("groupid", termid);	
		enrolledDb.termID = termid;
	}
	public void resetStudentID(SqlRun str, Integer studentid)
	{
		doUpdate(str);
		setStudentID(studentid);
		doSelect(str);
	}
	public void resetTermID(SqlRun str, Integer termid)
	{
		doUpdate(str);
		setTermID(termid);
		doSelect(str);
	}
}
class AllPayerDbModel extends MultiDbModel
{
	public boolean valueChanged()
	{
		return super.valueChanged() || transRegister.getDbModel().valueChanged();
	}
	public AllPayerDbModel()
		{super(smod.payerDm, smod.payertermregsDm); }
//		transRegister.getDbModel()); }
	public void doSelect(SqlRun str)
	{
		super.doSelect(str);
		familyTable.setPrimaryEntityID(str, getPayerID());
		transRegister.refresh(str);
	}
	public void doUpdate(SqlRun str)
	{
		super.doUpdate(str);
		// Update account transaction edits
		transRegister.getDbModel().doUpdate(str);
	}
	public void setPayerID(Integer payerid)
	{
//		Integer oldPayerID = getPayerID();
//		if (payerid == oldPayerID) return;
//		if (oldPayerID != null && payerid != null && payerid.equals(oldPayerID)) return;
		smod.payerDm.setKey(payerid);
		smod.payertermregsDm.setKey("entityid", payerid);
		transRegister.getDbModel().setKey("entityid", payerid);
		familyTable.setPrimaryEntityID(fapp.sqlRun(), payerid);
	}
	public Integer getPayerID()
		{ return (Integer)smod.payerDm.getKey(); }
	public void setTermID(Integer termid)
	{
		smod.payertermregsDm.setKey("termid", termid);	
	}
	public void resetPayerID(SqlRun str, Integer payerid)
	{
		doUpdate(str);
		setPayerID(payerid);
		doSelect(str);
	}
	public void resetTermID(SqlRun str, Integer termid)
	{
		doUpdate(str);
		setTermID(termid);
		doSelect(str);
	}
}
class AllParentDbModel extends MultiDbModel
{
	public AllParentDbModel(DbModel model)
		{ super(model); }
	public void setParentID(Integer parentid)
	{
		getModel(0).setKey(parentid);
	}
	public void resetParentID(SqlRun str, Integer parentid)
	{
		doUpdate(str);
		setParentID(parentid);
		doSelect(str);		
	}
	
}
class AllRecDbModel extends MultiDbModel
{
	public AllRecDbModel()
		{ super(allStudent, allPayer, allParent1, allParent2); }
//	boolean changed;
//	public boolean valueChanged()
//	{
//		return changed || super.valueChanged();
//	}
	public void setStudentID(Integer studentid)
	{
		allStudent.setStudentID(studentid);
		// The rest will wait for doSelect() below
	}
	public void setTermID(Integer termid)
	{
		allStudent.setTermID(termid);
		allPayer.setTermID(termid);		
	}
	public void resetTermID(SqlRun str, Integer termid)
	{
		allStudent.resetTermID(str, termid);
		allPayer.resetTermID(str, termid);
	}
	public void doSelect(SqlRun str)
	{
		refreshRBPlanSet(str);
		
		allStudent.doSelect(str);
		str.execUpdate(new UpdTasklet2() {
		public void run(SqlRun str) throws Exception {
			allPayer.setPayerID(smod.getPayerID());
			allParent1.setParentID(smod.getParent1ID());
			allParent2.setParentID(smod.getParent2ID());
			
			allPayer.doSelect(str);
			allParent1.doSelect(str);
			allParent2.doSelect(str);
		}});
	}
	void superDoUpdate(SqlRun str)
		{ super.doUpdate(str); }
	public void doUpdate(SqlRun str) {
		if (!valueChanged()) return;
		forceUpdate(str);
	}
	public void forceUpdate(SqlRun str) {
//		if (!valueChanged()) return;

		if (smod.studentRm.get("parent1id") == null || smod.termregsRm.get("payerid") == null) {
			JOptionPane.showMessageDialog(RegistrationPanel.this,
				"Cannot save record.  You must have a payer\nand parent in order to save.");
			return;
		}

		// Make sure payer has record in school system
		Integer payerid = (Integer)smod.termregsRm.get("payerid");
		if (payerid != null) str.execSql(SchoolDB.registerPayerSql(smod.getTermID(), payerid));

		// Transfer main parent over as primary entity id (family relationships)
		// Get household from parent1
		final IntVal primaryentityid = offstage.db.DB.getPrimaryEntityID(str, (Integer)smod.studentRm.get("parent1id"));
		str.execUpdate(new UpdTasklet2() {
		public void run(SqlRun str) throws Exception {
			// Setting parent results in setting household info.
			smod.studentRm.set("primaryentityid", primaryentityid.val);

			// Do the rest
			superDoUpdate(str);

			calcTuition(str);
		}});
	}
}

public void calcTuition(SqlRun str)
{
	// Calculate the tuition
	int col = smod.studentRm.findColumn("parent1id");
	Integer Oldparent1id = (Integer)smod.studentRm.getOrigValue(col);
	Integer Parent1id = (Integer)smod.studentRm.get(col);

	int termid = smod.getTermID();
	String payerIdSql = null;
	if (Oldparent1id != null && Parent1id != null) {
		if (Oldparent1id.intValue() == Parent1id.intValue()) {
			// Didn't change, they're both the same
			payerIdSql = "select " + Oldparent1id;
		} else {
			// Changed from one payer to another
			payerIdSql = "select " + Oldparent1id + " union select " + Parent1id;
		}
	} else if (Parent1id != null) {
		// Changed from no payer to a payer
		payerIdSql = "select " + Parent1id;
	} else if (Oldparent1id != null) {
		// Changed from a payer to no payer.
		payerIdSql = "select " + Oldparent1id;
	}
	if (payerIdSql != null) {
		TuitionCalc tc = new TuitionCalc(fapp, termid);
			tc.setPayerIDs(payerIdSql);
			tc.recalcTuition(str);
	}
}

// =====================================================

public void initRuntime(SqlRun str, FrontApp xfapp, SchoolModel xschoolModel)
//throws SQLException
{
	this.fapp = xfapp;
	this.smod = xschoolModel;

	// ================================================================
	// Account Transactions
	SqlSchema actransSchema = fapp.getSchema("actrans");
	final int createdCol = actransSchema.findCol("datecreated");
	final int tableoidCol = actransSchema.findCol("tableoid");
	SchemaBuf actransSb = new SchemaBuf(actransSchema) {
	public boolean isCellEditable(int row, int col) {
		if (col >= getColumnCount()) return false;
		if (row >= getRowCount()) return false;
		if (col == tableoidCol) return false;
		java.util.Date created = (java.util.Date)getValueAt(row, createdCol);
		if (created == null) return false;
		java.util.Date now = new java.util.Date();
		return (now.getTime() - created.getTime() < 86400 * 1000L);
	}};
	transRegister.initRuntime(fapp, actransSb, ActransSchema.AC_SCHOOL, false);

	// =====================================================================
	// Enrollments
	enrolledDb = new EnrolledDbModel(str);
//	enrolledDb = new SqlBufDbModel(str, fapp,
//		new String[] {"courseids", "enrollments"},
//		null,
//		new String[] {"enrollments"}) {
//	int termID, studentID;
//	public String getSelectSql(boolean proto) {
//		return
//			" select e.*,c.name,c.dayofweek,c.tstart,c.tnext" +
//			" from enrollments e, courseids c" +
//			" where e.courseid = c.courseid" +
//			" and c.termid = " + SqlInteger.sql(termID) + //smod.getTermID()) +
//			(proto ? " and false" : " and e.entityid = " + SqlInteger.sql(studentID)) + //smod.getStudentID())) +
//			" order by dayofweek, tstart, name";
//	}};

	// =============================================
	// Payer Group
	familyTable.initRuntime(fapp);

	// =====================================================================
	// Enrollments
	str.execUpdate(new UpdTasklet2() {
	public void run(SqlRun str) {
		RSSchema schema = (RSSchema)enrolledDb.getSchemaBuf().getSchema();
//		schema.setJTypes(fapp.getSchema("courseids"));
//		schema.setJTypes(fapp.getSchema("enrollments"));
//		allStudent.add(enrolledDb);
		enrollments.setModelU(enrolledDb.getTableModel(),
			new String[] {"Course", "Day", "Start", "Finish",
				"Role", "Custom Start", "Custom End (+1)"},
			new String[] {"name", "dayofweek", "tstart", "tnext",
				"courserole", "dstart", "dend"},
			new boolean[] {false, false, false, false,
				true, true, true, false}, fapp.swingerMap());
		enrollments.setFormatU("dayofweek", new DayOfWeekKeyedModel());
	}});
	
	// ==============================================================
	// Set up the basic model
	allStudent = new AllStudentDbModel();
	allPayer = new AllPayerDbModel();
	allParent1 = new AllParentDbModel(smod.parent1Dm);
	allParent2 = new AllParentDbModel(smod.parent2Dm);
	allRec = new AllRecDbModel();
	
	SwingerMap smap = fapp.swingerMap();

	// ===============================================================
	// Link events when an entityid changes
	smod.termregsRm.addColListener("payerid", new RowModel.ColAdapter() {
	public void valueChanged(int col) {
		if (allStudent.inSelect()) return;	// Only respond to widget changes
		Integer payerid = smod.getPayerID();
		allPayer.resetPayerID(fapp.sqlRun(), payerid);
	}});
	smod.studentRm.addColListener("parent1id", new RowModel.ColAdapter() {
	public void valueChanged(int col) {
		if (allStudent.inSelect()) return;	// Only respond to widget changes
		Integer parent1id = smod.getParent1ID();
		allParent1.resetParentID(fapp.sqlRun(), parent1id);
	}});
	smod.studentRm.addColListener("parent2id", new RowModel.ColAdapter() {
	public void valueChanged(int col) {
		if (allStudent.inSelect()) return;	// Only respond to widget changes
		Integer parent2id = smod.getParent2ID();
		allParent2.resetParentID(fapp.sqlRun(), parent2id);
	}});

		
	
	// ================================================================
	// Student
	// Display student info from persons table
	smod.termregsRm = new SchemaBufRowModel(smod.termregsDm.getSchemaBuf());
	smod.payertermregsRm = new SchemaBufRowModel(smod.payertermregsDm.getSchemaBuf());
	TypedWidgetBinder.bindRecursive(this.TermRegPanel, smod.termregsRm, smap);
	TypedWidgetBinder.bindRecursive(this.TermRegPanel, smod.payertermregsRm, smap);

	smod.studentRm = new SchemaBufRowModel(smod.studentDm.personDb.getSchemaBuf());
	new TypedWidgetBinder().bind(lEntityID, smod.studentRm, smap);
//	vHouseholdID.initRuntime(fapp);
//		new TypedWidgetBinder().bind(vHouseholdID, smod.studentRm, smap);
//	vStudentID.initRuntime(fapp);
	vStudentID.setJType(fapp.sqlRun());
		new TypedWidgetBinder().bind(vStudentID, smod.studentRm, smap);
	KeyedModel gmodel = new KeyedModel();
		gmodel.addItem(null, "<Unknown>");
		gmodel.addItem("M", "Male");
		gmodel.addItem("F", "Female");
		gender.setKeyedModel(gmodel);
		new TypedWidgetBinder().bind(gender, smod.studentRm, "gender", BT_READWRITE);
//		new TypedWidgetBinder().bind(familyTable, smod.studentRm, "primaryentityid", BT_READ);
	TypedWidgetBinder.bindRecursive(StudentTab, smod.studentRm, smap);

	// Initialize dropdowns when student changes.
	smod.studentRm.addColListener("entityid", new RowModel.ColAdapter() {
		// Do nothing when user just changes values; it must be saved first.
		// public void valueChanged(int col) {}
		// Do something when user moves to a different student
		public void curRowChanged(final int col) {
			SqlRun str = fapp.sqlRun();
				Integer ID = (Integer)smod.studentRm.get(col);
				if (ID == null) return;
				String lastname = (String)smod.studentRm.get(smod.studentRm.findColumn("lastname"));
				vPayerID.setSearch(str, lastname);
				vParent1ID.setSearch(str, lastname);
				vParent2ID.setSearch(str, lastname);
//			}});
		}
	});

	// Change person when user clicks on family...
	familyTable.addPropertyChangeListener("value", new PropertyChangeListener() {
	public void propertyChange(final PropertyChangeEvent evt) {
		fapp.guiRun().run(RegistrationPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			Integer EntityID = (Integer)evt.getNewValue();
			if (EntityID == null) return;
			changeStudent(str, EntityID);
		}});
	}});

	// Display names of related entities
	vParent1ID.initRuntime(fapp);
		new TypedWidgetBinder().bind(vParent1ID, smod.studentRm, smap);
	vParent2ID.initRuntime(fapp);
		new TypedWidgetBinder().bind(vParent2ID, smod.studentRm, smap);
	vPayerID.initRuntime(fapp);
		new TypedWidgetBinder().bind(vPayerID, smod.termregsRm, smap);

	// ================================================================
	// Payer
	TypedWidgetBinder.bindRecursive(PayerPanel, smod.payerRm, smap);
	payerPhonePanel.initRuntime(str, smod.payerDm.phoneDb.getSchemaBuf(),
			new String[] {"Type", "Number"},
			new String[] {"groupid", "phone"}, true, smap);
	payerCCInfo.initRuntime(fapp.keyRing());
	RowModel.ColListener payerCCListener = new RowModel.ColAdapter() {
		public void valueChanged(final int col) {
			String xzip = (String)smod.payerRm.get("zip");
				if (xzip != null && xzip.length() > 5) xzip = xzip.substring(0,5);
			String xname = (String)smod.payerRm.get("firstname") + " " + (String)smod.payerRm.get("lastname");
				xname = xname.toUpperCase();
			payerCCInfo.setDefaults(xname, xzip);
		}};
	smod.payerRm.addColListener("firstname", payerCCListener);
	smod.payerRm.addColListener("lastname", payerCCListener);
	smod.payerRm.addColListener("zip", payerCCListener);

	
	// Bind our account actions
	ActionJobBinder tbinder = new ActionJobBinder(
		this, fapp.guiRun(), transRegister.getTaskMap());
	tbinder.bind(this.bCash, "cash");
	tbinder.bind(this.bCheck, "check");
	tbinder.bind(this.bCc, "cc");
	tbinder.bind(this.bOtherTrans, "other");
	
	// ===============================================================
	// Parents
	TypedWidgetBinder.bindRecursive(ParentPanel, smod.parent1Rm, smap);
	ParentPhonePanel.initRuntime(str, smod.parent1Dm.phoneDb.getSchemaBuf(),
			new String[] {"Type", "Number"},
			new String[] {"groupid", "phone"}, true, smap);
//	SchemaBufRowModel parent2Rm = new SchemaBufRowModel(smod.parent2Dm.personDb.getSchemaBuf());
	TypedWidgetBinder.bindRecursive(Parent2Panel, smod.parent2Rm, smap);
	Parent2PhonePanel.initRuntime(str, smod.parent2Dm.phoneDb.getSchemaBuf(),
			new String[] {"Type", "Number"},
			new String[] {"groupid", "phone"}, true, smap);
	
	// ================================================================
	// Global Stuff
	// Edit another student
	searchBox.initRuntime(fapp);
	searchBox.addPropertyChangeListener("value", new PropertyChangeListener() {
	public void propertyChange(PropertyChangeEvent evt) {
//		fapp.runApp(new BatchRunnable() {
//		public void run(SqlRun str) throws Exception {
		SqlRun str = fapp.sqlRun();
			Integer EntityID = (Integer)searchBox.getValue();
			if (EntityID == null) return;
			changeStudent(str, EntityID);
	}});

	

	smod.addListener(new SchoolModel.Adapter() {
    public void termIDChanged(int oldTermID, int termID)  {
		allRec.setTermID(termID);

		SqlRun str = fapp.sqlRun();
		Integer eid = (Integer)smod.studentRm.get("entityid");
		if (eid != null) {
			// Ensure a registration record for this term
			str.execSql(SchoolDB.registerStudentSql(termID, eid, fapp.sqlTypeSet().date()));

			allRec.doUpdate(str);
			allRec.doSelect(str);
		}
	}});
//	smod.fireTermIDChanged(smod.getTermID(), smod.getTermID());
//	smod.setTermID(smod.getTermID());
	
	fapp.dbChange().addListener("termids", new DbChangeModel.Listener() {
    public void tableWillChange(SqlRun str, String table) {
		smod.oneTermDm.doSelect(str);
		refreshRBPlanSet(str);
	}});
}

void refreshRBPlanSet(SqlRun str)
{
	int termid = smod.getTermID();
	String sql = "select rbplansetclass from termids where groupid = " + termid;
	
	// Make sure we have our Tuition Plans in place
	str.execSql(sql, new RsTasklet2() {
	public void run(SqlRun str, ResultSet rs) throws Exception {
		rs.next();
		String className = rs.getString("rbplansetclass");
		boolean good = true;

		// Set up rbPlanSet
//		String className = (String)smod.oneTermRm.get("rbplansetclass");
		if (className != null) {
			Class klass = fapp.siteCode().loadClass(className);
			if (rbPlanSet == null || rbPlanSet.getClass() != klass) {
				rbPlanSet = (RBPlanSet)klass.newInstance();
			}

			// Make a dropdown for billing types
			RBPlan[] plans = rbPlanSet.getPlans();
			KeyedModel kmodel = new KeyedModel();
			kmodel.addItem(null, "Default (" + rbPlanSet.getDefPlan().getName() + ")");
			for (int i=0; i<plans.length; ++i) {
				kmodel.addItem(plans[i].getKey(), plans[i].getName());
			}
//				KeyedModel kmodel = KeyedModel.sameKeys(names);
			rbPlans.setKeyedModel(kmodel);
			rbPlans.setEnabled(true);
		} else {
			// No tuition plan for this term.
			// OffstageArts will not bill!
			rbPlanSet = null;
			rbPlans.setKeyedModel(KeyedModel.sameKeys(new Object[0]));
			rbPlans.setEnabled(false);
		}

	}});
}

	

public void changeStudent(SqlRun str, Integer entityid)// throws SQLException
{
	// See if old student needs saving...
	if (allRec.valueChanged()) {
		String[] options = new String[] {"Save", "Don't Save", "Cancel"};
		JOptionPane pane = new JOptionPane(
			"You have not yet saved the current record.\n" +
		"Would you like to save before moving on?",
			JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION,
			null, options, options[0]);
        JDialog dialog = pane.createDialog(RegistrationPanel.this, "Student not Saved");

        //pane.selectInitialValue();
        dialog.setVisible(true);
        dialog.dispose();
		
		if (pane.getValue() == options[0]) {
			allRec.doUpdate(str);		// Save
		} else if (pane.getValue() == options[1]) {
		} else {
			return;		// cancel
		}
	}
	
	String sql =
		// Ensure a registration record for this term
		SchoolDB.registerStudentSql(smod.getTermID(), entityid, fapp.sqlTypeSet().date()) + "\n;";
	str.execSql(sql);

	// Go to that record
	allRec.setStudentID(entityid);
	allRec.doSelect(str);
}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel20 = new javax.swing.JPanel();
        cardPanel = new javax.swing.JPanel();
        PeopleMain = new javax.swing.JPanel();
        EnrollmentTab = new javax.swing.JTabbedPane();
        jPanel12 = new javax.swing.JPanel();
        GroupScrollPanel = new javax.swing.JScrollPane();
        enrollments = new citibob.jschema.swing.StatusTable();
        jPanel15 = new javax.swing.JPanel();
        bAddEnrollment = new javax.swing.JButton();
        bRemoveEnrollment = new javax.swing.JButton();
        jPanel17 = new javax.swing.JPanel();
        AdultTabs = new javax.swing.JTabbedPane();
        PayerPanel = new javax.swing.JPanel();
        FirstMiddleLast = new javax.swing.JPanel();
        lFirst = new javax.swing.JLabel();
        lMiddle = new javax.swing.JLabel();
        lLast = new javax.swing.JLabel();
        salutation = new citibob.swing.typed.JTypedTextField();
        firstname = new citibob.swing.typed.JTypedTextField();
        middlename = new citibob.swing.typed.JTypedTextField();
        lastname = new citibob.swing.typed.JTypedTextField();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        payerPhonePanel = new offstage.gui.GroupPanel();
        jPanel5 = new javax.swing.JPanel();
        payerCCInfo = new offstage.swing.typed.CryptCCInfo();
        jLabel16 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        entityid = new citibob.swing.typed.JTypedTextField();
        lastupdated = new citibob.swing.typed.JTypedTextField();
        addressPanel = new javax.swing.JPanel();
        address1 = new citibob.swing.typed.JTypedTextField();
        address2 = new citibob.swing.typed.JTypedTextField();
        city = new citibob.swing.typed.JTypedTextField();
        state = new citibob.swing.typed.JTypedTextField();
        zip = new citibob.swing.typed.JTypedTextField();
        jLabel6 = new javax.swing.JLabel();
        EmailPanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        email1 = new citibob.swing.typed.JTypedTextField();
        bLaunchEmail = new javax.swing.JButton();
        Org = new javax.swing.JPanel();
        lFirst5 = new javax.swing.JLabel();
        orgname = new citibob.swing.typed.JTypedTextField();
        isorg = new citibob.swing.typed.JBoolCheckbox();
        ParentPanel = new javax.swing.JPanel();
        FirstMiddleLast3 = new javax.swing.JPanel();
        lFirst3 = new javax.swing.JLabel();
        lMiddle3 = new javax.swing.JLabel();
        lLast3 = new javax.swing.JLabel();
        salutation3 = new citibob.swing.typed.JTypedTextField();
        firstname3 = new citibob.swing.typed.JTypedTextField();
        middlename3 = new citibob.swing.typed.JTypedTextField();
        lastname3 = new citibob.swing.typed.JTypedTextField();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel18 = new javax.swing.JPanel();
        ParentPhonePanel = new offstage.gui.GroupPanel();
        jPanel21 = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        entityid2 = new citibob.swing.typed.JTypedTextField();
        lastupdated2 = new citibob.swing.typed.JTypedTextField();
        addressPanel2 = new javax.swing.JPanel();
        address5 = new citibob.swing.typed.JTypedTextField();
        address6 = new citibob.swing.typed.JTypedTextField();
        city2 = new citibob.swing.typed.JTypedTextField();
        state2 = new citibob.swing.typed.JTypedTextField();
        zip2 = new citibob.swing.typed.JTypedTextField();
        jLabel24 = new javax.swing.JLabel();
        EmailPanel2 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        email3 = new citibob.swing.typed.JTypedTextField();
        bLaunchEmail2 = new javax.swing.JButton();
        Parent2Panel = new javax.swing.JPanel();
        FirstMiddleLast4 = new javax.swing.JPanel();
        lFirst4 = new javax.swing.JLabel();
        lMiddle4 = new javax.swing.JLabel();
        lLast4 = new javax.swing.JLabel();
        salutation4 = new citibob.swing.typed.JTypedTextField();
        firstname4 = new citibob.swing.typed.JTypedTextField();
        middlename4 = new citibob.swing.typed.JTypedTextField();
        lastname4 = new citibob.swing.typed.JTypedTextField();
        jTabbedPane5 = new javax.swing.JTabbedPane();
        jPanel23 = new javax.swing.JPanel();
        Parent2PhonePanel = new offstage.gui.GroupPanel();
        jPanel25 = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        entityid3 = new citibob.swing.typed.JTypedTextField();
        lastupdated3 = new citibob.swing.typed.JTypedTextField();
        addressPanel3 = new javax.swing.JPanel();
        address7 = new citibob.swing.typed.JTypedTextField();
        address8 = new citibob.swing.typed.JTypedTextField();
        city3 = new citibob.swing.typed.JTypedTextField();
        state3 = new citibob.swing.typed.JTypedTextField();
        zip3 = new citibob.swing.typed.JTypedTextField();
        jLabel28 = new javax.swing.JLabel();
        EmailPanel3 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        email4 = new citibob.swing.typed.JTypedTextField();
        bLaunchEmail3 = new javax.swing.JButton();
        StudentAccounts = new javax.swing.JPanel();
        StudentTab = new javax.swing.JTabbedPane();
        StudentPane = new javax.swing.JPanel();
        FirstMiddleLast2 = new javax.swing.JPanel();
        lFirst2 = new javax.swing.JLabel();
        lMiddle2 = new javax.swing.JLabel();
        lLast2 = new javax.swing.JLabel();
        salutation2 = new citibob.swing.typed.JTypedTextField();
        firstname2 = new citibob.swing.typed.JTypedTextField();
        middlename2 = new citibob.swing.typed.JTypedTextField();
        lastname2 = new citibob.swing.typed.JTypedTextField();
        dob = new citibob.swing.typed.JTypedDateChooser();
        jLabel17 = new javax.swing.JLabel();
        gender = new citibob.swing.typed.JKeyedComboBox();
        lGender = new javax.swing.JLabel();
        AccountTab = new javax.swing.JTabbedPane();
        AccountPane = new javax.swing.JPanel();
        controller1 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        bCash = new javax.swing.JButton();
        bCheck = new javax.swing.JButton();
        bCc = new javax.swing.JButton();
        bOtherTrans = new javax.swing.JButton();
        transRegister = new offstage.accounts.gui.TransRegPanel();
        jTabbedPane6 = new javax.swing.JTabbedPane();
        FamilyScrollPanel = new javax.swing.JScrollPane();
        familyTable = new offstage.school.gui.SchoolFamilySelectorTable();
        jPanel19 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        ObsoleteStuff = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        bEmancipate = new javax.swing.JButton();
        bNewHousehold = new javax.swing.JButton();
        HouseholdPanel = new javax.swing.JPanel();
        FirstMiddleLast1 = new javax.swing.JPanel();
        lFirst1 = new javax.swing.JLabel();
        lMiddle1 = new javax.swing.JLabel();
        lLast1 = new javax.swing.JLabel();
        salutation1 = new citibob.swing.typed.JTypedTextField();
        firstname1 = new citibob.swing.typed.JTypedTextField();
        middlename1 = new citibob.swing.typed.JTypedTextField();
        lastname1 = new citibob.swing.typed.JTypedTextField();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel8 = new javax.swing.JPanel();
        householdPhonePanel = new offstage.gui.GroupPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        entityid1 = new citibob.swing.typed.JTypedTextField();
        lastupdated1 = new citibob.swing.typed.JTypedTextField();
        addressPanel1 = new javax.swing.JPanel();
        address3 = new citibob.swing.typed.JTypedTextField();
        address4 = new citibob.swing.typed.JTypedTextField();
        city1 = new citibob.swing.typed.JTypedTextField();
        state1 = new citibob.swing.typed.JTypedTextField();
        zip1 = new citibob.swing.typed.JTypedTextField();
        jLabel12 = new javax.swing.JLabel();
        EmailPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        email2 = new citibob.swing.typed.JTypedTextField();
        bLaunchEmail1 = new javax.swing.JButton();
        PeopleHeader = new javax.swing.JPanel();
        PeopleHeader1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        vPayerID = new offstage.swing.typed.EntityIDEditableLabel();
        jToolBar1 = new javax.swing.JToolBar();
        bSave = new javax.swing.JButton();
        bUndo = new javax.swing.JButton();
        bRecalcTuition = new javax.swing.JButton();
        vStudentID = new offstage.swing.typed.EntityIDLabel();
        lEntityID = new citibob.swing.typed.JTypedLabel();
        bNewStudent = new javax.swing.JButton();
        bNewPayer = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        vParent1ID = new offstage.swing.typed.EntityIDEditableLabel();
        bNewParent = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        vParent2ID = new offstage.swing.typed.EntityIDEditableLabel();
        bNewParent2 = new javax.swing.JButton();
        TermRegPanel = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        lDtregistered = new citibob.swing.typed.JTypedLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        programs = new citibob.swing.typed.JKeyedComboBox();
        jLabel19 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        lTuition1 = new citibob.swing.typed.JTypedLabel();
        jLabel33 = new javax.swing.JLabel();
        tuitionOverride = new citibob.swing.typed.JTypedTextField();
        scholarship = new citibob.swing.typed.JTypedTextField();
        dtSigned = new citibob.swing.typed.JTypedDateChooser();
        jLabel18 = new javax.swing.JLabel();
        rbPlans = new citibob.swing.typed.JKeyedComboBox();
        searchBox = new offstage.swing.typed.EntitySelector();

        setBackground(new java.awt.Color(102, 255, 51));
        setLayout(new java.awt.BorderLayout());

        jPanel20.setLayout(new java.awt.BorderLayout());

        cardPanel.setLayout(new java.awt.CardLayout());

        PeopleMain.setPreferredSize(new java.awt.Dimension(595, 480));
        PeopleMain.setLayout(new java.awt.GridBagLayout());

        EnrollmentTab.setMinimumSize(new java.awt.Dimension(306, 184));
        EnrollmentTab.setPreferredSize(new java.awt.Dimension(458, 184));

        jPanel12.setLayout(new java.awt.BorderLayout());

        enrollments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String []
            {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        enrollments.setMinimumSize(new java.awt.Dimension(60, 264));
        enrollments.setPreferredSize(new java.awt.Dimension(300, 264));
        GroupScrollPanel.setViewportView(enrollments);

        jPanel12.add(GroupScrollPanel, java.awt.BorderLayout.CENTER);

        bAddEnrollment.setText("Add Enrollment");
        bAddEnrollment.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bAddEnrollmentActionPerformed(evt);
            }
        });
        jPanel15.add(bAddEnrollment);

        bRemoveEnrollment.setText("Remove Enrollment");
        bRemoveEnrollment.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bRemoveEnrollmentActionPerformed(evt);
            }
        });
        jPanel15.add(bRemoveEnrollment);

        jPanel12.add(jPanel15, java.awt.BorderLayout.SOUTH);

        EnrollmentTab.addTab("Enrollments", jPanel12);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        PeopleMain.add(EnrollmentTab, gridBagConstraints);

        jPanel17.setMinimumSize(new java.awt.Dimension(400, 296));
        jPanel17.setPreferredSize(new java.awt.Dimension(400, 300));
        jPanel17.setLayout(new java.awt.GridBagLayout());

        AdultTabs.setMaximumSize(new java.awt.Dimension(295, 32767));
        AdultTabs.setPreferredSize(new java.awt.Dimension(295, 294));

        PayerPanel.setLayout(new java.awt.GridBagLayout());

        FirstMiddleLast.setMinimumSize(new java.awt.Dimension(290, 34));
        FirstMiddleLast.setPreferredSize(new java.awt.Dimension(217, 34));
        FirstMiddleLast.setLayout(new java.awt.GridBagLayout());

        lFirst.setText("First");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast.add(lFirst, gridBagConstraints);

        lMiddle.setText("Mid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast.add(lMiddle, gridBagConstraints);

        lLast.setText("Last");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast.add(lLast, gridBagConstraints);

        salutation.setColName("salutation");
        salutation.setPreferredSize(new java.awt.Dimension(40, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        FirstMiddleLast.add(salutation, gridBagConstraints);

        firstname.setColName("firstname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        FirstMiddleLast.add(firstname, gridBagConstraints);

        middlename.setColName("middlename");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        FirstMiddleLast.add(middlename, gridBagConstraints);

        lastname.setColName("lastname");
        lastname.setPreferredSize(new java.awt.Dimension(10, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        FirstMiddleLast.add(lastname, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        PayerPanel.add(FirstMiddleLast, gridBagConstraints);

        jTabbedPane2.setFont(new java.awt.Font("Dialog", 1, 10));

        payerPhonePanel.setPreferredSize(new java.awt.Dimension(453, 180));

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(payerPhonePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(payerPhonePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("Phone", jPanel4);

        jPanel5.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        jPanel5.add(payerCCInfo, gridBagConstraints);

        jLabel16.setText("Billing Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel5.add(jLabel16, gridBagConstraints);

        jTabbedPane2.addTab("Billing", jPanel5);

        jPanel7.setLayout(new java.awt.GridBagLayout());

        jLabel7.setText("ID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel7.add(jLabel7, gridBagConstraints);

        jLabel8.setText("Last Modified");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel7.add(jLabel8, gridBagConstraints);

        entityid.setEditable(false);
        entityid.setColName("entityid");
        entityid.setPreferredSize(new java.awt.Dimension(100, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel7.add(entityid, gridBagConstraints);

        lastupdated.setEditable(false);
        lastupdated.setColName("lastupdated");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel7.add(lastupdated, gridBagConstraints);

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(117, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Misc.", jPanel6);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        PayerPanel.add(jTabbedPane2, gridBagConstraints);

        addressPanel.setLayout(new java.awt.GridBagLayout());

        address1.setColName("address1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        addressPanel.add(address1, gridBagConstraints);

        address2.setColName("address2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        addressPanel.add(address2, gridBagConstraints);

        city.setColName("city");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        addressPanel.add(city, gridBagConstraints);

        state.setColName("state");
        state.setPreferredSize(new java.awt.Dimension(30, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        addressPanel.add(state, gridBagConstraints);

        zip.setColName("zip");
        zip.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        addressPanel.add(zip, gridBagConstraints);

        jLabel6.setText("Address / City,State,Zip");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        addressPanel.add(jLabel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        PayerPanel.add(addressPanel, gridBagConstraints);

        EmailPanel.setLayout(new java.awt.GridBagLayout());

        jLabel11.setText("Email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        EmailPanel.add(jLabel11, gridBagConstraints);

        email1.setColName("email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        EmailPanel.add(email1, gridBagConstraints);

        bLaunchEmail.setText("*");
        bLaunchEmail.setMargin(new java.awt.Insets(1, 1, 1, 1));
        bLaunchEmail.setPreferredSize(new java.awt.Dimension(14, 19));
        bLaunchEmail.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bLaunchEmailActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        EmailPanel.add(bLaunchEmail, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        PayerPanel.add(EmailPanel, gridBagConstraints);

        Org.setLayout(new java.awt.GridBagLayout());

        lFirst5.setText("Org Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        Org.add(lFirst5, gridBagConstraints);

        orgname.setColName("orgname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        Org.add(orgname, gridBagConstraints);

        isorg.setText("is Org?");
        isorg.setColName("isorg");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        Org.add(isorg, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        PayerPanel.add(Org, gridBagConstraints);

        AdultTabs.addTab("Payer", PayerPanel);

        ParentPanel.setLayout(new java.awt.GridBagLayout());

        FirstMiddleLast3.setLayout(new java.awt.GridBagLayout());

        lFirst3.setText("First");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast3.add(lFirst3, gridBagConstraints);

        lMiddle3.setText("Mid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast3.add(lMiddle3, gridBagConstraints);

        lLast3.setText("Last");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast3.add(lLast3, gridBagConstraints);

        salutation3.setColName("salutation");
        salutation3.setPreferredSize(new java.awt.Dimension(40, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        FirstMiddleLast3.add(salutation3, gridBagConstraints);

        firstname3.setColName("firstname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        FirstMiddleLast3.add(firstname3, gridBagConstraints);

        middlename3.setColName("middlename");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        FirstMiddleLast3.add(middlename3, gridBagConstraints);

        lastname3.setColName("lastname");
        lastname3.setPreferredSize(new java.awt.Dimension(10, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        FirstMiddleLast3.add(lastname3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        ParentPanel.add(FirstMiddleLast3, gridBagConstraints);

        jTabbedPane4.setFont(new java.awt.Font("Dialog", 1, 10));

        ParentPhonePanel.setPreferredSize(new java.awt.Dimension(453, 180));

        org.jdesktop.layout.GroupLayout jPanel18Layout = new org.jdesktop.layout.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ParentPhonePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ParentPhonePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jTabbedPane4.addTab("Phone", jPanel18);

        jPanel22.setLayout(new java.awt.GridBagLayout());

        jLabel22.setText("ID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel22.add(jLabel22, gridBagConstraints);

        jLabel23.setText("Last Modified");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel22.add(jLabel23, gridBagConstraints);

        entityid2.setEditable(false);
        entityid2.setColName("entityid");
        entityid2.setPreferredSize(new java.awt.Dimension(100, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel22.add(entityid2, gridBagConstraints);

        lastupdated2.setEditable(false);
        lastupdated2.setColName("lastupdated");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel22.add(lastupdated2, gridBagConstraints);

        org.jdesktop.layout.GroupLayout jPanel21Layout = new org.jdesktop.layout.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel21Layout.createSequentialGroup()
                .add(jPanel22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(153, Short.MAX_VALUE))
        );

        jTabbedPane4.addTab("Misc.", jPanel21);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        ParentPanel.add(jTabbedPane4, gridBagConstraints);

        addressPanel2.setLayout(new java.awt.GridBagLayout());

        address5.setColName("address1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        addressPanel2.add(address5, gridBagConstraints);

        address6.setColName("address2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        addressPanel2.add(address6, gridBagConstraints);

        city2.setColName("city");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        addressPanel2.add(city2, gridBagConstraints);

        state2.setColName("state");
        state2.setPreferredSize(new java.awt.Dimension(30, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        addressPanel2.add(state2, gridBagConstraints);

        zip2.setColName("zip");
        zip2.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        addressPanel2.add(zip2, gridBagConstraints);

        jLabel24.setText("Address / City,State,Zip");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        addressPanel2.add(jLabel24, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        ParentPanel.add(addressPanel2, gridBagConstraints);

        EmailPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel25.setText("Email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        EmailPanel2.add(jLabel25, gridBagConstraints);

        email3.setColName("email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        EmailPanel2.add(email3, gridBagConstraints);

        bLaunchEmail2.setText("*");
        bLaunchEmail2.setMargin(new java.awt.Insets(1, 1, 1, 1));
        bLaunchEmail2.setPreferredSize(new java.awt.Dimension(14, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        EmailPanel2.add(bLaunchEmail2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        ParentPanel.add(EmailPanel2, gridBagConstraints);

        AdultTabs.addTab("Parent 1", ParentPanel);

        Parent2Panel.setLayout(new java.awt.GridBagLayout());

        FirstMiddleLast4.setLayout(new java.awt.GridBagLayout());

        lFirst4.setText("First");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast4.add(lFirst4, gridBagConstraints);

        lMiddle4.setText("Mid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast4.add(lMiddle4, gridBagConstraints);

        lLast4.setText("Last");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast4.add(lLast4, gridBagConstraints);

        salutation4.setColName("salutation");
        salutation4.setPreferredSize(new java.awt.Dimension(40, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        FirstMiddleLast4.add(salutation4, gridBagConstraints);

        firstname4.setColName("firstname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        FirstMiddleLast4.add(firstname4, gridBagConstraints);

        middlename4.setColName("middlename");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        FirstMiddleLast4.add(middlename4, gridBagConstraints);

        lastname4.setColName("lastname");
        lastname4.setPreferredSize(new java.awt.Dimension(10, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        FirstMiddleLast4.add(lastname4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        Parent2Panel.add(FirstMiddleLast4, gridBagConstraints);

        jTabbedPane5.setFont(new java.awt.Font("Dialog", 1, 10));

        Parent2PhonePanel.setPreferredSize(new java.awt.Dimension(453, 180));

        org.jdesktop.layout.GroupLayout jPanel23Layout = new org.jdesktop.layout.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(Parent2PhonePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(Parent2PhonePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jTabbedPane5.addTab("Phone", jPanel23);

        jPanel26.setLayout(new java.awt.GridBagLayout());

        jLabel26.setText("ID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel26.add(jLabel26, gridBagConstraints);

        jLabel27.setText("Last Modified");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel26.add(jLabel27, gridBagConstraints);

        entityid3.setEditable(false);
        entityid3.setColName("entityid");
        entityid3.setPreferredSize(new java.awt.Dimension(100, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel26.add(entityid3, gridBagConstraints);

        lastupdated3.setEditable(false);
        lastupdated3.setColName("lastupdated");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel26.add(lastupdated3, gridBagConstraints);

        org.jdesktop.layout.GroupLayout jPanel25Layout = new org.jdesktop.layout.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel25Layout.createSequentialGroup()
                .add(jPanel26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(153, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("Misc.", jPanel25);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        Parent2Panel.add(jTabbedPane5, gridBagConstraints);

        addressPanel3.setLayout(new java.awt.GridBagLayout());

        address7.setColName("address1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        addressPanel3.add(address7, gridBagConstraints);

        address8.setColName("address2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        addressPanel3.add(address8, gridBagConstraints);

        city3.setColName("city");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        addressPanel3.add(city3, gridBagConstraints);

        state3.setColName("state");
        state3.setPreferredSize(new java.awt.Dimension(30, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        addressPanel3.add(state3, gridBagConstraints);

        zip3.setColName("zip");
        zip3.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        addressPanel3.add(zip3, gridBagConstraints);

        jLabel28.setText("Address / City,State,Zip");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        addressPanel3.add(jLabel28, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        Parent2Panel.add(addressPanel3, gridBagConstraints);

        EmailPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel29.setText("Email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        EmailPanel3.add(jLabel29, gridBagConstraints);

        email4.setColName("email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        EmailPanel3.add(email4, gridBagConstraints);

        bLaunchEmail3.setText("*");
        bLaunchEmail3.setMargin(new java.awt.Insets(1, 1, 1, 1));
        bLaunchEmail3.setPreferredSize(new java.awt.Dimension(14, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        EmailPanel3.add(bLaunchEmail3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        Parent2Panel.add(EmailPanel3, gridBagConstraints);

        AdultTabs.addTab("Parent 2", Parent2Panel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel17.add(AdultTabs, gridBagConstraints);

        StudentAccounts.setLayout(new java.awt.GridBagLayout());

        StudentTab.setMinimumSize(new java.awt.Dimension(300, 136));
        StudentTab.setPreferredSize(new java.awt.Dimension(300, 130));

        StudentPane.setLayout(new java.awt.GridBagLayout());

        FirstMiddleLast2.setLayout(new java.awt.GridBagLayout());

        lFirst2.setText("First");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast2.add(lFirst2, gridBagConstraints);

        lMiddle2.setText("Mid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast2.add(lMiddle2, gridBagConstraints);

        lLast2.setText("Last");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast2.add(lLast2, gridBagConstraints);

        salutation2.setColName("salutation");
        salutation2.setPreferredSize(new java.awt.Dimension(40, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        FirstMiddleLast2.add(salutation2, gridBagConstraints);

        firstname2.setColName("firstname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        FirstMiddleLast2.add(firstname2, gridBagConstraints);

        middlename2.setColName("middlename");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        FirstMiddleLast2.add(middlename2, gridBagConstraints);

        lastname2.setColName("lastname");
        lastname2.setPreferredSize(new java.awt.Dimension(10, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        FirstMiddleLast2.add(lastname2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        StudentPane.add(FirstMiddleLast2, gridBagConstraints);

        dob.setColName("dob");
        dob.setPreferredSize(new java.awt.Dimension(122, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        StudentPane.add(dob, gridBagConstraints);

        jLabel17.setText("DOB: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        StudentPane.add(jLabel17, gridBagConstraints);

        gender.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gender.setColName("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        StudentPane.add(gender, gridBagConstraints);

        lGender.setText("Gender: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        StudentPane.add(lGender, gridBagConstraints);

        StudentTab.addTab("Student", StudentPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        StudentAccounts.add(StudentTab, gridBagConstraints);

        AccountTab.setMinimumSize(new java.awt.Dimension(82, 99));

        AccountPane.setPreferredSize(new java.awt.Dimension(484, 100));
        AccountPane.setLayout(new java.awt.BorderLayout());

        controller1.setLayout(new java.awt.BorderLayout());

        jPanel13.setPreferredSize(new java.awt.Dimension(484, 35));
        jPanel13.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel15.setText("Transaction:");
        jPanel13.add(jLabel15);

        bCash.setText("Cash");
        bCash.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jPanel13.add(bCash);

        bCheck.setText("Check");
        bCheck.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jPanel13.add(bCheck);

        bCc.setText("Credit");
        bCc.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jPanel13.add(bCc);

        bOtherTrans.setText("Other");
        bOtherTrans.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jPanel13.add(bOtherTrans);

        controller1.add(jPanel13, java.awt.BorderLayout.CENTER);

        AccountPane.add(controller1, java.awt.BorderLayout.SOUTH);
        AccountPane.add(transRegister, java.awt.BorderLayout.CENTER);

        AccountTab.addTab("Account History", AccountPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        StudentAccounts.add(AccountTab, gridBagConstraints);

        familyTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String []
            {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        FamilyScrollPanel.setViewportView(familyTable);

        jTabbedPane6.addTab("Payer Group", FamilyScrollPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        StudentAccounts.add(jTabbedPane6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel17.add(StudentAccounts, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.7;
        PeopleMain.add(jPanel17, gridBagConstraints);

        cardPanel.add(PeopleMain, "peopleCard");

        jPanel19.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Press the \"Save\" button to display student details.");
        jPanel19.add(jLabel1, new java.awt.GridBagConstraints());

        cardPanel.add(jPanel19, "blankCard");

        jLabel5.setText("Household:");
        ObsoleteStuff.add(jLabel5);

        bEmancipate.setText("Emancipate");
        bEmancipate.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bEmancipateActionPerformed(evt);
            }
        });
        ObsoleteStuff.add(bEmancipate);

        bNewHousehold.setText("New");
        bNewHousehold.setMargin(new java.awt.Insets(0, 0, 0, 0));
        bNewHousehold.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bNewHouseholdActionPerformed(evt);
            }
        });
        ObsoleteStuff.add(bNewHousehold);

        HouseholdPanel.setLayout(new java.awt.GridBagLayout());

        FirstMiddleLast1.setLayout(new java.awt.GridBagLayout());

        lFirst1.setText("First");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast1.add(lFirst1, gridBagConstraints);

        lMiddle1.setText("Mid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast1.add(lMiddle1, gridBagConstraints);

        lLast1.setText("Last");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        FirstMiddleLast1.add(lLast1, gridBagConstraints);

        salutation1.setColName("salutation");
        salutation1.setPreferredSize(new java.awt.Dimension(40, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        FirstMiddleLast1.add(salutation1, gridBagConstraints);

        firstname1.setColName("firstname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        FirstMiddleLast1.add(firstname1, gridBagConstraints);

        middlename1.setColName("middlename");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        FirstMiddleLast1.add(middlename1, gridBagConstraints);

        lastname1.setColName("lastname");
        lastname1.setPreferredSize(new java.awt.Dimension(10, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        FirstMiddleLast1.add(lastname1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        HouseholdPanel.add(FirstMiddleLast1, gridBagConstraints);

        jTabbedPane3.setFont(new java.awt.Font("Dialog", 1, 10));

        householdPhonePanel.setPreferredSize(new java.awt.Dimension(453, 180));

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(householdPhonePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(householdPhonePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jTabbedPane3.addTab("Phone", jPanel8);

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 143, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 2529, Short.MAX_VALUE)
        );

        jTabbedPane3.addTab("Family", jPanel9);

        jPanel11.setLayout(new java.awt.GridBagLayout());

        jLabel9.setText("ID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel11.add(jLabel9, gridBagConstraints);

        jLabel10.setText("Last Modified");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel11.add(jLabel10, gridBagConstraints);

        entityid1.setEditable(false);
        entityid1.setColName("entityid");
        entityid1.setPreferredSize(new java.awt.Dimension(100, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel11.add(entityid1, gridBagConstraints);

        lastupdated1.setEditable(false);
        lastupdated1.setColName("lastupdated");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel11.add(lastupdated1, gridBagConstraints);

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .add(jPanel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(2496, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Misc.", jPanel10);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        HouseholdPanel.add(jTabbedPane3, gridBagConstraints);

        addressPanel1.setLayout(new java.awt.GridBagLayout());

        address3.setColName("address1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        addressPanel1.add(address3, gridBagConstraints);

        address4.setColName("address2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        addressPanel1.add(address4, gridBagConstraints);

        city1.setColName("city");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        addressPanel1.add(city1, gridBagConstraints);

        state1.setColName("state");
        state1.setPreferredSize(new java.awt.Dimension(30, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        addressPanel1.add(state1, gridBagConstraints);

        zip1.setColName("zip");
        zip1.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        addressPanel1.add(zip1, gridBagConstraints);

        jLabel12.setText("Address / City,State,Zip");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        addressPanel1.add(jLabel12, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        HouseholdPanel.add(addressPanel1, gridBagConstraints);

        EmailPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel13.setText("Email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        EmailPanel1.add(jLabel13, gridBagConstraints);

        email2.setColName("email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        EmailPanel1.add(email2, gridBagConstraints);

        bLaunchEmail1.setText("*");
        bLaunchEmail1.setMargin(new java.awt.Insets(1, 1, 1, 1));
        bLaunchEmail1.setPreferredSize(new java.awt.Dimension(14, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        EmailPanel1.add(bLaunchEmail1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        HouseholdPanel.add(EmailPanel1, gridBagConstraints);

        ObsoleteStuff.add(HouseholdPanel);

        cardPanel.add(ObsoleteStuff, "card4");

        jPanel20.add(cardPanel, java.awt.BorderLayout.CENTER);

        PeopleHeader.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        PeopleHeader.setLayout(new javax.swing.BoxLayout(PeopleHeader, javax.swing.BoxLayout.LINE_AXIS));

        PeopleHeader1.setPreferredSize(new java.awt.Dimension(300, 120));
        PeopleHeader1.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("Student:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 3);
        PeopleHeader1.add(jLabel2, gridBagConstraints);

        jLabel4.setText("Payer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 3);
        PeopleHeader1.add(jLabel4, gridBagConstraints);

        vPayerID.setColName("payerid");
        vPayerID.setPreferredSize(new java.awt.Dimension(200, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        PeopleHeader1.add(vPayerID, gridBagConstraints);

        bSave.setText("Save");
        bSave.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(bSave);

        bUndo.setText("Undo");
        bUndo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bUndoActionPerformed(evt);
            }
        });
        jToolBar1.add(bUndo);

        bRecalcTuition.setText("Recalc Tuition");
        bRecalcTuition.setEnabled(false);
        bRecalcTuition.setFocusable(false);
        bRecalcTuition.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bRecalcTuition.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bRecalcTuition.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bRecalcTuitionActionPerformed(evt);
            }
        });
        jToolBar1.add(bRecalcTuition);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weighty = 1.0;
        PeopleHeader1.add(jToolBar1, gridBagConstraints);

        vStudentID.setText("entityIDLabel1");
        vStudentID.setColName("entityid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        PeopleHeader1.add(vStudentID, gridBagConstraints);

        lEntityID.setText("jTypedLabel1");
        lEntityID.setColName("entityid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        PeopleHeader1.add(lEntityID, gridBagConstraints);

        bNewStudent.setText("New");
        bNewStudent.setMargin(new java.awt.Insets(0, 0, 0, 0));
        bNewStudent.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bNewStudentActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        PeopleHeader1.add(bNewStudent, gridBagConstraints);

        bNewPayer.setText("New");
        bNewPayer.setMargin(new java.awt.Insets(0, 0, 0, 0));
        bNewPayer.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bNewPayerActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        PeopleHeader1.add(bNewPayer, gridBagConstraints);

        jLabel20.setText("Parent 1:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 3);
        PeopleHeader1.add(jLabel20, gridBagConstraints);

        vParent1ID.setColName("parent1id");
        vParent1ID.setPreferredSize(new java.awt.Dimension(200, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        PeopleHeader1.add(vParent1ID, gridBagConstraints);

        bNewParent.setText("New");
        bNewParent.setMargin(new java.awt.Insets(0, 0, 0, 0));
        bNewParent.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bNewParentActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        PeopleHeader1.add(bNewParent, gridBagConstraints);

        jLabel21.setText("Parent 2:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 3);
        PeopleHeader1.add(jLabel21, gridBagConstraints);

        vParent2ID.setColName("parent2id");
        vParent2ID.setPreferredSize(new java.awt.Dimension(200, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        PeopleHeader1.add(vParent2ID, gridBagConstraints);

        bNewParent2.setText("New");
        bNewParent2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        bNewParent2.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bNewParent2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        PeopleHeader1.add(bNewParent2, gridBagConstraints);

        PeopleHeader.add(PeopleHeader1);

        TermRegPanel.setLayout(new java.awt.GridBagLayout());

        jLabel30.setText("Date Registered:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        TermRegPanel.add(jLabel30, gridBagConstraints);

        lDtregistered.setText("jTypedLabel1");
        lDtregistered.setColName("dtregistered");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        TermRegPanel.add(lDtregistered, gridBagConstraints);

        jLabel31.setText("Registration Signed:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        TermRegPanel.add(jLabel31, gridBagConstraints);

        jLabel14.setText("Tuition Plan:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        TermRegPanel.add(jLabel14, gridBagConstraints);

        programs.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        programs.setColName("programid");
        programs.setPreferredSize(new java.awt.Dimension(120, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        TermRegPanel.add(programs, gridBagConstraints);

        jLabel19.setText("Calculated Tuition: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        TermRegPanel.add(jLabel19, gridBagConstraints);

        jLabel32.setText("Tuition Override:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        TermRegPanel.add(jLabel32, gridBagConstraints);

        lTuition1.setText("2500");
        lTuition1.setColName("defaulttuition");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        TermRegPanel.add(lTuition1, gridBagConstraints);

        jLabel33.setText("Scholarship:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        TermRegPanel.add(jLabel33, gridBagConstraints);

        tuitionOverride.setText("jTypedTextField1");
        tuitionOverride.setColName("tuitionoverride");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        TermRegPanel.add(tuitionOverride, gridBagConstraints);

        scholarship.setText("jTypedTextField1");
        scholarship.setColName("scholarship");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        TermRegPanel.add(scholarship, gridBagConstraints);

        dtSigned.setColName("dtsigned");
        dtSigned.setPreferredSize(new java.awt.Dimension(140, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        TermRegPanel.add(dtSigned, gridBagConstraints);

        jLabel18.setText("Level:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        TermRegPanel.add(jLabel18, gridBagConstraints);

        rbPlans.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        rbPlans.setColName("rbplan");
        rbPlans.setPreferredSize(new java.awt.Dimension(120, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        TermRegPanel.add(rbPlans, gridBagConstraints);

        PeopleHeader.add(TermRegPanel);

        jPanel20.add(PeopleHeader, java.awt.BorderLayout.NORTH);

        add(jPanel20, java.awt.BorderLayout.CENTER);

        searchBox.setAutoSelectOnOne(true);
        searchBox.setMinimumSize(new java.awt.Dimension(200, 47));
        searchBox.setPreferredSize(new java.awt.Dimension(200, 89));
        add(searchBox, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

	private void bNewParent2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bNewParent2ActionPerformed
	{//GEN-HEADEREND:event_bNewParent2ActionPerformed
		newPayerAction("parent2id");
// TODO add your handling code here:
	}//GEN-LAST:event_bNewParent2ActionPerformed

	private void bNewParentActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bNewParentActionPerformed
	{//GEN-HEADEREND:event_bNewParentActionPerformed
		this.newPayerAction("parentid");
// TODO add your handling code here:
	}//GEN-LAST:event_bNewParentActionPerformed


void newPayerAction(final String colName)
{
	fapp.guiRun().run(RegistrationPanel.this, new SqlTask() {
	public void run(SqlRun str) throws Exception {
		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(RegistrationPanel.this);
		Wizard wizard = new offstage.wizards.newrecord.NewPersonWizard(fapp, root);
		wizard.runWizard();
		Integer eid = (Integer)wizard.getVal("entityid");
		if (eid != null) {
			smod.termregsRm.set(colName, eid);
			doUpdateSelect(str);
//			allRec.doUpdate(str);
//			allRec.doSelect(str);
		}
	}});
}

	private void bNewHouseholdActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bNewHouseholdActionPerformed
	{//GEN-HEADEREND:event_bNewHouseholdActionPerformed
		fapp.guiRun().run(RegistrationPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(RegistrationPanel.this);
			Wizard wizard = new offstage.wizards.newrecord.NewPersonWizard(fapp, root);
			wizard.runWizard();
			Integer eid = (Integer)wizard.getVal("entityid");
			if (eid != null) {
				smod.studentRm.set("primaryentityid", eid);
				doUpdateSelect(str);
	//			allRec.doUpdate(str);
	//			allRec.doSelect(str);
			}
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bNewHouseholdActionPerformed

	private void bNewPayerActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bNewPayerActionPerformed
	{//GEN-HEADEREND:event_bNewPayerActionPerformed
		newPayerAction("adultid");
// TODO add your handling code here:
	}//GEN-LAST:event_bNewPayerActionPerformed

	private void bNewStudentActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bNewStudentActionPerformed
	{//GEN-HEADEREND:event_bNewStudentActionPerformed
		fapp.guiRun().run(RegistrationPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(RegistrationPanel.this);
			Wizard wizard = new offstage.wizards.newrecord.NewPersonWizard(fapp, root);
			wizard.runWizard();
			Integer eid = (Integer)wizard.getVal("entityid");
			if (eid != null) {
//				str.execSql(SchoolDB.createStudentSql(eid));
				changeStudent(str, eid);
			}
		}});
	}//GEN-LAST:event_bNewStudentActionPerformed

	private void bRemoveEnrollmentActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bRemoveEnrollmentActionPerformed
	{//GEN-HEADEREND:event_bRemoveEnrollmentActionPerformed
		if (JOptionPane.showConfirmDialog(RegistrationPanel.this,
			"Are you sure you wish to\n" +
			"remove the selected enrollment?",
			"Remove Enrollment", JOptionPane.YES_NO_OPTION)
			== JOptionPane.NO_OPTION) return;
		
		fapp.guiRun().run(RegistrationPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			
			int row = enrollments.getSelectedRow();
			if (row < 0) return;
			JTypeTableModel x;
			CitibobTableModel model = enrollments.getModelU();
			int courseid = (Integer)model.getValueAt(row, model.findColumn("courseid"));
			int entityid = (Integer)model.getValueAt(row, model.findColumn("entityid"));
			str.execSql("delete from enrollments" +
				" where courseid = " + SqlInteger.sql(courseid) +
				" and entityid = " + SqlInteger.sql(entityid));
			enrolledDb.doSelect(str);
		}});
		
// TODO add your handling code here:
	}//GEN-LAST:event_bRemoveEnrollmentActionPerformed

	private void bAddEnrollmentActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bAddEnrollmentActionPerformed
	{//GEN-HEADEREND:event_bAddEnrollmentActionPerformed
		fapp.guiRun().run(RegistrationPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			enrolledDb.doUpdate(str);
			Wizard wizard = new EnrollWizard(fapp, RegistrationPanel.this);
//			TypedHashMap v = new TypedHashMap();
//				wizard.setVal("sterm", vTermID.getKeyedModel().toString(vTermID.getValue()));
				wizard.setVal("sperson", vStudentID.getText());
				wizard.setVal("entityid", vStudentID.getValue());
				wizard.setVal("termid", smod.getTermID());
//				wizard.setVal("courseroleModel",
//					fapp.getSchema("courseroles"), )
//						new citibob.sql.DbKeyedModel(st, null,
//		"courseroles", "courseroleid", "name", "orderid")));

			wizard.runWizard("add");
			enrolledDb.doSelect(str);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bAddEnrollmentActionPerformed

	private void bLaunchEmailActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bLaunchEmailActionPerformed
	{//GEN-HEADEREND:event_bLaunchEmailActionPerformed
		citibob.gui.BareBonesMailto.mailto((String)email1.getValue());
// TODO add your handling code here:
	}//GEN-LAST:event_bLaunchEmailActionPerformed

	private void bEmancipateActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bEmancipateActionPerformed
	{//GEN-HEADEREND:event_bEmancipateActionPerformed
		fapp.guiRun().run(RegistrationPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			smod.studentRm.set("primaryentityid", smod.studentRm.get("entityid"));
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bEmancipateActionPerformed

	private void bUndoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bUndoActionPerformed
	{//GEN-HEADEREND:event_bUndoActionPerformed
		fapp.guiRun().run(RegistrationPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			allRec.doSelect(str);
//str.execUpdate(new UpdTasklet2() {
//public void run(SqlRun str) throws Exception {
//	tuitionOverride.setValue(null);
//}});
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bUndoActionPerformed

	
private void doUpdateSelect(SqlRun str) throws Exception
{
	allRec.doUpdate(fapp.sqlRun());
	fapp.sqlRun().flush();
	allRec.doSelect(str);
}
	
	private void bSaveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bSaveActionPerformed
	{//GEN-HEADEREND:event_bSaveActionPerformed
		fapp.guiRun().run(RegistrationPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			allRec.forceUpdate(fapp.sqlRun());
			fapp.sqlRun().flush();
			allRec.doSelect(str);
		}});
	}//GEN-LAST:event_bSaveActionPerformed

	private void bRecalcTuitionActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bRecalcTuitionActionPerformed
	{//GEN-HEADEREND:event_bRecalcTuitionActionPerformed
		fapp.guiRun().run(RegistrationPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			calcTuition(str);
		}});
		// TODO add your handling code here:
}//GEN-LAST:event_bRecalcTuitionActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AccountPane;
    private javax.swing.JTabbedPane AccountTab;
    private javax.swing.JTabbedPane AdultTabs;
    private javax.swing.JPanel EmailPanel;
    private javax.swing.JPanel EmailPanel1;
    private javax.swing.JPanel EmailPanel2;
    private javax.swing.JPanel EmailPanel3;
    private javax.swing.JTabbedPane EnrollmentTab;
    private javax.swing.JScrollPane FamilyScrollPanel;
    private javax.swing.JPanel FirstMiddleLast;
    private javax.swing.JPanel FirstMiddleLast1;
    private javax.swing.JPanel FirstMiddleLast2;
    private javax.swing.JPanel FirstMiddleLast3;
    private javax.swing.JPanel FirstMiddleLast4;
    private javax.swing.JScrollPane GroupScrollPanel;
    private javax.swing.JPanel HouseholdPanel;
    private javax.swing.JPanel ObsoleteStuff;
    private javax.swing.JPanel Org;
    private javax.swing.JPanel Parent2Panel;
    private offstage.gui.GroupPanel Parent2PhonePanel;
    private javax.swing.JPanel ParentPanel;
    private offstage.gui.GroupPanel ParentPhonePanel;
    private javax.swing.JPanel PayerPanel;
    private javax.swing.JPanel PeopleHeader;
    private javax.swing.JPanel PeopleHeader1;
    private javax.swing.JPanel PeopleMain;
    private javax.swing.JPanel StudentAccounts;
    private javax.swing.JPanel StudentPane;
    private javax.swing.JTabbedPane StudentTab;
    private javax.swing.JPanel TermRegPanel;
    private citibob.swing.typed.JTypedTextField address1;
    private citibob.swing.typed.JTypedTextField address2;
    private citibob.swing.typed.JTypedTextField address3;
    private citibob.swing.typed.JTypedTextField address4;
    private citibob.swing.typed.JTypedTextField address5;
    private citibob.swing.typed.JTypedTextField address6;
    private citibob.swing.typed.JTypedTextField address7;
    private citibob.swing.typed.JTypedTextField address8;
    private javax.swing.JPanel addressPanel;
    private javax.swing.JPanel addressPanel1;
    private javax.swing.JPanel addressPanel2;
    private javax.swing.JPanel addressPanel3;
    private javax.swing.JButton bAddEnrollment;
    private javax.swing.JButton bCash;
    private javax.swing.JButton bCc;
    private javax.swing.JButton bCheck;
    private javax.swing.JButton bEmancipate;
    private javax.swing.JButton bLaunchEmail;
    private javax.swing.JButton bLaunchEmail1;
    private javax.swing.JButton bLaunchEmail2;
    private javax.swing.JButton bLaunchEmail3;
    private javax.swing.JButton bNewHousehold;
    private javax.swing.JButton bNewParent;
    private javax.swing.JButton bNewParent2;
    private javax.swing.JButton bNewPayer;
    private javax.swing.JButton bNewStudent;
    private javax.swing.JButton bOtherTrans;
    private javax.swing.JButton bRecalcTuition;
    private javax.swing.JButton bRemoveEnrollment;
    private javax.swing.JButton bSave;
    private javax.swing.JButton bUndo;
    private javax.swing.JPanel cardPanel;
    private citibob.swing.typed.JTypedTextField city;
    private citibob.swing.typed.JTypedTextField city1;
    private citibob.swing.typed.JTypedTextField city2;
    private citibob.swing.typed.JTypedTextField city3;
    private javax.swing.JPanel controller1;
    private citibob.swing.typed.JTypedDateChooser dob;
    private citibob.swing.typed.JTypedDateChooser dtSigned;
    private citibob.swing.typed.JTypedTextField email1;
    private citibob.swing.typed.JTypedTextField email2;
    private citibob.swing.typed.JTypedTextField email3;
    private citibob.swing.typed.JTypedTextField email4;
    private citibob.jschema.swing.StatusTable enrollments;
    private citibob.swing.typed.JTypedTextField entityid;
    private citibob.swing.typed.JTypedTextField entityid1;
    private citibob.swing.typed.JTypedTextField entityid2;
    private citibob.swing.typed.JTypedTextField entityid3;
    private offstage.school.gui.SchoolFamilySelectorTable familyTable;
    private citibob.swing.typed.JTypedTextField firstname;
    private citibob.swing.typed.JTypedTextField firstname1;
    private citibob.swing.typed.JTypedTextField firstname2;
    private citibob.swing.typed.JTypedTextField firstname3;
    private citibob.swing.typed.JTypedTextField firstname4;
    private citibob.swing.typed.JKeyedComboBox gender;
    private offstage.gui.GroupPanel householdPhonePanel;
    private citibob.swing.typed.JBoolCheckbox isorg;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTabbedPane jTabbedPane5;
    private javax.swing.JTabbedPane jTabbedPane6;
    private javax.swing.JToolBar jToolBar1;
    private citibob.swing.typed.JTypedLabel lDtregistered;
    private citibob.swing.typed.JTypedLabel lEntityID;
    private javax.swing.JLabel lFirst;
    private javax.swing.JLabel lFirst1;
    private javax.swing.JLabel lFirst2;
    private javax.swing.JLabel lFirst3;
    private javax.swing.JLabel lFirst4;
    private javax.swing.JLabel lFirst5;
    private javax.swing.JLabel lGender;
    private javax.swing.JLabel lLast;
    private javax.swing.JLabel lLast1;
    private javax.swing.JLabel lLast2;
    private javax.swing.JLabel lLast3;
    private javax.swing.JLabel lLast4;
    private javax.swing.JLabel lMiddle;
    private javax.swing.JLabel lMiddle1;
    private javax.swing.JLabel lMiddle2;
    private javax.swing.JLabel lMiddle3;
    private javax.swing.JLabel lMiddle4;
    private citibob.swing.typed.JTypedLabel lTuition1;
    private citibob.swing.typed.JTypedTextField lastname;
    private citibob.swing.typed.JTypedTextField lastname1;
    private citibob.swing.typed.JTypedTextField lastname2;
    private citibob.swing.typed.JTypedTextField lastname3;
    private citibob.swing.typed.JTypedTextField lastname4;
    private citibob.swing.typed.JTypedTextField lastupdated;
    private citibob.swing.typed.JTypedTextField lastupdated1;
    private citibob.swing.typed.JTypedTextField lastupdated2;
    private citibob.swing.typed.JTypedTextField lastupdated3;
    private citibob.swing.typed.JTypedTextField middlename;
    private citibob.swing.typed.JTypedTextField middlename1;
    private citibob.swing.typed.JTypedTextField middlename2;
    private citibob.swing.typed.JTypedTextField middlename3;
    private citibob.swing.typed.JTypedTextField middlename4;
    private citibob.swing.typed.JTypedTextField orgname;
    private offstage.swing.typed.CryptCCInfo payerCCInfo;
    private offstage.gui.GroupPanel payerPhonePanel;
    private citibob.swing.typed.JKeyedComboBox programs;
    private citibob.swing.typed.JKeyedComboBox rbPlans;
    private citibob.swing.typed.JTypedTextField salutation;
    private citibob.swing.typed.JTypedTextField salutation1;
    private citibob.swing.typed.JTypedTextField salutation2;
    private citibob.swing.typed.JTypedTextField salutation3;
    private citibob.swing.typed.JTypedTextField salutation4;
    private citibob.swing.typed.JTypedTextField scholarship;
    private offstage.swing.typed.EntitySelector searchBox;
    private citibob.swing.typed.JTypedTextField state;
    private citibob.swing.typed.JTypedTextField state1;
    private citibob.swing.typed.JTypedTextField state2;
    private citibob.swing.typed.JTypedTextField state3;
    private offstage.accounts.gui.TransRegPanel transRegister;
    private citibob.swing.typed.JTypedTextField tuitionOverride;
    private offstage.swing.typed.EntityIDEditableLabel vParent1ID;
    private offstage.swing.typed.EntityIDEditableLabel vParent2ID;
    private offstage.swing.typed.EntityIDEditableLabel vPayerID;
    private offstage.swing.typed.EntityIDLabel vStudentID;
    private citibob.swing.typed.JTypedTextField zip;
    private citibob.swing.typed.JTypedTextField zip1;
    private citibob.swing.typed.JTypedTextField zip2;
    private citibob.swing.typed.JTypedTextField zip3;
    // End of variables declaration//GEN-END:variables

//public static void showFrame(SqlRun str, final FrontApp fapp, String dupType, final String title)
//{
//	final SchoolPanel panel = new SchoolPanel();
//	panel.initRuntime(str, fapp);
//	str.execUpdate(new UpdTasklet2() {
//	public void run(SqlRun str) throws Exception {
//		JFrame frame = new JFrame(title);
//		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
////		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.getContentPane().add(panel);
//			new citibob.swing.prefs.SwingPrefs().setPrefs(frame, "", fapp.userRoot().node("SchoolFrame"));
//
//		frame.setVisible(true);
//	}});
//}


//public static void main(String[] args) throws Exception
//{
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	SqlRun str = pool.checkout().createStatement();
//	FrontApp fapp = new FrontApp(pool,null);
//
//	SchoolPanel panel = new SchoolPanel();
//	panel.initRuntime(fapp, st);
//	
//	JFrame frame = new JFrame();
//	frame.setSize(600,800);
//	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//	frame.getContentPane().add(panel);
//
//	frame.setVisible(true);
//}

}































////MultiDbModel all = new AllDbModel();
//class AllDbModel extends MultiDbModel
//{
//	public void doSelect(SqlRun str)
//	{
//		smod.studentDm.doSelect(str);
//		smod.termregsDm.doSelect(str);
//
//		str.execUpdate(new UpdTasklet2() {
//		public void run(SqlRun str) throws Exception {
//			Integer payerid = (Integer)smod.termregsRm.get("payerid");
//			
//			// Keys depend on results of previous queries
//			smod.parent1Dm.setKey((Integer)smod.studentRm.get("primaryentityid"));
//			smod.parent2Dm.setKey((Integer)smod.studentRm.get("parent2id"));
//			smod.payerDm.setKey(payerid); //(Integer)smod.termregsRm.get("payerid"));//smod.studentDm.getAdultID());
//			smod.payertermregsDm.setKey("entityid", payerid); //smod.termregsRm.get("payerid"));
//
//			smod.payertermregsDm.doSelect(str);
//			smod.parent1Dm.doSelect(str);
//			smod.parent2Dm.doSelect(str);
////			Integer pid = (Integer)smod.studentRm.get("primaryentityid");
//			familyTable.setPrimaryEntityID(str, payerid);
//			smod.payerDm.doSelect(str);
//			enrolledDb.doSelect(str);
//		}});
//	}
//	public void setKey(Object entityid)
//	{
//		smod.studentDm.setKey(entityid); //new Integer[] {entityid});
//		smod.termregsDm.setKey("entityid", entityid);
//		
//		// Set "key" for enrollments
//		int termid = smod.getTermID();
//		enrolledDb.setKey(entityid);
////		enrolledDb.setWhereClause("enrollments.courseid = courseids.courseid" +
////			" and courseids.termid = " + SqlInteger.sql(termid) +
////			" and enrollments.entityid = " + SqlInteger.sql(entityid));
//	}
//	void superDoUpdate(SqlRun str)
//		{ super.doUpdate(str); }
//	public void doUpdate(SqlRun str)
//	{
//		if (smod.studentRm.get("primaryentityid") == null || smod.termregsRm.get("payerid") == null) {
//			JOptionPane.showMessageDialog(RegistrationPanel.this,
//				"Cannot save record.  You must have a payer\nand parent in order to save.");
//			return;
//		}
//
//		// Make sure payer has record in school system
//		Integer payerid = (Integer)smod.termregsRm.get("payerid");
//		if (payerid != null) str.execSql(SchoolDB.registerPayerSql(smod.getTermID(), payerid));
//
//		// Transfer main parent over as primary entity id (family relationships)
//		final IntVal primaryentityid = offstage.db.DB.getPrimaryEntityID(str, (Integer)smod.studentRm.get("primaryentityid"));
//		str.execUpdate(new UpdTasklet2() {
//		public void run(SqlRun str) throws Exception {
//			smod.studentRm.set("primaryentityid", primaryentityid.val);//str.get("primaryentityid"));
//
//			// Do the rest
//			superDoUpdate(str);
//
//			// Calculate the tuition
//			int col = smod.studentRm.findColumn("primaryentityid");
//			Integer Oldparent1id = (Integer)smod.studentRm.getOrigValue(col);
//			Integer Parent1id = (Integer)smod.studentRm.get(col);
//
//			transRegister.getDbModel().doUpdate(str);
//			
//			int termid = smod.getTermID(); //(Integer)vTermID.getValue();
//			String payerIdSql = null;
//			if (Oldparent1id != null && Parent1id != null) {
//				if (Oldparent1id.intValue() == Parent1id.intValue()) {
//					// Didn't change, they're both the same
//					payerIdSql = "select " + Oldparent1id;
//				} else {
//					// Changed from one payer to another
//					payerIdSql = "select " + Oldparent1id + " union select " + Parent1id;
//				}
//			} else if (Parent1id != null) {
//				// Changed from no payer to a payer
//				payerIdSql = "select " + Parent1id;
//			} else if (Oldparent1id != null) {
//				// Changed from a payer to no payer.
//				payerIdSql = "select " + Oldparent1id;
//			}
//			if (payerIdSql != null) {
//				TuitionCalc tc = new TuitionCalc(fapp, termid);
//					tc.setPayerIDs(payerIdSql);
//					tc.recalcTuition(str);
//			}
////			if (Oldparent1id != null) SchoolDB.w_tuitiontrans_calcTuitionByAdult(str, termid, Oldparent1id, null);
////			if (Parent1id != null && !Parent1id.equals(Oldparent1id)) SchoolDB.w_tuitiontrans_calcTuitionByAdult(str, termid, Parent1id, null);
//		}});
//	}
//}
