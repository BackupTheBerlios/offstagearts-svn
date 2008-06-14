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
 * OpenClassPanel.java
 *
 * Created on April 13, 2008, 3:24 PM
 */

package offstage.frontdesk;

import citibob.jschema.SqlBufDbModel;
import citibob.sql.RsTasklet2;
import citibob.sql.SqlRun;
import citibob.sql.UpdTasklet;
import citibob.sql.pgsql.SqlDate;
import citibob.sql.pgsql.SqlInteger;
import citibob.swing.typed.Swinger;
import citibob.swing.typed.SwingerMap;
import citibob.task.SqlTask;
import citibob.wizard.TypedHashMap;
import citibob.wizard.Wizard;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Map;
import javax.swing.JOptionPane;
import offstage.FrontApp;
import offstage.accounts.gui.AccountsDB;
import offstage.accounts.gui.TransRegPanel;
import offstage.openclass.OpenClassDB;
import offstage.reports.SummaryReport;
import offstage.schema.Actrans2Schema;

/**
 
 @author  citibob
 */
public class OpenClassPanel extends javax.swing.JPanel
{
FrontApp app;
SqlBufDbModel enrollsDm;
FDPersonModel personDm;
SqlDate sqlDate;
int openclassAcTypeID;
int openclassAssetID;

// =====================================================
// =====================================================

	/** Creates new form OpenClassPanel */
	public OpenClassPanel()
	{
		initComponents();
		summaryPane.setContentType("text/html");
		summaryPane.setEditable(false);
	}

	public void refreshPersonDm(SqlRun str, int entityid)
	{
		personDm.setKey(entityid);
		transRegister.setEntityID(str, entityid);	// comes with own update
		personDm.doSelect(str);
		
		str.execUpdate(new UpdTasklet() {
		public void run() throws Exception {
			String html = SummaryReport.getHtml(personDm, app.sFormatMap());
			summaryPane.setText(html);
			summaryPane.setCaretPosition(0);
		}});
	}
	
	
	public void initRuntime(SqlRun str, final FrontApp xapp)
	{
		this.app = xapp;
		SwingerMap smap = xapp.swingerMap();
		personDm = new FDPersonModel(str, app);
				
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
				refreshPersonDm(str, entityid);
			str.popFlush();
		}});
		
		// ========================================================
		// User Transactions
		Actrans2Schema actrans2Schema = (Actrans2Schema)app.getSchema("actrans2");
//		SchemaBuf actransSb = new SchemaBuf(actrans2Schema) {
//		public boolean isCellEditable(int row, int col) {
//			return false;
////			if (col >= getColumnCount()) return false;
////			if (row >= getRowCount()) return false;
////			if (col == tableoidCol) return false;
////			java.util.Date created = (java.util.Date)getValueAt(row, createdCol);
////			if (created == null) return false;
////			java.util.Date now = new java.util.Date();
////			return (now.getTime() - created.getTime() < 86400 * 1000L);
//		}};
		openclassAcTypeID = app.schemaSet().getEnumInt("actrans2", "cr_actypeid", "openclass");
		openclassAssetID = app.schemaSet().getEnumInt("actrans2amt", "assetid", "openclass");
		transRegister.initRuntime(app, TransRegPanel.EM_NONE, openclassAcTypeID, openclassAssetID);
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
			
			String meetingIdSql =
				" select m.meetingid as id" +
				" from meetings m\n" +
				" inner join courseids c on (m.courseid = c.courseid)\n" +
				" inner join daysofweek dow on (c.dayofweek = dow.javaid)\n" +
				" inner join termids t on (c.termid = t.groupid)\n" +
				" inner join termtypes tt on (t.termtypeid = tt.termtypeid)\n" +
				" where m.dtstart >= " + sqlDate.toSql(dt0) +
				" and m.dtstart < " + sqlDate.toSql(dt1) + "\n" +
				" and dow.javaid >= 0\n" +
                " and tt.name = 'openclass'\n" +
				(proto ? " and false" : "");

			return
				OpenClassDB.classLeadersSql(meetingIdSql,
					"select courseroleid as id from courseroles where name='teacher'") +
	
				" select m.meetingid,m.courseid,m.dtstart,m.dtnext," +
				" c.name as coursename,\n" +
				" _c.mainid as mainid, _c.subid as subid," +
				" c.name || '(' || case when _c.subid is not null then" +
				"	(sub.displayname || ' for ' || teacher.displayname)" +
				"	(teacher.displayname) else" +
				"	end || ')' as displayname," +
				" c.enrolllimit,c.price,dow.shortname as dayofweek,\n" +
				" t.name as termname, t.groupid as termid\n" +
				" from meetings m\n" +
				" inner join courseids c on (m.courseid = c.courseid)\n" +
				" inner join daysofweek dow on (c.dayofweek = dow.javaid)\n" +
				" inner join termids t on (c.termid = t.groupid)\n" +
				" inner join _c on (_c.meetingid = m.meetingid)" +
				" left outer join teachers teacher on (teacher.entityid = _c.mainid)" +
				" left outer join teachers sub on (sub.entityid = _c.subid)" +
				" order by m.dtstart, c.name;\n" +
				
				OpenClassDB.classLeadersDropSql();
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
			
			SqlRun str = app.sqlRun();
			str.pushFlush();
				refreshPersonDm(str, entityid);
			str.popFlush();
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
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane3 = new javax.swing.JSplitPane();
        selector = new offstage.swing.typed.EntitySelector();
        jSplitPane4 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        summaryScroll = new javax.swing.JScrollPane();
        summaryPane = new javax.swing.JTextPane();
        jPanel4 = new javax.swing.JPanel();
        transRegister = new offstage.accounts.gui.TransRegPanel();
        jLabel1 = new javax.swing.JLabel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tMeetings = new citibob.swing.typed.JTypedSelectTable();
        chDate = new citibob.swing.typed.JTypedDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tEnrolls = new citibob.swing.typed.JTypedSelectTable();
        jToolBar2 = new javax.swing.JToolBar();
        jLabel3 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        bBuyClasses = new javax.swing.JButton();
        bRegister = new javax.swing.JButton();
        bRemove = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jSplitPane3.setLeftComponent(selector);

        jPanel3.setLayout(new java.awt.BorderLayout());

        summaryScroll.setViewportView(summaryPane);

        jPanel3.add(summaryScroll, java.awt.BorderLayout.CENTER);

        jSplitPane4.setLeftComponent(jPanel3);

        jPanel4.setLayout(new java.awt.BorderLayout());
        jPanel4.add(transRegister, java.awt.BorderLayout.CENTER);

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel1.setText("Open Class Account");
        jPanel4.add(jLabel1, java.awt.BorderLayout.PAGE_START);

        jSplitPane4.setRightComponent(jPanel4);

        jSplitPane3.setRightComponent(jSplitPane4);

        jSplitPane1.setTopComponent(jSplitPane3);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        tMeetings.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tMeetings);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jScrollPane1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(chDate, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel2.setText("Classes Today");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(jLabel2, gridBagConstraints);

        jSplitPane2.setLeftComponent(jPanel1);

        jPanel2.setLayout(new java.awt.BorderLayout());

        tEnrolls.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tEnrolls);

        jPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jToolBar2.setRollover(true);
        jPanel2.add(jToolBar2, java.awt.BorderLayout.SOUTH);

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel3.setText("Enrollment");
        jPanel2.add(jLabel3, java.awt.BorderLayout.PAGE_START);

        jSplitPane2.setRightComponent(jPanel2);

        jSplitPane1.setBottomComponent(jSplitPane2);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jToolBar1.setRollover(true);

        bBuyClasses.setText("Purchase Class Credit");
        bBuyClasses.setFocusable(false);
        bBuyClasses.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bBuyClasses.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bBuyClasses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBuyClassesActionPerformed(evt);
            }
        });
        jToolBar1.add(bBuyClasses);

        bRegister.setText("Sign-In");
        bRegister.setFocusable(false);
        bRegister.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bRegister.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRegisterActionPerformed(evt);
            }
        });
        jToolBar1.add(bRegister);

        bRemove.setText("Undo Sign-In");
        bRemove.setFocusable(false);
        bRemove.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bRemove.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRemoveActionPerformed(evt);
            }
        });
        jToolBar1.add(bRemove);

        add(jToolBar1, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

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
			
			// Figure out the price & discounts
//			Double basePrice = (Double)tMeetings.getValue("price");
			Map<Integer,Double> dollarDisc = OpenClassDB.getOCDiscounts(str, meetingid, entityid);
			double price = 0;
			for (Map.Entry<Integer,Double> ent : dollarDisc.entrySet()) {
				price += (ent.getKey().intValue() == 0 ?
					ent.getValue() : -ent.getValue());
			}

			// Check the account for available funds...
			Double bal = transRegister.getBalance();
			if (bal.doubleValue() < price) {
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
			Date today = sqlDate.truncate(new Date());
			
			TypedHashMap optional = new TypedHashMap();
				optional.put("description", "Open Class");
				optional.put("studentid", entityid);
				optional.put("termid", (Integer)tMeetings.getValue()); // Store the meetingid, so we can yank later on an undo
			sql = AccountsDB.w_actrans2_insert_sql(app, entityid, "openclass", openclassAcTypeID,
				"openclass", today, optional,
				new int[] {openclassAssetID}, new double[] {-price});

//			sql =
//				" insert into actrans " +
//				" (entityid, actranstypeid, actypeid, date, amount, description, studentid, termid)" +
//				" values (" + SqlInteger.sql(entityid) + ", " +
//				" (select actranstypeid from actranstypes where name = 'openclass'),\n" +
//				" (select actypeid from actypes where name = 'openclass'),\n " +
//				sqlDate.toSql(today) + ", " +
//				SqlDouble.sql(price) + ", " +
//				"'Open Class', " +
//				SqlInteger.sql(entityid) + ", " +
//				SqlInteger.sql((Integer)tMeetings.getValue()) +		// Store the meetingid, so we can yank later on an undo
//				")";
			str.execSql(sql);
				
			// Refresh
			enrollsDm.doSelect(str);
			transRegister.refresh(str);
		}});
	}//GEN-LAST:event_bRegisterActionPerformed

	private void bBuyClassesActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bBuyClassesActionPerformed
	{//GEN-HEADEREND:event_bBuyClassesActionPerformed
                app.guiRun().run(OpenClassPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
                        Integer entityid = (Integer)personDm.getKey();
                        Wizard wizard = new offstage.frontdesk.wizards.BuyClassesWizard(app, OpenClassPanel.this, entityid);
                        wizard.setVal("entityid", entityid);
                        wizard.runWizard();
			transRegister.refresh(str);
		}});
}//GEN-LAST:event_bBuyClassesActionPerformed

	private void bRemoveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bRemoveActionPerformed
	{//GEN-HEADEREND:event_bRemoveActionPerformed
		app.guiRun().run(this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			// Check that user has provided right parameters
			final Integer meetingid = (Integer)tMeetings.getValue();
			final Integer entityid = (Integer)tEnrolls.getValue();
			if (entityid == null || meetingid == null) {
				JOptionPane.showMessageDialog(OpenClassPanel.this,
					"You must select a person\nand course to register.");
				return;
			}
			
			// Get the amount to refund
//			Date dt0 = (Date)chDate.getValue();
//			Date dt1 = new Date(dt0.getTime() + 86400*1000L);	// DST not a problem, it's middle of the night
			String sql =
				" select -sum(amount) as amount from (\n" +
				" select tr.actransid, amount\n" +
				" from actrans2 tr, actrans2amt amt\n" +
				" where amt.actransid = tr.actransid\n" +
				" and tr.termid = " + meetingid + "\n" +
				" and cr_actypeid = " + openclassAcTypeID +
				" and cr_entityid = " + entityid +
				" and db_actypeid = " + openclassAcTypeID +
				" and db_entityid = (select entityid from entities where orgname = 'openclass' and sink)\n" +
				" and assetid = " + openclassAssetID + "\n" +
				" 	UNION\n" +
				" select tr.actransid, -amount as amount\n" +
				" from actrans2 tr, actrans2amt amt\n" +
				" where amt.actransid = tr.actransid\n" +
				" and tr.termid = " + meetingid + "\n" +
				" and db_actypeid = " + openclassAcTypeID +
				" and db_entityid = " + entityid +
				" and cr_actypeid = " + openclassAcTypeID +
				" and cr_entityid = (select entityid from entities where orgname = 'openclass' and sink)\n" +
				" and assetid = " + openclassAssetID + "\n" +
				" ) xx\n";
			str.execSql(sql, new RsTasklet2() {
			public void run(SqlRun str, ResultSet rs) throws SQLException {
				// Get the amount to refund
				double refund = 0;
				if (rs.next()) refund = rs.getDouble(1);
				
				// Refund the account if we need to
				StringBuffer sql = new StringBuffer();
				if (refund > 0) {
					TypedHashMap optional = new TypedHashMap();
						optional.put("description", "Open class sign-up cancelled");
						optional.put("studentid", entityid);
						optional.put("termid", meetingid);
					str.execSql(AccountsDB.w_actrans2_insert_sql(
						app, entityid, "openclass", openclassAcTypeID,
						"openclass", sqlDate.truncate(new Date()), optional,
						new int[] {openclassAssetID}, new double[] {refund}));
				}
				
				// Remove from the class
				str.execSql(
					" delete from subs" +
					" where meetingid = " + meetingid +
					" and entityid = " + entityid);
				
				// Refresh display
				enrollsDm.doSelect(str);
				transRegister.refresh(str);
			}});
			
		}});
}//GEN-LAST:event_bRemoveActionPerformed


	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bBuyClasses;
    private javax.swing.JButton bRegister;
    private javax.swing.JButton bRemove;
    private citibob.swing.typed.JTypedDateChooser chDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JSplitPane jSplitPane4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private offstage.swing.typed.EntitySelector selector;
    private javax.swing.JTextPane summaryPane;
    private javax.swing.JScrollPane summaryScroll;
    private citibob.swing.typed.JTypedSelectTable tEnrolls;
    private citibob.swing.typed.JTypedSelectTable tMeetings;
    private offstage.accounts.gui.TransRegPanel transRegister;
    // End of variables declaration//GEN-END:variables
	
};
