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
 * CoursesPanel.java
 *
 * Created on December 15, 2007, 4:39 PM
 */

package offstage.school.gui;

import citibob.jschema.DbModel;
import citibob.jschema.MultiDbModel;
import citibob.jschema.SchemaBufDbModel;
import citibob.task.SqlTask;
import citibob.task.ETask;
import citibob.sql.SqlRun;
import citibob.types.JEnum;
import offstage.FrontApp;

/**
 *
 
select t.*,xx.numcourses
from termids t, (
select t.groupid, count(*) as numcourses
from termids t, courseids c
where t.groupid = c.termid
group by t.groupid
) xx
where xx.groupid = t.groupid

 * @author  citibob
 */
public class TermsAddPanel extends javax.swing.JPanel
{
FrontApp fapp;
	
SchemaBufDbModel termsDm;

MultiDbModel allDm;

/** Creates new form CoursesPanel */
public TermsAddPanel()
{
	initComponents();
}



/** Creates new form CompleteStatusPanel */
public void initRuntime(FrontApp xfapp, SchoolModel smod, SqlRun str)
//throws SQLException
{
	this.fapp = xfapp;

	termsDm = new SchemaBufDbModel(
		fapp.getSchema("termids"), fapp.dbChange());
//	termsDm.setWhereClause("(firstdate > now() - interval '2 years' or iscurrent or firstdate is null)");
//	termsDm.setWhereClause("");
	termsDm.setOrderClause("firstdate desc, name");
		
	terms.setModelU(termsDm.getSchemaBuf(),
		new String[] {"Status", "Type", "Name", "From", "To (+1)", "Tuition Plans", "Is Current"},
		new String[] {"__status__", "termtypeid", "name", "firstdate", "nextdate", "rbplansetclass", "iscurrent"},
		null, fapp.swingerMap());

	allDm = new MultiDbModel(new DbModel[] {termsDm});
	allDm.doSelect(str);
}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel2 = new javax.swing.JPanel();
        termsPane = new javax.swing.JScrollPane();
        terms = new citibob.jschema.swing.SchemaBufTable();
        termsButtons = new javax.swing.JPanel();
        ddAdd = new javax.swing.JButton();
        ddDel = new javax.swing.JButton();
        ddUndel = new javax.swing.JButton();
        lDueDate = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        bSave = new javax.swing.JButton();
        bUndo = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.GridBagLayout());

        terms.setModel(new javax.swing.table.DefaultTableModel(
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
        termsPane.setViewportView(terms);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(termsPane, gridBagConstraints);

        ddAdd.setText("Add");
        ddAdd.setMargin(new java.awt.Insets(2, 2, 2, 2));
        ddAdd.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ddAddActionPerformed(evt);
            }
        });

        termsButtons.add(ddAdd);

        ddDel.setText("Del");
        ddDel.setMargin(new java.awt.Insets(2, 2, 2, 2));
        ddDel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ddDelActionPerformed(evt);
            }
        });

        termsButtons.add(ddDel);

        ddUndel.setText("(Undo)");
        ddUndel.setMargin(new java.awt.Insets(2, 2, 2, 2));
        ddUndel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ddUndelActionPerformed(evt);
            }
        });

        termsButtons.add(ddUndel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.7;
        jPanel2.add(termsButtons, gridBagConstraints);

        lDueDate.setText("Terms");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(lDueDate, gridBagConstraints);

        add(jPanel2, java.awt.BorderLayout.CENTER);

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

    }// </editor-fold>//GEN-END:initComponents

	private void ddUndelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ddUndelActionPerformed
	{//GEN-HEADEREND:event_ddUndelActionPerformed
		fapp.guiRun().run(TermsAddPanel.this, new ETask() {
		public void run() throws Exception {
			termsDm.getSchemaBuf().undeleteRow(terms.getSelectedRow());
			terms.requestFocus();
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_ddUndelActionPerformed

	private void bUndoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bUndoActionPerformed
	{//GEN-HEADEREND:event_bUndoActionPerformed
		fapp.guiRun().run(new SqlTask() {
		public void run(SqlRun str) throws Exception {
			allDm.doSelect(str);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bUndoActionPerformed

	private void bSaveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bSaveActionPerformed
	{//GEN-HEADEREND:event_bSaveActionPerformed
		fapp.guiRun().run(new SqlTask() {
		public void run(SqlRun str) throws Exception {
			allDm.doUpdate(str);
			str.flush();
			allDm.doSelect(str);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bSaveActionPerformed

	private void ddDelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ddDelActionPerformed
	{//GEN-HEADEREND:event_ddDelActionPerformed
		fapp.guiRun().run(TermsAddPanel.this, new ETask() {
		public void run() throws Exception {
			termsDm.getSchemaBuf().deleteRow(terms.getSelectedRow());
			terms.requestFocus();
		}});
	}//GEN-LAST:event_ddDelActionPerformed

	private void ddAddActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ddAddActionPerformed
	{//GEN-HEADEREND:event_ddAddActionPerformed
		fapp.guiRun().run(TermsAddPanel.this, new ETask() {
		public void run() throws Exception {
			JEnum tt = (JEnum)termsDm.getSchemaBuf().getJType(0, "termtypeid");
			Object defaultTermTypeID = tt.getKeyedModel().getKey(0); //getKeyList().get(0);
			termsDm.getSchemaBuf().insertRow(0,
				new String[] {"termtypeid", "iscurrent"},
				new Object[] {defaultTermTypeID, Boolean.TRUE});
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_ddAddActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bSave;
    private javax.swing.JButton bUndo;
    private javax.swing.JButton ddAdd;
    private javax.swing.JButton ddDel;
    private javax.swing.JButton ddUndel;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lDueDate;
    private citibob.jschema.swing.SchemaBufTable terms;
    private javax.swing.JPanel termsButtons;
    private javax.swing.JScrollPane termsPane;
    // End of variables declaration//GEN-END:variables
	
}