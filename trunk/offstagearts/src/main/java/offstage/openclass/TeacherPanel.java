/*
OffstageArts: Enterprise Database for Arts Organizations
This file Copyright (c) 2005-2008 by Robert Fischer

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
 * PersonPanel.java
 *
 * Created on February 9, 2005, 8:18 PM
 */

package offstage.openclass;

import offstage.frontdesk.*;
import javax.swing.*;
import citibob.jschema.*;
import citibob.swing.typed.*;
import citibob.task.*;
import citibob.sql.*;
import citibob.sql.pgsql.SqlInteger;
import citibob.text.PercentSFormat;
import citibob.util.ObjectUtil;
import citibob.wizard.Wizard;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import offstage.FrontApp;
import offstage.school.gui.SchoolModel;

/**
 *
 * @author  citibob
 */
public class TeacherPanel 
extends javax.swing.JPanel {
	
SchoolModel smod;
TeacherDbModel teacherDm;
SqlBufDbModel otherTeachersDm;
FrontApp app;
MultiDbModel allDm;
    
	/** Creates new form PersonPanel */
	public TeacherPanel() {
		initComponents();
		genderButtonGroup.add("M", maleButton);
		genderButtonGroup.add("F", femaleButton);
		genderButtonGroup.add(null, unknownGenderButton);
	}
	
public void refreshTeacherList(SqlRun str)
{
	teacherList.getTable().executeQuery(str, "select entityid from teachers", "lastname,firstname");
}
public void initRuntime(SqlRun str, FrontApp xapp, SchoolModel xsmod)
{
	this.app = xapp;
	this.smod = xsmod;
//	vNewTeacherID.initRuntime(app);

	
	
	// =======================
	// Discount codes --- makes its own model
//	allDm.oc
	
	
	// ====================================
	// Models
	teacherDm = new TeacherDbModel(str, app);
	SchemaBufRowModel personRm = new SchemaBufRowModel(teacherDm.onePerson.getSchemaBuf());
	SchemaBufRowModel teacherRm = new SchemaBufRowModel(teacherDm.oneTeacher.getSchemaBuf());
	
	TypedWidgetBinder.bindRecursive(this, personRm, app.swingerMap());
		new TypedWidgetBinder().bind(genderButtonGroup, personRm);
	TypedWidgetBinder.bindRecursive(this, teacherRm, app.swingerMap());
	tOpct.setJType(Double.class, new PercentSFormat());
	
	
//	teacherListDm = new SchemaBufDbModel(app.getSchema("teachers"), "teachers", null);

	// ================================================
	// Other Teachers
	otherTeachersDm = new SqlBufDbModel(str, app,
		new String[] {"enrollments"}, null, new String[] {"enrollments"}) {
	public SqlSet getSelectSql(boolean proto) {
//			int row = enrollments.getSelectedRow();
//			Integer courseid = null;
//			if (row >= 0) courseid = (Integer)teacherDm.enrolled.getSchemaBuf().getValueAt(row, "courseid");
		return new SqlSet(
			" select p.firstname || ' ' || p.lastname as teachername, e.*" +
			" from enrollments e, entities p" +
			" where e.entityid = p.entityid" +
			" and e.entityid <> " + teacherList.getValue() +
			" and courseid = " + enrollments.getValue() +
			" and courserole = " + app.schemaSet().getEnumInt("enrollments", "courserole", "teacher"));
	}};
	
	// ================================================
	// Update terms
//	vTermID.addPropertyChangeListener("value", new PropertyChangeListener() {
//	public void propertyChange(PropertyChangeEvent evt) {
	smod.addListener(new SchoolModel.Adapter() {
    public void termIDChanged(int oldTermID, int termID) {
		SqlRun str = app.sqlRun();
		app.sqlRun().pushFlush();
//				teacherDm.set
			teacherDm.enrolled.setTermID(smod.getTermID());
//			teacherDm.enrolled.setTermID((Integer)vTermID.getValue());
			teacherDm.enrolled.doSelect(str);
		// ------------ TODO: refresh enrollments here
//			schoolModel.setTermID((Integer)(vTermID.getValue()));
		app.sqlRun().popFlush();		// Flush, conditional on no other items around us.
	}});



	
	str.execUpdate(new UpdTasklet2() {		// Set up table AFTER enrolledDb has been initialized
	public void run(SqlRun str) {
		// ===========================================
		// List of teachers
		teacherList.initRuntime(app);
		teacherList.addPropertyChangeListener("value", new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			Integer entityid = (Integer)teacherList.getValue();
			teacherDm.setKey(entityid);
			app.sqlRun().pushFlush();
//				teacherDm.doUpdate(app.sqlRun());
				teacherDm.doSelect(app.sqlRun());
			app.sqlRun().popFlush();
		}});
		
		// =====================
		// Phone #s
		phonePanel.initRuntime(str, teacherDm.phones.getSchemaBuf(), "groupid",
			new String[] {"Type", "Number"},
			new String[] {"groupid", "phone"}, app.swingerMap());


		// =====================================================================
		// Enrollments
//	final EnrolledDbModel enrolledDb = new EnrolledDbModel(str, app, "uniqenrolls", "teacher");

		enrollments.setHighlightMouseover(false);
		enrollments.setModelU(teacherDm.enrolled.getTableModel(),
			new String[] {"Status", "Course", "Day", "Start", "Finish",
				"From", "To"},
			new String[] {"__status__", "name", "dayofweek", "tstart", "tnext",
				"dstart", "dend"},
			new boolean[] {false, false, false, false, false,
				true, true}, app.swingerMap());
		enrollments.setFormatU("dayofweek", new DayOfWeekKeyedModel());
		enrollments.setValueColU("courseid");
		// ====================================================
		// Discounts
		oCDiscPane.initRuntime(app, teacherDm.ocDiscModels);
		
		// ====================================================
//		str.execUpdate(new UpdTasklet() {
//		public void run() {
			otherTeachers.setModelU(otherTeachersDm.getSchemaBuf(),
				new String[] {"Status", "Teacher", "From", "To"},
				new String[] {"__status__", "teachername", "dstart", "dend"},
				new boolean[] {false,false, true, true},
				app.swingerMap());
//			otherTeachers.setHighlightMouseover(false);
//			otherTeachers.setEnabled(false);
			otherTeachers.setValueColU("entityid");
			
			// Listen for refreshes
//			enrollments.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//			public void valueChanged(ListSelectionEvent e) {
			enrollments.addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (ObjectUtil.eq(evt.getOldValue(), evt.getNewValue())) return;
				SqlRun str = app.sqlRun();
				str.pushFlush();
					otherTeachersDm.doSelect(str);
				str.popFlush();
			}});
			
//		}});
		
//		// Change teacher when you click here...
//		otherTeachers.addPropertyChangeListener("value", new PropertyChangeListener() {
//		public void propertyChange(PropertyChangeEvent evt) {
//			Integer entityid = (Integer)otherTeachers.getValue();
//			teacherDm.setKey(entityid);
//		}});
		
		refreshTeacherList(str);
		
		// ===================================================
		// Terms Selector
		// Set up terms selector
//		final DbKeyedModel tkmodel = ((TermidsSchema)app.getSchema("termids")).currentTermsKmodel;
//			vTermID.setKeyedModel(tkmodel, null);
		
		allDm = new MultiDbModel(teacherDm, otherTeachersDm) {
			public void doSelect(SqlRun str) {
				super.doSelect(str);
				
				final Object val = enrollments.getValue();
				str.execUpdate(new UpdTasklet() {
				public void run() {
					enrollments.setValue(val);
				}});
			}
		};
			
	}});
	
}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        genderButtonGroup = new citibob.swing.typed.KeyedButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        bSave = new javax.swing.JButton();
        bUndo = new javax.swing.JButton();
        jSplitPane2 = new javax.swing.JSplitPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        TeacherList = new javax.swing.JPanel();
        teacherList = new offstage.swing.typed.IdSqlPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        bAddTeacher = new javax.swing.JButton();
        bRemoveTeacher = new javax.swing.JButton();
        jSplitPane3 = new javax.swing.JSplitPane();
        TeacherDetails = new javax.swing.JPanel();
        AddrPanel = new javax.swing.JPanel();
        addressPanel = new javax.swing.JPanel();
        address1 = new citibob.swing.typed.JTypedTextField();
        address2 = new citibob.swing.typed.JTypedTextField();
        city = new citibob.swing.typed.JTypedTextField();
        state = new citibob.swing.typed.JTypedTextField();
        zip = new citibob.swing.typed.JTypedTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        entityid = new citibob.swing.typed.JTypedTextField();
        lastupdated = new citibob.swing.typed.JTypedTextField();
        jPanel4 = new javax.swing.JPanel();
        mainPanel = new javax.swing.JPanel();
        MiscInfo = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        url = new citibob.swing.typed.JTypedTextField();
        dob = new citibob.swing.typed.JTypedDateChooser();
        email1 = new citibob.swing.typed.JTypedTextField();
        jLabel11 = new javax.swing.JLabel();
        bLaunchEmail = new javax.swing.JButton();
        bLaunchBrowser = new javax.swing.JButton();
        mailprefid = new citibob.swing.typed.JKeyedComboBox();
        jLabel1 = new javax.swing.JLabel();
        FirstMiddleLast = new javax.swing.JPanel();
        lFirst = new javax.swing.JLabel();
        lMiddle = new javax.swing.JLabel();
        lLast = new javax.swing.JLabel();
        salutation = new citibob.swing.typed.JTypedTextField();
        firstname = new citibob.swing.typed.JTypedTextField();
        middlename = new citibob.swing.typed.JTypedTextField();
        lastname = new citibob.swing.typed.JTypedTextField();
        Gender = new javax.swing.JPanel();
        maleButton = new javax.swing.JRadioButton();
        femaleButton = new javax.swing.JRadioButton();
        unknownGenderButton = new javax.swing.JRadioButton();
        teacherTablePane = new javax.swing.JPanel();
        tDisplayName = new citibob.swing.typed.JTypedTextField();
        tOpct = new citibob.swing.typed.JTypedTextField();
        tHourlyrate = new citibob.swing.typed.JTypedTextField();
        tPerClassRate = new citibob.swing.typed.JTypedTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        DetailsPlusPhoneDiscount = new javax.swing.JPanel();
        phonePanel = new offstage.gui.GroupPanel();
        lPhoneNumbers = new javax.swing.JLabel();
        oCDiscPane = new offstage.openclass.OCDiscPane();
        lDiscountCodes = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane5 = new javax.swing.JSplitPane();
        EnrollmentsPane = new javax.swing.JPanel();
        GroupScrollPanel = new javax.swing.JScrollPane();
        enrollments = new citibob.swing.typed.JTypedSelectTable();
        jPanel15 = new javax.swing.JPanel();
        bAddEnrollment = new javax.swing.JButton();
        bRemoveEnrollment = new javax.swing.JButton();
        lDiscountCodes1 = new javax.swing.JLabel();
        EnrollmentsPane1 = new javax.swing.JPanel();
        GroupScrollPanel1 = new javax.swing.JScrollPane();
        otherTeachers = new citibob.swing.typed.JTypedSelectTable();
        lDiscountCodes2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        bAddTeacher1 = new javax.swing.JButton();
        bRemoveTeacher1 = new javax.swing.JButton();

        genderButtonGroup.setColName("gender");

        setLayout(new java.awt.BorderLayout());

        jToolBar1.setRollover(true);

        bSave.setText("Save");
        bSave.setFocusable(false);
        bSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(bSave);

        bUndo.setText("Undo");
        bUndo.setFocusable(false);
        bUndo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bUndo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bUndoActionPerformed(evt);
            }
        });
        jToolBar1.add(bUndo);

        add(jToolBar1, java.awt.BorderLayout.SOUTH);

        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        TeacherList.setLayout(new java.awt.BorderLayout());
        TeacherList.add(teacherList, java.awt.BorderLayout.CENTER);

        jLabel9.setFont(new java.awt.Font("Dialog", 1, 11));
        jLabel9.setText("Teachers");
        TeacherList.add(jLabel9, java.awt.BorderLayout.PAGE_START);

        bAddTeacher.setText("Add");
        bAddTeacher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAddTeacherActionPerformed(evt);
            }
        });
        jPanel5.add(bAddTeacher);

        bRemoveTeacher.setText("Remove");
        bRemoveTeacher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRemoveTeacherActionPerformed(evt);
            }
        });
        jPanel5.add(bRemoveTeacher);

        TeacherList.add(jPanel5, java.awt.BorderLayout.PAGE_END);

        jSplitPane1.setLeftComponent(TeacherList);

        TeacherDetails.setMinimumSize(new java.awt.Dimension(355, 306));
        TeacherDetails.setLayout(new java.awt.GridBagLayout());

        AddrPanel.setLayout(new java.awt.GridBagLayout());

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
        state.setMinimumSize(new java.awt.Dimension(30, 19));
        state.setPreferredSize(new java.awt.Dimension(30, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        addressPanel.add(state, gridBagConstraints);

        zip.setColName("zip");
        zip.setMinimumSize(new java.awt.Dimension(80, 19));
        zip.setPreferredSize(new java.awt.Dimension(80, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        addressPanel.add(zip, gridBagConstraints);

        jLabel3.setText("Address / City,State,Zip");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        addressPanel.add(jLabel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        AddrPanel.add(addressPanel, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel13.setText("ID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(jLabel13, gridBagConstraints);

        jLabel14.setText("Last Modified");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(jLabel14, gridBagConstraints);

        entityid.setEditable(false);
        entityid.setColName("entityid");
        entityid.setPreferredSize(new java.awt.Dimension(100, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(entityid, gridBagConstraints);

        lastupdated.setEditable(false);
        lastupdated.setColName("lastupdated");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(lastupdated, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        AddrPanel.add(jPanel3, gridBagConstraints);

        jPanel4.setLayout(new java.awt.GridLayout(2, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        AddrPanel.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        TeacherDetails.add(AddrPanel, gridBagConstraints);

        mainPanel.setLayout(new java.awt.GridBagLayout());

        MiscInfo.setLayout(new java.awt.GridBagLayout());

        jLabel5.setText("DOB");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        MiscInfo.add(jLabel5, gridBagConstraints);

        jLabel7.setText("URL");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        MiscInfo.add(jLabel7, gridBagConstraints);

        url.setColName("url");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        MiscInfo.add(url, gridBagConstraints);

        dob.setColName("dob");
        dob.setPreferredSize(new java.awt.Dimension(122, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        MiscInfo.add(dob, gridBagConstraints);

        email1.setColName("email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        MiscInfo.add(email1, gridBagConstraints);

        jLabel11.setText("Email");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        MiscInfo.add(jLabel11, gridBagConstraints);

        bLaunchEmail.setText("*");
        bLaunchEmail.setMargin(new java.awt.Insets(1, 1, 1, 1));
        bLaunchEmail.setPreferredSize(new java.awt.Dimension(14, 19));
        bLaunchEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bLaunchEmailActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        MiscInfo.add(bLaunchEmail, gridBagConstraints);

        bLaunchBrowser.setText("*");
        bLaunchBrowser.setMargin(new java.awt.Insets(1, 1, 1, 1));
        bLaunchBrowser.setPreferredSize(new java.awt.Dimension(14, 19));
        bLaunchBrowser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bLaunchBrowserActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        MiscInfo.add(bLaunchBrowser, gridBagConstraints);

        mailprefid.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        mailprefid.setColName("mailprefid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        MiscInfo.add(mailprefid, gridBagConstraints);

        jLabel1.setText("Mail Pref ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        MiscInfo.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        mainPanel.add(MiscInfo, gridBagConstraints);

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
        salutation.setPreferredSize(new java.awt.Dimension(30, 19));
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
        middlename.setPreferredSize(new java.awt.Dimension(30, 19));
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
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        mainPanel.add(FirstMiddleLast, gridBagConstraints);

        Gender.setPreferredSize(new java.awt.Dimension(85, 50));
        Gender.setLayout(new java.awt.GridBagLayout());

        maleButton.setText("Male");
        maleButton.setMargin(null);
        maleButton.setPreferredSize(new java.awt.Dimension(54, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        Gender.add(maleButton, gridBagConstraints);

        femaleButton.setText("Female");
        femaleButton.setMargin(null);
        femaleButton.setPreferredSize(new java.awt.Dimension(69, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        Gender.add(femaleButton, gridBagConstraints);

        unknownGenderButton.setText("Unknown");
        unknownGenderButton.setMargin(null);
        unknownGenderButton.setPreferredSize(new java.awt.Dimension(85, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        Gender.add(unknownGenderButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        mainPanel.add(Gender, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        TeacherDetails.add(mainPanel, gridBagConstraints);

        teacherTablePane.setLayout(new java.awt.GridBagLayout());

        tDisplayName.setText("jTypedTextField1");
        tDisplayName.setColName("displayname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        teacherTablePane.add(tDisplayName, gridBagConstraints);

        tOpct.setText("jTypedTextField1");
        tOpct.setColName("ocpct");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        teacherTablePane.add(tOpct, gridBagConstraints);

        tHourlyrate.setText("jTypedTextField1");
        tHourlyrate.setColName("hourlyrate");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        teacherTablePane.add(tHourlyrate, gridBagConstraints);

        tPerClassRate.setText("jTypedTextField1");
        tPerClassRate.setColName("perclassrate");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        teacherTablePane.add(tPerClassRate, gridBagConstraints);

        jLabel2.setText("Open Class Share ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        teacherTablePane.add(jLabel2, gridBagConstraints);

        jLabel4.setText("Hourly Rate ($) ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        teacherTablePane.add(jLabel4, gridBagConstraints);

        jLabel6.setText("Per-Class Rate ($) ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        teacherTablePane.add(jLabel6, gridBagConstraints);

        jLabel8.setText("Display Name ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        teacherTablePane.add(jLabel8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        TeacherDetails.add(teacherTablePane, gridBagConstraints);

        jSplitPane3.setLeftComponent(TeacherDetails);

        DetailsPlusPhoneDiscount.setLayout(new java.awt.GridBagLayout());

        phonePanel.setMinimumSize(new java.awt.Dimension(153, 120));
        phonePanel.setPreferredSize(new java.awt.Dimension(453, 180));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        DetailsPlusPhoneDiscount.add(phonePanel, gridBagConstraints);

        lPhoneNumbers.setFont(new java.awt.Font("Dialog", 1, 11));
        lPhoneNumbers.setText("Phone Numbers");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        DetailsPlusPhoneDiscount.add(lPhoneNumbers, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        DetailsPlusPhoneDiscount.add(oCDiscPane, gridBagConstraints);

        lDiscountCodes.setFont(new java.awt.Font("Dialog", 1, 11));
        lDiscountCodes.setText("Discount Codes");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        DetailsPlusPhoneDiscount.add(lDiscountCodes, gridBagConstraints);

        jSplitPane3.setRightComponent(DetailsPlusPhoneDiscount);

        jSplitPane1.setRightComponent(jSplitPane3);

        jSplitPane2.setLeftComponent(jSplitPane1);

        jPanel1.setLayout(new java.awt.BorderLayout());

        EnrollmentsPane.setLayout(new java.awt.BorderLayout());

        enrollments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        GroupScrollPanel.setViewportView(enrollments);

        EnrollmentsPane.add(GroupScrollPanel, java.awt.BorderLayout.CENTER);

        bAddEnrollment.setText("Add Class"); // NOI18N
        bAddEnrollment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAddEnrollmentActionPerformed(evt);
            }
        });
        jPanel15.add(bAddEnrollment);

        bRemoveEnrollment.setText("Remove Class"); // NOI18N
        bRemoveEnrollment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRemoveEnrollmentActionPerformed(evt);
            }
        });
        jPanel15.add(bRemoveEnrollment);

        EnrollmentsPane.add(jPanel15, java.awt.BorderLayout.SOUTH);

        lDiscountCodes1.setFont(new java.awt.Font("Dialog", 1, 11));
        lDiscountCodes1.setText("This Teacher's Classes");
        EnrollmentsPane.add(lDiscountCodes1, java.awt.BorderLayout.PAGE_START);

        jSplitPane5.setLeftComponent(EnrollmentsPane);

        EnrollmentsPane1.setLayout(new java.awt.BorderLayout());

        otherTeachers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        GroupScrollPanel1.setViewportView(otherTeachers);

        EnrollmentsPane1.add(GroupScrollPanel1, java.awt.BorderLayout.CENTER);

        lDiscountCodes2.setFont(new java.awt.Font("Dialog", 1, 11));
        lDiscountCodes2.setText("Other Teachers for Class");
        EnrollmentsPane1.add(lDiscountCodes2, java.awt.BorderLayout.PAGE_START);

        bAddTeacher1.setText("Add");
        bAddTeacher1.setEnabled(false);
        bAddTeacher1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAddTeacher1ActionPerformed(evt);
            }
        });
        jPanel6.add(bAddTeacher1);

        bRemoveTeacher1.setText("Remove");
        bRemoveTeacher1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRemoveTeacher1ActionPerformed(evt);
            }
        });
        jPanel6.add(bRemoveTeacher1);

        EnrollmentsPane1.add(jPanel6, java.awt.BorderLayout.PAGE_END);

        jSplitPane5.setRightComponent(EnrollmentsPane1);

        jPanel1.add(jSplitPane5, java.awt.BorderLayout.CENTER);

        jSplitPane2.setRightComponent(jPanel1);

        add(jSplitPane2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

	private void bLaunchEmailActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bLaunchEmailActionPerformed
	{//GEN-HEADEREND:event_bLaunchEmailActionPerformed
		citibob.gui.BareBonesMailto.mailto((String)email1.getValue());
	}//GEN-LAST:event_bLaunchEmailActionPerformed

	private void bLaunchBrowserActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bLaunchBrowserActionPerformed
	{//GEN-HEADEREND:event_bLaunchBrowserActionPerformed
		citibob.gui.BareBonesBrowserLaunch.openURL((String)url.getValue());
	}//GEN-LAST:event_bLaunchBrowserActionPerformed

private void bAddTeacherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAddTeacherActionPerformed
	app.guiRun().run(TeacherPanel.this, new SqlTask() {
	public void run(SqlRun str) throws Exception {
		Wizard wizard = new NewTeacherWizard(app, TeacherPanel.this);
		if (!wizard.runWizard()) {
			return;
		}
		Integer entityid = (Integer)wizard.getVal("entityid");
//		teacherDm.setKey(entityid);
//		teacherDm.doSelect(str);
		
		refreshTeacherList(str);
		teacherList.setValue(entityid);
	}});
	// TODO add your handling code here:
}//GEN-LAST:event_bAddTeacherActionPerformed

private void bSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSaveActionPerformed
	app.guiRun().run(TeacherPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			allDm.doUpdate(str);
			allDm.doSelect(str);
		}});
		// TODO add your handling code here:
}//GEN-LAST:event_bSaveActionPerformed

private void bUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bUndoActionPerformed
	app.guiRun().run(TeacherPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			allDm.doSelect(str);
		}});
		// TODO add your handling code here:
}//GEN-LAST:event_bUndoActionPerformed

private void bAddEnrollmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAddEnrollmentActionPerformed
	app.guiRun().run(TeacherPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {

			//enrolledDb.doUpdate(str);
			Wizard wizard = new EnrollWizard(app, TeacherPanel.this);
			wizard.setVal("sperson", "<Person>");
			wizard.setVal("entityid", teacherList.getValue());
			wizard.setVal("termid", smod.getTermID());
//			wizard.setVal("termid", vTermID.getValue());
			wizard.setVal("courserole", app.schemaSet().getEnumInt("enrollments", "courserole", "teacher"));
			wizard.runWizard("add");
			teacherDm.enrolled.doSelect(str);
			//enrolledDb.doSelect(str);
		}});
		// TODO add your handling code here:
}//GEN-LAST:event_bAddEnrollmentActionPerformed

private void bRemoveEnrollmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRemoveEnrollmentActionPerformed
	app.guiRun().run(TeacherPanel.this, new SqlTask() {
	public void run(SqlRun str) throws Exception {
		if (JOptionPane.showConfirmDialog(TeacherPanel.this,
			"Are you sure you wish to\n" +
			"remove the selected class?",
			"Remove Class", JOptionPane.YES_NO_OPTION)
			!= JOptionPane.YES_OPTION) return;

		Integer courseid = (Integer)enrollments.getValue("courseid");
		Integer entityid = (Integer)enrollments.getValue("entityid");
//		Integer entityid = (Integer)teacherList.getValue();
		str.execSql("delete from enrollments" +
			" where courseid = " + SqlInteger.sql(courseid) +
			" and entityid = " + SqlInteger.sql(entityid) +
			" and courserole = " + app.schemaSet().getEnumInt("enrollments", "courserole", "teacher"));
		teacherDm.enrolled.doSelect(str);
	}});
//			int row = enrollments.getSelectedRow();
//			if (row < 0) return;
//			JTypeTableModel x;
//			CitibobTableModel model = enrollments.getModelU();
//			int courseid = (Integer)model.getValueAt(row, model.findColumn("courseid"));
//			int entityid = (Integer)model.getValueAt(row, model.findColumn("entityid"));
//			str.execSql("delete from enrollments" +
//					" where courseid = " + SqlInteger.sql(courseid) +
//					" and entityid = " + SqlInteger.sql(entityid));
//			enrolledDb.doSelect(str);
//		}});
//		
//		// TODO add your handling code here:
}//GEN-LAST:event_bRemoveEnrollmentActionPerformed

private void bRemoveTeacherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRemoveTeacherActionPerformed
	app.guiRun().run(TeacherPanel.this, new SqlTask() {
	public void run(SqlRun str) throws Exception {
		Integer entityid = (Integer)teacherList.getValue();
		if (entityid == null) return;
		if (JOptionPane.showConfirmDialog(TeacherPanel.this,
				"Are you sure you wish to\n" +
				"remove this person as a teacher?",
				"Remove Teacher", JOptionPane.YES_NO_OPTION)
				!= JOptionPane.YES_OPTION) return;

		String sql =
			" delete from teachers where entityid = " + entityid;
		str.execSql(sql);
		app.dbChange().fireTableWillChange(str, "teachers");
		refreshTeacherList(str);
		teacherDm.setKey(null);
		teacherDm.doSelect(str);
	}});
}//GEN-LAST:event_bRemoveTeacherActionPerformed

private void bAddTeacher1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAddTeacher1ActionPerformed
	// TODO add your handling code here:
}//GEN-LAST:event_bAddTeacher1ActionPerformed

private void bRemoveTeacher1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRemoveTeacher1ActionPerformed
	
	app.guiRun().run(TeacherPanel.this, new SqlTask() {
	public void run(SqlRun str) throws Exception {
		if (JOptionPane.showConfirmDialog(TeacherPanel.this,
			"Are you sure you wish to remove\n" +
			"the selected teacher from the class?",
			"Remove Teacher", JOptionPane.YES_NO_OPTION)
			!= JOptionPane.YES_OPTION) return;

		Integer courseid = (Integer)enrollments.getValue("courseid");
		Integer entityid = (Integer)otherTeachers.getValue("entityid");
		str.execSql("delete from enrollments" +
			" where courseid = " + SqlInteger.sql(courseid) +
			" and entityid = " + SqlInteger.sql(entityid) +
			" and courserole = " + app.schemaSet().getEnumInt("enrollments", "courserole", "teacher"));
		otherTeachersDm.doSelect(str);
	}});
}//GEN-LAST:event_bRemoveTeacher1ActionPerformed
	

	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AddrPanel;
    private javax.swing.JPanel DetailsPlusPhoneDiscount;
    private javax.swing.JPanel EnrollmentsPane;
    private javax.swing.JPanel EnrollmentsPane1;
    private javax.swing.JPanel FirstMiddleLast;
    private javax.swing.JPanel Gender;
    private javax.swing.JScrollPane GroupScrollPanel;
    private javax.swing.JScrollPane GroupScrollPanel1;
    private javax.swing.JPanel MiscInfo;
    private javax.swing.JPanel TeacherDetails;
    private javax.swing.JPanel TeacherList;
    private citibob.swing.typed.JTypedTextField address1;
    private citibob.swing.typed.JTypedTextField address2;
    private javax.swing.JPanel addressPanel;
    private javax.swing.JButton bAddEnrollment;
    private javax.swing.JButton bAddTeacher;
    private javax.swing.JButton bAddTeacher1;
    private javax.swing.JButton bLaunchBrowser;
    private javax.swing.JButton bLaunchEmail;
    private javax.swing.JButton bRemoveEnrollment;
    private javax.swing.JButton bRemoveTeacher;
    private javax.swing.JButton bRemoveTeacher1;
    private javax.swing.JButton bSave;
    private javax.swing.JButton bUndo;
    private citibob.swing.typed.JTypedTextField city;
    private citibob.swing.typed.JTypedDateChooser dob;
    private citibob.swing.typed.JTypedTextField email1;
    private citibob.swing.typed.JTypedSelectTable enrollments;
    private citibob.swing.typed.JTypedTextField entityid;
    private javax.swing.JRadioButton femaleButton;
    private citibob.swing.typed.JTypedTextField firstname;
    private citibob.swing.typed.KeyedButtonGroup genderButtonGroup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JSplitPane jSplitPane5;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lDiscountCodes;
    private javax.swing.JLabel lDiscountCodes1;
    private javax.swing.JLabel lDiscountCodes2;
    private javax.swing.JLabel lFirst;
    private javax.swing.JLabel lLast;
    private javax.swing.JLabel lMiddle;
    private javax.swing.JLabel lPhoneNumbers;
    private citibob.swing.typed.JTypedTextField lastname;
    private citibob.swing.typed.JTypedTextField lastupdated;
    private citibob.swing.typed.JKeyedComboBox mailprefid;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JRadioButton maleButton;
    private citibob.swing.typed.JTypedTextField middlename;
    private offstage.openclass.OCDiscPane oCDiscPane;
    private citibob.swing.typed.JTypedSelectTable otherTeachers;
    private offstage.gui.GroupPanel phonePanel;
    private citibob.swing.typed.JTypedTextField salutation;
    private citibob.swing.typed.JTypedTextField state;
    private citibob.swing.typed.JTypedTextField tDisplayName;
    private citibob.swing.typed.JTypedTextField tHourlyrate;
    private citibob.swing.typed.JTypedTextField tOpct;
    private citibob.swing.typed.JTypedTextField tPerClassRate;
    private offstage.swing.typed.IdSqlPanel teacherList;
    private javax.swing.JPanel teacherTablePane;
    private javax.swing.JRadioButton unknownGenderButton;
    private citibob.swing.typed.JTypedTextField url;
    private citibob.swing.typed.JTypedTextField zip;
    // End of variables declaration//GEN-END:variables
	// --------------------------------------------------------------

}
