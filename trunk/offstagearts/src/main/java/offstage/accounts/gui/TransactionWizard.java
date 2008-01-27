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

	int entityid, actypeid;
	SqlDate sqlDate;
/*
addState(new AbstractWizState("", "", "") {
	public HtmlWiz newWiz(WizState.Context con)
		{ return new }
	public void process(citibob.sql.SqlRunner str)
	{
		
	}
});
*/

/** Does an insert, using all the field names in v automatically. */
void vInsert(Wizard.Context con, String table) throws SQLException
{
	ConsSqlQuery sql = newInsertQuery(table, con.v);
	if (!sql.containsColumn("entityid")) sql.addColumn("entityid", SqlInteger.sql(entityid));
	sql.addColumn("actypeid", SqlInteger.sql(actypeid));
//	sql.addColumn("date", sqlDate.toSql(new java.util.Date()));		// Store day that it is in home timezone
	sql.addColumn("datecreated", sqlDate.toSql(new java.util.Date()));		// Store day that it is in home timezone
	con.str.execSql(sql.getSql());
}

public TransactionWizard(offstage.FrontApp xfapp, Component comp,
Integer xentityid, int xactypeid)
{
	super("Transactions", xfapp, comp);
	this.entityid = xentityid;
	this.actypeid = xactypeid;
	sqlDate = new SqlDate(fapp.getTimeZone(), false);
// ---------------------------------------------
//addState(new AbstractWizState("init", "init", "init") {
//	public HtmlWiz newWiz(WizState.Context con) throws Exception
//		{ return new InitWiz(frame); }
//	public void process(citibob.sql.SqlRunner str) throws Exception
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
		double namount = ((Number)v.get("namount")).doubleValue();
		con.v.put("amount", new Double(-namount));
		vInsert(con, "cashpayments");
	}
});
addState(new AbstractWizState("cashrefund", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new CashRefundWiz(frame, fapp); }
	public void process(Wizard.Context con) throws Exception
	{
		vInsert(con, "cashpayments");
	}
});
addState(new AbstractWizState("adjpayment", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new AdjpaymentWiz(frame, fapp); }
	public void process(Wizard.Context con) throws Exception
	{
//		double namount = ((Number)v.get("namount")).doubleValue();
//		con.v.put("amount", new Double(-namount));
		vInsert(con, "adjpayments");
	}
});
addState(new AbstractWizState("checkpayment", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new CheckpaymentWiz(frame, fapp); }
	public void process(Wizard.Context con) throws Exception
	{
		double namount = ((Number)v.get("namount")).doubleValue();
		v.put("amount", new Double(-namount));
		vInsert(con, "checkpayments");
	}
});
addState(new AbstractWizState("checkrefund", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new CheckRefundWiz(frame, fapp); }
	public void process(Wizard.Context con) throws Exception
	{
		vInsert(con, "checkpayments");
	}
});
addState(new AbstractWizState("ccpayment", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new CcpaymentWiz(frame, con.str, entityid, fapp); }
	public void process(Wizard.Context con) throws Exception
	{
		CcpaymentWiz cwiz = (CcpaymentWiz)wiz;
//		cwiz.getWidget("ccchooser")
		CCChooser cc = (CCChooser)cwiz.getComponent("ccchooser");
		// TODO: Log change to credit card # on file (or should I?)
		cc.saveNewCardIfNeeded(con.str);
		
		ConsSqlQuery sql = new ConsSqlQuery("ccpayments", ConsSqlQuery.INSERT);
		cc.getCard(sql);
		sql.addColumn("amount", SqlDouble.sql(-((Number)v.get("namount")).doubleValue()));
		sql.addColumn("description", SqlString.sql((String)v.get("description")));
		sql.addColumn("entityid", SqlInteger.sql(entityid));
		sql.addColumn("actypeid", SqlInteger.sql(actypeid));
		sql.addColumn("date", sqlDate.toSql(v.get("date")));		// Store day that it is in home timezone
//		sql.addColumn("date", sqlDate.toSql(new java.util.Date()));		// Store day that it is in home timezone
		con.str.execSql(sql.getSql());
	}
});
// ---------------------------------------------
}
// ---------------------------------------------


}
