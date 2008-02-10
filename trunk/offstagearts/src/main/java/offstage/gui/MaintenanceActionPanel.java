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
import citibob.task.*;
import offstage.wizards.newrecord.*;
import citibob.swing.*;
import javax.swing.*;
import offstage.cleanse.*;
import citibob.sql.*;
import offstage.wizards.newgroupid.NewGroupidWizard;

/**
 *
 * @author citibob
 */
public class MaintenanceActionPanel
extends ActionPanel
{
	
/** Creates a new instance of ActionPanel */
public void initRuntime(FrontApp xfapp)
throws org.xml.sax.SAXException, java.io.IOException
{
	super.initRuntime(xfapp);

	addAction("mailprefs", "", "admin", new ERunnable() {
	public void run() throws Exception {
		new citibob.mail.MailPrefsDialog(
			(JFrame)SwingUtilities.getRoot(MaintenanceActionPanel.this)).setVisible(true);
	}});

	addAction("newcategory", "", "admin", new ERunnable() {
	public void run() throws Exception {
		Wizard wizard = new NewGroupidWizard((FrontApp)app, MaintenanceActionPanel.this);
		wizard.runWizard();
	}});

	addAction("newkey", "", "admin", new ERunnable() {
	public void run() throws Exception {
		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(MaintenanceActionPanel.this);
		Wizard wizard = new offstage.crypt.wiz.NewKeyWizard((FrontApp)app, root);
		wizard.runWizard();
	}});

	addAction("dupkey", "", "admin", new ERunnable() {
	public void run() throws Exception {
		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(MaintenanceActionPanel.this);
		Wizard wizard = new offstage.crypt.wiz.DupKeyWizard((FrontApp)app, root);
		wizard.runWizard();
	}});

	addAction("restorekey", "", "admin", new ERunnable() {
	public void run() throws Exception {
//		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(MaintenanceActionPanel.this);
		Wizard wizard = new offstage.crypt.wiz.RestoreKeyWizard((FrontApp)app, MaintenanceActionPanel.this);
		wizard.runWizard();
	}});

	addAction("ccbatch", "", "admin", new ERunnable() {
	public void run() throws Exception {
//		JFrame root = (javax.swing.JFrame)WidgetTree.getRoot(MaintenanceActionPanel.this);
		Wizard wizard = new offstage.crypt.wiz.CCBatchWizard((FrontApp)app, MaintenanceActionPanel.this);
		wizard.runWizard();
	}});

	addAction("processdupnames", "", "admin", new BatchRunnable() {
	public void run(SqlRunner str) throws Exception {
		CleansePanel.showFrame(str, (FrontApp)app, "n", "Duplicate Names");
	}});
	addAction("processdupaddrs", "", "admin", new BatchRunnable() {
	public void run(SqlRunner str) throws Exception {
		CleansePanel.showFrame(str, (FrontApp)app, "a", "Duplicate Addresses");
	}});
	

	addListener(this);
	loadHtml();
}

//// ===================================================
//// ObjHtmlPanel.Listener
//public void linkSelected(java.net.URL href, String target)
//{
//	String url = href.toExternalForm();
//	int slash = url.lastIndexOf('/');
//	if (slash > 0) url = url.substring(slash+1);
//	
//	CBTask t = actionMap.get(url);
//	(FrontApp)app.runGui(this, t.getPermissions(), t.getCBRunnable());
//}
}

//
//public boolean doTicketSalesReport(String title, int groupid) throws Exception
//{
//	SqlTableModel report = new SqlTableModel((FrontApp)app.getSqlTypeSet(),
//		" select p.entityid,p.firstname,p.lastname,p.city,p.state,p.zip," +
//		" t.numberoftickets,t.payment,tt.tickettype\n" +
//		" from persons p, ticketeventsales t, tickettypes tt\n" +
//		" where p.entityid = t.entityid\n" +
//		" and t.tickettypeid = tt.tickettypeid\n" +
//		" and t.groupid in (314,315)\n" +
//		" order by p.lastname,p.firstname\n");
//	report.executeQuery(st);
//	OffstageGuiUtil.saveCSVReport(report.newTableModel(), "Save" + title,
//		(FrontApp)app, frame);
//	return true;
//}
