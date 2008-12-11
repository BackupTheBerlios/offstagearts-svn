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

package offstage.gui;

import citibob.swing.html.ActionPanel;
import citibob.task.ETask;
import offstage.FrontApp;


/**
 *
 * @author citibob
 */
public class WelcomeActionPanel
extends ActionPanel
{
	
/** Creates a new instance of ActionPanel */
public void initRuntime(FrontApp xfapp)
throws org.xml.sax.SAXException, java.io.IOException
{
	super.initRuntime(xfapp);

	addAction("devel", "", new ETask() {
	public void run() throws Exception {
		app.frameSet().openFrame("devel");
	}});
	addAction("frontdesk", "", new ETask() {
	public void run() throws Exception {
		app.frameSet().openFrame("frontdesk");
	}});
	addAction("schoolReg", "", new ETask() {
	public void run() throws Exception {
		app.frameSet().openFrame("schoolReg");
	}});
	addAction("schoolSetup", "", new ETask() {
	public void run() throws Exception {
		app.frameSet().openFrame("schoolSetup");
	}});
	addAction("accounting", "", new ETask() {
	public void run() throws Exception {
		app.frameSet().openFrame("accounting");
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
//	(FrontApp)app.guiRun().run(this, t.getPermissions(), t.getCBRunnable());
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
