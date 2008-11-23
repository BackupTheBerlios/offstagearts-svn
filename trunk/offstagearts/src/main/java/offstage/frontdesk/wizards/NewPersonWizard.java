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
//package offstage.frontdesk.wizards;
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
//import citibob.swing.html.*;
//import citibob.wizard.*;
//import javax.swing.*;
//import java.sql.*;
//import offstage.db.*;
//import offstage.wizards.*;
//import citibob.sql.*;
//import citibob.sql.pgsql.*;
//import citibob.jschema.log.*;
//import citibob.util.IntVal;
//import java.awt.Component;
//import offstage.wizards.newrecord.DupsWiz;
//
///**
// *
// * @author citibob
// */
//public class NewPersonWizard extends OffstageWizard {
//
//	/*
//addState(new State("", "", "") {
//	public HtmlWiz newWiz(WizState.Context con)
//		{ return new }
//	public void process(citibob.sql.SqlRun str)
//	{
//		
//	}
//});
//*/
//
//public NewPersonWizard(offstage.FrontApp xfapp, Component comp)
//{
//	super("New Record", xfapp, comp);
//// ---------------------------------------------
////addState(new State("init", "init", "init") {
////	public HtmlWiz newWiz(WizState.Context con) throws Exception
////		{ return new InitWiz(frame); }
////	public void process(citibob.sql.SqlRun str) throws Exception
////	{
////		String s = v.getString("type");
////		if (s != null) state = s;
////	}
////});
////// ---------------------------------------------
////addState(new State("person", "init", null) {
//addState(new AbstractWizState("person", null, null) {
//	public HtmlWiz newWiz(Wizard.Context con) throws Exception
//		{ return new PersonWiz(frame, con.str, fapp); }
//	public void process(Wizard.Context con) throws Exception
//	{
//		if (stateName == null) {
//			// First: do a simple check of data entry
//			if (!isValid()) {
//				JOptionPane.showMessageDialog((JDialog)wiz,
//					"Invalid input.\nPlease fill in all required (starred) fields!");
//				stateName = "person";
//			} else {
//				final DupCheck dc = new DupCheck(con.str, v);
//				con.str.execUpdate(new UpdTasklet2() {
////				offstage.db.DupCheck.checkDups(con.str, v, 3, 20, new UpdTasklet2() {
//				public void run(SqlRun str) {
//					String idSql = dc.getIDSql(3, 20);
//					v.put("idsql", idSql);
//					System.out.println("DupCheck sql: " + idSql);
//					final IntVal ndups = DB.countIDList("count", str, idSql);
//					str.execUpdate(new UpdTasklet2() {
//					public void run(SqlRun str) throws Exception {
////						int ndups = (Integer)str.get("count");
//						if (ndups.val == 0) {
//							createPerson(str, false);
//							stateName = "finished";
//						} else {
//							stateName = "checkdups";
//						}
//					}});
//					//state = (ndups == 0 ? "finished" : "checkdups");
//				}});
//			}
//		}
//	}
//});
//// ---------------------------------------------
//// Duplicates were found; double-check.
//addState(new AbstractWizState("checkdups", null, null) {
//	public HtmlWiz newWiz(Wizard.Context con) throws Exception
//		{ return new DupsWiz(frame, con.str, fapp, con.v.getString("idsql")); }
//	public void process(Wizard.Context con) throws Exception
//	{
//		String submit = v.getString("submit");
//		if ("dontadd".equals(submit)) stateName = null;
//		if ("addanyway".equals(submit)) {
//			createPerson(con.str, false);
//			stateName = "finished";
//System.out.println("Add anyway!");
//		}
//	}
//});
//// ---------------------------------------------
//addState(new AbstractWizState("org", null, null) {
//	public HtmlWiz newWiz(Wizard.Context con) throws Exception
//		{ return new OrgWiz(frame, con.str, fapp); }
//	public void process(Wizard.Context con) throws Exception
//	{
//		if (stateName == null) {
//			// First: do a simple check of data entry
//			if (!isValidOrg()) {
//				JOptionPane.showMessageDialog((JDialog)wiz,
//					"Invalid input.\nPlease fill in all required (starred) fields!");
//				stateName = "org";
//			} else {
//				final DupCheck dc = new DupCheck(con.str, con.v);
////				offstage.db.DupCheck.checkDups(con.str, con.v, 3, 20, new UpdTasklet2() {
//				con.str.execUpdate(new UpdTasklet2() {
//				public void run(SqlRun str) {
////					String idSql = (String)str.get("idsql");
//					String idSql = dc.getIDSql(3, 20);
//					v.put("idsql", idSql);
//					System.out.println("DupCheck sql: " + idSql);
//					final IntVal ndups = DB.countIDList("ndups", str, idSql);
//					str.execUpdate(new UpdTasklet2() {
//					public void run(SqlRun str) throws SQLException {
////						int ndups = (Integer)str.get("ndups");
//						if (ndups.val == 0) {
//							createPerson(str, true);
//							stateName = null;// "finished";
//						} else {
//							stateName = "checkdups";
//						}
//					}});
//				//state = (ndups == 0 ? "finished" : "checkdups");
//				}});
//			}
//		}
//	}
//});
//// ---------------------------------------------
//// Duplicates were found; double-check.
//addState(new AbstractWizState("finished", null, null) {
//	public HtmlWiz newWiz(Wizard.Context con) throws Exception
//		{ return new FinishedWiz(frame); }
//	public void process(Wizard.Context con) throws Exception
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
//void createPerson(SqlRun str, final boolean isorg) throws SQLException
//{
//	// Make main record
//	final IntVal iid = SqlSerial.getNextVal(str, "entities_entityid_seq");
//	str.execUpdate(new UpdTasklet2() {
//	public void run(SqlRun str) {
////		int id = (Integer)str.get("entities_entityid_seq");
//		v.put("entityid", new Integer(iid.val));
//		ConsSqlQuery q = new ConsSqlQuery("persons", ConsSqlQuery.INSERT);
//		q.addColumn("entityid", SqlInteger.sql(iid.val));
//		q.addColumn("primaryentityid", SqlInteger.sql(iid.val));
//		addSCol(q, "lastname");
//		addSCol(q, "middlename");
//		addSCol(q, "firstname");
//		addSCol(q, "address1");
//		addSCol(q, "address2");
//		addSCol(q, "city");
//		addSCol(q, "state");
//		addSCol(q, "zip");
//		addSCol(q, "occupation");
//		addSCol(q, "title");
//		addSCol(q, "orgname");
//		addSCol(q, "email");
//		addSCol(q, "url");
//		q.addColumn("isorg", SqlBool.sql(isorg));
//		String sql = q.getSql();
//	System.out.println(sql);
//		str.execSql(sql);
//		fapp.queryLogger().log(new QueryLogRec(q, fapp.schemaSet().get("persons")));
//
//		// Make phone record --- first dig for keyed model...
//		String phone = v.getString("phone");
//		if (phone != null) {
//			String phoneType = (isorg ? "work" : "home");
//			q = new ConsSqlQuery("phones", ConsSqlQuery.INSERT);
//			q.addColumn("entityid", SqlInteger.sql(iid.val));
//			q.addColumn("groupid", "(select groupid from phoneids where name = " + SqlString.sql(phoneType) + ")");
//			q.addColumn("phone", SqlString.sql(phone));
//			sql = q.getSql();
//	System.out.println(sql);
//			str.execSql(sql);
//
//			fapp.queryLogger().log(new QueryLogRec(q, fapp.schemaSet().get("phones")));
//		}
//
//		// Do interests
//		Integer interestid = v.getInteger("interestid");
//		if (interestid != null) {
//			q = new ConsSqlQuery("interests", ConsSqlQuery.INSERT);
//			q.addColumn("entityid", SqlInteger.sql(iid.val));
//			q.addColumn("groupid", SqlInteger.sql(interestid));
//			sql = q.getSql();
//	System.out.println(sql);
//			str.execSql(sql);
//			fapp.queryLogger().log(new QueryLogRec(q, fapp.schemaSet().get("phones")));
//		}
//	}});
//}
//
//boolean notnull(String field)
//{
//	return (v.getString(field) != null);
//}
///** Initial check on validity of info inputted. */
//boolean isValid()
//{
//	return notnull("lastname");
//}
//
///** Initial check on validity of info inputted. */
//boolean isValidOrg()
//{
//	return notnull("orgname");
//}
//
//
//
//}
