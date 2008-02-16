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
 * OffstageFrameSet.java
 *
 * Created on January 5, 2008, 9:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.gui;

import citibob.gui.ConsoleFrame;
import citibob.gui.FrameSet.Maker;
import citibob.sql.SqlRunner;
import javax.swing.*;
import offstage.FrontApp;
import offstage.accounts.gui.AccountFrame;
import offstage.cleanse.CleanseFrame;
import offstage.devel.gui.DevelFrame;
import offstage.school.gui.SchoolFrame;

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
	return frame;
}


/** Creates a new instance of OffstageFrameSet */
public OffstageFrameSet(FrontApp xfapp) {
	super(xfapp.getSwingPrefs(), xfapp.userRoot());
	wmenu = new WindowMenu(xfapp);
	this.fapp = xfapp;

addMaker("devel", new Maker() {
public JFrame newFrame() throws Exception {
	SqlRunner str = fapp.getBatchSet();
	final DevelFrame f = new DevelFrame();
	f.initRuntime(fapp.getBatchSet(), fapp);
	str.flush();
//	str.execUpdate(new UpdRunnable() {
//	public void run(SqlRunner str) throws Exception {
//		f.pack();
//	}});
	return f;
}});
// ----------------------------------------
addMaker("school", new Maker() {
public JFrame newFrame() throws Exception {
	final SchoolFrame f = new SchoolFrame();
	SqlRunner str = fapp.getBatchSet();
	f.initRuntime(str, fapp);
	str.flush();
//	str.execUpdate(new UpdRunnable() {
//	public void run(SqlRunner str) throws Exception {
//		f.pack();
//	}});
	return f;
}});
// ----------------------------------------
addMaker("dups", new Maker() {
public JFrame newFrame() throws Exception {
	final CleanseFrame f = new CleanseFrame();
	SqlRunner str = fapp.getBatchSet();
	f.initRuntime(str, fapp, "n");
	str.flush();
	return f;
//	SqlRunner str = fapp.getBatchSet();
//	final offstage.cleanse.CleansePanel panel = new CleansePanel();
//	panel.initRuntime(str, fapp, "n");
//	final JFrame frame = new JFrame("Duplicate Names");
//	frame.getContentPane().add(panel);
//	str.flush();
//	return frame;
}});
// ----------------------------------------
addMaker("console", new Maker() {
public JFrame newFrame() {
	ConsoleFrame consoleFrame = new ConsoleFrame();
	consoleFrame.initRuntime("Java Console", fapp.userRoot().node("ConsoleFrame"));
	return consoleFrame;
}});
addMaker("maintenance", new Maker() {
public JFrame newFrame() throws Exception {
	OffstageGui offstageGui = new OffstageGui();
	offstageGui.initRuntime(fapp);
	return offstageGui;
}});
addMaker("accounting", new Maker() {
public JFrame newFrame() throws Exception {
	AccountFrame frame = new AccountFrame();
	frame.initRuntime(fapp);
	return frame;
}});
// ----------------------------------------
//addMaker("mailprefs", new Maker() {
//public JFrame newFrame() {
//	return new citibob.mail.MailPrefsDialog(this);
//}});


}
}
