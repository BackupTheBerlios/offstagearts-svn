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

import citibob.jschema.SchemaBuf;
import citibob.jschema.SqlBufDbModel;
import citibob.sql.SqlRun;
import citibob.sql.SqlSet;
import citibob.sql.UpdTasklet2;
import citibob.sql.pgsql.SqlInteger;
import citibob.swing.table.SortedTableModel;
import citibob.task.SqlTask;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.JOptionPane;
import offstage.FrontApp;
import offstage.db.DB;

/**
 *
 * @author  citibob
 */
public class CleansePanel extends javax.swing.JPanel
{

// Mode we're operating in; see MergeActions.M_*
int cleanseMode;




FrontApp app;

/** Databases from which the two recrods come */
int dbid0, dbid1;

Integer curAction;		// Action of the currently-selected dup row
DupDbModel dupDm;

	/** Creates new form CleansePanel */
	public CleansePanel()
	{
		initComponents();

		tfSearch.addKeyListener(new KeyAdapter() {
		public void keyTyped(KeyEvent e) {
			//System.out.println(e.getKeyChar());
			if (e.getKeyChar() == '\n') {
				bSearchActionPerformed(null);
			}
		}});
		
		dupTable.addPropertyChangeListener("value", new java.beans.PropertyChangeListener() {
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
		MergeModel mergeDm = mergePane.getMergeDm();
		mergeDm.setEntityID(0, (Integer)dupTable.getValue("entityid0"));
		mergeDm.setEntityID(1, (Integer)dupTable.getValue("entityid1"));

		curAction = (Integer)dupTable.getValue("action");
		bApproveAction.setText((String)MergeActions.actionKmodel.get(curAction).obj);
		refresh(str);
	}
	void refresh(SqlRun str)
	{
		mergePane.refresh(str);
	}
	
	void setDbids(SqlRun str, Integer dbid0, Integer dbid1)
	{
		if (dbid0 == null || dbid1 == null) return;

		this.dbid0 = dbid0;
		this.dbid1 = dbid1;
		dupDm.doSelect(str);
	}
	
	/** @param dupType = 'a' (address), 'n' (names), 'o' (organization) */
	public void initRuntime(SqlRun str, FrontApp fapp, final Integer Dbid0, final Integer Dbid1, int cleanseMode)
	{
		mergePane.initRuntime(str, fapp);

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

		
		bApproveAction.setText((String)MergeActions.actionKmodel.get(null).obj);
		if (cleanseMode != MergeActions.M_APPROVE) bApproveAction.setEnabled(false);

		// Set up duplicates display
		dupDm = new DupDbModel();
		
		str.execUpdate(new UpdTasklet2() {
		public void run(SqlRun str) {
			dupDm.setIdSql(null);
//			dupDm.doSelect(str);		// Taken care of below in setDbids()
			
			dupTable.setModelU(new SortedTableModel(dupDm.getSchemaBuf()),
				new String[] {"#", "Score", "Action", "ID-0", "Name-0", "ID-1", "Name-1"},
	//				new String[] {"score", "score", "entityid0", "string0", "entityid1", "string1"},
				new String[] {"__rowno__", "score", "action", "entityid0", "string0", "entityid1", "string1"},
				new String[] {null, null, null, "string0", "string0", "string1", "string1"},
				new boolean[] {false, false,false,false,false,false,false},
				app.swingerMap());
	//			dupTable.setRenderEditU("score", new java.text.DecimalFormat("#.00"));
			dupTable.setFormatU("action", MergeActions.actionKmodel);
			dupTable.setFormatU("score", "#.00");
			dupTable.setValueColU("__rowno__");
			
			setDbids(str, Dbid0, Dbid1);
		}});

	}

void doAction(SqlRun str, int action) throws IOException
{
	if (mergePane.doAction(str, cleanseMode, action)) {
		// Update our list of records-to-merge if something happened.
		int row = dupTable.getSelectedRow();
		dupDm.getSchemaBuf().removeRow(row);
		reselect(str);
	}
}


	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainPane = new javax.swing.JSplitPane();
        mergePane = new offstage.cleanse.MergePane();
        mergeControls = new javax.swing.JPanel();
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
        bMergeAllTo0 = new javax.swing.JButton();
        bMergeAllTo1 = new javax.swing.JButton();
        topPane = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cbDbid0 = new citibob.swing.typed.JKeyedComboBox();
        jLabel4 = new javax.swing.JLabel();
        cbDbid1 = new citibob.swing.typed.JKeyedComboBox();

        setLayout(new java.awt.BorderLayout());

        mainPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        mainPane.setTopComponent(mergePane);

        mergeControls.setLayout(new java.awt.GridBagLayout());

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
        mergeControls.add(dupTablePane, gridBagConstraints);

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
        mergeControls.add(leftButtonPanel, gridBagConstraints);

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
        mergeControls.add(jToolBar1, gridBagConstraints);

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
        gridBagConstraints.gridy = 4;
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
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        rightButtonPanel.add(bSubordinate0, gridBagConstraints);

        bMergeAllTo0.setText("<- Merge All");
        bMergeAllTo0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bMergeAllTo0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        rightButtonPanel.add(bMergeAllTo0, gridBagConstraints);

        bMergeAllTo1.setText("Merge All ->");
        bMergeAllTo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bMergeAllTo1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        rightButtonPanel.add(bMergeAllTo1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        mergeControls.add(rightButtonPanel, gridBagConstraints);

        mainPane.setRightComponent(mergeControls);

        add(mainPane, java.awt.BorderLayout.CENTER);

        topPane.setLayout(new java.awt.GridBagLayout());

        jLabel3.setText("Duplicate Run: ");
        topPane.add(jLabel3, new java.awt.GridBagConstraints());

        cbDbid0.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        topPane.add(cbDbid0, gridBagConstraints);

        jLabel4.setText(" -- ");
        topPane.add(jLabel4, new java.awt.GridBagConstraints());

        cbDbid1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        topPane.add(cbDbid1, gridBagConstraints);

        add(topPane, java.awt.BorderLayout.PAGE_START);
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
			doAction(str, MergeActions.MC_DUPOK);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bDupOKActionPerformed

//	// This is for assigning records to the same household because they
//	// have the same address.  It will be moved to a separate screen.
//	private void subordinateAction(final int eix)
//	{
//		app.guiRun().run(CleansePanel.this, new SqlTask() {
//		public void run(SqlRun str) throws Exception {
////			dm[0].getEntity().getSchemaBuf().setValueAt(dm[1].getIntKey(), 0, "primaryentityid");
//			mergeDm.doUpdate(str);
//			
//			// Change around household...
//			MergeSql merge = new MergeSql(app);
//			Integer pid = (Integer)dm[1-eix].getEntitySb().getValueAt(0, "primaryentityid");
//			merge.subordinateEntities(dm[eix].getKey(), pid); //dm[1-eix].getIntKey());
//			String sql = merge.toSql();
//			str.execSql(sql);
//
//			
//			refresh(str);
//		}});
//	}
	
	private void bSubordinate1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bSubordinate1ActionPerformed
	{//GEN-HEADEREND:event_bSubordinate1ActionPerformed
//		subordinateAction(1);
// TODO add your handling code here:
	}//GEN-LAST:event_bSubordinate1ActionPerformed

	private void bSubordinate0ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bSubordinate0ActionPerformed
	{//GEN-HEADEREND:event_bSubordinate0ActionPerformed
//		subordinateAction(0);
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

void doDbAllAction(SqlRun str, int action) throws IOException
{
	SchemaBuf sb = dupDm.getSchemaBuf();
	int e0_col = sb.findColumn("entityid0");
	int e1_col = sb.findColumn("entityid1");
//	int action_col = sb.findColumn("action");
	for (int row = 0; row < sb.getRowCount(); ++row) {
		Integer entityid0 = (Integer)sb.getValueAt(row, e0_col);
		Integer entityid1 = (Integer)sb.getValueAt(row, e1_col);
//		Integer action = (Integer)sb.getValueAt(row, action_col);
		MergeActions.doDbAction(str, app, cleanseMode,
			action, entityid0, entityid1);
	}
}


	private void bDelete1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bDelete1ActionPerformed
	{//GEN-HEADEREND:event_bDelete1ActionPerformed
		app.guiRun().run(CleansePanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			doAction(str, MergeActions.MC_DEL_1);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bDelete1ActionPerformed

	private void bDelete0ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bDelete0ActionPerformed
	{//GEN-HEADEREND:event_bDelete0ActionPerformed
		app.guiRun().run(CleansePanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			doAction(str, MergeActions.MC_DEL_0);
		}});

// TODO add your handling code here:
	}//GEN-LAST:event_bDelete0ActionPerformed

	private void bSaveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bSaveActionPerformed
	{//GEN-HEADEREND:event_bSaveActionPerformed
		app.guiRun().run(CleansePanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			mergePane.getMergeDm().doUpdate(str);
			refresh(str);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bSaveActionPerformed

	
	
private void bMergeTo0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bMergeTo0ActionPerformed
	app.guiRun().run(CleansePanel.this, new SqlTask() {
	public void run(SqlRun str) throws Exception {
		doAction(str, MergeActions.MC_MERGE_TO_0);
	}});
}//GEN-LAST:event_bMergeTo0ActionPerformed

private void bMergeTo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bMergeTo1ActionPerformed
	app.guiRun().run(CleansePanel.this, new SqlTask() {
	public void run(SqlRun str) throws Exception {
		doAction(str, MergeActions.MC_MERGE_TO_1);
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
			doAction(str, MergeActions.MC_DEL_BOTH);
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
			    MergeActions.doDbAction(str, app, cleanseMode,
					action, entityid0, entityid1);
			    str.flush();
			}
//			str.flush();
			
			dupDm.setIdSql(null);
			dupDm.doSelect(str);
//System.out.println("=============== Not executing SQL =================\n");
//System.out.println(str.clear());
//System.out.println("=============== Finished Not executing SQL =================\n");

		}});
		// TODO add your handling code here:
}//GEN-LAST:event_bApproveAllActionPerformed

	private void bMergeAllTo0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bMergeAllTo0ActionPerformed
		app.guiRun().run(CleansePanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			if (JOptionPane.showConfirmDialog(CleansePanel.this,
				"Do you really wish to merge all records <-\n?", "Merge All <-",
				JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

			doDbAllAction(str, MergeActions.MC_MERGE_TO_0);
			str.flush();
			
			dupDm.setIdSql(null);
			dupDm.doSelect(str);

		}});
}//GEN-LAST:event_bMergeAllTo0ActionPerformed

	private void bMergeAllTo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bMergeAllTo1ActionPerformed
		app.guiRun().run(CleansePanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			if (JOptionPane.showConfirmDialog(CleansePanel.this,
				"Do you really wish to merge all records ->\n?", "Merge All ->",
				JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

			doDbAllAction(str, MergeActions.MC_MERGE_TO_1);
			
			dupDm.setIdSql(null);
			dupDm.doSelect(str);
		}});

}//GEN-LAST:event_bMergeAllTo1ActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bApproveAction;
    private javax.swing.JButton bApproveAll;
    private javax.swing.JButton bDelete0;
    private javax.swing.JButton bDelete1;
    private javax.swing.JButton bDeleteBoth;
    private javax.swing.JButton bDupOK;
    private javax.swing.JButton bMergeAllTo0;
    private javax.swing.JButton bMergeAllTo1;
    private javax.swing.JButton bMergeTo0;
    private javax.swing.JButton bMergeTo1;
    private javax.swing.JButton bRefreshList;
    private javax.swing.JButton bSave;
    private javax.swing.JButton bSearch;
    private javax.swing.JButton bSubordinate0;
    private javax.swing.JButton bSubordinate1;
    private citibob.swing.typed.JKeyedComboBox cbDbid0;
    private citibob.swing.typed.JKeyedComboBox cbDbid1;
    private citibob.swing.typed.JTypedSelectTable dupTable;
    private javax.swing.JScrollPane dupTablePane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel leftButtonPanel;
    private javax.swing.JSplitPane mainPane;
    private javax.swing.JPanel mergeControls;
    private offstage.cleanse.MergePane mergePane;
    private javax.swing.JPanel rightButtonPanel;
    private javax.swing.JTextField tfSearch;
    private javax.swing.JPanel topPane;
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
SqlSet idSql;

public DupDbModel() { super(app, new String[] {}); }

public void setIdSql(SqlSet idSql) { this.idSql = idSql; }
public SqlSet getSelectSql(boolean proto)
{
	if (proto) return new SqlSet(
		" select dups.*,e0.lastupdated as lastupdated0,e1.lastupdated as lastupdated1, ml.action" +
		" from dups, entities e0, entities e1, mergelog ml\n" +
		" where false");
	
	// Do the real query
	StringBuffer preSql = new StringBuffer();
	StringBuffer sql = new StringBuffer();
	StringBuffer postSql = new StringBuffer();

	if (idSql != null) {
		preSql.append(
			" create temporary table _ids_dups (id integer);\n");
		preSql.append(
			" insert into _ids_dups (id) " + idSql + ";\n");
		postSql.append("drop table _ids_dups");
	}

	preSql.append(
		" create temporary table _d (" +
			" eid0 int, headid0 int," +
			" eid1 int, headid1 int," +
			" action integer);\n");
	postSql.append("drop table _d;\n");

	if (cleanseMode == MergeActions.M_APPROVE) {
		preSql.append(
			" insert into _d (eid0, eid1, action)" +
			" select dups.entityid0, dups.entityid1, ml.action" +
			" from dups, entities e0, entities e1, mergelog ml\n" +
			(idSql == null ? "" : ", _ids_dups as ids0, _ids_dups as ids1\n") +
			" where dups.entityid0 = e0.entityid\n" +
			" and dups.entityid1 = e1.entityid\n" +
			" and e0.dbid = " + SqlInteger.sql(dbid0) + " and e1.dbid = " + SqlInteger.sql(dbid1) +
			" and ((dups.entityid0 = ml.entityid0 and dups.entityid1 = ml.entityid1)\n" +
			"    or (dups.entityid0 = ml.entityid1 and dups.entityid1 = ml.entityid0))\n" +
			" and ml.provisional\n" +
			(idSql == null ? "" : " and ids0.id = e0.entityid and ids1.id = e1.entityid") +
			";\n");
	} else {
		// Add all dups in our (dbid0, dbid1) category
		preSql.append(
			" insert into _d (eid0, eid1)" +
			" select dups.entityid0, dups.entityid1" +
			" from dups, entities e0, entities e1" +
			(idSql == null ? "" : ", _ids_dups as ids0, _ids_dups as ids1") +
			" where dups.entityid0 = e0.entityid" +
			" and dups.entityid1 = e1.entityid" +
			" and e0.dbid = " + SqlInteger.sql(dbid0) + " and e1.dbid = " + SqlInteger.sql(dbid1) +
			" and not e0.obsolete and not e1.obsolete" +
			" and score <= 1.01" +
			(idSql == null ? "" : " and ids0.id = e0.entityid and ids1.id = e1.entityid") +
			";\n");

		// Remove items already merged
		preSql.append(
			" delete from _d\n" +
			" using mergelog ml\n" + 
			" where (_d.eid0 = ml.entityid0 and _d.eid1 = ml.entityid1)\n" +
			" or (_d.eid1 = ml.entityid0 and _d.eid0 = ml.entityid1);\n");

		// Eliminate children from different households.  Should really be done in original dup finding.
		preSql.append(
			DB.updateOneOf("headof", "_d", "eid0", "headid0") +
			DB.updateOneOf("headof", "_d", "eid0", "headid0"));
		preSql.append(
			" delete from _d where not\n" +
			" ((eid0 = headid0 or eid1 = headid1)" +
			" or headid0 = headid1);\n");
	}

	// Do our final select
	sql.append(
		" select dups.*," +
			" e0.lastupdated as lastupdated0," +
			" e1.lastupdated as lastupdated1," +
			" _d.action\n" +
		" from _d,dups, entities e0, entities e1" +
		" where _d.eid0 = dups.entityid0 and _d.eid1 = dups.entityid1" +
		" and _d.eid0 = e0.entityid and _d.eid1 = e1.entityid");

	return new SqlSet(preSql, sql, postSql);
}}
// ==========================================================
	
}
