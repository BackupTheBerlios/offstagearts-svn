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
 * AccountsPanel.java
 *
 * Created on December 10, 2007, 1:01 AM
 */

package offstage.accounts.gui;

import citibob.app.App;
import citibob.jschema.IntKeyedDbModel;
import citibob.jschema.SchemaSet;
import citibob.sql.SqlRun;
import citibob.sql.UpdTasklet;
import citibob.swing.typed.TypedWidgetBinder;
import citibob.task.ActionJobBinder;
import citibob.task.SqlTask;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import offstage.FrontApp;
import offstage.db.EntityDbModel;

/**
 *
 * @author  citibob
 */
public class AccountPanel extends javax.swing.JPanel
{

IntKeyedDbModel onePerson;
Integer Entityid;
Integer Actypeid;
App app;

/** Creates new form AccountsPanel */
public AccountPanel()
{
	initComponents();
}
public void initRuntime(FrontApp fapp)
{
	this.app = fapp;
	

//	SchemaBuf actransSb = new SchemaBuf(fapp.getSchema("actrans"));
	transRegister.initRuntime(fapp, TransRegPanel.EM_ALL, 1, 0);
	
	// Bind our account actions
	ActionJobBinder tbinder = new ActionJobBinder(this, fapp.guiRun(),
		transRegister.getTaskMap());
	tbinder.bind(this.bCash, "cash");
	tbinder.bind(this.bCheck, "check");
	tbinder.bind(this.bCc, "cc");
	tbinder.bind(this.bOtherTrans, "other");
	tbinder.bind(this.bDelete, "delete");
	
	// Set up our models
//	QueryLogger logger = fapp.getLogger();
//	final SchemaSet osset = fapp.schemaSet();
	onePerson = new EntityDbModel(fapp.schemaSet().get("persons"), fapp);
//		onePerson.setLogger(logger);

	TypedWidgetBinder.bindRecursive(entityPanel, onePerson.getSchemaBuf(), fapp.swingerMap());

	// Change entity when a person is selected.
	selector.initRuntime(fapp);
	selector.addPropertyChangeListener("value", new PropertyChangeListener() {
	public void propertyChange(PropertyChangeEvent evt) {
		Entityid = (Integer)evt.getNewValue();
		refreshEntityid(app.sqlRun());
	}});

	// Change account when Actypeid dropdown is selected
	selActypeid.addPropertyChangeListener("value", new PropertyChangeListener() {
	public void propertyChange(PropertyChangeEvent evt) {
		// Property change was (almost probably) due to a mouse click;
		// So no gui runner her.
		Actypeid = (Integer)selActypeid.getValue();
		refreshActypeid(app.sqlRun());
	}});
	
	fapp.sqlRun().execUpdate(new UpdTasklet() {
	public void run() {
		// This ends up affecting TransRegPanel, which needs to be
		// fully initialized.
		selActypeid.setKeyedModel(app.schemaSet(), "actrans2", "cr_actypeid");
	}});
}

/** Called when Entityid changes */
void refreshEntityid(SqlRun str)
{
	onePerson.setKey(Entityid);
	onePerson.doSelect(str);
	transRegister.setEntityID(str, Entityid);
}

void refreshActypeid(SqlRun str)
{
	transRegister.setAcTypeID(str, Actypeid.intValue());
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
        AccountPane = new javax.swing.JPanel();
        entityPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        selActypeid = new citibob.swing.typed.JKeyedComboBox();
        jLabel4 = new javax.swing.JLabel();
        firstName = new citibob.swing.typed.JTypedLabel();
        lastName = new citibob.swing.typed.JTypedLabel();
        space = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        entityid = new citibob.swing.typed.JTypedLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        personDetails = new javax.swing.JTextPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        transRegister = new offstage.accounts.gui.TransRegPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        bCash = new javax.swing.JButton();
        bCheck = new javax.swing.JButton();
        bCc = new javax.swing.JButton();
        bOtherTrans = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        bDelete = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        transDetails = new javax.swing.JTextPane();
        jSplitPane3 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTypeColTable1 = new citibob.swing.JTypeColTable();
        selector = new offstage.swing.typed.EntitySelector();
        jToolBar1 = new javax.swing.JToolBar();
        bSave = new javax.swing.JButton();
        bRefresh = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        AccountPane.setPreferredSize(new java.awt.Dimension(484, 100));
        AccountPane.setLayout(new java.awt.BorderLayout());

        entityPanel.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("Account: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        entityPanel.add(jLabel2, gridBagConstraints);

        selActypeid.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        selActypeid.setMinimumSize(new java.awt.Dimension(150, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        entityPanel.add(selActypeid, gridBagConstraints);

        jLabel4.setText("Name: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        entityPanel.add(jLabel4, gridBagConstraints);

        firstName.setText("jTypedLabel1");
        firstName.setColName("firstname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        entityPanel.add(firstName, gridBagConstraints);

        lastName.setText("jTypedLabel1");
        lastName.setColName("lastname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        entityPanel.add(lastName, gridBagConstraints);

        space.setText(" ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        entityPanel.add(space, gridBagConstraints);

        jLabel5.setText("ID: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        entityPanel.add(jLabel5, gridBagConstraints);

        entityid.setText("jTypedLabel1");
        entityid.setColName("entityid");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        entityPanel.add(entityid, gridBagConstraints);

        personDetails.setMinimumSize(new java.awt.Dimension(6, 150));
        jScrollPane3.setViewportView(personDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        entityPanel.add(jScrollPane3, gridBagConstraints);

        AccountPane.add(entityPanel, java.awt.BorderLayout.PAGE_START);

        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel3.setLayout(new java.awt.BorderLayout());
        jPanel3.add(transRegister, java.awt.BorderLayout.CENTER);

        jSplitPane2.setLeftComponent(jPanel3);

        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel13.setPreferredSize(new java.awt.Dimension(484, 35));
        jPanel13.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel17.setText("Transaction:");
        jPanel13.add(jLabel17);

        bCash.setText("Cash");
        bCash.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jPanel13.add(bCash);

        bCheck.setText("Check");
        bCheck.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jPanel13.add(bCheck);

        bCc.setText("Credit");
        bCc.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jPanel13.add(bCc);

        bOtherTrans.setText("Other");
        bOtherTrans.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jPanel13.add(bOtherTrans);

        jLabel1.setText("     ");
        jPanel13.add(jLabel1);

        bDelete.setText("Delete");
        bDelete.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jPanel13.add(bDelete);

        jPanel4.add(jPanel13, java.awt.BorderLayout.SOUTH);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabel6.setText("Transaction Details:");
        jPanel5.add(jLabel6, java.awt.BorderLayout.NORTH);

        jScrollPane2.setViewportView(transDetails);

        jPanel5.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel5, java.awt.BorderLayout.CENTER);

        jSplitPane2.setRightComponent(jPanel4);

        AccountPane.add(jSplitPane2, java.awt.BorderLayout.CENTER);

        jSplitPane1.setLeftComponent(AccountPane);

        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel3.setText("Balance Summary:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(jLabel3, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(22, 150));

        jTypeColTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTypeColTable1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(jScrollPane1, gridBagConstraints);

        jSplitPane3.setTopComponent(jPanel2);

        selector.setAutoSelectOnOne(true);
        jSplitPane3.setRightComponent(selector);

        jSplitPane1.setRightComponent(jSplitPane3);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jToolBar1.setRollover(true);

        bSave.setText("Save");
        bSave.setFocusable(false);
        bSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bSave.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(bSave);

        bRefresh.setText("Referesh");
        bRefresh.setFocusable(false);
        bRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bRefresh.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bRefreshActionPerformed(evt);
            }
        });
        jToolBar1.add(bRefresh);

        add(jToolBar1, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

	private void bSaveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bSaveActionPerformed
	{//GEN-HEADEREND:event_bSaveActionPerformed
		app.guiRun().run(AccountPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			transRegister.getDbModel().doUpdate(str);
			transRegister.refresh(str);
		}});
		// TODO add your handling code here:
	}//GEN-LAST:event_bSaveActionPerformed

	private void bRefreshActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bRefreshActionPerformed
	{//GEN-HEADEREND:event_bRefreshActionPerformed
		app.guiRun().run(AccountPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			transRegister.getDbModel().doSelect(str);
		}});
		// TODO add your handling code here:
	}//GEN-LAST:event_bRefreshActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AccountPane;
    private javax.swing.JButton bCash;
    private javax.swing.JButton bCc;
    private javax.swing.JButton bCheck;
    private javax.swing.JButton bDelete;
    private javax.swing.JButton bOtherTrans;
    private javax.swing.JButton bRefresh;
    private javax.swing.JButton bSave;
    private javax.swing.JPanel entityPanel;
    private citibob.swing.typed.JTypedLabel entityid;
    private citibob.swing.typed.JTypedLabel firstName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JToolBar jToolBar1;
    private citibob.swing.JTypeColTable jTypeColTable1;
    private citibob.swing.typed.JTypedLabel lastName;
    private javax.swing.JTextPane personDetails;
    private citibob.swing.typed.JKeyedComboBox selActypeid;
    private offstage.swing.typed.EntitySelector selector;
    private javax.swing.JLabel space;
    private javax.swing.JTextPane transDetails;
    private offstage.accounts.gui.TransRegPanel transRegister;
    // End of variables declaration//GEN-END:variables
	
}
