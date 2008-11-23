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
package offstage.frontdesk.wizards;

import citibob.jschema.SqlSchema;
import citibob.jschema.log.QueryLogRec;
import citibob.sql.ConsSqlQuery;
import citibob.sql.SqlRun;
import citibob.sql.UpdTasklet2;
import citibob.sql.pgsql.SqlBool;
import citibob.sql.pgsql.SqlInteger;
import citibob.sql.pgsql.SqlSerial;
import citibob.sql.pgsql.SqlString;
import citibob.swing.html.HtmlWiz;
import citibob.util.IntVal;
import citibob.wizard.AbstractWizState;
import citibob.wizard.Wizard;
import java.awt.Component;
import java.sql.SQLException;
import maestro.Maestro;
import offstage.accounts.gui.AccountsDB;
import offstage.accounts.gui.CashpaymentWiz;
import offstage.accounts.gui.CcpaymentWiz;
import offstage.accounts.gui.CheckpaymentWiz;
import offstage.wizards.OffstageWizard;

/*
 * NewRecordWizard.java
 *
 * Created on October 8, 2006, 11:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */


/**
 *
 * @author citibob
 */
public class BuyClassesWizard extends OffstageWizard {

int entityid, openclassActypeID;
double dollars, credits;
String selectedPackage;
int openclassAssetID;
static String descriptionInit = "Purchase Class Credit";

public BuyClassesWizard(offstage.FrontApp xfapp, Component comp,
Integer xentityid)
{
    super("Buy Classes", xfapp, comp);
	openclassAssetID = app.schemaSet().getEnumInt("actrans2amt", "assetid", "openclass");
	openclassActypeID = app.schemaSet().getEnumInt("actrans2", "cr_actypeid", "openclass");
	this.entityid = xentityid;

addStartState(new AbstractWizState("credits", null, "transtype") {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new PackageSelectionWiz(frame, con.str, fapp, v); }
	public void process(Wizard.Context con) throws Exception {
		Double Dollars = (Double)getVal("dollars");
		if (Dollars == null) stateName = "credits";
		dollars = Dollars;
		if (dollars < 0) stateName = "credits";
		credits = (Double)getVal("credits");
//		credits = Maestro.dollarsToCredits(dollars);
	}
       
});
addState(new AbstractWizState("transtype", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new OpenTransTypeWiz(frame, con.str, fapp, v); }
	public void process(Wizard.Context con) throws Exception
		{ stateName = v.getString("submit"); }
});
addState(new AbstractWizState("cashpayment", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new CashpaymentWiz(frame, fapp, descriptionInit, dollars, false); }
	public void process(Wizard.Context con) throws Exception
	{
        SqlSchema actrans2 = fapp.getSchema("actrans2");
		String sql = AccountsDB.w_actrans2_insert_sql(
			fapp, entityid, "received", openclassActypeID,
			"cash", actrans2.getCol("date").newDate(), con.v,
			new int[] {0}, new double[] {dollars}) +
        AccountsDB.w_actrans2_insert_sql(
			fapp, entityid, "billed", openclassActypeID,
			"cash", actrans2.getCol("date").newDate(), con.v,
			new int[] {0, openclassAssetID}, new double[] {-dollars, credits});
		con.str.execSql(sql);
	}
});

addState(new AbstractWizState("checkpayment", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new CheckpaymentWiz(frame, fapp, descriptionInit, dollars, false); }
	public void process(Wizard.Context con) throws Exception
        {
                SqlSchema actrans2 = fapp.getSchema("actrans2");
		String sql = AccountsDB.w_actrans2_insert_sql(
			fapp, entityid, "received", openclassActypeID,
			"check", actrans2.getCol("date").newDate(), con.v,
			new int[] {0}, new double[] {dollars}) +
                        AccountsDB.w_actrans2_insert_sql(
			fapp, entityid, "billed", openclassActypeID,
			"check", actrans2.getCol("date").newDate(), con.v,
			new int[] {0, openclassAssetID}, new double[] {-dollars, credits});
		con.str.execSql(sql);
        }
});
addState(new AbstractWizState("ccpayment", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new CcpaymentWiz(frame, con.str, entityid, fapp, descriptionInit, dollars, false); }
	public void process(Wizard.Context con) throws Exception
	{
                SqlSchema actrans2 = fapp.getSchema("actrans2");
		String sql = AccountsDB.w_actrans2_insert_sql(
			fapp, entityid, "received", openclassActypeID,
			"credit", actrans2.getCol("date").newDate(), con.v,
			new int[] {0}, new double[] {dollars}) +
                        AccountsDB.w_actrans2_insert_sql(
			fapp, entityid, "billed", openclassActypeID,
			"credit", actrans2.getCol("date").newDate(), con.v,
			new int[] {0, openclassAssetID}, new double[] {-dollars, credits});
		con.str.execSql(sql);
	}

});
// ---------------------------------------------

}

// ====================================================
private void addSCol(ConsSqlQuery q, String col)
{
	String val = v.getString(col);
	if (val != null) q.addColumn(col, SqlString.sql(val));
}
void createPerson(SqlRun str, final boolean isorg) throws SQLException
{
	// Make main record
	final IntVal iid = SqlSerial.getNextVal(str, "entities_entityid_seq");
	str.execUpdate(new UpdTasklet2() {
	public void run(SqlRun str) {
//		int id = (Integer)str.get("entities_entityid_seq");
		v.put("entityid", new Integer(iid.val));
		ConsSqlQuery q = new ConsSqlQuery("persons", ConsSqlQuery.INSERT);
		q.addColumn("entityid", SqlInteger.sql(iid.val));
		q.addColumn("primaryentityid", SqlInteger.sql(iid.val));
		addSCol(q, "lastname");
		addSCol(q, "middlename");
		addSCol(q, "firstname");
		addSCol(q, "address1");
		addSCol(q, "address2");
		addSCol(q, "city");
		addSCol(q, "state");
		addSCol(q, "zip");
		addSCol(q, "occupation");
		addSCol(q, "title");
		addSCol(q, "orgname");
		addSCol(q, "email");
		addSCol(q, "url");
		q.addColumn("isorg", SqlBool.sql(isorg));
		String sql = q.getSql();
	System.out.println(sql);
		str.execSql(sql);
		fapp.queryLogger().log(new QueryLogRec(q, fapp.schemaSet().get("persons")));

		// Make phone record --- first dig for keyed model...
		String phone = v.getString("phone");
		if (phone != null) {
			String phoneType = (isorg ? "work" : "home");
			q = new ConsSqlQuery("phones", ConsSqlQuery.INSERT);
			q.addColumn("entityid", SqlInteger.sql(iid.val));
			q.addColumn("groupid", "(select groupid from phoneids where name = " + SqlString.sql(phoneType) + ")");
			q.addColumn("phone", SqlString.sql(phone));
			sql = q.getSql();
	System.out.println(sql);
			str.execSql(sql);

			fapp.queryLogger().log(new QueryLogRec(q, fapp.schemaSet().get("phones")));
		}

		// Do interests
		Integer interestid = v.getInteger("interestid");
		if (interestid != null) {
			q = new ConsSqlQuery("interests", ConsSqlQuery.INSERT);
			q.addColumn("entityid", SqlInteger.sql(iid.val));
			q.addColumn("groupid", SqlInteger.sql(interestid));
			sql = q.getSql();
	System.out.println(sql);
			str.execSql(sql);
			fapp.queryLogger().log(new QueryLogRec(q, fapp.schemaSet().get("phones")));
		}
	}});
}

boolean notnull(String field)
{
	return (v.getString(field) != null);
}
/** Initial check on validity of info inputted. */
boolean isValid()
{
	return notnull("lastname");
}

/** Initial check on validity of info inputted. */
boolean isValidOrg()
{
	return notnull("orgname");
}



}
