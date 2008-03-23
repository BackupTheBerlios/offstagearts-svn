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
//package offstage.wizards.newrecord;
///*
// * NewRecordWizard.java
// *
// * Created on October 8, 2006, 11:27 PM
// *
// * To change this template, choose Tools | Options and locate the template under
// * the Source Creation and Management node. Right-click the template and choose
// * Open. You can then make changes to the template in the Source Editor.
// */
//
//import citibob.sql.pgsql.SqlInteger;
//import citibob.swing.html.*;
//import citibob.wizard.*;
//import javax.swing.*;
//import java.sql.*;
//import offstage.db.*;
//import offstage.wizards.*;
//import offstage.*;
//import citibob.sql.*;
//import citibob.sql.pgsql.*;
//import citibob.jschema.*;
//
///**
// *
// * @author citibob
// */
//public class NewRecordWizard extends OffstageWizard {
//
//	Statement st;		// Datbase connection
//	/*
//addState(new State("", "", "") {
//	public HtmlWiz newWiz(citibob.sql.SqlRun str)
//		{ return new }
//	public void process(citibob.sql.SqlRun str)
//	{
//		
//	}
//});
//*/
//	
//public NewRecordWizard(offstage.FrontApp xfapp, java.awt.Frame xframe)
//{
//	super("New Record Wizard", xfapp, xframe, "init");
//	this.st = xst;
//// ---------------------------------------------
//addState(new State("init", "init", "init") {
//	public HtmlWiz newWiz(citibob.sql.SqlRun str) throws Exception
//		{ return new InitWiz(frame); }
//	public void process(citibob.sql.SqlRun str) throws Exception
//	{
//		String s = v.getString("type");
//		if (s != null) state = s;
//	}
//});
//// ---------------------------------------------
//addState(new State("person", "init", null) {
//	public HtmlWiz newWiz(citibob.sql.SqlRun str) throws Exception
//		{ return new PersonWiz(frame, st, fapp); }
//	public void process(citibob.sql.SqlRun str) throws Exception
//	{
//		if (state == null) {
//			String idSql = offstage.db.DupCheck.checkDups(st, v, 3, 20);
//			v.put("idsql", idSql);
//			System.out.println("DupCheck sql: " + idSql);
//			int ndups = DB.countIDList(st, idSql);
//			if (ndups == 0) {
//				createPerson();
//				state = "finished";
//			} else {
//				state = "checkperson";
//			}
//			//state = (ndups == 0 ? "finished" : "checkperson");
//		}
//	}
//});
//// ---------------------------------------------
//// Duplicates were found; double-check.
//addState(new State("checkperson", "person", null) {
//	public HtmlWiz newWiz(citibob.sql.SqlRun str) throws Exception
//		{ return new DupsWiz(frame, st, fapp, v.getString("idsql")); }
//	public void process(citibob.sql.SqlRun str) throws Exception
//	{
//		String submit = v.getString("submit");
//		if ("dontadd".equals(submit)) state = null;
//		if ("addanyway".equals(submit)) {
//			createPerson();
//			state = "finished";
//System.out.println("Add anyway!");
//		}
//	}
//});
//// ---------------------------------------------
//// Duplicates were found; double-check.
//addState(new State("finished", null, null) {
//	public HtmlWiz newWiz(citibob.sql.SqlRun str) throws Exception
//		{ return new FinishedWiz(frame); }
//	public void process(citibob.sql.SqlRun str) throws Exception
//		{}
//});
//// ---------------------------------------------
//
//}
//
//// ====================================================
//private void addSCol(ConsSqlQuery q, String col)
//{
//	String val = v.getString(col);
//	if (val != null) q.addColumn(col, SqlString.sql(val));
//}
//void createPerson() throws SQLException
//{
//	// Make main record
//	int id = DB.r_nextval(st, "entities_entityid_seq");
//	ConsSqlQuery q = new ConsSqlQuery("persons", ConsSqlQuery.INSERT);
//	q.addColumn("entityid", SqlInteger.sql(id));
//	addSCol(q, "lastname");
//	addSCol(q, "middlename");
//	addSCol(q, "firstname");
//	addSCol(q, "address1");
//	addSCol(q, "address2");
//	addSCol(q, "city");
//	addSCol(q, "state");
//	addSCol(q, "zip");
//	addSCol(q, "email");
//	String sql = q.getSql();
//System.out.println(sql);
//	st.execute(sql);
//	
//	// Make phone record --- first dig for keyed model...
//	String phone = v.getString("phone");
//	if (phone != null) {
//		q = new ConsSqlQuery("phones", ConsSqlQuery.INSERT);
//		q.addColumn("entityid", SqlInteger.sql(id));
//		q.addColumn("groupid", "(select groupid from phoneids where name = 'home')");
//		q.addColumn("phone", SqlString.sql(phone));
//		sql = q.getSql();
//System.out.println(sql);
//		st.execute(sql);
//	}
////	Schema phones = fapp.getSchemaSet().phones;
////	Column col = phones.getCol(phones.findCol("groupid"));
////	SqlEnum type = (SqlEnum)col.getType();
////	KeyedModel kmodel = type.getKeyedModel();
////	kmodel.
//}
//
//public static void main(String[] args) throws Exception
//{
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	Statement st = pool.checkout().createStatement();
//	FrontApp fapp = new FrontApp(pool,null);
//	Wizard wizard = new NewRecordWizard(fapp, st, null);
//	wizard.runWizard();
//}
//
//}
