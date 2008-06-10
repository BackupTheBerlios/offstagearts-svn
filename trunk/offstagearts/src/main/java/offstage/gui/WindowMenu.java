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
 * WIndowMenu.java
 *
 * Created on January 6, 2008, 12:28 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.gui;

import citibob.app.App;
import java.awt.Component;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 *
 * @author citibob
 */
public class WindowMenu {

App app;
//JFrame frame;

public WindowMenu(App app)
{
	this.app = app;
}

void setWindowMenu(JFrame f)
{
	// Find the Window menu
//	this.frame = f;
	JMenuBar mbar = (JMenuBar)f.getJMenuBar();
	if (mbar == null) return;
	JMenu menu;
	for (int i=0; ; ) {
		menu = mbar.getMenu(i);
		if ("Window".equals(menu.getText())) break;
		++i;
		if (i == mbar.getMenuCount()) return;		// Couldn't find it
	}

	// Add our menu items...
	JMenuItem mi;

	addFrameMenuItem(menu, "Development", "devel");
	addFrameMenuItem(menu, "School", "school");
	addFrameMenuItem(menu, "Accounting", "accounting");
	addFrameMenuItem(menu, "Front Desk", "frontdesk");
	addFrameMenuItem(menu, "Open Class", "openclass");
	menu.add(new JSeparator());
	addFrameMenuItem(menu, "Duplicates", "dups");
	addFrameMenuItem(menu, "Console", "console");
	addFrameMenuItem(menu, "Resources", "resources");

}

void addFrameMenuItem(JMenu menu, String text, String frameName)
{
	JMenuItem mi = new JMenuItem(text);
	mi.addActionListener(new OpenFrameListener(frameName));
	menu.add(mi);
}


class OpenFrameListener implements ActionListener
{
	String frameName;
	public OpenFrameListener(String frameName) {
		this.frameName = frameName;
	}
	public void actionPerformed(java.awt.event.ActionEvent evt) {
		app.guiRun().run((Component)evt.getSource(), new citibob.task.ETask() {
		public void run() throws Exception {
			app.frameSet().openFrame(frameName);
		}});
	}
}


}
