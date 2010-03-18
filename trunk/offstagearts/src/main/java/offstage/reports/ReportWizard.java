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
package offstage.reports;
/*
 * NewRecordWizard.java
 *
 * Created on October 8, 2006, 11:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

import citibob.swing.*;
import citibob.wizard.*;
import java.sql.*;
import offstage.wizards.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.reports.Reports;
import java.awt.Component;
import offstage.reports.*;

/**
 *
 * @author citibob
 */
public class ReportWizard extends OffstageWizard {

	/*
addState(new State("", "", "") {
	public HtmlWiz newWiz(WizState.Context con)
		{ return new }
	public void process(citibob.sql.SqlRun str)
	{
		
	}
});
*/
	
public ReportWizard(offstage.FrontApp xfapp, Component component)
{
	super("Report Wizard", xfapp, component);
// ---------------------------------------------
addState(new AbstractWizState("ticketparams", null, "editquery") {
	public Wiz newWiz(Wizard.Context con) throws Exception
		{ return new TicketParamsWiz(frame, fapp); }
	public void process(Wizard.Context con) throws Exception
	{
		int groupid = v.getInt("groupid");
		String sql =
			" select p.entityid,p.firstname,p.lastname,p.city,p.state,p.zip," +
			" t.numberoftickets,t.payment,tt.name as tickettype\n" +
			" from persons p, ticketeventsales t, tickettypes tt\n" +
			" where p.entityid = t.entityid\n" +
			" and t.tickettypeid = tt.tickettypeid\n" +
			" and t.groupid = " + SqlInteger.sql(groupid) + "\n" +
			" order by p.lastname,p.firstname\n";
		con.str.execSql(sql, new RsTasklet2() {
		public void run(SqlRun str, ResultSet rs) throws Exception {
			Reports rr = fapp.reports();
			rr.writeCSV(
				rr.format(rr.toTableModel(rs)),
				frame, "Save Ticket Sales Repot");
			
		}});
	}
});
// ---------------------------------------------
addState(new AbstractWizState("savecsv") {
	public Wiz newWiz(Wizard.Context con) throws Exception
		{ return new JPanelWizWrapper(frame, null, null,
			  new ChooseFileWiz(app, ChooseFileWiz.M_WRITE,
			  "Please select file in which to save report",
			  con.v.getString("reportname"), ".csv")); }
	public void process(Wizard.Context con) throws Exception
	{
		if ("newquery".equals(con.v.get("submit"))) stateName = "newquery";
	}
});

// ---------------------------------------------
addState(new AbstractWizState("schoolaccounts", null, null) {
	public Wiz newWiz(Wizard.Context con) throws Exception
		{ return new SchoolAccountsWiz(frame, fapp); }
	public void process(Wizard.Context con) throws Exception
	{
	}
});

}


}
