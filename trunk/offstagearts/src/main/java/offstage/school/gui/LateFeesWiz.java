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
 * OrgWiz.java
 *
 * Created on October 8, 2006, 6:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.school.gui;

import citibob.swing.typed.*;
import citibob.swing.html.*;
import citibob.text.DivDoubleSFormat;
import citibob.types.JDate;
import java.sql.*;
import java.util.Calendar;

/**
 *
 * @author citibob
 */
public class LateFeesWiz extends HtmlWiz {
	
/**
 * Creates a new instance of OrgWiz 
 */
public LateFeesWiz(java.awt.Frame owner, citibob.app.App app)
throws org.xml.sax.SAXException, java.io.IOException, SQLException
{
	super(owner, app.swingerMap());

//	JTypedTextField latedays = new JTypedTextField();
//		latedays.setJType(Integer.class, new FormatSFormat(new NumberFormat("#")));
//	.newSwinger()
//		new JTypedDateChooser();
//		asofdate.setJType(new JDate(app.getTimeZone(), false), )

	SwingerMap smap = app.swingerMap();
	JDate jdate = new JDate(app.timeZone(), false);
	TypedWidget asofdate = smap.newWidget(jdate);
	TypedWidget latedays = smap.newWidget(Integer.class);
	JTypedTextField multiplier = new JTypedTextField();
		multiplier.setJType(Double.class, new DivDoubleSFormat("0.00", .01));

	Calendar cal = jdate.getCalendar();
	cal.setTime(new java.util.Date());
	cal.set(Calendar.DAY_OF_MONTH,1);

	asofdate.setValue(cal.getTime());
	latedays.setValue(30);
	multiplier.setValue(.015D);
		
	addWidget("latedays", latedays);
	addWidget("asofdate", asofdate);
	addWidget("multiplier", multiplier);
	
	loadHtml();
}

}
