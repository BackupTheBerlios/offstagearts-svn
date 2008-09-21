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
 * OffstageFrameSet.java
 *
 * Created on January 5, 2008, 9:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.gui;

import javax.swing.JFrame;
import offstage.FrontApp;
import citibob.gui.*;
import citibob.sql.SqlRun;
import offstage.accounts.gui.AccountFrame;
import offstage.cleanse.CleanseFrame;
import offstage.cleanse.CleansePanel;
import offstage.config.ResourcesFrame;
import offstage.devel.gui.DevelFrame;
import offstage.frontdesk.FrontDeskFrame;
import offstage.school.gui.SchoolRegFrame;
import offstage.school.gui.SchoolSetupFrame;

/**
 *
 * @author citibob
 */
public class OffstageFrameSet extends citibob.gui.FrameSet {

FrontApp fapp;
WindowMenu wmenu;

public JFrame newFrame(FrameRec rec) throws Exception
{
	JFrame frame = super.newFrame(rec);
	wmenu.setWindowMenu(frame);
	if ("maintenance".equals(rec.name)) {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	frame.setTitle(frame.getTitle() + " --- OffstageArts " + fapp.version().toString());
	return frame;
}




/** Creates a new instance of OffstageFrameSet */
public OffstageFrameSet(FrontApp xfapp) {
	super(xfapp.swingPrefs(), xfapp.userRoot());
	wmenu = new WindowMenu(xfapp);
	this.fapp = xfapp;

addMaker("devel", new FrameMaker() {
public JFrame newFrame() throws Exception {
	SqlRun str = fapp.sqlRun();
	final DevelFrame f = new DevelFrame();
	f.initRuntime(fapp.sqlRun(), fapp);
	str.flush();
	return f;
}});
addMaker("frontdesk", new FrameMaker() {
public JFrame newFrame() throws Exception {
	SqlRun str = fapp.sqlRun();
	final FrontDeskFrame f = new FrontDeskFrame();
	f.initRuntime(fapp.sqlRun(), fapp);
	str.flush();
	return f;
}});
//addMaker("openclass", new FrameMaker() {
//public JFrame newFrame() throws Exception {
//	SqlRun str = fapp.sqlRun();
//	final OpenClassFrame f = new OpenClassFrame();
//	f.initRuntime(fapp.sqlRun(), fapp);
//	str.flush();
//	return f;
//}});
// ----------------------------------------
addMaker("schoolSetup", new FrameMaker() {
public JFrame newFrame() throws Exception {
	final SchoolSetupFrame f = new SchoolSetupFrame();
	SqlRun str = fapp.sqlRun();
	f.initRuntime(str, fapp);
	str.flush();
	return f;
}});
addMaker("schoolReg", new FrameMaker() {
public JFrame newFrame() throws Exception {
	final SchoolRegFrame f = new SchoolRegFrame();
	SqlRun str = fapp.sqlRun();
	f.initRuntime(str, fapp);
	str.flush();
	return f;
}});
// ----------------------------------------
addMaker("dups", new FrameMaker() {
public JFrame newFrame() throws Exception {
	final CleanseFrame f = new CleanseFrame();
	SqlRun str = fapp.sqlRun();
//	f.initRuntime(str, fapp, "n", CleansePanel.M_PROVISIONAL);
	f.initRuntime(str, fapp, 0, 0, CleansePanel.M_PROVISIONAL);
	str.flush();
	return f;
//	SqlRun str = fapp.getBatchSet();
//	final offstage.cleanse.CleansePanel panel = new CleansePanel();
//	panel.initRuntime(str, fapp, "n");
//	final JFrame frame = new JFrame("Duplicate Names");
//	frame.getContentPane().add(panel);
//	str.flush();
//	return frame;
}});
addMaker("dupsApprove", new FrameMaker() {
public JFrame newFrame() throws Exception {
	final CleanseFrame f = new CleanseFrame();
	SqlRun str = fapp.sqlRun();
//	f.initRuntime(str, fapp, "n", CleansePanel.M_APPROVE);
	f.initRuntime(str, fapp, 0, 0, CleansePanel.M_APPROVE);
	str.flush();
	return f;
}});
// ----------------------------------------
addMaker("console", new FrameMaker() {
public JFrame newFrame() {
	ConsoleFrame consoleFrame = new ConsoleFrame();
	consoleFrame.initRuntime("Java Console",
		fapp.swingPrefs(), fapp.userRoot().node("ConsoleFrame"));
	return consoleFrame;
}});
addMaker("maintenance", new FrameMaker() {
public JFrame newFrame() throws Exception {
	OffstageGui offstageGui = new OffstageGui();
	offstageGui.initRuntime(fapp);
	return offstageGui;
}});
addMaker("accounting", new FrameMaker() {
public JFrame newFrame() throws Exception {
	AccountFrame frame = new AccountFrame();
	frame.initRuntime(fapp);
	return frame;
}});
addMaker("resources", new FrameMaker() {
public JFrame newFrame() throws Exception {
	ResourcesFrame frame = new ResourcesFrame();
	SqlRun str = fapp.sqlRun();
	frame.initRuntime(str, fapp);
	str.flush();
	return frame;
}});
// ----------------------------------------
//addMaker("mailprefs", new FrameMaker() {
//public JFrame newFrame() {
//	return new citibob.mail.MailPrefsDialog(this);
//}});


}
}
