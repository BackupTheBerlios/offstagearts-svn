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
 * CoursesEditor.java
 *
 * Created on June 8, 2007, 10:08 PM
 */

package offstage.wizard.editcourses;

import java.sql.*;
import citibob.jschema.*;
import citibob.jschema.swing.*;
import citibob.swing.table.*;
import citibob.swing.typed.*;
import citibob.task.*;
import citibob.jschema.swing.StatusTable;
import citibob.sql.*;
import citibob.app.*;
import offstage.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import citibob.util.*;
import java.util.*;
import citibob.sql.pgsql.*;
import citibob.swing.sql.*;
import citibob.sql.*;
import citibob.types.*;

/**
 *
 * @author  citibob
 */
public class CoursesWiz extends citibob.swing.JPanelWiz
{

IntKeyedDbModel coursesSb;
FrontApp fapp;

/** Creates new form CompleteStatusPanel */
public CoursesWiz(FrontApp xfapp, SqlRunner str, final int termid)
throws SQLException
{
	super("Edit Courses");
	initComponents();
	this.fapp = xfapp;

	// Set up terms selector
//	SqlBatchSet str0 = new SqlBatchSet();
	terms.setKeyedModel(new DbKeyedModel(str, fapp.getDbChange(), "termids",
		"select groupid, name from termids where iscurrent order by firstdate"));
//	str0.runBatches(fapp);

	str.execUpdate(new UpdRunnable() {
	public void run(SqlRunner str) throws Exception {

		terms.addPropertyChangeListener("value", new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			fapp.runApp(new BatchRunnable() {
			public void run(SqlRunner str) throws Exception {
				termChanged(str);
			}});
		}});

		// Set up courses editor
		coursesSb = new IntKeyedDbModel(fapp.getSchema("courseids"),
			"termid", fapp.getDbChange(), new IntKeyedDbModel.Params());
		coursesSb.setOrderClause("dayofweek, tstart, name");

		terms.setValue(termid);
		//termChanged(str);
//		terms.setSelectedIndex(0);		// Should throw a value changed event
		courses.setModelU(coursesSb.getSchemaBuf(),
			new String[] {"Name", "Day", "Start", "End", "Tuition", "Enroll Limit"},
			new String[] {"name", "dayofweek", "tstart", "tnext", "price", "enrolllimit"},
			null, fapp.getSwingerMap());
//		courses.setRowSelectionAllowed(false);

//courses.setRowSelectionAllowed(false);
//courses.setColumnSelectionAllowed(false);
//courses.setCellSelectionEnabled(false);

//		public void setModelU(SchemaBuf schemaBuf,
//String[] xColNames, String[] xSColMap,
//boolean[] xEditable, SwingerMap swingers)

		KeyedModel dkm = new DayOfWeekKeyedModel();
		courses.setRenderEditU("dayofweek", dkm);

//		Swinger swing = new SqlTimeSwinger(true, "HH:mm");
//		courses.setRenderEditU("tstart", swing);
//		courses.setRenderEditU("tnext", swing);

//		KeyedRenderEdit tkre = new KeyedRenderEdit(new TimeSKeyedModel(7,0, 23,0, 15*60));
//		courses.setRenderEditU("tstart_s", tkre);
//		courses.setRenderEditU("tnext_s", tkre);

		if (courses.getModelU().getRowCount() > 0) courses.setRowSelectionInterval(0,0);
	}});
}

void termChanged(SqlRunner str) throws SQLException
{
	coursesSb.setKey((Integer)terms.getValue());
	coursesSb.doUpdate(str);
//	str.execUpdate(new UpdRunnable() {
//	public void run(SqlRunner str) throws Exception {
//	}});
	coursesSb.doSelect(str);
}
	
	/** After the Wiz is done running, report its output into a Map. */
	public void getAllValues(java.util.Map map)
	{
		Object courseid = courses.getOneSelectedValU("courseid");
		map.put("courseid", courseid);
	}

	public void backPressed() { saveCur(); }
	public void nextPressed() { saveCur(); }

	public void saveCur()
	{
		fapp.runGui(CoursesWiz.this, new BatchRunnable() {
		public void run(SqlRunner str) throws Exception {
			if (coursesSb.valueChanged()) {
				coursesSb.doUpdate(str);
				coursesSb.doSelect(str);
			}
		}});
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

        jPanel1 = new javax.swing.JPanel();
        bAdd = new javax.swing.JButton();
        bDel = new javax.swing.JButton();
        bRestore = new javax.swing.JButton();
        bSave = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        courses = new citibob.jschema.swing.StatusTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        terms = new citibob.swing.typed.JKeyedComboBox();

        setLayout(new java.awt.BorderLayout());

        bAdd.setText("Add");
        bAdd.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bAddActionPerformed(evt);
            }
        });

        jPanel1.add(bAdd);

        bDel.setText("Del");
        bDel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bDelActionPerformed(evt);
            }
        });

        jPanel1.add(bDel);

        bRestore.setText("Restore");
        bRestore.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bRestoreActionPerformed(evt);
            }
        });

        jPanel1.add(bRestore);

        bSave.setText("Save");
        bSave.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bSaveActionPerformed(evt);
            }
        });

        jPanel1.add(bSave);

        add(jPanel1, java.awt.BorderLayout.SOUTH);

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
        jScrollPane1.setViewportView(courses);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Term: ");
        jPanel2.add(jLabel1, new java.awt.GridBagConstraints());

        terms.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(terms, gridBagConstraints);

        add(jPanel2, java.awt.BorderLayout.NORTH);

    }// </editor-fold>//GEN-END:initComponents
	
	private void bSaveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bSaveActionPerformed
	{//GEN-HEADEREND:event_bSaveActionPerformed
		saveCur();
//		fapp.runGui(CoursesWiz.this, new StRunnable() {
//		public void run(SqlRunner str) throws Exception {
//			  coursesSb.doUpdate(st);
//			  coursesSb.doSelect(st);
//		  }});
// TODO add your handling code here:
	}//GEN-LAST:event_bSaveActionPerformed

	private void bRestoreActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bRestoreActionPerformed
	{//GEN-HEADEREND:event_bRestoreActionPerformed
		fapp.runGui(CoursesWiz.this, new BatchRunnable()
		{ public void run(SqlRunner str) throws Exception
		  {
			  coursesSb.doSelect(str);
		  }});
// TODO add your handling code here:
	}//GEN-LAST:event_bRestoreActionPerformed

	private void bDelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bDelActionPerformed
	{//GEN-HEADEREND:event_bDelActionPerformed
		fapp.runGui(CoursesWiz.this, new ERunnable()
		{ public void run() throws Exception
		  {
			  int selected = courses.getSelectedRow();
			  if (selected != -1) {
				  coursesSb.getSchemaBuf().deleteRow(selected);
			  }
		  }});
	}//GEN-LAST:event_bDelActionPerformed

	private void bAddActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bAddActionPerformed
	{//GEN-HEADEREND:event_bAddActionPerformed
		fapp.runGui(CoursesWiz.this, new ERunnable()
		{ public void run() throws Exception
		  {
			  coursesSb.getSchemaBuf().insertRow(-1);
		  }});
// TODO add your handling code here:
	}//GEN-LAST:event_bAddActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAdd;
    private javax.swing.JButton bDel;
    private javax.swing.JButton bRestore;
    private javax.swing.JButton bSave;
    private citibob.jschema.swing.StatusTable courses;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private citibob.swing.typed.JKeyedComboBox terms;
    // End of variables declaration//GEN-END:variables
	
}
