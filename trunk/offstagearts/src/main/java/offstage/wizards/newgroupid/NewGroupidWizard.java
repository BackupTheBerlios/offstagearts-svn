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
package offstage.wizards.newgroupid;
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
import java.awt.Component;

/**
 *
 * @author citibob
 */
public class NewGroupidWizard extends OffstageWizard {

	/*
addState(new State("", "", "") {
	public HtmlWiz newWiz(WizState.Context con)
		{ return new }
	public void process(citibob.sql.SqlRunner str)
	{
		
	}
});
*/
	
public NewGroupidWizard(offstage.FrontApp xfapp, Component component)
{
	super("New Category", xfapp, component);
// ---------------------------------------------
//addState(new State("init", "init", "init") {
//	public HtmlWiz newWiz(WizState.Context con) throws Exception
//		{ return new InitWiz(frame); }
//	public void process(citibob.sql.SqlRunner str) throws Exception
//	{
//		String s = v.getString("type");
//		if (s != null) state = s;
//	}
//});
//// ---------------------------------------------
//addState(new State("person", "init", null) {
addStartState(new AbstractWizState("grouplist", null, "catname") {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new GroupListWiz(frame); }
	public void process(Wizard.Context con) throws Exception
	{
		String table = v.getString("submit");
		v.put("table", table);
		if ("donationids".equals(table)) stateName = "donationname";
		else stateName = "catname";
	}
});
// ---------------------------------------------
// Query for name of new category
addState(new AbstractWizState("catname", "grouplist", "finished") {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new CatNameWiz(frame, v.getString("table")); }
	public void process(Wizard.Context con) throws Exception
	{
		String catname = v.getString("catname");
		if (catname == null || "".equals(catname)) return;
		String table = v.getString("table");
		String sql =
			" insert into " + table +
			" (name) values (" + SqlString.sql(catname) + ")";
System.out.println(sql);
		con.str.execSql(sql);
		fapp.getDbChange().fireTableWillChange(con.str, table);
	}
});
// ---------------------------------------------
// Query for name of new donation category
addState(new AbstractWizState("donationname", "grouplist", "finished") {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new DonationNameWiz(frame); }
	public void process(Wizard.Context con) throws Exception
	{
		String catname = v.getString("catname");
		if (catname == null || "".equals(catname)) return;
		int fiscalyear = (int)v.getLong("fiscalyear");
		String sql =
			" insert into donationids" +
			" (name, fiscalyear) values (" +
			SqlString.sql(catname) + ", " + SqlInteger.sql(fiscalyear) + ")";
System.out.println(sql);
		con.str.execSql(sql);
		fapp.getDbChange().fireTableWillChange(con.str, "donationids");
	}
});
// ---------------------------------------------
// Query for name of new donation category
addState(new AbstractWizState("finished", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new FinishedWiz(frame); }
	public void process(Wizard.Context con) throws Exception
	{
	}
});
// ---------------------------------------------
}
// =========================================================================






}
