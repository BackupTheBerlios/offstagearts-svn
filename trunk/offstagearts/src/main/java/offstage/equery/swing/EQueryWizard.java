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
package offstage.equery.swing;
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
import citibob.util.IntVal;
import offstage.equery.*;
import offstage.reports.*;
import java.io.*;
import offstage.gui.*;

/**
 *
 * @author citibob
 */
public class EQueryWizard extends OffstageWizard {

	/*
addState(new State("", "", "") {
	public HtmlWiz newWiz(WizState.Context con)
		{ return new }
	public void process(citibob.sql.SqlRun str)
	{
		
	}
});
*/
	
public EQueryWizard(offstage.FrontApp xfapp, java.awt.Component comp)
{
	super("Query Wizard", xfapp, comp);
// ---------------------------------------------
addState(new AbstractWizState("listquery", null, "editquery") {
	public Wiz newWiz(Wizard.Context con) throws Exception
		{ return new JPanelWizWrapper(frame, "", "",
			  new ListQueryWiz(con.str, fapp)); }
	public void process(Wizard.Context con) throws Exception
	{
		if ("newquery".equals(con.v.get("submit"))) stateName = "newquery";
	}
});
// ---------------------------------------------
addState(new AbstractWizState("newquery", null, "editquery") {
	public Wiz newWiz(Wizard.Context con) throws Exception
	{
		NewQueryWiz w = new NewQueryWiz(frame);
		return w;
	}
	public void process(final Wizard.Context con) throws Exception
	{
		final IntVal equeryID = SqlSerial.getNextVal(con.str, "equeries_equeryid_seq");
		con.str.execUpdate(new UpdTasklet2() {
		public void run(SqlRun str) {
			con.v.put("equeryid", equeryID);
			String sql = "insert into equeries (equeryid, name, equery, lastmodified) values (" +
				SqlInteger.sql(equeryID.val) + ", " +
				SqlString.sql(con.v.getString("queryname")) +
				", '', now())";
			con.str.execSql(sql);
		}});
	}
});
// ---------------------------------------------
addState(new AbstractWizState("editquery", "listquery", "reporttype") {
//addState(new AbstractWizState("editquery", null, null) {
	public Wiz newWiz(Wizard.Context con) throws Exception {
		EditQueryWiz eqw = new EditQueryWiz(con.str, fapp, con.v.getInt("equeryid"));
		return new JPanelWizWrapper(frame, "", "", eqw);
	}
	public void process(Wizard.Context con) throws Exception
	{
		if ("deletequery".equals(con.v.get("submit"))) {
//			equeryDm.doDelete(st);
			con.str.execSql("delete from equeries where equeryid = " + SqlInteger.sql(con.v.getInt("equeryid")));
			stateName = stateRec.getBack();
		}

	}
});
// ---------------------------------------------
//addState(new AbstractWizState("reporttype", "editquery", null) {
//	public Wiz newWiz(Wizard.Context con) throws Exception
//		{ return new ReportTypeWiz(frame); }
//	public void process(final Wizard.Context con) throws Exception
//	{
////		citibob.swing.SwingUtil.setCursor(frame, java.awt.Cursor.WAIT_CURSOR);
//		String submit = con.v.getString("submit");
//		EQuery equery = (EQuery)con.v.get("equery");
//		String equeryName = con.v.getString("equeryname");
//		if ("mailinglabels".equals(submit)) {
//			equery.makeMailing(con.str, equeryName, fapp.equerySchema(), null);
//			con.str.execUpdate(new UpdTasklet2() {
//			public void run(SqlRun str) {
//				final int mailingID = (Integer)con.str.get("groupids_groupid_seq");
//				fapp.getMailingModel().setKey(mailingID);
//				fapp.getMailingModel().doSelect(con.str);
//				fapp.setScreen(FrontApp.MAILINGS_SCREEN);
//			}});
//			stateName = stateRec.getNext();
//		} else if ("peopletab".equals(submit)) {
//			EntityListTableModel res = fapp.getSimpleSearchResults();
//			String sql = equery.getSql(fapp.equerySchema(), false);
//System.out.println("EQueryWizard sql: " + sql);
//			res.setRows(con.str, sql, null);
//			fapp.setScreen(FrontApp.PEOPLE_SCREEN);
//			stateName = stateRec.getNext();
//		} else if ("donationreport".equals(submit)) {
//			String idSql = equery.getSql(fapp.equerySchema(), false);
//			DonationReport.writeCSV(fapp, con.str, frame, "Donation Report", idSql);
//			stateName = stateRec.getNext();
////			stateName = (doDonationReport(con.str, "Donation Report", sql) ? stateRec.getNext() : stateRec.getName());
//		} else if ("donationreport_nodup".equals(submit)) {
//			String idSql = equery.getSql(fapp.equerySchema(), true);
////			sql = DB.removeDupsIDSql(sql);
//			DonationReport.writeCSV(fapp, con.str, frame, "Donation Report", idSql);
//			stateName = stateRec.getNext();			
////			stateName = (doDonationReport(con.str, "Donation Report (One per Household)", sql) ? stateRec.getNext() : stateRec.getName());
////		} else if ("segmentation".equals(submit)) {
////			String idSql = equery.getSql(fapp.equerySchema(), false);
////			SegmentationReport.writeCSV(fapp, con.str, frame, "Segmentation Report", idSql);
////			stateName = stateRec.getNext();			
//////			stateName = (doSpreadsheetReport(con.str, "Donation Report", sql) ? stateRec.getNext() : stateRec.getName());
//		}
//		
////		// Go on no matter what we chose...
////		if (!"back".equals(submit)) state = stateRec.getNext();
////		citibob.swing.SwingUtil.setCursor(frame, java.awt.Cursor.DEFAULT_CURSOR);
//	}
//});
// ---------------------------------------------
// ---------------------------------------------
addState(new AbstractWizState("savecsv") {
	public Wiz newWiz(Wizard.Context con) throws Exception
		{ return new JPanelWizWrapper(frame, "", null,
			  new ChooseFileWiz(app, ChooseFileWiz.M_WRITE, "Please select file in which to save report", "savecsv", ".csv")); }
//			  new ChooseFileWiz(app, "Please select file in which to save report", con.v.getString("reportname"), ".csv")); }
	public void process(Wizard.Context con) throws Exception
	{
		if ("newquery".equals(con.v.get("submit"))) stateName = "newquery";
	}
});

addState(new AbstractWizState("choosetemplate") {
	public Wiz newWiz(Wizard.Context con) throws Exception
		{ return new JPanelWizWrapper(frame, "",null,
			  // Puts chosen filename in v.get("file")
			  new ChooseFileWiz(app, ChooseFileWiz.M_READ, "Please select mail merge template for report",
				"choosetemplate", con.v.getString("extension"))); }
//			  new ChooseFileWiz(app, "Please select file in which to save report", con.v.getString("reportname"), ".csv")); }
	public void process(Wizard.Context con) throws Exception
	{}
});

addState(new AbstractWizState("segreport") {
	public Wiz newWiz(Wizard.Context con) throws Exception
		{ return new SegReportWiz(frame, app); }
	public void process(Wizard.Context con) throws Exception
	{}
});
addState(new AbstractWizState("donationyears") {
	public Wiz newWiz(Wizard.Context con) throws Exception
		{ return new DonationYearsWiz(frame, app); }
	public void process(Wizard.Context con) throws Exception
	{}
});





}
// ==================================================================
// Different ways through this Wizard for different reports
public boolean runMailMerge() throws Exception
{
	setWizardName("Mail Merge Report");
	setVal("extension", ".odt");
	setNavigator(new HashNavigator(new String[] {
		"choosetemplate", "listquery",
		"editquery", "<end>"
	}));
	return runWizard("choosetemplate");
	
//EQuery equery = (EQuery)v.get("equery");
//File file = (File)v.get("file");
//
//System.out.println(equery);
//System.out.println(file);
}
public boolean runSegmentation() throws Exception
{
	setWizardName("Segmentation Report");
	setNavigator(new HashNavigator(new String[] {
		"editquery", "segreport",
		"segreport", "savecsv",
		"savecsv", "<end>"
	}));
	return runWizard("listquery");	
}

public boolean runClauseReport() throws Exception
{
	setWizardName("Clause Report");
	setNavigator(new HashNavigator(new String[] {
		"editquery", "savecsv",
		"savecsv", "<end>"
	}));
	return runWizard("listquery");	
}
public boolean runDonationReport() throws Exception
{
	setWizardName("Donation Report");
	setNavigator(new HashNavigator(new String[] {
		"editquery", "donationyears",
		"donationyears", "savecsv",
		"savecsv", "<end>"
	}));
	return runWizard("listquery");	
}
public boolean runMailingLabels(SqlRun str) throws Exception
{
	setWizardName("Mailing Labels");
	setNavigator(new HashNavigator(new String[] {
		"editquery", "<end>"
	}));
	if (!runWizard("listquery")) return false;

	Wizard wizard = this;
	EQuery equery = (EQuery)wizard.getVal("equery");

	String idSql = equery.getSql(fapp.equerySchema(), false);
	LabelReport.viewReport(str, fapp, idSql, "zip,address1");
//	String sql = LabelReport.getSql(idSql, "zip");
//	str.execSql(sql, new RsTasklet2() {
//	public void run(SqlRun str, ResultSet rs) throws Exception {
//		Reports rr = fapp.getReports();
//		rr.viewJasper(rr.toJasper(rs), null, "AddressLabels.jrxml");
//	}});
	return true;
}

}
