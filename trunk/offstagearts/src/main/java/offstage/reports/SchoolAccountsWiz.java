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
 * PersonWiz.java
 *
 * Created on October 8, 2006, 6:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.reports;

import citibob.swing.html.HtmlWiz;
import citibob.swing.typed.TypedWidget;
import citibob.types.JDay;
import citibob.util.Day;
import offstage.FrontApp;

public class SchoolAccountsWiz extends HtmlWiz {

public SchoolAccountsWiz(java.awt.Frame owner, FrontApp app)
throws org.xml.sax.SAXException, java.io.IOException
{
	super(owner, app.swingerMap());
	setSize(600,400);

	Day today = new Day(System.currentTimeMillis(), app.timeZone());

	TypedWidget asOfDay = app.swingerMap().newWidget(new JDay());
		asOfDay.setValue(today);
	addWidget("asOfDay", asOfDay);

	loadHtml();
}
}

