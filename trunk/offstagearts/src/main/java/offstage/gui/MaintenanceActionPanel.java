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
/*
 * ActionPanel.java
 *
 * Created on October 22, 2006, 10:08 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.gui;

import citibob.wizard.Wizard;
import offstage.*;
import citibob.swing.html.*;
import java.util.*;
import citibob.multithread.*;
import offstage.wizards.newrecord.*;
import citibob.swing.*;
import javax.swing.*;
import offstage.cleanse.*;
import citibob.sql.*;
import java.io.File;
import offstage.equery.EQuery;
import offstage.equery.swing.EQueryWizard;
import offstage.reports.ClauseReport;
import offstage.reports.DonationReport;
import offstage.reports.MailMerge;
import offstage.reports.SegmentationReport;

/**
 *
 * @author citibob
 */
public class MaintenanceActionPanel
extends ObjHtmlPanel
implements ObjHtmlPanel.Listener
{

FrontApp fapp;
HashMap<String,CBTask> actionMap = new HashMap();

MaintenanceActionPanel getThis() { return this; }


/** Creates a new instance of ActionPanel */
public void initRuntime(FrontApp xfapp)
throws org.xml.sax.SAXException, java.io.IOException
{
	this.fapp = xfapp;


	actionMap.put("mailprefs", new CBTask("", "admin", new ERunnable() {
	public void run() throws Exception {
		new citibob.mail.MailPrefsDialog(
			(JFrame)SwingUtilities.getRoot(MaintenanceActionPanel.this)).setVisible(true);
	}}));

	actionMap.put("newcategory", new CBTask("", "admin", new ERunnable() {
	public void run() throws Exception {
		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
		Wizard wizard = new offstage.wizards.newgroupid.NewGroupidWizard(fapp, root);
		wizard.runWizard();
	}}));

	actionMap.put("newkey", new CBTask("", "admin", new ERunnable() {
	public void run() throws Exception {
		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
		Wizard wizard = new offstage.crypt.wiz.NewKeyWizard(fapp, root);
		wizard.runWizard();
	}}));

	actionMap.put("dupkey", new CBTask("", "admin", new ERunnable() {
	public void run() throws Exception {
		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
		Wizard wizard = new offstage.crypt.wiz.DupKeyWizard(fapp, root);
		wizard.runWizard();
	}}));

	actionMap.put("restorekey", new CBTask("", "admin", new ERunnable() {
	public void run() throws Exception {
		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
		Wizard wizard = new offstage.crypt.wiz.RestoreKeyWizard(fapp, root);
		wizard.runWizard();
	}}));

	actionMap.put("ccbatch", new CBTask("", "admin", new ERunnable() {
	public void run() throws Exception {
		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(getThis());
		Wizard wizard = new offstage.crypt.wiz.CCBatchWizard(fapp, root);
		wizard.runWizard();
	}}));

	actionMap.put("processdupnames", new CBTask("", "admin", new BatchRunnable() {
	public void run(SqlRunner str) throws Exception {
		CleansePanel.showFrame(str, fapp, "n", "Duplicate Names");
	}}));
	actionMap.put("processdupaddrs", new CBTask("", "admin", new BatchRunnable() {
	public void run(SqlRunner str) throws Exception {
		CleansePanel.showFrame(str, fapp, "a", "Duplicate Addresses");
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
	
	CBTask t = actionMap.get(url);
	fapp.runGui(this, t.getPermissions(), t.getCBRunnable());
}
}

//
//public boolean doTicketSalesReport(String title, int groupid) throws Exception
//{
//	SqlTableModel report = new SqlTableModel(fapp.getSqlTypeSet(),
//		" select p.entityid,p.firstname,p.lastname,p.city,p.state,p.zip," +
//		" t.numberoftickets,t.payment,tt.tickettype\n" +
//		" from persons p, ticketeventsales t, tickettypes tt\n" +
//		" where p.entityid = t.entityid\n" +
//		" and t.tickettypeid = tt.tickettypeid\n" +
//		" and t.groupid in (314,315)\n" +
//		" order by p.lastname,p.firstname\n");
//	report.executeQuery(st);
//	OffstageGuiUtil.saveCSVReport(report.newTableModel(), "Save" + title,
//		fapp, frame);
//	return true;
//}
