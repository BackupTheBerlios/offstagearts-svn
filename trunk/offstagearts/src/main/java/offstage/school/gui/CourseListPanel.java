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
 * CourseListPanel.java
 *
 * Created on December 15, 2007, 4:39 PM
 */

package offstage.school.gui;

import citibob.jschema.DayOfWeekKeyedModel;
import citibob.jschema.IntKeyedDbModel;
import citibob.jschema.RSSchema;
import citibob.jschema.SchemaBuf;
import citibob.jschema.SqlSchemaInfo;
import citibob.jschema.SqlBufDbModel;
import citibob.task.ETask;
import citibob.sql.ConsSqlQuery;
import citibob.sql.SqlRun;
import citibob.sql.UpdTasklet2;
import citibob.sql.pgsql.SqlInteger;
import citibob.swing.table.DelegateStyledTM;
import citibob.task.SqlTask;
import citibob.text.SFormat;
import citibob.types.KeyedModel;
import citibob.wizard.Wizard;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;
import java.util.TreeSet;
import offstage.FrontApp;
import offstage.reports.PhoneJoin;
import offstage.school.tuition.TuitionCalc;
import offstage.types.PhoneSFormat;

/**
 *
 * @author  citibob
 */
public class CourseListPanel extends javax.swing.JPanel
{
	
FrontApp fapp;
SchoolModel smod;

IntKeyedDbModel coursesDb;
SqlBufDbModel enrolledDb;
int courseid;		// Currently select course
int courseRow;		// Currently selected row of courses table

/** Creates new form CoursesPanel */
public CourseListPanel()
{
	initComponents();
}

	

/** Creates new form CompleteStatusPanel */
public void initRuntime(FrontApp xfapp, SchoolModel smod, SqlRun str)
//throws SQLException
{
	this.fapp = xfapp;
	this.smod = smod;

//	// Set up terms selector
//	terms.setKeyedModel(new DbKeyedModel(str, fapp.getDbChange(), "termids",
//		"select groupid, name from termids where iscurrent order by firstdate"));
////	str0.runBatches(fapp);

	smod.addListener(new SchoolModel.Adapter() {
    public void termIDChanged(int oldTermID, int termID) {
		fapp.sqlRun().pushFlush();
		termChanged(fapp.sqlRun());
		fapp.sqlRun().popFlush();
	}});

	// Set up courses editor
	coursesDb = new IntKeyedDbModel(fapp.getSchema("courseids"),
		"termid", fapp.dbChange()) {
	/** Override stuff to delete from enrollments table when we delete from courseids table. */
	protected ConsSqlQuery doSimpleDeleteNoRemoveRow(int row, SqlRun str, SqlSchemaInfo qs) {
		ConsSqlQuery q = super.doSimpleDeleteNoRemoveRow(row, str, qs);
		int courseid = (Integer)getSchemaBuf().getValueAt(row, "courseid");
		String sql =
			" delete from enrollments where courseid = " + SqlInteger.sql(courseid) + ";\n" +
			" delete from enrollments where courseid = " + SqlInteger.sql(courseid) + ";\n";
		str.execSql(sql);
		return q;
	}};
	coursesDb.setDoInsertKeys(false);
	coursesDb.setOrderClause("dayofweek, tstart, name");

	courses.setModelU(coursesDb.getSchemaBuf(),
		new String[] {"#", "Name", "Day", "Start", "End", "Enroll Limit"},
		new String[] {"__rowno__", "name", "dayofweek", "tstart", "tnext", "enrolllimit"},
		new boolean[] {false,false,false,false,false,false},
		fapp.swingerMap());
	courses.setFormatU("dayofweek", new DayOfWeekKeyedModel());

	courses.addPropertyChangeListener("value", new PropertyChangeListener() {
	public void propertyChange(PropertyChangeEvent evt) {
//	courses.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//	public void valueChanged(ListSelectionEvent e) {
		courseRow = courses.getSelectedRow();
		int oldid = courseid;
		courseid = courseRow < 0 ? -1 : (Integer)coursesDb.getSchemaBuf().getValueAt(courseRow, "courseid");
		if (courseid != oldid) {
			fapp.sqlRun().pushFlush();
			courseChanged(fapp.sqlRun());
			fapp.sqlRun().popFlush();
		}
	}});
	
	// Set up enrollments editor
	// Enrollments
	enrolledDb = new SqlBufDbModel(str, fapp,
		new String[] {"enrollments"},
		null,
		new String[] {"enrollments"}) {
	public String getSelectSql(boolean proto) {
		StringBuffer sql = new StringBuffer();
		
		String studentIdSql =
			" select e.entityid as id" +
			" from courseids c\n" +
			" inner join enrollments e on (c.courseid = e.courseid)\n" +
			" where c.courseid = " + SqlInteger.sql(courseid);
		sql.append(
			" create temporary table _sids (id int);\n" +
			" insert into _sids " + studentIdSql + ";\n");
		String parentIdSql =
			" select distinct p.parent1id as id\n" +
			" from _sids\n" +
			" inner join persons p on (_sids.id = p.entityid)\n";
		sql.append(
			" create temporary table _pids (id int);\n" +
			" insert into _pids " + parentIdSql + ";\n");

		PhoneJoin pj = new PhoneJoin(2);
		sql.append(pj.pphonesSql("_pph", parentIdSql) + ";\n");
		
		sql.append(
			" select e.courserole,e.dstart,e.dend,\n" +
			" (case when st.firstname is null then '' else st.firstname || ' ' end ||\n" +
			"  case when st.lastname is null then '' else st.lastname end) as st_name,\n" +
			" (case when p1.firstname is null then '' else p1.firstname || ' ' end ||\n" +
			"  case when p1.lastname is null then '' else p1.lastname end) as p1_name,\n" +
			" e.entityid, e.courseid, st.parent1id, tr.payerid,\n" +
			" _pph_phoneid1,_pph_phone1,_pph_phoneid2,_pph_phone2\n" +
			" from courseids c\n" +
			" left outer join enrollments e on (c.courseid = e.courseid)\n" +
			" left outer join persons st on (e.entityid = st.entityid)\n" +
			" left outer join persons p1 on (st.parent1id = p1.entityid)\n" +
			" left outer join termregs tr on (tr.groupid = c.termid and tr.entityid = st.entityid)\n" +
			" left outer join (" + pj.pphonesTable("_pph") + ") pph on (pph.id = p1.entityid)" +
			" where c.courseid = " + SqlInteger.sql(courseid) +
			" order by e.courserole,st.lastname,st.firstname;\n");
		sql.append(
			" drop table _pph;\n" +
			" drop table _sids;\n");

		return sql.toString();
		
		
//		sql.append(
//			" select e.courserole,e.dstart,e.dend,\n" +
//			" (case when st.firstname is null then '' else st.firstname || ' ' end ||\n" +
//			"  case when st.lastname is null then '' else st.lastname end) as st_name,\n" +
//			" (case when p1.firstname is null then '' else p1.firstname || ' ' end ||\n" +
//			"  case when p1.lastname is null then '' else p1.lastname end) as p1_name,\n" +
//			" e.entityid, e.courseid, st.parent1id, tr.payerid\n" +
////			" from courseids c, enrollments e, persons st\n" + //, entities_school st_s\n" +
//			" from courseids c\n" + //, entities_school st_s\n" +
//			" left outer join enrollments e on (c.courseid = e.courseid)\n" +
//			" left outer join persons st on (e.entityid = st.entityid)\n" +
//			" left outer join persons p1 on (st.parent1id = p1.entityid)\n" +
//			" left outer join termregs tr on (tr.groupid = c.termid and tr.entityid = st.entityid)\n" +
////			" where e.courseid = c.courseid\n" +
////			" and e.entityid = st.entityid\n" +
////			" and st_s.entityid = st.entityid\n" +
//			" where c.courseid = " + SqlInteger.sql(courseid) +
//			" order by e.courserole,st.lastname,st.firstname\n");
	}};
	str.execUpdate(new UpdTasklet2() {
	public void run(SqlRun str) {
		RSSchema schema = (RSSchema)enrolledDb.getSchemaBuf().getSchema();

		SFormat phoneFmt = new PhoneSFormat();
		
		DelegateStyledTM stm = new DelegateStyledTM(enrolledDb.getTableModel());
		stm.setColumns(fapp.swingerMap(),
			"__status__", "Status", false, null,
			"__rowno__", "#", false, null,
			"st_name", "Name", false, null,
			"dstart", "Custom Start", true, null,
			"dend", "Custom End", true, null,
			"courserole", "Role", true, null,
			"p1_name", "Parent Name", false, null,
			"_pph_phone1", "Phone1", false, phoneFmt,
			"_pph_phone2", "Phone2", false, phoneFmt);
		KeyedModel phoneidsKm = fapp.schemaSet().getKeyedModel("phones", "groupid");
		stm.setTooltips(fapp.swingerMap(),
			null, null,
			null, null,
			null, null,
			null, null,
			null, null,
			null, null,
			null, null,
			"_pph_phoneid1", phoneidsKm,
			"_pph_phoneid2", phoneidsKm);
		enrollments.setStyledTM(stm);
		
//			
//		stm.setEditable(false, false, false, true, true, true, false, false, false);
//		
//		enrollments.setModelU(enrolledDb.getTableModel(),
//			new String[] {"Status", "#", "Name", "Custom Start", "Custom End",
//				"Role", "Parent Name", "Phone1", "Phone2"},
//			new String[] {"__status__", "__rowno__", "st_name", "dstart", "dend",
//				"courserole", "p1_name", "_pph_phone1", "_pph_phone2"},
//			new String[] {null, null, null, null, null,
//				null, null, "_pph_phoneids1", "_pph_phoneids2"},
//			new boolean[] {false, false, false, true, true, true, false},
//			fapp.swingerMap());
//		enrollments.setT`
////		enrollments.setRenderEditU("dayofweek", new DayOfWeekKeyedModel());


	}});


}

void termChanged(SqlRun str) 
{
	enrolledDb.doUpdate(str);
	coursesDb.setKey((Integer)smod.getTermID());
	coursesDb.doUpdate(str);
	coursesDb.doSelect(str);
}

void courseChanged(SqlRun str)
{
	enrolledDb.setKey(courseid);
	enrolledDb.doUpdate(str);
	enrolledDb.doSelect(str);
}
	
void all_doSelect(SqlRun str)
{
	final int id = courseid;
	enrolledDb.doSelect(str);
	coursesDb.doSelect(str);

	str.execUpdate(new UpdTasklet2() {
	public void run(SqlRun str) throws Exception {
		courses.setSelectedRowU(id, "courseid");
	}});
}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jToolBar1 = new javax.swing.JToolBar();
        bSave = new javax.swing.JButton();
        bUndo = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        coursesPane = new javax.swing.JScrollPane();
        courses = new citibob.swing.typed.JTypedSelectTable();
        jPanel4 = new javax.swing.JPanel();
        meetingsPane = new javax.swing.JScrollPane();
        enrollments = new citibob.jschema.swing.SchemaBufTable();
        lMeetings = new javax.swing.JLabel();
        meetingsButtons = new javax.swing.JPanel();
        bAddMeeting = new javax.swing.JButton();
        bDelMeeting = new javax.swing.JButton();
        bUndoDelMeeting = new javax.swing.JButton();
        bDelAllMeeting = new javax.swing.JButton();
        bUndelAllMeeting = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

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

        add(jToolBar1, java.awt.BorderLayout.SOUTH);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Courses");
        jPanel3.add(jLabel1, java.awt.BorderLayout.NORTH);

        courses.setModel(new javax.swing.table.DefaultTableModel(
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
        coursesPane.setViewportView(courses);

        jPanel3.add(coursesPane, java.awt.BorderLayout.CENTER);

        jSplitPane1.setLeftComponent(jPanel3);

        jPanel4.setLayout(new java.awt.BorderLayout());

        meetingsPane.setPreferredSize(new java.awt.Dimension(300, 275));

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
        meetingsPane.setViewportView(enrollments);

        jPanel4.add(meetingsPane, java.awt.BorderLayout.CENTER);

        lMeetings.setText("Enrollment");
        jPanel4.add(lMeetings, java.awt.BorderLayout.NORTH);

        bAddMeeting.setText("Add");
        bAddMeeting.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bAddMeeting.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bAddMeetingActionPerformed(evt);
            }
        });
        meetingsButtons.add(bAddMeeting);

        bDelMeeting.setText("Del");
        bDelMeeting.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bDelMeeting.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bDelMeetingActionPerformed(evt);
            }
        });
        meetingsButtons.add(bDelMeeting);

        bUndoDelMeeting.setText("(Undo)");
        bUndoDelMeeting.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bUndoDelMeeting.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bUndoDelMeetingActionPerformed(evt);
            }
        });
        meetingsButtons.add(bUndoDelMeeting);

        bDelAllMeeting.setText("Del All");
        bDelAllMeeting.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bDelAllMeeting.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bDelAllMeetingActionPerformed(evt);
            }
        });
        meetingsButtons.add(bDelAllMeeting);

        bUndelAllMeeting.setText("(Undo)");
        bUndelAllMeeting.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bUndelAllMeeting.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bUndelAllMeetingActionPerformed(evt);
            }
        });
        meetingsButtons.add(bUndelAllMeeting);

        jPanel4.add(meetingsButtons, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setRightComponent(jPanel4);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

	private void bUndelAllMeetingActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bUndelAllMeetingActionPerformed
	{//GEN-HEADEREND:event_bUndelAllMeetingActionPerformed
		fapp.guiRun().run(CourseListPanel.this, new ETask() {
		public void run() throws Exception {
			enrolledDb.getSchemaBuf().undeleteAllRows();
		}});

// TODO add your handling code here:
	}//GEN-LAST:event_bUndelAllMeetingActionPerformed

	private void bUndoDelMeetingActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bUndoDelMeetingActionPerformed
	{//GEN-HEADEREND:event_bUndoDelMeetingActionPerformed
		fapp.guiRun().run(CourseListPanel.this, new ETask() {
		public void run() throws Exception {
			enrolledDb.getSchemaBuf().undeleteRow(enrollments.getSelectedRow());
			enrollments.requestFocus();
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bUndoDelMeetingActionPerformed

	private void bDelAllMeetingActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bDelAllMeetingActionPerformed
	{//GEN-HEADEREND:event_bDelAllMeetingActionPerformed
		fapp.guiRun().run(CourseListPanel.this, new ETask() {
		public void run() throws Exception {
			enrolledDb.getSchemaBuf().deleteAllRows();
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bDelAllMeetingActionPerformed

	private void bUndoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bUndoActionPerformed
	{//GEN-HEADEREND:event_bUndoActionPerformed
		fapp.guiRun().run(CourseListPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			all_doSelect(str);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bUndoActionPerformed

	private void bSaveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bSaveActionPerformed
	{//GEN-HEADEREND:event_bSaveActionPerformed
		fapp.guiRun().run(new SqlTask() {
		public void run(SqlRun str) throws Exception {
			// Re-calculate tuition for changed students
			Integer studentid = fapp.schemaSet().getEnumInt("enrollments", "courserole", "student");
			Set<Integer> payers = new TreeSet();
			SchemaBuf sb = enrolledDb.getSchemaBuf();
			for (int i=0; i<sb.getRowCount(); ++i) {
				Integer courserole = (Integer)sb.getValueAt(i, "courserole");
				if (courserole.equals(studentid))
					payers.add((Integer)sb.getValueAt(i, "payerid"));
			}
			enrolledDb.doUpdate(str);
			coursesDb.doUpdate(str);
			TuitionCalc tc = new TuitionCalc(fapp, smod.getTermID());
				tc.setPayerIDs(payers);
				tc.recalcTuition(str);
			str.flush();
			all_doSelect(str);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bSaveActionPerformed

	private void bDelMeetingActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bDelMeetingActionPerformed
	{//GEN-HEADEREND:event_bDelMeetingActionPerformed
		fapp.guiRun().run(CourseListPanel.this, new ETask() {
		public void run() throws Exception {
			enrolledDb.getSchemaBuf().deleteRow(enrollments.getSelectedRow());
			enrollments.requestFocus();
		}});
	}//GEN-LAST:event_bDelMeetingActionPerformed

	private void bAddMeetingActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bAddMeetingActionPerformed
	{//GEN-HEADEREND:event_bAddMeetingActionPerformed
		fapp.guiRun().run(CourseListPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {

			Wizard wizard = new EnrollWizard(fapp, null);
//				wizard.setVal("sperson", vStudentID.getText());
				wizard.setVal("courseid", courseid);
				wizard.setVal("termid", smod.getTermID());

			if (wizard.runWizard("addbycourse")) {
				TuitionCalc tc = new TuitionCalc(fapp, smod.getTermID());
					tc.setPayerIDs(
						" select parent1id from entities es" +
						" where entityid = " + wizard.getVal("entityid"));
					tc.recalcTuition(str);
				enrolledDb.doSelect(str);
			}
		}});

//		fapp.guiRun().run(CourseListPanel.this, new ERunnable()
//		{ public void run() throws Exception
//		  {
//			  enrollmentsSb.getSchemaBuf().insertRow(-1);
//		  }});
// TODO add your handling code here:
	}//GEN-LAST:event_bAddMeetingActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAddMeeting;
    private javax.swing.JButton bDelAllMeeting;
    private javax.swing.JButton bDelMeeting;
    private javax.swing.JButton bSave;
    private javax.swing.JButton bUndelAllMeeting;
    private javax.swing.JButton bUndo;
    private javax.swing.JButton bUndoDelMeeting;
    private citibob.swing.typed.JTypedSelectTable courses;
    private javax.swing.JScrollPane coursesPane;
    private citibob.jschema.swing.SchemaBufTable enrollments;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lMeetings;
    private javax.swing.JPanel meetingsButtons;
    private javax.swing.JScrollPane meetingsPane;
    // End of variables declaration//GEN-END:variables
	
}
