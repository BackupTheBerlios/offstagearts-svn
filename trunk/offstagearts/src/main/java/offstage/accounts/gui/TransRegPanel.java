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

import citibob.jschema.SchemaBuf;
import citibob.jschema.SqlBufDbModel;
import citibob.sql.RsTasklet2;
import citibob.sql.SqlRun;
import citibob.sql.UpdTasklet2;
import citibob.sql.pgsql.*;
import citibob.swing.table.JTypeTableModel;
import citibob.task.SqlTask;
import citibob.task.JobMap;
import citibob.task.SqlTask;
import citibob.wizard.Wizard;
import offstage.FrontApp;

/**
 *
 * @author  citibob
 */
public class TransRegPanel extends javax.swing.JPanel
{
	
public SqlBufDbModel actransDb;
FrontApp fapp;
int assetid;
JobMap taskMap;
Integer entityid;
int actypeid;

// =====================================================
class Actrans2DbModel extends SqlBufDbModel
{
	public Actrans2DbModel(SqlRun str) {
		super(str, fapp,
			new String[] {"actrans2", "actrans2amt"},
			null,
			new String[] {"actrans2", "actrans2amt"});
	}
	public String getSelectSql(boolean proto) {
		return
			" select ac.actransid, ac.actranstypeid, ac.date," +
			" 1 as multiplier, amt.amount as amount, ac.description" +
			" from actrans2 ac, actrans2amt amt" +
			" where ac.cr_entityid = " + entityid + " and ac.actypeid = " + actypeid +
			" and ac.actransid = amt.actransid and amt.assetid = " + assetid +
			" and now()-ac.date < '450 days'" +
			(proto ? " and 1=0" : "") +
			"      UNION" +
			" select ac.actransid, ac.actranstypeid, ac.date," +
			" -1 as multiplier, amt.amount as amount, ac.description" +
			" from actrans2 ac, actrans2amt amt" +
			" where ac.db_entityid = " + entityid + " and ac.actypeid = " + actypeid +
			" and ac.actransid = amt.actransid and amt.assetid = " + assetid +
			" and now()-ac.date < '450 days'" +
			(proto ? " and 1=0" : "") +
			" order by date desc, actransid desc";
	}
}
// =====================================================

public static final int EM_NONE = 0;
public static final int EM_RECENT = 1;
public static final int EM_ALL = 2;
static class TransTableModel extends citibob.swing.table.WrapJTypeTableModel
{
int multiplierCol;
int amountCol;
int createdCol;
int editMode;

	public TransTableModel(JTypeTableModel xsub, int editMode) {
		super(xsub);
		this.editMode = editMode;
		
		multiplierCol = sub.findColumn("multiplier");
		amountCol = sub.findColumn("amount");
		createdCol = sub.findColumn("datecreated");
	}
	public void setValueAt(Object val, int row, int col)
	{
		// Convert single-entry accounting on screen to double-entry in database.
		if (col == amountCol) {
			if (val == null) {
				super.setValueAt(null, row, col);
				return;
			}
			int mult = ((Number)getValueAt(row, multiplierCol)).intValue();
			double vval = ((Number)getValueAt(row, col)).doubleValue();
			super.setValueAt(mult * vval, row, col);
			return;
		}
		super.setValueAt(val, row, col);
	}
	
	public Object getValueAt(int row, int col)
	{
		// Convert single-entry accounting on screen to double-entry in database.
		if (col == amountCol) {
			Object val = super.getValueAt(row, col);
			if (val == null) return null;
			return new Double(((Number)val).doubleValue());
		}
		return super.getValueAt(row, col);
	}
	
	public boolean isCellEditable(int row, int col) {
		switch(editMode) {
			case EM_NONE : return false;
			case EM_ALL : return super.isCellEditable(row, col);
			case EM_RECENT : {
				if (col >= getColumnCount()) return false;
				if (row >= getRowCount()) return false;
				java.util.Date created = (java.util.Date)getValueAt(row, createdCol);
				if (created == null) return false;
				java.util.Date now = new java.util.Date();
				return (now.getTime() - created.getTime() < 86400 * 1000L);
			}
		}
		return false;
	};
	
}
// =====================================================
//		str.execUpdate(new UpdTasklet() {
//		public void run() {
//			multiplierCol = getSchemaBuf().findColumn("multiplier");
//			amountCol = getSchemaBuf().findColumn("amount");
//		}});


/** Creates new form AccountsPanel */
public TransRegPanel()
{
	initComponents();
}

public SqlBufDbModel getDbModel() { return actransDb; }




public void initRuntime(final FrontApp fapp, final int editMode, int actypeid, int assetid) //, boolean superuser)
{
	this.fapp = fapp;
	this.assetid = assetid;
	
	SqlRun str = fapp.sqlRun();
	actransDb = new Actrans2DbModel(str);
	str.execUpdate(new UpdTasklet2() {
	public void run(SqlRun str) {
		JTypeTableModel model = new TransTableModel(
			actransDb.getSchemaBuf(), editMode);
		trans.setModelU(model, //actransDb.getSchemaBuf(),
			new String[] {"Status", "Type", "Date", "Amount", "Description"},
			new String[] {"__status__", "actranstypeid", "date", "amount", "description"},
			new String[] {null, null, null, null, "description"},
			editMode == EM_ALL ? new boolean[] {false,false,true,true,true} : null,
	//		new boolean[] {false, false, false, false},
			fapp.swingerMap());
		actransDb.setLogger(fapp.queryLogger());		
	}});

	
//	actransDb = new IntsKeyedDbModel(actransSb, new String[] {"entityid", "actypeid"}, true);
//	actransDb.setKey(1, actypeid);
//	actransDb.setWhereClause(
////		" actypeid = " + SqlInteger.sql(ActransSchema.AC_SCHOOL) +
//		" now()-date < '450 days'");
//	actransDb.setOrderClause("date desc, actransid desc");

	// Set up the task map, which will be used to bind actions to buttons
	taskMap = new JobMap();
	String[] permissions = null;
	taskMap.add("cash", permissions, new RunWizard("cashpayment"));
	taskMap.add("check", permissions, new RunWizard("checkpayment"));
	taskMap.add("cc", permissions, new RunWizard("ccpayment"));
	taskMap.add("other", permissions, new RunWizard("transtype"));
	taskMap.add("delete", permissions, new SqlTask() {
	public void run(SqlRun str) throws Exception {
		SchemaBuf sb = actransDb.getSchemaBuf();
		sb.deleteRow(trans.getSelectedRow());
	}});

}

public JobMap getTaskMap() { return taskMap; }
public Integer getEntityID() { return entityid; }
public Integer getAcTypeID() { return actypeid; }


public void setEntityID(SqlRun str, Integer entityid) // throws SQLException
{
//	actransDb.setKey(0, entityid);
	this.entityid = entityid;
	refresh(str);
}
public void setAcTypeID(SqlRun str, int actypeid)
{
//	actransDb.setKey(1, actypeid);
	this.actypeid = actypeid;
	refresh(str);	
}
public void refresh(SqlRun str) // throws SQLException
{
	actransDb.doSelect(str);
	
	// Set up account balance
	acbal.setJType(Double.class, java.text.NumberFormat.getCurrencyInstance());
//	Integer entityid = actransDb.getIntKey(0);
//	Integer actypeid = actransDb.getIntKey(1);
	String sql =
		AccountsDB.w_tmp_acct_balance_sql("select " + entityid + " as id", actypeid, assetid) +
		" select bal from _bal;\n" +
		" drop table _bal;";
	str.execSql(sql, new RsTasklet2() {
	public void run(citibob.sql.SqlRun str, java.sql.ResultSet rs) throws Exception {
		rs.next();
		acbal.setValue(rs.getDouble(1));
	}});
	
//	
//	
//	DB.r_acct_balance("bal", str, , ,
//	new UpdTasklet2() { public void run(SqlRun str) throws Exception {
//		acbal.setValue(str.get("bal"));
//	}});
}

public Double getBalance() { return (Double)acbal.getValue(); }


	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        TopPane = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        acbal = new citibob.swing.typed.JTypedLabel();
        GroupScrollPanel1 = new javax.swing.JScrollPane();
        trans = new citibob.jschema.swing.SchemaBufTable();

        setLayout(new java.awt.BorderLayout());

        TopPane.setLayout(new java.awt.GridBagLayout());

        jLabel18.setText("Balance: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        TopPane.add(jLabel18, gridBagConstraints);

        acbal.setText("2500");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        TopPane.add(acbal, gridBagConstraints);

        add(TopPane, java.awt.BorderLayout.NORTH);

        trans.setModel(new javax.swing.table.DefaultTableModel(
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
        GroupScrollPanel1.setViewportView(trans);

        add(GroupScrollPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane GroupScrollPanel1;
    private javax.swing.JPanel TopPane;
    private citibob.swing.typed.JTypedLabel acbal;
    private javax.swing.JLabel jLabel18;
    private citibob.jschema.swing.SchemaBufTable trans;
    // End of variables declaration//GEN-END:variables

// =========================================================
class RunWizard implements SqlTask
{
String startState;
public RunWizard(String startState) { this.startState = startState; }
public void run(SqlRun str) throws Exception {
	int entityid = getEntityID();
	Wizard wizard = new TransactionWizard(fapp, TransRegPanel.this, entityid, getAcTypeID());
	wizard.setVal("entityid", entityid);
	wizard.runWizard(startState);
	refresh(str);
}
}

}
