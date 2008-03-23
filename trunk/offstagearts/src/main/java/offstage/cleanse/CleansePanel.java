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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;
import javax.swing.*;
import javax.swing.event.*;

/**
 *
 * @author  citibob
 */
public class CleansePanel extends javax.swing.JPanel
{
	
App app;

// The two records we're comparing
DevelModel[] dm = new DevelModel[2];
MultiDbModel allDm;		// = dm[0] and dm[1]
RSTableModel dupModel;
//Integer entityid0, entityid1;
String dupType;

	/** Creates new form CleansePanel */
	public CleansePanel()
	{
		initComponents();
        entityPanel0.setAllBackground(new java.awt.Color(51, 204, 0));
        entityPanel1.setAllBackground(new java.awt.Color(255, 204, 204));
		
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
//				int row = e.getFirstIndex();
//				dm[0].setKey((Integer)dupModel.getValueAt(row, "entityid0"));
//				dm[1].setKey((Integer)dupModel.getValueAt(row, "entityid1"));
				Integer Entityid0 = (Integer)dupTable.getValue("entityid0");
				Integer Entityid1 = (Integer)dupTable.getValue("entityid1");
//System.out.println("EntityID changed: " + entityid0 + " " + entityid1);

				Date dt0 = (Date)dupTable.getValue("lastupdated0");
				long ms0 = (dt0 == null ? 0 : dt0.getTime());
				Date dt1 = (Date)dupTable.getValue("lastupdated1");
				long ms1 = (dt1 == null ? 0 : dt1.getTime());
				
				// Swap so newer record is always on the left.
				if (ms0 < ms1) {
					Integer EID = Entityid0;
					Entityid0 = Entityid1;
					Entityid1 = EID;
				}
System.out.println("XYZZZ: " + dt0 + " " + dt1);
				
				dm[0].setKey(Entityid0);
				dm[1].setKey(Entityid1);
				allDm.doSelect(str);
			}});
		}});
	}
	/** @param dupType = 'a' (address), 'n' (names), 'o' (organization) */
	public void initRuntime(SqlRun str, FrontApp fapp, String dupType)
	{
		this.app = fapp;
		this.dupType = dupType;
		
		dm[0] = new DevelModel(app);
		entityPanel0.initRuntime(str, fapp, dm[0]);
		dm[1] = new DevelModel(app);
		entityPanel1.initRuntime(str, fapp, dm[1]);
		allDm = new MultiDbModel(dm);

		refreshDupTable(str, null);
	}
	
	void refreshDupTable(SqlRun str, String idSql)
	{
		String sql =
			(idSql == null ? "" :
				" create temporary table _ids (id integer);\n" +
				" insert into _ids " + idSql + ";\n") +
			" select dups.*,e0.lastupdated as lastupdated0,e1.lastupdated as lastupdated1" +
			" from dups, entities e0, entities e1" +
			(idSql == null ? "" : ", _ids as ids0, _ids as ids1") +
			" where dups.entityid0 = e0.entityid" +
			" and dups.entityid1 = e1.entityid" +
			" and dups.type=" + SqlString.sql(dupType) +
			" and not e0.obsolete and not e1.obsolete" +
			" and score <= 1.0" +
			(idSql == null ? "" : " and ids0.id = e0.entityid and ids1.id = e1.entityid") +
// Eliminate children from different households.  Should really be done in original dup finding.
" and ((e0.entityid = e0.primaryentityid or e1.entityid = e1.primaryentityid)" +
" or e0.primaryentityid = e1.primaryentityid)" +
			"    EXCEPT" +
			" select dups.*,e0.lastupdated as lastupdated0,e1.lastupdated as lastupdated1" +
			" from dups, mergelog ml, entities e0, entities e1" +
//			(idSql == null ? "" : ", _ids") +
			" where dups.entityid0 = e0.entityid" +
			" and dups.entityid1 = e1.entityid" +
			" and ml.dupok" +
			" and dups.type=" + SqlString.sql(dupType) +
			" and dups.entityid0 = ml.entityid0" +
			" and dups.entityid1 = ml.entityid1" +
//			(idSql == null ? "" : " and (_ids.id = e0.entityid or _ids.id = e1.entityid)") +
			"    EXCEPT" +
			" select dups.*,e0.lastupdated as lastupdated0,e1.lastupdated as lastupdated1" +
			" from dups, mergelog ml, entities e0, entities e1" +
//			(idSql == null ? "" : ", _ids") +
			" where dups.entityid0 = e0.entityid" +
			" and dups.entityid1 = e1.entityid" +
			" and ml.dupok" +
			" and dups.type=" + SqlString.sql(dupType) +
			" and dups.entityid0 = ml.entityid1" +
			" and dups.entityid1 = ml.entityid0" +
//			(idSql == null ? "" : " and (_ids.id = e0.entityid or _ids.id = e1.entityid)") +
			" order by score desc,string0,string1,entityid0,entityid1;\n" +
			(idSql == null ? "" : " drop table _ids;\n");
		dupModel = new RSTableModel(app.sqlTypeSet());
		dupModel.executeQuery(str, sql);
		str.execUpdate(new UpdTasklet2() {
		public void run(SqlRun str) throws Exception {
			dupTable.setModelU(dupModel,
				new String[] {"#", "Score", "ID-0", "Name-0", "ID-1", "Name-1"},
//				new String[] {"score", "score", "entityid0", "string0", "entityid1", "string1"},
				new String[] {"__rowno__", "score", "entityid0", "string0", "entityid1", "string1"},
				new String[] {null, null, "string0", "string0", "string1", "string1"},
				new boolean[] {false, false,false,false,false,false},
				app.swingerMap());
//			dupTable.setRenderEditU("score", new java.text.DecimalFormat("#.00"));
			dupTable.setFormatU("score", "#.00");
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
        java.awt.GridBagConstraints gridBagConstraints;

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        entityPanel0 = new offstage.devel.gui.EntityPanel();
        entityPanel1 = new offstage.devel.gui.EntityPanel();
        jPanel2 = new javax.swing.JPanel();
        dupTablePane = new javax.swing.JScrollPane();
        dupTable = new citibob.swing.typed.JTypedSelectTable();
        leftButtonPanel = new javax.swing.JPanel();
        bDelete0 = new javax.swing.JButton();
        bMerge1 = new javax.swing.JButton();
        bDeleteBoth = new javax.swing.JButton();
        bDelete1 = new javax.swing.JButton();
        bDupOK = new javax.swing.JButton();
        bMerge0 = new javax.swing.JButton();
        jToolBar1 = new javax.swing.JToolBar();
        bSave = new javax.swing.JButton();
        bUndo = new javax.swing.JButton();
        bRefreshList = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        tfSearch = new javax.swing.JTextField();
        bSearch = new javax.swing.JButton();
        rightButtonPanel = new javax.swing.JPanel();
        bSubordinate1 = new javax.swing.JButton();
        bSubordinate0 = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        entityPanel0.setBackground(new java.awt.Color(51, 204, 0));
        jPanel1.add(entityPanel0);

        entityPanel1.setBackground(new java.awt.Color(255, 204, 204));
        jPanel1.add(entityPanel1);

        jSplitPane1.setLeftComponent(jPanel1);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        dupTable.setModel(new javax.swing.table.DefaultTableModel(
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
        bDelete0.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bDelete0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        leftButtonPanel.add(bDelete0, gridBagConstraints);

        bMerge1.setText("<- Merge");
        bMerge1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bMerge1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        leftButtonPanel.add(bMerge1, gridBagConstraints);

        bDeleteBoth.setText("Delete Both");
        bDeleteBoth.setFocusable(false);
        bDeleteBoth.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bDeleteBoth.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bDeleteBoth.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
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
        bDelete1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bDelete1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        leftButtonPanel.add(bDelete1, gridBagConstraints);

        bDupOK.setText("Duplicate OK");
        bDupOK.setPreferredSize(new java.awt.Dimension(103, 40));
        bDupOK.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
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

        bMerge0.setText("Merge ->");
        bMerge0.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bMerge0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        leftButtonPanel.add(bMerge0, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel2.add(leftButtonPanel, gridBagConstraints);

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
        jToolBar1.add(bUndo);

        bRefreshList.setText("Refresh List");
        bRefreshList.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bRefreshListActionPerformed(evt);
            }
        });
        jToolBar1.add(bRefreshList);

        jLabel1.setText("       ");
        jToolBar1.add(jLabel1);

        tfSearch.setMaximumSize(new java.awt.Dimension(120, 2147483647));
        tfSearch.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                tfSearchActionPerformed(evt);
            }
        });
        jToolBar1.add(tfSearch);

        bSearch.setFocusable(false);
        bSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bSearch.setLabel("Search");
        bSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bSearch.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bSearchActionPerformed(evt);
            }
        });
        jToolBar1.add(bSearch);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel2.add(jToolBar1, gridBagConstraints);

        rightButtonPanel.setLayout(new java.awt.GridBagLayout());

        bSubordinate1.setText("Subordinate");
        bSubordinate1.setEnabled(false);
        bSubordinate1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
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
        bSubordinate0.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
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

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

	private void bRefreshListActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bRefreshListActionPerformed
	{//GEN-HEADEREND:event_bRefreshListActionPerformed
		app.guiRun().run(CleansePanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			refreshDupTable(str, null);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bRefreshListActionPerformed

	private void bDupOKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bDupOKActionPerformed
	{//GEN-HEADEREND:event_bDupOKActionPerformed
		app.guiRun().run(CleansePanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			Integer entityid0 = (Integer)dm[0].getKey();
			Integer entityid1 = (Integer)dm[1].getKey();
			allDm.doUpdate(str);
			allDm.doSelect(str);
			str.execSql(
				" delete from mergelog where entityid0 = " + entityid0 + " and entityid1 = " + entityid1 + ";\n" +
				" insert into mergelog (entityid0, entityid1, dupok, dtime) values (" +
				entityid0 + "," + entityid1 + ",true,now());\n");
			int row = dupTable.getSelectedRow();
			dupModel.removeRow(row);
			dupTable.setSelectedRow(row);
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
			MergeSql merge = new MergeSql(app.schemaSet());
			Integer pid = (Integer)dm[1-eix].getEntitySb().getValueAt(0, "primaryentityid");
			merge.subordinateEntities(dm[eix].getKey(), pid); //dm[1-eix].getIntKey());
			String sql = merge.toSql();
			str.execSql(sql);

			
			allDm.doSelect(str);
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

private void deleteAction(final int... eix)
{
	app.guiRun().run(CleansePanel.this, new SqlTask() {
	public void run(SqlRun str) throws Exception {
		allDm.doUpdate(str);
		for (int i=0; i<eix.length; ++i) dm[eix[i]].doDelete(str);
		allDm.doSelect(str);
		int row = dupTable.getSelectedRow();
		dupModel.removeRow(row);
		dupTable.setSelectedRow(row);
	}});
}
	private void bDelete1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bDelete1ActionPerformed
	{//GEN-HEADEREND:event_bDelete1ActionPerformed
		deleteAction(1);
// TODO add your handling code here:
	}//GEN-LAST:event_bDelete1ActionPerformed

	private void bDelete0ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bDelete0ActionPerformed
	{//GEN-HEADEREND:event_bDelete0ActionPerformed
		deleteAction(0);
// TODO add your handling code here:
	}//GEN-LAST:event_bDelete0ActionPerformed

	private void bSaveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bSaveActionPerformed
	{//GEN-HEADEREND:event_bSaveActionPerformed
		app.guiRun().run(CleansePanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			allDm.doUpdate(str);
			allDm.doSelect(str);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bSaveActionPerformed

private void mergeAction(final Integer entityid0, final Integer entityid1)
{
	app.guiRun().run(CleansePanel.this, new SqlTask() {
	public void run(SqlRun str) throws Exception {
		allDm.doUpdate(str);
		String sql = MergeSql.mergeEntities(app, entityid0, entityid1);
//System.out.println("================= CleansePanel");
//System.out.println(sql);
		str.execSql(sql);
		allDm.doSelect(str);
		str.execSql(
			" delete from mergelog where entityid0 = " + entityid0 + " and entityid1 = " + entityid1 + ";\n" +
			" insert into mergelog (entityid0, entityid1, dupok, dtime) values (" +
			entityid0 + "," + entityid1 + ",false,now());\n");
		dupModel.removeRow(dupTable.getSelectedRow());
	}});
}
	
	private void bMerge1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bMerge1ActionPerformed
	{//GEN-HEADEREND:event_bMerge1ActionPerformed
		mergeAction((Integer)dm[1].getKey(), (Integer)dm[0].getKey());
	}//GEN-LAST:event_bMerge1ActionPerformed

	private void bMerge0ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bMerge0ActionPerformed
	{//GEN-HEADEREND:event_bMerge0ActionPerformed
		mergeAction((Integer)dm[0].getKey(), (Integer)dm[1].getKey());
	}//GEN-LAST:event_bMerge0ActionPerformed

	private void bSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSearchActionPerformed
		app.guiRun().run(CleansePanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			String text = tfSearch.getText();
			String idSql = DB.simpleSearchSql(text);
			refreshDupTable(str, idSql);
		}});
		 // TODO add your handling code here:
}//GEN-LAST:event_bSearchActionPerformed

	private void tfSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfSearchActionPerformed
		// TODO add your handling code here:
	}//GEN-LAST:event_tfSearchActionPerformed

	private void bDeleteBothActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bDeleteBothActionPerformed
	{//GEN-HEADEREND:event_bDeleteBothActionPerformed
		deleteAction(0, 1);
		// TODO add your handling code here:
}//GEN-LAST:event_bDeleteBothActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bDelete0;
    private javax.swing.JButton bDelete1;
    private javax.swing.JButton bDeleteBoth;
    private javax.swing.JButton bDupOK;
    private javax.swing.JButton bMerge0;
    private javax.swing.JButton bMerge1;
    private javax.swing.JButton bRefreshList;
    private javax.swing.JButton bSave;
    private javax.swing.JButton bSearch;
    private javax.swing.JButton bSubordinate0;
    private javax.swing.JButton bSubordinate1;
    private javax.swing.JButton bUndo;
    private citibob.swing.typed.JTypedSelectTable dupTable;
    private javax.swing.JScrollPane dupTablePane;
    private offstage.devel.gui.EntityPanel entityPanel0;
    private offstage.devel.gui.EntityPanel entityPanel1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel leftButtonPanel;
    private javax.swing.JPanel rightButtonPanel;
    private javax.swing.JTextField tfSearch;
    // End of variables declaration//GEN-END:variables


public static void showFrame(SqlRun str, final FrontApp fapp, String dupType, final String title)
{
	final CleansePanel panel = new CleansePanel();
	panel.initRuntime(str, fapp, dupType);
	str.execUpdate(new UpdTasklet2() {
	public void run(SqlRun str) throws Exception {
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(panel);
//			new citibob.swing.prefs.SwingPrefs().setPrefs(frame, "", fapp.userRoot().node("CleanseFrame"));

		frame.setVisible(true);
	}});
}
		
		
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
	
	
	
}
