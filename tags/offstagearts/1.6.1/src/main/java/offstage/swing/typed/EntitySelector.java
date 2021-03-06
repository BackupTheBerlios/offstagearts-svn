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
 * SimpleSearchPanel.java
 *
 * Created on June 5, 2005, 5:47 PM
 */

package offstage.swing.typed;
import java.sql.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.swing.table.*;
import citibob.task.*;
import offstage.db.*;
import java.awt.event.*;
import citibob.swing.typed.*;
import citibob.types.KeyedModel;
import javax.swing.table.*;
import java.awt.*;
import offstage.FrontApp;
import offstage.equery.EQuery;
import offstage.equery.swing.EQueryWizard;

/**
 *
 * @author  citibob
 */
public class EntitySelector extends citibob.swing.typed.JTypedPanel {
	
// searchResultsTable is the main "sub widget", whose value change events get reported in JTypedPanel
FrontApp app;
int termid;					// If >=0, only select persons registered for this term.
boolean inDropDown;			// True if this widget is being used in a dropdown

private boolean autoSelectOnOne = false;		// If true, auto-select if we get only one element in our search results

/** Creates new form SimpleSearchPanel */
public EntitySelector() {
	initComponents();
}

public void setDropDown(boolean dropDown)
{ inDropDown = dropDown; }

/** Property change in the "main" widget (search panel) is the result of a user
 action (we know this to be the case, in this case).  Therefore, it must get
 its own DB transaction. */
public void propertyChange(final java.beans.PropertyChangeEvent evt)
{
		// We were started by mouse click (or some semblance thereof)
		// But don't do a busy cursor if we're within a dropdown
//		if (inDropDown) {
			app.sqlRun().pushFlush();
			EntitySelector.super.propertyChange(evt);
			app.sqlRun().popFlush();
//System.out.println("Event source = " + evt.getSource());
//		} else {
//			app.guiRun().run(EntitySelector.this, new SqlTask() {
//			public void run(SqlRun str) throws Exception {
//				EntitySelector.super.propertyChange(evt);
//			}});
//		}
//	} else {
//		// We're just in the middle of a long line of cascading events
//		EntitySelector.super.propertyChange(evt);		
//	}
}	


public void initRuntime(FrontApp xapp) //Statement st, FullEntityDbModel dm)
{ initRuntime(xapp, -1); }

public void initRuntime(FrontApp xapp, int termid) //Statement st, FullEntityDbModel dm)
{
	this.termid = termid;
	this.app = xapp;
	searchResultsTable.initRuntime(app);
	super.setSubWidget(searchResultsTable);

	// Set up DBID dropdown
	KeyedModel km = app.schemaSet().getKeyedModel("entities", "dbid");
	cbDbid.setKeyedModel(km);
	
	// Pressing ENTER will initiate search.
	searchWord.addKeyListener(new KeyAdapter() {
	public void keyTyped(KeyEvent e) {
		//System.out.println(e.getKeyChar());
		if (e.getKeyChar() == '\n') {
			app.guiRun().run(EntitySelector.this, new SqlTask() {
			public void run(SqlRun str) throws Exception {
				runSearch(str);
			}});
		}
	}});
}

public void setSearchIdSql(SqlRun str, String idSql)
{
	searchResultsTable.executeQuery(str, idSql, null);
	str.execUpdate(new UpdTasklet2() {
	public void run(SqlRun str) throws Exception {
		if (searchResultsTable.getModel().getRowCount() == 1 && isAutoSelectOnOne()) {
			searchResultsTable.setRowSelectionInterval(0,0);	// Auto-select the one item; Should fire an event...
		}
	}});	
}

public void setSearch(SqlRun str, String text)//, int dbid)
//throws SQLException
{
	int dbid = (Integer)cbDbid.getValue();
	String idSql = (termid >= 0 ? DB.registeredSearchSql(text, dbid, termid) : DB.simpleSearchSql(text, dbid));
	setSearchIdSql(str, idSql);
}

void runSearch(SqlRun str) { //throws Exception {
//	app.guiRun().run(this, new BatchRunnable() {
//	public void run(SqlRun str) throws Exception {
	String text = searchWord.getText();
	setSearch(app.sqlRun(), text);
//	app.getBatchSet().runBatches();
//	}});
}


///** Allows others to add a double-click-to-select mouse listener. */
//public JTypedSelectTable getSearchTable()
//	{ return searchResultsTable; }
// ----------------------------------------------------------------------

/** Used when this widget is in a popup; put the focus immediately on the search field. */
public void requestTextFocus()
{
	searchWord.setText("");
	searchWord.requestFocus();
}

/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        searchWord = new javax.swing.JTextField();
        bSearch = new javax.swing.JButton();
        bAdvanced = new javax.swing.JButton();
        cbDbid = new citibob.swing.typed.JKeyedComboBox();
        FamilyScrollPanel = new javax.swing.JScrollPane();
        searchResultsTable = new offstage.swing.typed.IdSqlTable();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(searchWord, gridBagConstraints);

        bSearch.setText("Search");
        bSearch.setPreferredSize(new java.awt.Dimension(84, 25));
        bSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSearchActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanel1.add(bSearch, gridBagConstraints);

        bAdvanced.setText("Advanced");
        bAdvanced.setPreferredSize(new java.awt.Dimension(100, 25));
        bAdvanced.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAdvancedActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(bAdvanced, gridBagConstraints);

        cbDbid.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(cbDbid, gridBagConstraints);

        add(jPanel1, java.awt.BorderLayout.SOUTH);

        FamilyScrollPanel.setPreferredSize(new java.awt.Dimension(300, 64));

        searchResultsTable.setModel(new javax.swing.table.DefaultTableModel(
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
        FamilyScrollPanel.setViewportView(searchResultsTable);

        add(FamilyScrollPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

	
	private void bSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSearchActionPerformed
		app.guiRun().run(EntitySelector.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			runSearch(str);
//			app.getBatchSet().runBatches();
		}});
	}//GEN-LAST:event_bSearchActionPerformed

	private void bAdvancedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAdvancedActionPerformed
		app.guiRun().run(EntitySelector.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			EQueryWizard wizard = new EQueryWizard(app, EntitySelector.this);
			if (!wizard.runAdvancedSearch(str)) return;
			EQuery equery = (EQuery)wizard.getVal("equery");
			setSearchIdSql(str, equery.getSql(app.equerySchema()));
		}});
		// TODO add your handling code here:
}//GEN-LAST:event_bAdvancedActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane FamilyScrollPanel;
    private javax.swing.JButton bAdvanced;
    private javax.swing.JButton bSearch;
    private citibob.swing.typed.JKeyedComboBox cbDbid;
    private javax.swing.JPanel jPanel1;
    private offstage.swing.typed.IdSqlTable searchResultsTable;
    private javax.swing.JTextField searchWord;
    // End of variables declaration//GEN-END:variables
// ===========================================================
///** Pass along change in value from underlying typed widget --- but only
// if new value is non-null.   This widget can onl*/
//public void propertyChange(java.beans.PropertyChangeEvent evt) {
//	Object newval = evt.getNewValue();
//	firePropertyChange("value", evt.getOldValue(), newval);
//}	
// ===========================================================

	
//public static void main(String[] args) throws Exception
//{	
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	Statement st = pool.checkout().createStatement();
//	FrontApp fapp = new FrontApp(pool,null);
//	
//	javax.swing.JFrame frame = new javax.swing.JFrame();
//	EntitySelector panel = new EntitySelector();
//	panel.initRuntime(fapp);
//	frame.getContentPane().add(panel);
//	frame.pack();
//	frame.setVisible(true);
//}

	public boolean isAutoSelectOnOne()
	{
		return autoSelectOnOne;
	}

	public void setAutoSelectOnOne(boolean autoSelectOnOne)
	{
		this.autoSelectOnOne = autoSelectOnOne;
	}

	
}
