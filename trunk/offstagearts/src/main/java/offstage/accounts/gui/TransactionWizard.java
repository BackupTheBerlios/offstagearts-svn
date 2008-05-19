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
package offstage.accounts.gui;
/*
 * NewRecordWizard.java
 *
 * Created on October 8, 2006, 11:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

import citibob.app.App;
import citibob.sql.pgsql.SqlInteger;
import citibob.swing.html.*;
import citibob.wizard.*;
import javax.swing.*;
import java.sql.*;
import offstage.db.*;
import offstage.wizards.*;
import offstage.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.jschema.log.*;
import java.awt.Component;
import offstage.crypt.*;
import offstage.swing.typed.*;

/**
 *
 * @author citibob
 */
public class TransactionWizard extends OffstageWizard {

//	final static String sinkName = "received";
	int entityid, actypeid;


	
	

public TransactionWizard(offstage.FrontApp xfapp, Component comp,
Integer xentityid, int xactypeid)
{
	super("Transactions", xfapp, comp);
	this.entityid = xentityid;
	this.actypeid = xactypeid;
// ---------------------------------------------
//addState(new AbstractWizState("init", "init", "init") {
//	public HtmlWiz newWiz(WizState.Context con) throws Exception
//		{ return new InitWiz(frame); }
//	public void process(citibob.sql.SqlRun str) throws Exception
//	{
//		String s = v.getString("type");
//		if (s != null) state = s;
//	}
//});
// ---------------------------------------------
addStartState(new AbstractWizState("transtype", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new TransTypeWiz(frame, con.str, fapp, v); }
	public void process(Wizard.Context con) throws Exception
		{ stateName = v.getString("submit"); }
});
addState(new AbstractWizState("cashpayment", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new CashpaymentWiz(frame, fapp); }
	public void process(Wizard.Context con) throws Exception
	{
		double amount = -((Number)v.get("amount")).doubleValue();
		String sql = AccountsDB.w_actrans2_insert_sql(
			fapp, entityid, "received", actypeid,
			"cash", (Date)v.get("date"), con.v,
			new int[] {0}, new double[] {amount});
		con.str.execSql(sql);
	}
});
addState(new AbstractWizState("cashrefund", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new CashRefundWiz(frame, fapp); }
	public void process(Wizard.Context con) throws Exception
	{
		double amount = ((Number)v.get("amount")).doubleValue();
		String sql = AccountsDB.w_actrans2_insert_sql(
			fapp, entityid, "received", actypeid,
			"cash", (Date)v.get("date"), con.v,
			new int[] {0}, new double[] {amount});
		con.str.execSql(sql);
	}
});
addState(new AbstractWizState("adjpayment", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new AdjpaymentWiz(frame, fapp); }
	public void process(Wizard.Context con) throws Exception
	{
		double amount = ((Number)v.get("amount")).doubleValue();
		String sql = AccountsDB.w_actrans2_insert_sql(
			fapp, entityid, "received", actypeid,
			"adj", (Date)v.get("date"), con.v,
			new int[] {0}, new double[] {amount});
		con.str.execSql(sql);
	}
});
addState(new AbstractWizState("checkpayment", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new CheckpaymentWiz(frame, fapp); }
	public void process(Wizard.Context con) throws Exception
	{
		double amount = -((Number)v.get("amount")).doubleValue();
		String sql = AccountsDB.w_actrans2_insert_sql(
			fapp, entityid, "received", actypeid,
			"check", (Date)v.get("date"), con.v,
			new int[] {0}, new double[] {amount});
		con.str.execSql(sql);
	}
});
addState(new AbstractWizState("checkrefund", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new CheckRefundWiz(frame, fapp); }
	public void process(Wizard.Context con) throws Exception
	{
		double amount = ((Number)v.get("amount")).doubleValue();
		String sql = AccountsDB.w_actrans2_insert_sql(
			fapp, entityid, "received", actypeid,
			"cash", (Date)v.get("date"), con.v,
			new int[] {0}, new double[] {amount});
		con.str.execSql(sql);
	}
});
addState(new AbstractWizState("ccpayment", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new CcpaymentWiz(frame, con.str, entityid, fapp); }
	public void process(Wizard.Context con) throws Exception
	{
		double amount = -((Number)v.get("amount")).doubleValue();
		String sql = AccountsDB.w_actrans2_insert_sql(
			fapp, entityid, "received", actypeid,
			"credit", (Date)v.get("date"), con.v,
			new int[] {0}, new double[] {amount});
		con.str.execSql(sql);
	}
});
// ---------------------------------------------
}
// ---------------------------------------------


}
