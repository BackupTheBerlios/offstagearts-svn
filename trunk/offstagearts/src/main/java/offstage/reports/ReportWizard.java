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

import citibob.reports.*;
import offstage.reports.*;
import citibob.sql.pgsql.SqlInteger;
import citibob.swing.*;
import citibob.wizard.*;
import javax.swing.*;
import java.sql.*;
import offstage.db.*;
import offstage.wizards.*;
import offstage.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import offstage.equery.*;
import offstage.reports.*;
import java.io.*;
import offstage.gui.*;

/**
 *
 * @author citibob
 */
public class ReportWizard extends OffstageWizard {

	/*
addState(new State("", "", "") {
	public HtmlWiz newWiz(WizState.Context con)
		{ return new }
	public void process(citibob.sql.SqlRunner str)
	{
		
	}
});
*/
	
public ReportWizard(offstage.FrontApp xfapp, javax.swing.JFrame xframe, String startState)
{
	super("Report Wizard", xfapp, xframe, startState);
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
		con.str.execSql(sql, new RsRunnable() {
		public void run(SqlRunner str, ResultSet rs) throws Exception {
			Reports rr = fapp.getReports();
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
//addState(new State("listquery", null, "editquery") {
//	public Wiz newWiz(WizState.Context con) throws Exception
//		{ return new JPanelWizWrapper(frame, null, "",
//			  new ListQueryWiz(st, fapp)); }
//	public void process(citibob.sql.SqlRunner str) throws Exception
//	{
//		if ("newquery".equals(v.get("submit"))) state = "newquery";
//	}
//});
//// ---------------------------------------------
//addState(new State("newquery", null, "editquery") {
//	public Wiz newWiz(WizState.Context con) throws Exception
//	{
//		NewQueryWiz w = new NewQueryWiz(frame);
//		return w;
//	}
//	public void process(citibob.sql.SqlRunner str) throws Exception
//	{
//		int equeryID = DB.r_nextval(st, "equeries_equeryid_seq");
//		String sql = "insert into equeries (equeryid, name, equery, lastmodified) values (" +
//			SqlInteger.sql(equeryID) + ", " +
//			SqlString.sql(v.getString("queryname")) +
//			", '', now())";
//		st.executeUpdate(sql);
//		v.put("equeryid", equeryID);
////		System.out.println(sql);
//	}
//});
//// ---------------------------------------------
//addState(new State("editquery", "listquery", "reporttype") {
//	public Wiz newWiz(WizState.Context con) throws Exception {
//		EditQueryWiz eqw = new EditQueryWiz(st, fapp, v.getInt("equeryid"));
//		return new JPanelWizWrapper(frame, "", "", eqw);
//	}
//	public void process(citibob.sql.SqlRunner str) throws Exception
//	{
//		if ("deletequery".equals(v.get("submit"))) {
////			equeryDm.doDelete(st);
//			st.executeUpdate("delete from equeries where equeryid = " + SqlInteger.sql(v.getInt("equeryid")));
//			state = stateRec.back;
//		}
//
//	}
//});
//// ---------------------------------------------
//addState(new State("reporttype", "editquery", null) {
//	public Wiz newWiz(WizState.Context con) throws Exception
//		{ return new ReportTypeWiz(frame); }
//	public void process(citibob.sql.SqlRunner str) throws Exception
//	{
////		citibob.swing.SwingUtil.setCursor(frame, java.awt.Cursor.WAIT_CURSOR);
//		String submit = v.getString("submit");
//		EQuery equery = (EQuery)v.get("equery");
//		String equeryName = v.getString("equeryname");
//		if ("mailinglabels".equals(submit)) {
//			int mailingID = equery.makeMailing(st, equeryName, fapp.getEquerySchema());
//			fapp.getMailingModel().setKey(mailingID);
//			fapp.getMailingModel().doSelect(st);
//			fapp.setScreen(FrontApp.MAILINGS_SCREEN);
//			state = stateRec.next;
//		} else if ("peopletab".equals(submit)) {
//			EntityListTableModel res = fapp.getSimpleSearchResults();
//			String sql = equery.getSql(fapp.getEquerySchema());
//			res.setRows(st, sql, null);
//			fapp.setScreen(FrontApp.PEOPLE_SCREEN);
//			state = stateRec.next;
//		} else if ("donationreport".equals(submit)) {
//			String sql = equery.getSql(fapp.getEquerySchema());
//			state = (doDonationReport("Donation Report", sql) ? stateRec.next : stateRec.name);
//		} else if ("donationreport_nodup".equals(submit)) {
//			String sql = equery.getSql(fapp.getEquerySchema());
//			sql = DB.removeDupsIDSql(sql);
//			state = (doDonationReport("Donation Report (One per Household)", sql) ? stateRec.next : stateRec.name);
//		}
//		
////		// Go on no matter what we chose...
////		if (!"back".equals(submit)) state = stateRec.next;
////		citibob.swing.SwingUtil.setCursor(frame, java.awt.Cursor.DEFAULT_CURSOR);
//	}
//});
//// ---------------------------------------------
//
//
//
//
//
//
}
//// ==================================================================
//public boolean doDonationReport(String title, String sql) throws Exception
//{
//	DonationReport report = new DonationReport(fapp, sql);
//	report.doSelect(st);
//	OffstageGuiUtil.saveCSVReport(report.newTableModel(), "Save" + title,
//		fapp, frame);
//	return true;
//}
////		
////	DonationReport report = new DonationReport(fapp, sql);
////	report.doSelect(st);	
////	String dir = fapp.userRoot().get("saveReportDir", null);
////	JFileChooser chooser = new JFileChooser(dir);
////	chooser.setDialogTitle("Save " + title);
////	chooser.addChoosableFileFilter(
////		new javax.swing.filechooser.FileFilter() {
////		public boolean accept(File file) {
////			String filename = file.getName();
////			return filename.endsWith(".csv");
////		}
////		public String getDescription() {
////			return "*.csv";
////		}
////	});
////	String path = null;
////	String fname = null;
////	for (;;) {
////		chooser.showSaveDialog(frame);
////
////		path = chooser.getCurrentDirectory().getAbsolutePath();
////		if (chooser.getSelectedFile() == null) return false;
////		fname = chooser.getSelectedFile().getPath();
////		if (!fname.endsWith(".csv")) fname = fname + ".csv";
////		File f = new File(fname);
////		if (!f.exists()) break;
////		if (JOptionPane.showConfirmDialog(
////			frame, "The file " + f.getName() + " already exists.\nWould you like to ovewrite it?",
////			"Overwrite File?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) break;
////	}
////	fapp.userRoot().put("saveReportDir", path);
////
////	CSVReportOutput csv = new CSVReportOutput(report.newTableModel(), null, null,
////		fapp.getSFormatMap());
////	csv.writeReport(new File(fname));
////	return true;
////}
//// ==================================================================


}
