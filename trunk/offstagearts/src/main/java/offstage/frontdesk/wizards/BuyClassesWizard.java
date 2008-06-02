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
/*
 * NewRecordWizard.java
 *
 * Created on October 8, 2006, 11:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

import citibob.jschema.SqlSchema;
import citibob.swing.html.*;
import citibob.wizard.*;
import java.sql.*;
import offstage.wizards.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.log.*;
import citibob.util.IntVal;
import java.awt.Component;
import offstage.accounts.gui.AccountsDB;
import offstage.accounts.gui.CashpaymentWiz;


/**
 *
 * @author citibob
 */
public class BuyClassesWizard extends OffstageWizard {

int entityid, actypeid;
String selectedPackage;
double courseAmount;
double dollarAmount;
int courseType;

public BuyClassesWizard(offstage.FrontApp xfapp, Component comp,
Integer xentityid, int xactypeid)
{
        super("Buy Classes", xfapp, comp);
	this.entityid = xentityid;
	this.actypeid = xactypeid;
        
addStartState(new AbstractWizState("person", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new PackageSelectionWiz(frame, con.str, fapp, v); }
	public void process(Wizard.Context con) throws Exception {   
                        selectedPackage = v.getString("submit");
                        if (selectedPackage.equals("onepack")) {
                            courseAmount = 1; //what is correct courseAmount?
                            dollarAmount = 17; //what is correct dollar amount?
                            courseType = fapp.schemaSet().getEnumInt("actrans2amt", "assetid", "openclass");
                        } else if (selectedPackage.equals("tenpack")) {
                            courseAmount = 10; //what is correct courseAmount?
                            dollarAmount = 150; //what is correct dollar amount?
                            courseType = fapp.schemaSet().getEnumInt("actrans2amt", "assetid", "openclass");
                        } else if (selectedPackage.equals("twentypack")) {
                            courseAmount = 20; //what is correct courseAmount?
                            dollarAmount = 300; //what is correct dollar amount?
                            courseType = fapp.schemaSet().getEnumInt("actrans2amt", "assetid", "openclass");
                        } else if (selectedPackage.equals("thirtypack")) {
                            courseAmount = 30; //what is correct courseAmount?
                            dollarAmount = 450; //what is correct dollar amount?
                            courseType = fapp.schemaSet().getEnumInt("actrans2amt", "assetid", "openclass");
                        } else { // circus pack
                            courseAmount = 1; //what is correct courseAmount?
                            dollarAmount = 500; //what is correct dollar amount?
                            courseType = fapp.schemaSet().getEnumInt("actrans2amt", "assetid", "circusclass");
                        }
                        stateName = "transtype";
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
		{ return new CashpaymentWiz(frame, fapp); }
	public void process(Wizard.Context con) throws Exception
	{
                SqlSchema actrans2 = fapp.getSchema("actrans2");
		String sql = AccountsDB.w_actrans2_insert_sql(
			fapp, entityid, "received", actypeid,
			"cash", actrans2.getCol("date").newDate(), con.v,
			new int[] {0}, new double[] {dollarAmount}) +
                        AccountsDB.w_actrans2_insert_sql(
			fapp, entityid, "billed", actypeid,
			"openclass", actrans2.getCol("date").newDate(), con.v,
			new int[] {0, courseType}, new double[] {-dollarAmount, courseAmount});
		con.str.execSql(sql);
	}
});
addState(new AbstractWizState("checkpayment", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new CheckpaymentWiz(frame, fapp); }
	public void process(Wizard.Context con) throws Exception
        {
                SqlSchema actrans2 = fapp.getSchema("actrans2");
		String sql = AccountsDB.w_actrans2_insert_sql(
			fapp, entityid, "received", actypeid,
			"check", actrans2.getCol("date").newDate(), con.v,
			new int[] {0}, new double[] {dollarAmount}) +
                        AccountsDB.w_actrans2_insert_sql(
			fapp, entityid, "billed", actypeid,
			"openclass", actrans2.getCol("date").newDate(), con.v,
			new int[] {0, courseType}, new double[] {-dollarAmount, courseAmount});
		con.str.execSql(sql);
        }
});
addState(new AbstractWizState("ccpayment", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new CcpaymentWiz(frame, con.str, entityid, fapp); }
	public void process(Wizard.Context con) throws Exception
	{
                SqlSchema actrans2 = fapp.getSchema("actrans2");
		String sql = AccountsDB.w_actrans2_insert_sql(
			fapp, entityid, "received", actypeid,
			"credit", actrans2.getCol("date").newDate(), con.v,
			new int[] {0}, new double[] {dollarAmount}) +
                        AccountsDB.w_actrans2_insert_sql(
			fapp, entityid, "billed", actypeid,
			"openclass", actrans2.getCol("date").newDate(), con.v,
			new int[] {0, courseType}, new double[] {-dollarAmount, courseAmount});
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
