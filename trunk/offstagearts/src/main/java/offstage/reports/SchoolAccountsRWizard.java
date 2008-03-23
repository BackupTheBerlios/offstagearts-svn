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
///*
// * SchoolAccountsReport.java
// *
// * Created on December 2, 2007, 1:14 AM
// *
// * To change this template, choose Tools | Template Manager
// * and open the template in the editor.
// */
//
//package offstage.reports;
//
//import citibob.reports.*;
//import offstage.reports.*;
//import citibob.sql.pgsql.SqlInteger;
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
//import offstage.equery.*;
//import offstage.reports.*;
//import java.io.*;
//import offstage.gui.*;
//
///**
// *
// * @author citibob
// */
//public class SchoolAccountsRWizard extends ReportWizard
//{
//	
///** Creates a new instance of SchoolAccountsReport */
//public SchoolAccountsRWizard(offstage.FrontApp xfapp, javax.swing.JFrame xframe)
//{
//	super(xfapp, xframe, null);
//}
//
//public TypedHashMap runWizard(int termid) throws Exception
//{
//	TypedHashMap xv = new TypedHashMap();
//	xv.put("termid", new Integer(termid));
//	xv.put("reportname", "SchoolAccounts");		// Used for proper prefernces in file chooser
//	
//	// The list of states this Wizard will go through.
//	setNavigator(new LineNavigator(new String[]
//		{"savecsv"}
//	));
//	return super.runWizard("savecsv", xv);
//}
//
///** After the Wizard has been run, generates the report. */
//public static void doReport()
//{
//	
//}
//
//
//public static void main(String[] args) throws Exception
//{
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	offstage.FrontApp fapp = new offstage.FrontApp(pool,null);
//	
//	SchoolAccountsRWizard wizard = new SchoolAccountsRWizard(fapp, null);
//	TypedHashMap map = wizard.runWizard(346);
//	System.out.println("hoi");
//	System.exit(0);
//
////	SqlBatch str = new SqlBatch();
////	int termid = 346;
////	SchoolAccounts sa = new SchoolAccounts(fapp);
////	sa.findUnpaid(str, termid);
////	str.exec(pool);
//}
//
//}
