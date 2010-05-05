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
/*
 * ActionPanel.java
 *
 * Created on October 22, 2006, 10:08 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.devel.gui;

import citibob.wizard.Wizard;
import offstage.*;
import citibob.swing.html.*;
import java.util.*;
import citibob.task.*;
import citibob.sql.*;
import java.io.File;
import offstage.email.VettEmail;
import offstage.equery.EQuery;
import offstage.equery.swing.EQueryWizard;
import offstage.reports.AlumReport;
import offstage.reports.ClauseReport;
import offstage.reports.DonationReport;
import offstage.reports.MailMerge;
import offstage.reports.SegmentationReport;

/**
 *
 * @author citibob
 */
public class DevelActionPanel
extends ObjHtmlPanel
implements ObjHtmlPanel.Listener
{

FrontApp fapp;
HashMap<String,Job> actionMap = new HashMap();

DevelActionPanel getThis() { return this; }


/** Creates a new instance of ActionPanel */
public void initRuntime(FrontApp xfapp)
throws org.xml.sax.SAXException, java.io.IOException
{
	this.fapp = xfapp;


	actionMap.put("ticketsalesreport", new Job("", new ETask() {
	public void run() throws Exception {
//		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
		Wizard wizard = new offstage.reports.ReportWizard(fapp, DevelActionPanel.this);
		wizard.runWizard("ticketparams");
	}}));

	actionMap.put("mailmerge", new Job("", new SqlTask() {
	public void run(SqlRun str) throws Exception {
//		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
		EQueryWizard wizard = new EQueryWizard(fapp, DevelActionPanel.this);
		if (wizard.runMailMerge()) {
			System.out.println((String)wizard.getVal("submit"));
			MailMerge.viewReport(str, fapp, (EQuery)wizard.getVal("equery"), (File)wizard.getVal("file"));
		}
	}}));

	actionMap.put("segmentation", new Job("", new SqlTask() {
	public void run(SqlRun str) throws Exception {
//		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
		EQueryWizard wizard = new EQueryWizard(fapp, DevelActionPanel.this);
		if (wizard.runSegmentation()) {
			EQuery equery = (EQuery)wizard.getVal("equery");
			String idSql = equery.getSql(fapp.equerySchema());
			SegmentationReport.writeCSV(fapp, str, idSql,
				(List<String>)wizard.getVal("segtypes"),
				(File)wizard.getVal("file"));
		}
	}}));

	actionMap.put("mailinglabels", new Job("", new SqlTask() {
	public void run(SqlRun str) throws Exception {
//		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
		EQueryWizard wizard = new EQueryWizard(fapp, DevelActionPanel.this);
		wizard.runMailingLabels(str);
	}}));

	actionMap.put("emails", new Job("", new SqlTask() {
	public void run(SqlRun str) throws Exception {
		EQueryWizard wizard = new EQueryWizard(fapp, DevelActionPanel.this);
		if (!wizard.runBulkEmails(str)) return;

		// Text of the email we wish to send
		byte[] msgBytes = (byte[])wizard.getVal("emails");
//if (msg != null) System.out.println("Email = " + msg.subject);

		// SQL of people we wish to send to
		EQuery equery = (EQuery)wizard.getVal("equery");

		VettEmail.sendJangoMail(fapp, str, msgBytes,
			equery.getSql((fapp.equerySchema())),
			equery.toXML((fapp.equerySchema())),
				(Integer)wizard.getVal("equeryid"), VettEmail.ET_BULK);
		
//		//		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
//		EQueryWizard wizard = new EQueryWizard(fapp, DevelActionPanel.this);
//		wizard.runCSEmails(str);
	}}));

	actionMap.put("donationreport", new Job("", new SqlTask() {
	public void run(SqlRun str) throws Exception {
//		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
		EQueryWizard wizard = new EQueryWizard(fapp, DevelActionPanel.this);
		if (wizard.runDonationReport()) {
			EQuery equery = (EQuery)wizard.getVal("equery");
			String idSql = equery.getSql(fapp.equerySchema());
			DonationReport.writeCSV(fapp, str, idSql,
				((Number)wizard.getVal("minyear")).intValue(),
				((Number)wizard.getVal("maxyear")).intValue(),
				(File)wizard.getVal("file"));
		}
	}}));


	actionMap.put("clausereport", new Job("", new SqlTask() {
	public void run(SqlRun str) throws Exception {
//		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
		EQueryWizard wizard = new EQueryWizard(fapp, DevelActionPanel.this);
		if (wizard.runClauseReport()) {
			EQuery equery = (EQuery)wizard.getVal("equery");
			ClauseReport.writeClauseCSV(fapp, str,
				(EQuery)wizard.getVal("equery"),
				(File)wizard.getVal("file"));
		}
	}}));

	actionMap.put("castingreport", new Job("", new SqlTask() {
	public void run(SqlRun str) throws Exception {
//		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
		EQueryWizard wizard = new EQueryWizard(fapp, DevelActionPanel.this);
		if (wizard.runClauseReport()) {
			EQuery equery = (EQuery)wizard.getVal("equery");
			ClauseReport.writeCastingCSV(fapp, str,
				(EQuery)wizard.getVal("equery"),
				(File)wizard.getVal("file"));
		}
	}}));

	actionMap.put("alumreport", new Job("", new SqlTask() {
	public void run(SqlRun str) throws Exception {
//		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
		EQueryWizard wizard = new EQueryWizard(fapp, DevelActionPanel.this);
		if (wizard.runClauseReport()) {
			EQuery equery = (EQuery)wizard.getVal("equery");
			AlumReport.writeAlumCSV(fapp, str,
				(EQuery)wizard.getVal("equery"),
				(File)wizard.getVal("file"));
		}
	}}));

	
	addListener(this);
	loadHtml();
}

// ===================================================
// ObjHtmlPanel.Listener
public void linkSelected(java.net.URL href, String target)
{
	String url = href.toExternalForm();
	int slash = url.lastIndexOf('/');
	if (slash > 0) url = url.substring(slash+1);
	
	Job t = actionMap.get(url);
	fapp.guiRun().run(this, new Job(t.getPermissions(), t.getCBRunnable()));
}
}
