/*
 * OpenClassPanel.java
 *
 * Created on April 13, 2008, 3:24 PM
 */

package offstage.frontdesk;

import citibob.jschema.DbModel;
import citibob.jschema.SchemaBuf;
import citibob.jschema.SqlBufDbModel;
import citibob.jschema.SqlSchema;
import citibob.sql.SqlDouble;
import citibob.sql.SqlRun;
import citibob.sql.UpdTasklet;
import citibob.sql.pgsql.SqlString;
import citibob.sql.pgsql.SqlDate;
import citibob.sql.pgsql.SqlInteger;
import citibob.swing.typed.Swinger;
import citibob.swing.typed.SwingerMap;
import citibob.task.SqlTask;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import offstage.FrontApp;
import offstage.schema.ActransSchema;
import offstage.school.tuition.TuitionData;

/**
 
 @author  citibob
 */
public class OpenClassPanel extends javax.swing.JPanel
{
FrontApp app;
SqlBufDbModel enrollsDm;
DbModel personDm;
SqlDate sqlDate;

// =====================================================
// =====================================================

	/** Creates new form OpenClassPanel */
	public OpenClassPanel()
	{
		initComponents();
	}
	
	public void initRuntime(SqlRun str, final FrontApp xapp)
	{
		this.app = xapp;
		SwingerMap smap = xapp.swingerMap();

		personPanel.initRuntime(str, xapp);
		personDm = personPanel.dmod;
//		searchPanel.initRuntime(xapp, personDm);
		
		// Change entity when a person is selected.
		selector.initRuntime(app);
		selector.setAutoSelectOnOne(true);
		selector.addPropertyChangeListener("value", new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			Integer entityid = (Integer)evt.getNewValue();
			if (entityid == null) return;
			
			SqlRun str = app.sqlRun();
			str.pushFlush();
				personDm.setKey(entityid);
				transReg.setEntityID(str, entityid);	// comes with own update
				personDm.doSelect(str);
			str.popFlush();
		}});
		
		// ========================================================
		// User Transactions
		SqlSchema actransSchema = app.getSchema("actrans");
		SchemaBuf actransSb = new SchemaBuf(actransSchema) {
		public boolean isCellEditable(int row, int col) {
			return false;
//			if (col >= getColumnCount()) return false;
//			if (row >= getRowCount()) return false;
//			if (col == tableoidCol) return false;
//			java.util.Date created = (java.util.Date)getValueAt(row, createdCol);
//			if (created == null) return false;
//			java.util.Date now = new java.util.Date();
//			return (now.getTime() - created.getTime() < 86400 * 1000L);
		}};
		transReg.initRuntime(app, actransSb, ActransSchema.AC_OPENCLASS, false);
		// ========================================================
		// ========================================================
		// tMeetings...
		
		// Set up date chooser
		sqlDate = new SqlDate(xapp.timeZone(), false);
		Swinger swinger = smap.newSwinger(sqlDate);
		swinger.configureWidget(chDate);

		// Set up model for list of course meetings
		final SqlBufDbModel meetingsDm = new SqlBufDbModel(str, xapp,
			new String[] {"meetings", "courseids", "termids"},
			null,
			null) {
		public String getSelectSql(boolean proto) {
			Date dt0 = (Date)chDate.getValue();
			Date dt1 = new Date(dt0.getTime() + 86400*1000L);	// DST not a problem, it's middle of the night
			return
				" select m.meetingid,m.courseid,m.dtstart,m.dtnext,c.name as coursename,\n" +
				" c.enrolllimit,c.price,dow.shortname as dayofweek,\n" +
				" t.name as termname, t.groupid as termid\n" +
				" from meetings m\n" +
				" inner join courseids c on (m.courseid = c.courseid)\n" +
				" inner join daysofweek dow on (c.dayofweek = dow.javaid)\n" +
				" inner join termids t on (c.termid = t.groupid)\n" +
				" where m.dtstart >= " + sqlDate.toSql(dt0) +
				" and m.dtstart < " + sqlDate.toSql(dt1) + "\n" +
				" and dow.javaid >= 0\n" +
				(proto ? " and false" : "") +
				" order by m.dtstart, c.name";
		}};
//		meetingsDm.getSchemaBuf().
		
		// Set up in the table
		str.execUpdate(new UpdTasklet() {
		public void run() {
			tMeetings.setModelU(meetingsDm.getSchemaBuf(),
				new String[] {"Course", "Start Time", "Price", "Term"},
				new String[] {"coursename", "dtstart", "price", "termname"},
				new boolean[] {false,false,false,false},
				xapp.swingerMap());
//			tMeetings.setFormatU("dayofweek", new DayOfWeekKeyedModel());
			//tMeetings.setFormatU("coursename", new StringSFormat());
			//tMeetings.setFormatU("termname", new StringSFormat());
			tMeetings.setFormatU("dtstart", "hh:mm a");
			tMeetings.setFormatU("price", NumberFormat.getCurrencyInstance());
			
			tMeetings.setValueColU("meetingid");
		}});

		// Refresh courses when date changes
		chDate.addPropertyChangeListener("value", new PropertyChangeListener() {
	    public void propertyChange(PropertyChangeEvent evt) {
			xapp.sqlRun().pushFlush();
			meetingsDm.doSelect(xapp.sqlRun());
			xapp.sqlRun().popFlush();
		}});

		// ============================================================
		// Enrollment in Course
		// Set up model for list of course meetings
		enrollsDm = new SqlBufDbModel(str, xapp,
			new String[] {"entities"},
			null,
			null) {
		public String getSelectSql(boolean proto) {
			Integer meetingID = (Integer)tMeetings.getValue();
			String sql =
				// Main Enrollment
				" select xx.entityid,xx.enrolled,cr.courseroleid,cr.name as courserole,e.lastname,e.firstname\n" +
				" from\n" +
				" (select m.meetingid, m.courseid, e.entityid, e.courserole, true as enrolled\n" +
				" from meetings m\n" +
				" inner join courseids c on (c.courseid = m.courseid)\n" +
				" inner join enrollments e on (c.courseid = e.courseid)\n" +
				" where m.meetingid = " + SqlInteger.sql(meetingID) + "\n" +
				(proto ? " and false\n" : "") +
				" 	UNION\n" +
				
				// Addition Subs
				" select s.meetingid, m.courseid, s.entityid, s.courserole, false as enrolled\n" +
				" from subs s\n" +
				" inner join meetings m on (s.meetingid = m.meetingid)\n" +
				" where s.meetingid = " + SqlInteger.sql(meetingID) + "\n" +
				" and subtype = '+'\n" +
				(proto ? " and false\n" : "") +
				" 	EXCEPT\n" +
				
				// Subtraction Subs
				" select s.meetingid, m.courseid, s.entityid, e.courserole, true as enrolled\n" +
				" from subs s\n" +
				" inner join meetings m on (s.meetingid = m.meetingid)\n" +
				" inner join courseids c on (c.courseid = m.courseid)\n" +
				" inner join enrollments e on (c.courseid = e.courseid and s.entityid = e.entityid)\n" +
				" where s.meetingid = " + SqlInteger.sql(meetingID) + "\n" +
				" and subtype = '-'" +
				(proto ? " and false\n" : "") +
				" ) xx\n" +
				" inner join courseroles cr on (cr.courseroleid = xx.courserole)\n" +
				" inner join entities e on (xx.entityid = e.entityid)\n" +
				" order by cr.orderid, e.lastname, e.firstname\n";
			return sql;
		}};
		
		// Set up in the table
		str.execUpdate(new UpdTasklet() {
		public void run() {
			tEnrolls.setModelU(enrollsDm.getSchemaBuf(),
				new String[] {"ID", "Last Name", "First Name", "Role", "Enrolled"},
				new String[] {"entityid", "lastname", "firstname", "courserole", "enrolled"},
				new boolean[] {false,false,false,false, false},
				xapp.swingerMap());
			//tEnrolls.setFormatU("courserole", new StringSFormat());
			tEnrolls.setValueColU("entityid");
		}});

		// Select new course on right when course selection changes 
		tMeetings.addPropertyChangeListener("value", new PropertyChangeListener() {
	    public void propertyChange(PropertyChangeEvent evt) {
			xapp.sqlRun().pushFlush();
			enrollsDm.doSelect(xapp.sqlRun());
			xapp.sqlRun().popFlush();
		}});

		// Select person from enrollments pane
		tEnrolls.addPropertyChangeListener("value", new PropertyChangeListener() {
	    public void propertyChange(PropertyChangeEvent evt) {
			Integer entityid = (Integer)tEnrolls.getValue();
			if (entityid == null) return;
			
			xapp.sqlRun().pushFlush();
			DbModel dm = personDm;
			dm.setKey(entityid);
			dm.doSelect(xapp.sqlRun());
			xapp.sqlRun().popFlush();
		}});
	
		str.execUpdate(new UpdTasklet() {
		public void run() {
			// Set date to today --- get the ball rolling for refresh!
			chDate.setValue(sqlDate.truncate(new java.util.Date()));
		}});
	}
	/** This method is called from within the constructor to
	 initialize the form.
	 WARNING: Do NOT modify this code. The content of this method is
	 always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane3 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        personPanel = new offstage.frontdesk.FDPersonPanel();
        jToolBar1 = new javax.swing.JToolBar();
        bSavePerson = new javax.swing.JButton();
        bUndoPerson = new javax.swing.JButton();
        bBuyClasses = new javax.swing.JButton();
        jSplitPane4 = new javax.swing.JSplitPane();
        transReg = new offstage.accounts.gui.TransRegPanel();
        selector = new offstage.swing.typed.EntitySelector();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tMeetings = new citibob.swing.typed.JTypedSelectTable();
        chDate = new citibob.swing.typed.JTypedDateChooser();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tEnrolls = new citibob.swing.typed.JTypedSelectTable();
        jToolBar2 = new javax.swing.JToolBar();
        bRegister = new javax.swing.JButton();
        bRemove = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel3.setLayout(new java.awt.BorderLayout());
        jPanel3.add(personPanel, java.awt.BorderLayout.PAGE_START);

        jToolBar1.setRollover(true);

        bSavePerson.setText("Save");
        bSavePerson.setFocusable(false);
        bSavePerson.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bSavePerson.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bSavePerson.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bSavePersonActionPerformed(evt);
            }
        });
        jToolBar1.add(bSavePerson);

        bUndoPerson.setText("Undo");
        bUndoPerson.setFocusable(false);
        bUndoPerson.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bUndoPerson.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bUndoPerson.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bUndoPersonActionPerformed(evt);
            }
        });
        jToolBar1.add(bUndoPerson);

        bBuyClasses.setText("Buy Classes");
        bBuyClasses.setFocusable(false);
        bBuyClasses.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bBuyClasses.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bBuyClasses.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bBuyClassesActionPerformed(evt);
            }
        });
        jToolBar1.add(bBuyClasses);

        jPanel3.add(jToolBar1, java.awt.BorderLayout.SOUTH);

        jSplitPane3.setLeftComponent(jPanel3);

        jSplitPane4.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane4.setTopComponent(transReg);
        jSplitPane4.setBottomComponent(selector);

        jSplitPane3.setRightComponent(jSplitPane4);

        jSplitPane1.setTopComponent(jSplitPane3);

        jPanel1.setLayout(new java.awt.BorderLayout());

        tMeetings.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tMeetings);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);
        jPanel1.add(chDate, java.awt.BorderLayout.NORTH);

        jSplitPane2.setLeftComponent(jPanel1);

        jPanel2.setLayout(new java.awt.BorderLayout());

        tEnrolls.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tEnrolls);

        jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jToolBar2.setRollover(true);

        bRegister.setText("Register");
        bRegister.setFocusable(false);
        bRegister.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bRegister.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bRegister.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bRegisterActionPerformed(evt);
            }
        });
        jToolBar2.add(bRegister);

        bRemove.setText("Remove");
        bRemove.setFocusable(false);
        bRemove.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bRemove.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bRemove.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bRemoveActionPerformed(evt);
            }
        });
        jToolBar2.add(bRemove);

        jPanel2.add(jToolBar2, java.awt.BorderLayout.SOUTH);

        jSplitPane2.setRightComponent(jPanel2);

        jSplitPane1.setBottomComponent(jSplitPane2);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

	private void bUndoPersonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bUndoPersonActionPerformed
	{//GEN-HEADEREND:event_bUndoPersonActionPerformed
		app.guiRun().run(this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			personDm.doSelect(str);
			transReg.refresh(str);
		}});
	}//GEN-LAST:event_bUndoPersonActionPerformed

	private void bRegisterActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bRegisterActionPerformed
	{//GEN-HEADEREND:event_bRegisterActionPerformed
		app.guiRun().run(this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			// Check that user has provided right parameters
			Integer entityid = (Integer)personDm.getKey();
			Integer meetingid = (Integer)tMeetings.getValue();
			if (entityid == null || meetingid == null) {
				JOptionPane.showMessageDialog(OpenClassPanel.this,
					"You must select a person\nand course to register.");
				return;
			}
			
			// Check the account for available funds...
			Double price = (Double)tMeetings.getValue("price");
			Double bal = transReg.getBalance();
			if (-bal.doubleValue() < price.doubleValue()) {
				JOptionPane.showMessageDialog(OpenClassPanel.this,
					"Insufficient funds.  You must first buy classes.");
				return;
				
			}
			
			// Do the registration
			String sql =
				" insert into subs" +
				" (meetingid, entityid, subtype, courserole, dtapproved, enterdtime) values\n(" +
				SqlInteger.sql(meetingid) + ", " +
				SqlInteger.sql(entityid) + ", " +
				"'+', (select courseroleid from courseroles where name = 'student'), " +
				"now(), now())";
			str.execSql(sql);
			
			// Debit the account
			Date today = new Date();
			sqlDate.truncate(today);
			sql =
				" insert into actrans " +
				" (entityid, actranstypeid, actypeid, date, amount, description, studentid, termid)" +
				" values (" + SqlInteger.sql(entityid) + ", " +
				" (select actranstypeid from actranstypes where name = 'openclass'),\n" +
				" (select actypeid from actypes where name = 'openclass'),\n " +
				sqlDate.toSql(today) + ", " +
				SqlDouble.sql(price) + ", " +
				"'Open Class', " +
				SqlInteger.sql(entityid) + ", " +
				SqlInteger.sql((Integer)tMeetings.getValue()) +		// Store the meetingid, so we can yank later on an undo
				")";
			str.execSql(sql);
				
			// Refresh
			enrollsDm.doSelect(str);
			transReg.refresh(str);
		}});
	}//GEN-LAST:event_bRegisterActionPerformed

	private void bSavePersonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bSavePersonActionPerformed
	{//GEN-HEADEREND:event_bSavePersonActionPerformed
		app.guiRun().run(this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			personDm.doUpdate(str);
			personDm.doSelect(str);
		}});
	}//GEN-LAST:event_bSavePersonActionPerformed

	private void bBuyClassesActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bBuyClassesActionPerformed
	{//GEN-HEADEREND:event_bBuyClassesActionPerformed
		// TODO add your handling code here:
}//GEN-LAST:event_bBuyClassesActionPerformed

	private void bRemoveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bRemoveActionPerformed
	{//GEN-HEADEREND:event_bRemoveActionPerformed
		app.guiRun().run(this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			// Check that user has provided right parameters
			Integer meetingid = (Integer)tMeetings.getValue();
			Integer entityid = (Integer)tEnrolls.getValue();
			if (entityid == null || meetingid == null) {
				JOptionPane.showMessageDialog(OpenClassPanel.this,
					"You must select a person\nand course to register.");
				return;
			}
			
			String sql =
				// Remove sub record
				" delete from subs" +
				" where entityid = " + SqlInteger.sql(entityid) +
				" and meetingid = " + SqlInteger.sql(meetingid) + ";\n" +			
				// Remove payment record
				" delete from actrans" +
				" where entityid = " + SqlInteger.sql(entityid) + "\n" +
				" and actypeid = (select actypeid from actypes where name = 'openclass')\n " +
				" and termid = " + SqlInteger.sql(meetingid) + ";\n";
			str.execSql(sql);
			
			// Refresh display
			enrollsDm.doSelect(str);
			transReg.refresh(str);
		}});
}//GEN-LAST:event_bRemoveActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bBuyClasses;
    private javax.swing.JButton bRegister;
    private javax.swing.JButton bRemove;
    private javax.swing.JButton bSavePerson;
    private javax.swing.JButton bUndoPerson;
    private citibob.swing.typed.JTypedDateChooser chDate;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JSplitPane jSplitPane4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private offstage.frontdesk.FDPersonPanel personPanel;
    private offstage.swing.typed.EntitySelector selector;
    private citibob.swing.typed.JTypedSelectTable tEnrolls;
    private citibob.swing.typed.JTypedSelectTable tMeetings;
    private offstage.accounts.gui.TransRegPanel transReg;
    // End of variables declaration//GEN-END:variables
	
};
