/*
OffstageArts: Enterprise Database for Arts Organizations
This file Copyright (c) 2005-2007 by Robert Fischer

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your optvion) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
/*
 * SchoolFrame.java
 *
 * Created on December 9, 2007, 4:45 PM
 */

package offstage.school.gui;

import citibob.task.BatchRunnable;
import citibob.reports.Reports;
import citibob.sql.DbKeyedModel;
import citibob.sql.RsRunnable;
import citibob.sql.SqlRunner;
import citibob.sql.UpdRunnable;
import citibob.swing.table.JTypeTableModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import offstage.reports.AcctStatement;
import offstage.reports.LabelReport;
import offstage.reports.RollBook;
import offstage.reports.SchoolAccounts;
import offstage.reports.YDPConfirmationLetter;
import offstage.*;
import offstage.reports.StudentSchedule;
import offstage.school.tuition.TuitionCalc;

/**
 *
 * @author  citibob
 */
public class SchoolFrame extends javax.swing.JFrame
{

FrontApp fapp;
SchoolModel schoolModel;

/** Creates new form SchoolFrame */
public SchoolFrame()
{
	initComponents();
	tabs.setSelectedComponent(regPanel);
}

public void initRuntime(SqlRunner str, FrontApp xfapp)
//throws SQLException
{
	this.fapp = xfapp;

	this.schoolModel = new SchoolModel(fapp);
	
	coursesPanel.initRuntime(fapp, schoolModel, str);
	schedulePanel.initRuntime(fapp, schoolModel, str);
	termsAddPanel.initRuntime(fapp, schoolModel, str);
	this.courseListPanel.initRuntime(fapp, schoolModel, str);

	// Set up terms selector
//setKeyedModel selects the term --- but the KeyedModel is not getting filled in till afterwards
	final DbKeyedModel tkmodel = new DbKeyedModel(str, fapp.getDbChange(), "termids",
		"select groupid, name from termids where iscurrent order by firstdate desc");
	str.execUpdate(new UpdRunnable() {
	public void run(SqlRunner str) throws Exception {
		vTermID.addPropertyChangeListener("value", new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			// Property change was (almost probably) due to a mouse click;
			// So we need the runApp() here.
			fapp.runApp(new BatchRunnable() {
			public void run(SqlRunner str) throws Exception {
//System.out.println("a value = " + vTermID.getValue());
				schoolModel.setTermID((Integer)(vTermID.getValue()));
//System.out.println("b value = " + vTermID.getValue());
			}});
		}});
		vTermID.setKeyedModel(tkmodel);
//		if (tkmodel.size() > 0) vTermID.setSelectedIndex(0);
	}});

	regPanel.initRuntime(str, xfapp, schoolModel);
	
//	str.execUpdate(new UpdRunnable() {
//	public void run(SqlRunner str) throws Exception {
//		// Also set up frame preferences
//		new citibob.swing.prefs.SwingPrefs().setPrefs(SchoolFrame.this, "", fapp.userRoot().node("SchoolFrame"));
//	}});

}

//int getTermID()
//{
//	Integer Termid = (Integer)vTermID.getValue();
//	return (Termid == null ? -1 : Termid);
//}



/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jPanel1 = new javax.swing.JPanel();
        vTermID = new citibob.swing.typed.JKeyedComboBox();
        jLabel3 = new javax.swing.JLabel();
        tabs = new javax.swing.JTabbedPane();
        termsAddPanel = new offstage.school.gui.TermsAddPanel();
        schedulePanel = new offstage.school.gui.TermsPanel();
        coursesPanel = new offstage.school.gui.CoursesPanel();
        regPanel = new offstage.school.gui.RegistrationPanel();
        courseListPanel = new offstage.school.gui.CourseListPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mActions = new javax.swing.JMenu();
        miRecalcAllTuition = new javax.swing.JMenuItem();
        miApplyLateFees = new javax.swing.JMenuItem();
        miRefresh = new javax.swing.JMenuItem();
        mStudent = new javax.swing.JMenu();
        miConfirmationLetter = new javax.swing.JMenuItem();
        miSchedule = new javax.swing.JMenuItem();
        miAccountStatement = new javax.swing.JMenuItem();
        mReports = new javax.swing.JMenu();
        miConfirmationLetters = new javax.swing.JMenuItem();
        miStudentSchedules = new javax.swing.JMenuItem();
        miAccountStatements = new javax.swing.JMenuItem();
        miPayerLabels = new javax.swing.JMenuItem();
        miRollBooks = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        miStudentAccounts = new javax.swing.JMenuItem();
        mWindow = new javax.swing.JMenu();
        mHelp = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("School");

        vTermID.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        vTermID.setPreferredSize(new java.awt.Dimension(68, 19));

        jLabel3.setText("Term: ");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jLabel3)
                .add(3, 3, 3)
                .add(vTermID, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLabel3)
            .add(vTermID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        tabs.addTab("Terms", termsAddPanel);
        tabs.addTab("Schedule", schedulePanel);
        tabs.addTab("Courses", coursesPanel);
        tabs.addTab("Registration", regPanel);
        tabs.addTab("Enrollments", courseListPanel);

        getContentPane().add(tabs, java.awt.BorderLayout.CENTER);

        mActions.setText("Actions");

        miRecalcAllTuition.setText("Recalc All Tuition");
        miRecalcAllTuition.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                miRecalcAllTuitionActionPerformed(evt);
            }
        });
        mActions.add(miRecalcAllTuition);

        miApplyLateFees.setText("Apply Late Fees");
        miApplyLateFees.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                miApplyLateFeesActionPerformed(evt);
            }
        });
        mActions.add(miApplyLateFees);

        miRefresh.setText("Refresh");
        miRefresh.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                miRefreshActionPerformed(evt);
            }
        });
        mActions.add(miRefresh);

        jMenuBar1.add(mActions);

        mStudent.setText("Student");

        miConfirmationLetter.setText("Confirmation Letter");
        miConfirmationLetter.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                miConfirmationLetterActionPerformed(evt);
            }
        });
        mStudent.add(miConfirmationLetter);

        miSchedule.setText("Schedule");
        miSchedule.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                miScheduleActionPerformed(evt);
            }
        });
        mStudent.add(miSchedule);

        miAccountStatement.setText("Account Statement");
        miAccountStatement.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                miAccountStatementActionPerformed(evt);
            }
        });
        mStudent.add(miAccountStatement);

        jMenuBar1.add(mStudent);

        mReports.setText("Reports");

        miConfirmationLetters.setText("Confirmation Letters");
        miConfirmationLetters.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                miConfirmationLettersActionPerformed(evt);
            }
        });
        mReports.add(miConfirmationLetters);

        miStudentSchedules.setText("Student Schedules");
        miStudentSchedules.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                miStudentSchedulesActionPerformed(evt);
            }
        });
        mReports.add(miStudentSchedules);

        miAccountStatements.setText("Acct Statements & Labels");
        miAccountStatements.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                miAccountStatementsActionPerformed(evt);
            }
        });
        mReports.add(miAccountStatements);

        miPayerLabels.setText("Parent/Payer Lables");
        miPayerLabels.setEnabled(false);
        miPayerLabels.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                miPayerLabelsActionPerformed(evt);
            }
        });
        mReports.add(miPayerLabels);

        miRollBooks.setText("Roll Books");
        miRollBooks.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                miRollBooksActionPerformed(evt);
            }
        });
        mReports.add(miRollBooks);
        mReports.add(jSeparator1);

        miStudentAccounts.setText("School Accounts Summary");
        miStudentAccounts.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                miStudentAccountsActionPerformed(evt);
            }
        });
        mReports.add(miStudentAccounts);

        jMenuBar1.add(mReports);

        mWindow.setText("Window");
        jMenuBar1.add(mWindow);

        mHelp.setText("Help");
        jMenuBar1.add(mHelp);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void miRefreshActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_miRefreshActionPerformed
	{//GEN-HEADEREND:event_miRefreshActionPerformed
// TODO: This is just here for testing.
vTermID.setKeyedModel(vTermID.getKeyedModel());
// TODO add your handling code here:
	}//GEN-LAST:event_miRefreshActionPerformed

	private void miApplyLateFeesActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_miApplyLateFeesActionPerformed
	{//GEN-HEADEREND:event_miApplyLateFeesActionPerformed
		fapp.runGui(SchoolFrame.this, new BatchRunnable() {
		public void run(SqlRunner str) throws Exception {
			final LateFeesWizard wizard = new LateFeesWizard(fapp, SchoolFrame.this);
			if (!wizard.runWizard("latefees")) return;
			
//			int termid = schoolModel.getTermID();
//			Calendar cal = Calendar.getInstance(fapp.getTimeZone());
//				cal.set(Calendar.HOUR_OF_DAY, 0);
//				cal.set(Calendar.MINUTE, 0);
//				cal.set(Calendar.SECOND, 0);
//				cal.set(Calendar.MILLISECOND, 0);
//				cal.add(Calendar.DAY_OF_MONTH, -30);
System.out.println("asofdate: " + (java.util.Date)wizard.getVal("asofdate"));
			final SchoolAccounts rep = new SchoolAccounts(str, fapp.getTimeZone(), -1,
				(java.util.Date)wizard.getVal("asofdate"), (Integer)wizard.getVal("latedays"));
			str.execUpdate(new UpdRunnable() {
			public void run(SqlRunner str) throws Exception {
				rep.applyLateFees(str, (Double)wizard.getVal("multiplier"));
			}});
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_miApplyLateFeesActionPerformed

	private void miConfirmationLetterActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_miConfirmationLetterActionPerformed
	{//GEN-HEADEREND:event_miConfirmationLetterActionPerformed
		fapp.runGui(SchoolFrame.this, new BatchRunnable() {
		public void run(SqlRunner str) throws Exception {
			Integer eid = (Integer)schoolModel.studentRm.get("entityid");
			int termid = schoolModel.getTermID();
			if (eid == null)
				JOptionPane.showMessageDialog(SchoolFrame.this,
					"You must have a student selected for this report!", "", JOptionPane.OK_OPTION);
			else YDPConfirmationLetter.viewReport(str, fapp, termid, eid);
		}});

// TODO add your handling code here:
	}//GEN-LAST:event_miConfirmationLetterActionPerformed

	private void miAccountStatementActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_miAccountStatementActionPerformed
	{//GEN-HEADEREND:event_miAccountStatementActionPerformed
		fapp.runGui(SchoolFrame.this, new BatchRunnable() {
		public void run(SqlRunner str) throws Exception {
			int termid = schoolModel.getTermID();
			Integer payerid = (Integer)schoolModel.schoolRm.get("adultid");
			if (payerid == null) JOptionPane.showMessageDialog(SchoolFrame.this,
				"You must have a student selected for this report!", "", JOptionPane.OK_OPTION);
			else AcctStatement.doAccountStatementsAndLabels(str, fapp, termid, payerid, new java.util.Date());
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_miAccountStatementActionPerformed

	private void miScheduleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_miScheduleActionPerformed
	{//GEN-HEADEREND:event_miScheduleActionPerformed
		fapp.runGui(SchoolFrame.this, new BatchRunnable() {
		public void run(SqlRunner str) throws Exception {
			Integer eid = (Integer)schoolModel.studentRm.get("entityid");
			if (eid == null) JOptionPane.showMessageDialog(SchoolFrame.this,
				"You must have a student selected for this report!", "", JOptionPane.OK_OPTION);
			else StudentSchedule.viewStudentSchedules(fapp, str, schoolModel.getTermID(), eid);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_miScheduleActionPerformed

	private void miStudentAccountsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_miStudentAccountsActionPerformed
	{//GEN-HEADEREND:event_miStudentAccountsActionPerformed
		fapp.runGui(SchoolFrame.this, new BatchRunnable() {
		public void run(SqlRunner str) throws Exception {
			int termid = schoolModel.getTermID();
//			Calendar cal = Calendar.getInstance(fapp.getTimeZone());
//				cal.set(Calendar.HOUR_OF_DAY, 0);
//				cal.set(Calendar.MINUTE, 0);
//				cal.set(Calendar.SECOND, 0);
//				cal.set(Calendar.MILLISECOND, 0);
//				cal.add(Calendar.DAY_OF_MONTH, -30);
			final SchoolAccounts rep = new SchoolAccounts(str, fapp.getTimeZone(),
				termid, new java.util.Date(), 30);
			str.execUpdate(new UpdRunnable() {
			public void run(SqlRunner str) throws Exception {
				Reports reports = fapp.getReports(); //new OffstageReports(fapp);
				reports.viewXls(rep.model, null, "StudentAccounts.xls");
			}});
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_miStudentAccountsActionPerformed

	private void miPayerLabelsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_miPayerLabelsActionPerformed
	{//GEN-HEADEREND:event_miPayerLabelsActionPerformed
		fapp.runGui(SchoolFrame.this, new BatchRunnable()
		{
			public void run(SqlRunner str) throws Exception
			{
				int termid = schoolModel.getTermID();
				String idSql =
					" select xx.entityid\n" +
					" from (\n" +
					" 	select distinct s.adultid as entityid\n" +
					" 	from termregs tr, entities_school s\n" +
					" 	where tr.groupid = " + termid + "\n" +
					" 	and tr.entityid = s.entityid\n" +
					"          UNION\n" +
					" 	select distinct s.parentid as entityid\n" +
					" 	from termregs tr, entities_school s\n" +
					" 	where tr.groupid = " + termid + "\n" +
					" 	and tr.entityid = s.entityid\n" +
					" ) xx, persons p\n" +
					" where xx.entityid = p.entityid\n" +
					" order by p.lastname, p.firstname";
				String sql = LabelReport.getSql(idSql, null);
				System.out.println("==================");
				System.out.println(sql);
				System.out.println("==================");
				str.execSql(sql, new RsRunnable()
				{
					public void run(SqlRunner str, ResultSet rs) throws Exception
					{
						Reports rr = fapp.getReports();
						rr.viewJasper(rr.toJasper(rs), null, "AddressLabels.jrxml");
					}});
//			st.executeUpdate(LabelReport.cleanupSql());
			}});
// TODO add your handling code here:
	}//GEN-LAST:event_miPayerLabelsActionPerformed

	private void miConfirmationLettersActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_miConfirmationLettersActionPerformed
	{//GEN-HEADEREND:event_miConfirmationLettersActionPerformed
		fapp.runGui(SchoolFrame.this, new BatchRunnable() {
		public void run(SqlRunner str) throws Exception {
			int termid = schoolModel.getTermID();
			YDPConfirmationLetter.viewReport(str, fapp, termid, -1);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_miConfirmationLettersActionPerformed

	private void miAccountStatementsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_miAccountStatementsActionPerformed
	{//GEN-HEADEREND:event_miAccountStatementsActionPerformed
		fapp.runGui(SchoolFrame.this, new BatchRunnable()
		{
			public void run(SqlRunner str) throws Exception
			{
				int termid = schoolModel.getTermID();
				AcctStatement.doAccountStatementsAndLabels(str, fapp, termid, -1, new java.util.Date());
			}});
// TODO add your handling code here:
	}//GEN-LAST:event_miAccountStatementsActionPerformed

	private void miStudentSchedulesActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_miStudentSchedulesActionPerformed
	{//GEN-HEADEREND:event_miStudentSchedulesActionPerformed
		fapp.runGui(SchoolFrame.this, new BatchRunnable() {
		public void run(SqlRunner str) throws Exception {
			StudentSchedule.viewStudentSchedules(fapp, str, schoolModel.getTermID(), -1);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_miStudentSchedulesActionPerformed

	private void miRollBooksActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_miRollBooksActionPerformed
	{//GEN-HEADEREND:event_miRollBooksActionPerformed
		fapp.runGui(SchoolFrame.this, new BatchRunnable()
		{
			public void run(SqlRunner str) throws Exception
			{
				final RollBook report = new RollBook(fapp, schoolModel.getTermID());
				report.doSelect(str);
				str.execUpdate(new UpdRunnable()
				{
					public void run(SqlRunner str) throws Exception
					{
						JTypeTableModel model = report.newTableModel();
						JRDataSource jrdata = new JRTableModelDataSource(model);
						fapp.getReports().viewJasper(
							fapp.getReports().toJasper(model), null, "RollBook.jrxml");
//				offstage.reports.ReportOutput.viewJasperReport(fapp, "RollBook.jrxml", jrdata, null);// TODO add your handling code here:
					}});
			}});// TODO add your handling code here:
	}//GEN-LAST:event_miRollBooksActionPerformed

	private void miRecalcAllTuitionActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_miRecalcAllTuitionActionPerformed
	{//GEN-HEADEREND:event_miRecalcAllTuitionActionPerformed
		fapp.runGui(SchoolFrame.this, new BatchRunnable()
		{
			public void run(SqlRunner str) throws Exception
			{
				TuitionCalc tc = new TuitionCalc(fapp, schoolModel.getTermID());
					tc.setAllPayerIDs();
					tc.recalcTuition(str);
			}});
// TODO add your handling code here:
	}//GEN-LAST:event_miRecalcAllTuitionActionPerformed

	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private offstage.school.gui.CourseListPanel courseListPanel;
    private offstage.school.gui.CoursesPanel coursesPanel;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JMenu mActions;
    private javax.swing.JMenu mHelp;
    private javax.swing.JMenu mReports;
    private javax.swing.JMenu mStudent;
    private javax.swing.JMenu mWindow;
    private javax.swing.JMenuItem miAccountStatement;
    private javax.swing.JMenuItem miAccountStatements;
    private javax.swing.JMenuItem miApplyLateFees;
    private javax.swing.JMenuItem miConfirmationLetter;
    private javax.swing.JMenuItem miConfirmationLetters;
    private javax.swing.JMenuItem miPayerLabels;
    private javax.swing.JMenuItem miRecalcAllTuition;
    private javax.swing.JMenuItem miRefresh;
    private javax.swing.JMenuItem miRollBooks;
    private javax.swing.JMenuItem miSchedule;
    private javax.swing.JMenuItem miStudentAccounts;
    private javax.swing.JMenuItem miStudentSchedules;
    private offstage.school.gui.RegistrationPanel regPanel;
    private offstage.school.gui.TermsPanel schedulePanel;
    private javax.swing.JTabbedPane tabs;
    private offstage.school.gui.TermsAddPanel termsAddPanel;
    private citibob.swing.typed.JKeyedComboBox vTermID;
    // End of variables declaration//GEN-END:variables

public static void showFrame(SqlRunner str, final FrontApp fapp)
{
	final SchoolFrame frame = new SchoolFrame();
	frame.initRuntime(str, fapp);
	str.execUpdate(new UpdRunnable() {
	public void run(SqlRunner str) throws Exception {
		frame.setVisible(true);
	}});
}

	
}
