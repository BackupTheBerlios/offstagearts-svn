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
 * CleansePanel.java
 *
 * Created on November 3, 2007, 10:54 PM
 */

package offstage.cleanse;

import citibob.jschema.*;
import citibob.task.*;
import citibob.sql.*;
import java.util.*;
import java.sql.*;
import offstage.devel.gui.DevelModel;
import offstage.schema.*;
import citibob.jschema.log.*;
import offstage.db.*;
import offstage.*;
import citibob.app.*;
import citibob.sql.pgsql.*;
import citibob.swing.WidgetTree;
import citibob.swing.table.SortedTableModel;
import citibob.types.KeyedModel;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Date;
import javax.swing.*;
import javax.swing.event.*;
import offstage.reports.SummaryReport;

/**
 *
 * @author  citibob
 */
public class CleansePanel extends javax.swing.JPanel
{

	
// Mode we're operating in.
int cleanseMode;
public static final int M_REGULAR = 0;
public static final int M_PROVISIONAL = 1;
public static final int M_APPROVE = 2;

// Merge codes in the mergelog table
public static final int MC_DUPOK = 0;
public static final int MC_MERGE_TO_0 = 1;		// Merge into entityid0
public static final int MC_MERGE_TO_1 = 2;
public static final int MC_DEL_0 = 3;
public static final int MC_DEL_1 = 4;
public static final int MC_DEL_BOTH = 5;
static KeyedModel actionKmodel = new KeyedModel();
static {
	actionKmodel.addItem(null, "");
	actionKmodel.addItem(MC_DUPOK, "Do Not Merge");
	actionKmodel.addItem(MC_MERGE_TO_0, "<- Merge");
	actionKmodel.addItem(MC_MERGE_TO_1, "Merge ->");
	actionKmodel.addItem(MC_DEL_0, "Delete New");
	actionKmodel.addItem(MC_DEL_1, "Delete Old");
	actionKmodel.addItem(MC_DEL_BOTH, "Delete Both");
}


FrontApp app;

//int dupid = 0;				// Duplicates run to look at
int dbid0, dbid1;

// The two records we're comparing
DevelModel[] dm = new DevelModel[2];
Integer curAction;		// Action of the currently-selected dup row
MultiDbModel allDm;		// = dm[0] and dm[1]
//RSTableModel dupModel;
DupDbModel dupDm;
//Integer entityid0, entityid1;
//String dupType;

	/** Creates new form CleansePanel */
	public CleansePanel()
	{
		initComponents();
// Format for smaller screen!
displayTabs.remove(editTab);
		
		
		Color color;
		color = new java.awt.Color(51, 204, 0);
        entityPanel0.setAllBackground(color);
		summaryPane0.setBackground(color);
//		lNewRecord.setBackground(color);		// Didn't have any effect
		
		color = new java.awt.Color(255, 204, 204);
        entityPanel1.setAllBackground(color);
		summaryPane1.setBackground(color);
//		lOldRecord.setBackground(color);
		
		summaryPane0.setContentType("text/html");
		summaryPane0.setEditable(false);
		summaryPane1.setContentType("text/html");
		summaryPane1.setEditable(false);
		
		tfSearch.addKeyListener(new KeyAdapter() {
		public void keyTyped(KeyEvent e) {
			//System.out.println(e.getKeyChar());
			if (e.getKeyChar() == '\n') {
				bSearchActionPerformed(null);
			}
		}});
		
		dupTable.addPropertyChangeListener("value", new java.beans.PropertyChangeListener() {
//		dupTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//		public void valueChanged(final ListSelectionEvent e) {
	    public void propertyChange(final java.beans.PropertyChangeEvent evt) {
			// User wants to switch to a new cell...
			app.guiRun().run(CleansePanel.this, new SqlTask() {
			public void run(SqlRun str) throws Exception {
				if (evt.getNewValue() == null) return;		// We've become un-selected
				reselect(str);
			}});
		}});
	}
	
	void reselect(SqlRun str)
	{
	//				int row = e.getFirstIndex();
	//				dm[0].setKey((Integer)dupModel.getValueAt(row, "entityid0"));
	//				dm[1].setKey((Integer)dupModel.getValueAt(row, "entityid1"));
		Integer Entityid0 = (Integer)dupTable.getValue("entityid0");
		Integer Entityid1 = (Integer)dupTable.getValue("entityid1");
	//System.out.println("EntityID changed: " + entityid0 + " " + entityid1);

//		// Swapt so newest is first (if we're not approving past merges)
//		if (cleanseMode != M_APPROVE) {
//			Date dt0 = (Date)dupTable.getValue("lastupdated0");
//			long ms0 = (dt0 == null ? 0 : dt0.getTime());
//			Date dt1 = (Date)dupTable.getValue("lastupdated1");
//			long ms1 = (dt1 == null ? 0 : dt1.getTime());
//
//			// Swap so newer record is always on the left.
//			if (ms0 < ms1) {
//				Integer EID = Entityid0;
//				Entityid0 = Entityid1;
//				Entityid1 = EID;
//			}
////	System.out.println("XYZZZ: " + dt0 + " " + dt1);
//		}
		curAction = (Integer)dupTable.getValue("action");
		bApproveAction.setText((String)actionKmodel.get(curAction).obj);
		dm[0].setKey(Entityid0);
		dm[1].setKey(Entityid1);
		refresh(str);
	}
	void refresh(SqlRun str)
	{
		allDm.doSelect(str);
		str.execUpdate(new UpdTasklet() {
		public void run() throws Exception {
			SummaryReport sr = new SummaryReport(app);
			
			String html0 = sr.getHtml(dm[0]);
			summaryPane0.setText(html0);
			summaryPane0.setCaretPosition(0);
			
			String html1 = sr.getHtml(dm[1]);
			summaryPane1.setText(html1);
			summaryPane1.setCaretPosition(0);
		}});
	}
	
	void setDbids(SqlRun str, Integer dbid0, Integer dbid1)
	{
		if (dbid0 == null || dbid1 == null) return;

		this.dbid0 = dbid0;
		this.dbid1 = dbid1;
		dupDm.doSelect(str);
//		refresh(str);
	}
//	void setDupid(SqlRun str, int dupid)
//	{
//		this.dupid = dupid;
//		dupDm.doSelect(str);
////		refresh(str);
//	}
	
	/** @param dupType = 'a' (address), 'n' (names), 'o' (organization) */
	public void initRuntime(SqlRun str, FrontApp fapp, final Integer Dbid0, final Integer Dbid1, int cleanseMode)
	{
		this.app = fapp;
//		this.dupType = dupType;
//		this.dupid = dupid;
		this.cleanseMode = cleanseMode;

		cbDbid0.setKeyedModel(app.schemaSet().getKeyedModel("entities", "dbid"));
		cbDbid0.setValue(0);
		cbDbid1.setKeyedModel(app.schemaSet().getKeyedModel("entities", "dbid"));
		cbDbid1.setValue(0);
		
		PropertyChangeListener propChange = new java.beans.PropertyChangeListener() {
	    public void propertyChange(final java.beans.PropertyChangeEvent evt) {
			
			// User wants to switch to a new cell...
			app.guiRun().run(CleansePanel.this, new SqlTask() {
			public void run(SqlRun str) throws Exception {
				setDbids(str, (Integer)cbDbid0.getValue(), (Integer)cbDbid1.getValue());
			}});
		}};
		cbDbid0.addPropertyChangeListener("value", propChange);
		cbDbid1.addPropertyChangeListener("value", propChange);

		
		bApproveAction.setText((String)actionKmodel.get(null).obj);
		if (cleanseMode != M_APPROVE) bApproveAction.setEnabled(false);
		
		dm[0] = new DevelModel(app);
		entityPanel0.initRuntime(str, fapp, dm[0]);
		dm[1] = new DevelModel(app);
		entityPanel1.initRuntime(str, fapp, dm[1]);
		allDm = new MultiDbModel(dm);

		// Set up duplicates display
		dupDm = new DupDbModel();
		
		str.execUpdate(new UpdTasklet2() {
		public void run(SqlRun str) {
			dupDm.setIdSql(null);
			dupDm.doSelect(str);
			
			dupTable.setModelU(new SortedTableModel(dupDm.getSchemaBuf()),
				new String[] {"#", "Score", "Action", "ID-0", "Name-0", "ID-1", "Name-1"},
	//				new String[] {"score", "score", "entityid0", "string0", "entityid1", "string1"},
				new String[] {"__rowno__", "score", "action", "entityid0", "string0", "entityid1", "string1"},
				new String[] {null, null, null, "string0", "string0", "string1", "string1"},
				new boolean[] {false, false,false,false,false,false,false},
				app.swingerMap());
	//			dupTable.setRenderEditU("score", new java.text.DecimalFormat("#.00"));
			dupTable.setFormatU("action", actionKmodel);
			dupTable.setFormatU("score", "#.00");
			dupTable.setValueColU("__rowno__");
			
			setDbids(str, Dbid0, Dbid1);
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

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        dupTablePane = new javax.swing.JScrollPane();
        dupTable = new citibob.swing.typed.JTypedSelectTable();
        leftButtonPanel = new javax.swing.JPanel();
        bDelete0 = new javax.swing.JButton();
        bMergeTo0 = new javax.swing.JButton();
        bDeleteBoth = new javax.swing.JButton();
        bDelete1 = new javax.swing.JButton();
        bDupOK = new javax.swing.JButton();
        bMergeTo1 = new javax.swing.JButton();
        bApproveAction = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        bSave = new javax.swing.JButton();
        bRefreshList = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        tfSearch = new javax.swing.JTextField();
        bSearch = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        bApproveAll = new javax.swing.JButton();
        rightButtonPanel = new javax.swing.JPanel();
        bSubordinate1 = new javax.swing.JButton();
        bSubordinate0 = new javax.swing.JButton();
        displayTabs = new javax.swing.JTabbedPane();
        editTab = new javax.swing.JPanel();
        entityPanel0 = new offstage.devel.gui.EntityPanel();
        entityPanel1 = new offstage.devel.gui.EntityPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        summaryPane0 = new javax.swing.JTextPane();
        lNewRecord = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        summaryPane1 = new javax.swing.JTextPane();
        lOldRecord = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cbDbid0 = new citibob.swing.typed.JKeyedComboBox();
        jLabel4 = new javax.swing.JLabel();
        cbDbid1 = new citibob.swing.typed.JKeyedComboBox();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        dupTable.setModel(new javax.swing.table.DefaultTableModel(
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
        dupTablePane.setViewportView(dupTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(dupTablePane, gridBagConstraints);

        leftButtonPanel.setLayout(new java.awt.GridBagLayout());

        bDelete0.setText("Delete New");
        bDelete0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDelete0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        leftButtonPanel.add(bDelete0, gridBagConstraints);

        bMergeTo0.setText("<- Merge");
        bMergeTo0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bMergeTo0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        leftButtonPanel.add(bMergeTo0, gridBagConstraints);

        bDeleteBoth.setText("Delete Both");
        bDeleteBoth.setFocusable(false);
        bDeleteBoth.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bDeleteBoth.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bDeleteBoth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDeleteBothActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(13, 3, 3, 3);
        leftButtonPanel.add(bDeleteBoth, gridBagConstraints);

        bDelete1.setText("Delete Old");
        bDelete1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDelete1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        leftButtonPanel.add(bDelete1, gridBagConstraints);

        bDupOK.setText("Do Not Merge");
        bDupOK.setPreferredSize(new java.awt.Dimension(103, 40));
        bDupOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDupOKActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 3);
        leftButtonPanel.add(bDupOK, gridBagConstraints);

        bMergeTo1.setText("Merge ->");
        bMergeTo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bMergeTo1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        leftButtonPanel.add(bMergeTo1, gridBagConstraints);

        bApproveAction.setText("Delete Both");
        bApproveAction.setFocusable(false);
        bApproveAction.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bApproveAction.setPreferredSize(new java.awt.Dimension(94, 40));
        bApproveAction.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bApproveAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bApproveActionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 3);
        leftButtonPanel.add(bApproveAction, gridBagConstraints);

        jLabel2.setText("Approve Provisional Decision:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(16, 0, 0, 0);
        leftButtonPanel.add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel2.add(leftButtonPanel, gridBagConstraints);

        bSave.setText("Save");
        bSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(bSave);

        bRefreshList.setText("Refresh List");
        bRefreshList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRefreshListActionPerformed(evt);
            }
        });
        jToolBar1.add(bRefreshList);

        jLabel1.setText("       ");
        jToolBar1.add(jLabel1);

        tfSearch.setMaximumSize(new java.awt.Dimension(120, 2147483647));
        tfSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfSearchActionPerformed(evt);
            }
        });
        jToolBar1.add(tfSearch);

        bSearch.setFocusable(false);
        bSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bSearch.setLabel("Search");
        bSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSearchActionPerformed(evt);
            }
        });
        jToolBar1.add(bSearch);

        jLabel5.setText("       ");
        jToolBar1.add(jLabel5);

        bApproveAll.setText("Approve All");
        bApproveAll.setFocusable(false);
        bApproveAll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bApproveAll.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bApproveAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bApproveAllActionPerformed(evt);
            }
        });
        jToolBar1.add(bApproveAll);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel2.add(jToolBar1, gridBagConstraints);

        rightButtonPanel.setLayout(new java.awt.GridBagLayout());

        bSubordinate1.setText("Subordinate");
        bSubordinate1.setEnabled(false);
        bSubordinate1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSubordinate1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        rightButtonPanel.add(bSubordinate1, gridBagConstraints);

        bSubordinate0.setText("Subordinate");
        bSubordinate0.setEnabled(false);
        bSubordinate0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSubordinate0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        rightButtonPanel.add(bSubordinate0, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel2.add(rightButtonPanel, gridBagConstraints);

        jSplitPane1.setRightComponent(jPanel2);

        editTab.setLayout(new javax.swing.BoxLayout(editTab, javax.swing.BoxLayout.LINE_AXIS));

        entityPanel0.setBackground(new java.awt.Color(51, 204, 0));
        editTab.add(entityPanel0);

        entityPanel1.setBackground(new java.awt.Color(255, 204, 204));
        editTab.add(entityPanel1);

        displayTabs.addTab("Edit", editTab);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jPanel4.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setViewportView(summaryPane0);

        jPanel4.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        lNewRecord.setFont(new java.awt.Font("Lucida Grande", 0, 24));
        lNewRecord.setText("New Record");
        jPanel4.add(lNewRecord, java.awt.BorderLayout.PAGE_START);

        jPanel3.add(jPanel4);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setViewportView(summaryPane1);

        jPanel5.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        lOldRecord.setFont(new java.awt.Font("Lucida Grande", 0, 24));
        lOldRecord.setText("Old Record");
        jPanel5.add(lOldRecord, java.awt.BorderLayout.PAGE_START);

        jPanel3.add(jPanel5);

        displayTabs.addTab("Summary", jPanel3);

        jSplitPane1.setTopComponent(displayTabs);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel3.setText("Duplicate Run: ");
        jPanel6.add(jLabel3, new java.awt.GridBagConstraints());

        cbDbid0.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(cbDbid0, gridBagConstraints);

        jLabel4.setText(" -- ");
        jPanel6.add(jLabel4, new java.awt.GridBagConstraints());

        cbDbid1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(cbDbid1, gridBagConstraints);

        add(jPanel6, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

	private void bRefreshListActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bRefreshListActionPerformed
	{//GEN-HEADEREND:event_bRefreshListActionPerformed
		app.guiRun().run(CleansePanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			dupDm.setIdSql(null);
			dupDm.doSelect(str);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bRefreshListActionPerformed

	private void bDupOKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bDupOKActionPerformed
	{//GEN-HEADEREND:event_bDupOKActionPerformed
		app.guiRun().run(CleansePanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			doAction(str, MC_DUPOK);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bDupOKActionPerformed

	
	private void subordinateAction(final int eix)
	{
		app.guiRun().run(CleansePanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
//			dm[0].getEntity().getSchemaBuf().setValueAt(dm[1].getIntKey(), 0, "primaryentityid");
			allDm.doUpdate(str);
			
			// Change around household...
			MergeSql merge = new MergeSql(app);
			Integer pid = (Integer)dm[1-eix].getEntitySb().getValueAt(0, "primaryentityid");
			merge.subordinateEntities(dm[eix].getKey(), pid); //dm[1-eix].getIntKey());
			String sql = merge.toSql();
			str.execSql(sql);

			
			refresh(str);
		}});
	}
	
	private void bSubordinate1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bSubordinate1ActionPerformed
	{//GEN-HEADEREND:event_bSubordinate1ActionPerformed
		subordinateAction(1);
// TODO add your handling code here:
	}//GEN-LAST:event_bSubordinate1ActionPerformed

	private void bSubordinate0ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bSubordinate0ActionPerformed
	{//GEN-HEADEREND:event_bSubordinate0ActionPerformed
		subordinateAction(0);
// TODO add your handling code here:
	}//GEN-LAST:event_bSubordinate0ActionPerformed
//
//private void deleteAction(int eix)
//{
//	// Delete the immediate record
//	SchemaBufDbModel dm = getEntity();
//	SchemaBuf sb = dm[eix].getSchemaBuf();
//	sb.setValueAt(Boolean.TRUE, 0, sb.findColumn("obsolete"));
//	dm.doUpdate(str);
//}

	
void doAction(SqlRun str, int action) throws IOException
{
	switch(action) {
		case MC_DEL_0 :
		case MC_DEL_1 :
		case MC_DEL_BOTH :
			if (!deleteAction(str, action)) return;
			break;
		case MC_MERGE_TO_0 :
		case MC_MERGE_TO_1 :
			if (!mergeAction(str, action)) return;
			break;
		case MC_DUPOK :
			dupOKAction(str);
			break;
		default : return;
	}
	
	int row = dupTable.getSelectedRow();
	dupDm.getSchemaBuf().removeRow(row);
//	dupTable.setSelectedRow(row);
	reselect(str);

}
void doDbAction(SqlRun str, int action, int entityid0, int entityid1) throws IOException
{
	switch(action) {
		case MC_DEL_0 :
		case MC_DEL_1 :
		case MC_DEL_BOTH :
			deleteDbAction(str, action, entityid0, entityid1);
			break;
		case MC_MERGE_TO_0 :
		case MC_MERGE_TO_1 :
			mergeDbAction(str, action, entityid0, entityid1);
			break;
		case MC_DUPOK :
			dupOKDbAction(str, entityid0, entityid1);
			break;
		default : return;
	}
}

private void dupOKDbAction(SqlRun str, int entityid0, int entityid1)
{
	str.execSql(
		" delete from mergelog where entityid0 = " + entityid0 + " and entityid1 = " + entityid1 + ";\n" +
		" delete from mergelog where entityid0 = " + entityid1 + " and entityid1 = " + entityid0 + ";\n" +
		" insert into mergelog (entityid0, entityid1, action, provisional, dtime) values (" +
		entityid0 + "," + entityid1 + "," + MC_DUPOK + "," +
		SqlBool.sql(cleanseMode == M_PROVISIONAL) + ", now());\n");
}

private void dupOKAction(SqlRun str)
{
	Integer entityid0 = (Integer)dm[0].getKey();
	Integer entityid1 = (Integer)dm[1].getKey();
	allDm.doUpdate(str);
	refresh(str);
	dupOKDbAction(str, entityid0, entityid1);
//	int row = dupTable.getSelectedRow();
//	dupDm.getSchemaBuf().removeRow(row);
//	dupTable.setSelectedRow(row);
}

private void deleteDbAction(SqlRun str, final int action, int entityid0, int entityid1)
{
	if (cleanseMode != M_PROVISIONAL) {
		// Really delete them
		switch(action) {
			case MC_DEL_0 :
				dm[0].doDelete(str);
			break;
			case MC_DEL_1 :
				dm[1].doDelete(str);
			break;
			case MC_DEL_BOTH :
				dm[0].doDelete(str);
				dm[1].doDelete(str);
			break;
		}
	}

	// Insert into mergelog
	str.execSql(
		" delete from mergelog where entityid0 = " + entityid0 + " and entityid1 = " + entityid1 + ";\n" +
		" delete from mergelog where entityid0 = " + entityid1 + " and entityid1 = " + entityid0 + ";\n" +
		" insert into mergelog (entityid0, entityid1, action, provisional, dtime) values (" +
		entityid0 + "," + entityid1 + "," + action + ", " +
		SqlBool.sql(cleanseMode == M_PROVISIONAL) + ", now());\n");
	
}
private boolean deleteAction(SqlRun str, final int action)
{
	String whichRecord;
	switch(action) {
		case MC_DEL_1 : whichRecord = "the old (red) record"; break;
		case MC_DEL_0 : whichRecord = "the new (green) record"; break;
		case MC_DEL_BOTH : whichRecord = "both records"; break;
		default : throw new IllegalArgumentException("deleteAction() cannot do action = " + action);
	}

	if (JOptionPane.showConfirmDialog(CleansePanel.this,
		"Do you really wish to delete\n" +
		whichRecord + "?", "Delete Record",
		JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return false;

	final Integer entityid0 = (Integer)dm[0].getKey();
	final Integer entityid1 = (Integer)dm[1].getKey();

	allDm.doUpdate(str);
	deleteDbAction(str, action, entityid0, entityid1);
	

	refresh(str);
	return true;
//	int row = dupTable.getSelectedRow();
//	dupDm.getSchemaBuf().removeRow(row);
//	dupTable.setSelectedRow(row);
}
	private void bDelete1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bDelete1ActionPerformed
	{//GEN-HEADEREND:event_bDelete1ActionPerformed
		app.guiRun().run(CleansePanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			doAction(str, MC_DEL_1);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bDelete1ActionPerformed

	private void bDelete0ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bDelete0ActionPerformed
	{//GEN-HEADEREND:event_bDelete0ActionPerformed
		app.guiRun().run(CleansePanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			doAction(str, MC_DEL_0);
		}});

// TODO add your handling code here:
	}//GEN-LAST:event_bDelete0ActionPerformed

	private void bSaveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bSaveActionPerformed
	{//GEN-HEADEREND:event_bSaveActionPerformed
		app.guiRun().run(CleansePanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			allDm.doUpdate(str);
			refresh(str);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bSaveActionPerformed

void mergeDbAction(SqlRun str, final int action, int entityid0, int entityid1)
{
	int entityidFrom, entityidTo;
	if (action == MC_MERGE_TO_0) {
		entityidFrom = entityid1;
		entityidTo = entityid0;
	} else {
		entityidFrom = entityid0;
		entityidTo = entityid1;
	}
	if (cleanseMode != M_PROVISIONAL) {

///** Merges data FROM dm0 TO dm1 */
//public static String mergeEntities(FrontApp app, Object entityid0, Object entityid1)
//{
//	merge.mergeEntities(entityid0, entityid1);
//	String sql = merge.toSql();
//	return sql;
//}
		MergeSql merge = new MergeSql(app);//.schemaSet());
		merge.mergeEntities(entityidFrom, entityidTo);
//		String sql = MergeSql.mergeEntities(app, entityidFrom, entityidTo);
		str.execSql(merge.toSql());
		
		// Mark it as coming from the correct database
		str.execSql(
			" update entities set dbid = \n" +
			" (select dbid from entities where entityid = " + entityid1 + ")\n" +
			" where entityid = " + entityidTo);
	}
	str.execSql(
		" delete from mergelog where entityid0 = " + entityid0 + " and entityid1 = " + entityid1 + ";\n" +
		" delete from mergelog where entityid0 = " + entityid1 + " and entityid1 = " + entityid0 + ";\n" +
		" insert into mergelog (entityid0, entityid1, action, provisional, dtime) values (" +
		entityid0 + "," + entityid1 + "," + action + ", " +
		SqlBool.sql(cleanseMode == M_PROVISIONAL) + ", now());\n");	
}
	
/**@param dm0 The left-hand item displayed to the user (generally the newer one). 
 * @param action MERGE_TO_0 or MERGE_TO_1 */
private boolean mergeAction(SqlRun str, final int action) throws IOException
{
	allDm.doUpdate(str);

	final DevelModel dm0 = dm[0];
	final DevelModel dm1 = dm[1];


	DevelModel dmFrom, dmTo;
	if (action == MC_MERGE_TO_0) {
		dmFrom = dm1;
		dmTo = dm0;
	} else {
		dmFrom = dm0;
		dmTo = dm1;
	}

	// First do a trial merge...
	MergeSql.bufMerge(app.dataTabSet(), dmFrom, dmTo);
	String html;
	SummaryReport sr = new SummaryReport(app);
	html = sr.getHtml(dmTo); //, app.sFormatMap());

	MergeConfirm confirm = new MergeConfirm(WidgetTree.getJFrame(CleansePanel.this), app, html);
	confirm.setVisible(true);

	if (confirm.okPressed) {

		final Integer entityid0 = (Integer)dm0.getKey();
		final Integer entityid1 = (Integer)dm1.getKey();
		final Integer entityidFrom = (Integer)dmFrom.getKey();
		final Integer entityidTo = (Integer)dmTo.getKey();

		mergeDbAction(str, action, entityid0, entityid1);
//		dupDm.getSchemaBuf().removeRow(dupTable.getSelectedRow());
			refresh(str);
		return true;
	} else {
		// Re-read what we had before we merged in the buffers
		refresh(str);
		return false;
	}
}
	
private void bMergeTo0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bMergeTo0ActionPerformed
	app.guiRun().run(CleansePanel.this, new SqlTask() {
	public void run(SqlRun str) throws Exception {
		doAction(str, MC_MERGE_TO_0);
	}});
}//GEN-LAST:event_bMergeTo0ActionPerformed

private void bMergeTo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bMergeTo1ActionPerformed
	app.guiRun().run(CleansePanel.this, new SqlTask() {
	public void run(SqlRun str) throws Exception {
		doAction(str, MC_MERGE_TO_1);
	}});
}//GEN-LAST:event_bMergeTo1ActionPerformed

	private void bSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSearchActionPerformed
// Comment out for now
//		app.guiRun().run(CleansePanel.this, new SqlTask() {
//		public void run(SqlRun str) throws Exception {
//			String text = tfSearch.getText();
//			String idSql = DB.simpleSearchSql(text);
//			dupDm.setIdSql(idSql);
//			dupDm.doSelect(str);
//		}});
		 // TODO add your handling code here:
}//GEN-LAST:event_bSearchActionPerformed

	private void tfSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfSearchActionPerformed
		// TODO add your handling code here:
	}//GEN-LAST:event_tfSearchActionPerformed

	private void bDeleteBothActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bDeleteBothActionPerformed
	{//GEN-HEADEREND:event_bDeleteBothActionPerformed
		app.guiRun().run(CleansePanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			doAction(str, MC_DEL_BOTH);
		}});

		// TODO add your handling code here:
}//GEN-LAST:event_bDeleteBothActionPerformed

	private void bApproveActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bApproveActionActionPerformed
		app.guiRun().run(CleansePanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			doAction(str, curAction);
		}});
		// TODO add your handling code here:
}//GEN-LAST:event_bApproveActionActionPerformed

	private void bApproveAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bApproveAllActionPerformed
		app.guiRun().run(CleansePanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			SchemaBuf sb = dupDm.getSchemaBuf();
			int e0_col = sb.findColumn("entityid0");
			int e1_col = sb.findColumn("entityid1");
			int action_col = sb.findColumn("action");
			for (int row = 0; row < sb.getRowCount(); ++row) {
				Integer entityid0 = (Integer)sb.getValueAt(row, e0_col);
				Integer entityid1 = (Integer)sb.getValueAt(row, e1_col);
				Integer action = (Integer)sb.getValueAt(row, action_col);
				doDbAction(str, action, entityid0, entityid1);
			}
			
			dupDm.setIdSql(null);
			dupDm.doSelect(str);
		}});
		// TODO add your handling code here:
}//GEN-LAST:event_bApproveAllActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bApproveAction;
    private javax.swing.JButton bApproveAll;
    private javax.swing.JButton bDelete0;
    private javax.swing.JButton bDelete1;
    private javax.swing.JButton bDeleteBoth;
    private javax.swing.JButton bDupOK;
    private javax.swing.JButton bMergeTo0;
    private javax.swing.JButton bMergeTo1;
    private javax.swing.JButton bRefreshList;
    private javax.swing.JButton bSave;
    private javax.swing.JButton bSearch;
    private javax.swing.JButton bSubordinate0;
    private javax.swing.JButton bSubordinate1;
    private citibob.swing.typed.JKeyedComboBox cbDbid0;
    private citibob.swing.typed.JKeyedComboBox cbDbid1;
    private javax.swing.JTabbedPane displayTabs;
    private citibob.swing.typed.JTypedSelectTable dupTable;
    private javax.swing.JScrollPane dupTablePane;
    private javax.swing.JPanel editTab;
    private offstage.devel.gui.EntityPanel entityPanel0;
    private offstage.devel.gui.EntityPanel entityPanel1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lNewRecord;
    private javax.swing.JLabel lOldRecord;
    private javax.swing.JPanel leftButtonPanel;
    private javax.swing.JPanel rightButtonPanel;
    private javax.swing.JTextPane summaryPane0;
    private javax.swing.JTextPane summaryPane1;
    private javax.swing.JTextField tfSearch;
    // End of variables declaration//GEN-END:variables


//public static void showFrame(SqlRun str, final FrontApp fapp, String dupType,
//int cleanseMode, final String title)
//{
//	final CleansePanel panel = new CleansePanel();
//	panel.initRuntime(str, fapp, dupType, cleanseMode);
//	str.execUpdate(new UpdTasklet2() {
//	public void run(SqlRun str) throws Exception {
//		JFrame frame = new JFrame(title);
//		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
////		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.getContentPane().add(panel);
////			new citibob.swing.prefs.SwingPrefs().setPrefs(frame, "", fapp.userRoot().node("CleanseFrame"));
//
//		frame.setVisible(true);
//	}});
//}
		
		
//public static void main(String[] args) throws Exception
//{
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	FrontApp fapp = new FrontApp(pool,null);
//	SqlBatchSet str = new SqlBatchSet(pool);
//	
//	CleansePanel panel = new CleansePanel();
//	panel.initRuntime(str, fapp, "n");
//	str.runBatches();
//	
//	JFrame frame = new JFrame();
////	frame.setSize(600,800);
//	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//	frame.getContentPane().add(panel);
//		new citibob.swing.prefs.SwingPrefs().setPrefs(frame, "", fapp.userRoot().node("CleanseFrame"));
//
//	frame.setVisible(true);
//}
	
// ==========================================================
class DupDbModel extends SqlBufDbModel {
String idSql;

public DupDbModel() { super(app, new String[] {}); }

public void setIdSql(String idSql) { this.idSql = idSql; }
public String getSelectSql(boolean proto) {
	if (proto) {
		return
			" select dups.*,e0.lastupdated as lastupdated0,e1.lastupdated as lastupdated1, ml.action" +
			" from dups, entities e0, entities e1, mergelog ml\n" +
			" where false";
	} else {
		StringBuffer sql = new StringBuffer();
		
		if (idSql != null) sql.append(
			" create temporary table _ids (id integer);\n" +
			" insert into _ids " + idSql + ";\n");

		if (cleanseMode == M_APPROVE) {
			sql.append(
				" select dups.*,e0.lastupdated as lastupdated0,e1.lastupdated as lastupdated1, ml.action" +
				" from dups, entities e0, entities e1, mergelog ml\n" +
				(idSql == null ? "" : ", _ids as ids0, _ids as ids1\n") +
				" where dups.entityid0 = e0.entityid\n" +
				" and dups.entityid1 = e1.entityid\n" +
				" and e0.dbid = " + SqlInteger.sql(dbid0) + " and e1.dbid = " + SqlInteger.sql(dbid1) +
				" and ((dups.entityid0 = ml.entityid0 and dups.entityid1 = ml.entityid1)\n" +
				"    or (dups.entityid0 = ml.entityid1 and dups.entityid1 = ml.entityid0))\n" +
				" and ml.provisional\n" +
				(idSql == null ? "" : " and ids0.id = e0.entityid and ids1.id = e1.entityid\n") +
				" order by dtime");
		} else {
			sql.append(
			" select dups.*,e0.lastupdated as lastupdated0,e1.lastupdated as lastupdated1, null as action" +
			" from dups, entities e0, entities e1" +
// TODO: This line (below) is the problem.  Search only works if BOTH names
// match the search.  It should work when only ONE name matches the search.
			
// Solution: use the basic SQL below...			
//select entityid0,entityid1
//from dups, _ids
//where (dups.entityid0 = _ids.id or dups.entityid1 = _ids.id)
//   EXCEPT
//select entityid0,entityid1
//from mergelog ml, _ids
//where (ml.entityid0 = _ids.id or ml.entityid1 = _ids.id)

			(idSql == null ? "" : ", _ids as ids0, _ids as ids1") +
			" where dups.entityid0 = e0.entityid" +
			" and dups.entityid1 = e1.entityid" +
			" and e0.dbid = " + SqlInteger.sql(dbid0) + " and e1.dbid = " + SqlInteger.sql(dbid1) +
//			" and dups.dupid = " + SqlInteger.sql(dupid) +
//			" and dups.type=" + SqlString.sql(dupType) +
			" and not e0.obsolete and not e1.obsolete" +
			" and score <= 1.0" +
			(idSql == null ? "" : " and ids0.id = e0.entityid and ids1.id = e1.entityid\n") +
// Eliminate children from different households.  Should really be done in original dup finding.
" and ((e0.entityid = e0.primaryentityid or e1.entityid = e1.primaryentityid)" +
" or e0.primaryentityid = e1.primaryentityid)\n" +
			"    EXCEPT" +
			" select dups.*,e0.lastupdated as lastupdated0,e1.lastupdated as lastupdated1, null as action" +
			" from dups, mergelog ml, entities e0, entities e1" +
//			(idSql == null ? "" : ", _ids") +
			" where dups.entityid0 = e0.entityid" +
			" and dups.entityid1 = e1.entityid" +
			" and e0.dbid = " + SqlInteger.sql(dbid0) + " and e1.dbid = " + SqlInteger.sql(dbid1) +
//			" and ml.action = " + MC_DUPOK +
//			" and dups.dupid = " + SqlInteger.sql(dupid) +
//			" and dups.type=" + SqlString.sql(dupType) +
			" and ((dups.entityid0 = ml.entityid0 and dups.entityid1 = ml.entityid1)\n" +
			"   or (dups.entityid0 = ml.entityid1 and dups.entityid1 = ml.entityid0))\n" +
//			(idSql == null ? "" : " and (_ids.id = e0.entityid or _ids.id = e1.entityid)") +
//			"    EXCEPT" +
//			" select dups.*,e0.lastupdated as lastupdated0,e1.lastupdated as lastupdated1, null as action" +
//			" from dups, mergelog ml, entities e0, entities e1" +
////			(idSql == null ? "" : ", _ids") +
//			" where dups.entityid0 = e0.entityid" +
//			" and dups.entityid1 = e1.entityid" +
////			" and ml.action = " + MC_DUPOK +
//			" and dups.dbid0 = " + SqlInteger.sql(dbid0) + " and dups.dbid1 = " + SqlInteger.sql(dbid1) +
////			" and dups.dupid = " + SqlInteger.sql(dupid) +
////			" and dups.type=" + SqlString.sql(dupType) +
//			" and dups.entityid0 = ml.entityid1" +
//			" and dups.entityid1 = ml.entityid0" +
////			(idSql == null ? "" : " and (_ids.id = e0.entityid or _ids.id = e1.entityid)") +
			" order by score desc,string0,string1,entityid0,entityid1;\n" +
			(idSql == null ? "" : " drop table _ids;\n"));
		}
		return sql.toString();
	}
}}
// ==========================================================
	
}
