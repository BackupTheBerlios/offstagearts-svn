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
//package offstage.wizard.editcourses;
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
//import citibob.swing.*;
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
//public class EditCoursesWizard extends OffstageWizard {
//
//	/*
//addState(new State("", "", "") {
//	public HtmlWiz newWiz(WizState.Context con)
//		{ return new }
//	public void process(citibob.sql.SqlRunner str)
//	{
//		
//	}
//});
//*/
//	
//public EditCoursesWizard(offstage.FrontApp xfapp, java.awt.Frame xframe)
//{
//	super("Edit Courses", xfapp, xframe, "termlist");
//// ---------------------------------------------
//addState(new AbstractWizState("termlist", null, "courselist") {
//	public Wiz newWiz(Wizard.Context con) throws Exception
//		{ return new JPanelWizWrapper(frame, null, ">> Courses",
//			  new TermsWiz(fapp, con.str)); }
//	public void process(Wizard.Context con) throws Exception
//	{
//		Integer Termid = (Integer)v.get("termid");
//		if (Termid == null) stateName = "termlist";
//	}
//});
//// ---------------------------------------------
//addState(new AbstractWizState("courselist", "termlist", "meetings") {
//	public Wiz newWiz(Wizard.Context con) throws Exception
//	{
//		Integer Termid = (Integer)v.get("termid");
//		return new JPanelWizWrapper(frame, "", ">> Meetings",
//			  new CoursesWiz(fapp, con.str, Termid));
//	}
//	public void process(Wizard.Context con) throws Exception
//	{
//		Integer Termid = (Integer)v.get("courseid");
//		if (Termid == null) stateName = "courselist";
//	}
//});
//// ---------------------------------------------
//addState(new AbstractWizState("meetings", "courselist", null) {
//	public Wiz newWiz(Wizard.Context con) throws Exception
//	{
//		Integer Termid = (Integer)v.get("termid");
//		Integer Courseid = (Integer)v.get("courseid");
//		return new JPanelWizWrapper(frame, "", "Finished",
//			  new MeetingsWiz(fapp, con.str, Termid, Courseid));
//	}
//	public void process(Wizard.Context con) throws Exception
//		{}
//});
//// ---------------------------------------------
////// Query for name of new category
////addState(new State("catname", "grouplist", "finished") {
////	public HtmlWiz newWiz(WizState.Context con) throws Exception
////		{ return new CatNameWiz(frame, v.getString("table")); }
////	public void process(citibob.sql.SqlRunner str) throws Exception
////	{
////		String catname = v.getString("catname");
////		if (catname == null || "".equals(catname)) return;
////		String table = v.getString("table");
////		String sql =
////			" insert into " + table +
////			" (name) values (" + SqlString.sql(catname) + ")";
////System.out.println(sql);
////		st.executeUpdate(sql);
////		fapp.getDbChange().fireTableChanged(st, table);
////	}
////});
////// ---------------------------------------------
////// Query for name of new donation category
////addState(new State("donationname", "grouplist", "finished") {
////	public HtmlWiz newWiz(WizState.Context con) throws Exception
////		{ return new DonationNameWiz(frame); }
////	public void process(citibob.sql.SqlRunner str) throws Exception
////	{
////		String catname = v.getString("catname");
////		if (catname == null || "".equals(catname)) return;
////		int fiscalyear = (int)v.getLong("fiscalyear");
////		String sql =
////			" insert into donationids" +
////			" (name, fiscalyear) values (" +
////			SqlString.sql(catname) + ", " + SqlInteger.sql(fiscalyear) + ")";
////System.out.println(sql);
////		st.executeUpdate(sql);
////		fapp.getDbChange().fireTableChanged(st, "donationids");
////	}
////});
////// ---------------------------------------------
////// Query for name of new donation category
////addState(new State("finished", null, null) {
////	public HtmlWiz newWiz(WizState.Context con) throws Exception
////		{ return new FinishedWiz(frame); }
////	public void process(citibob.sql.SqlRunner str) throws Exception
////	{
////	}
////});
//// ---------------------------------------------
//}
//// =========================================================================
//
//
//
//
//
//}
