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
package offstage.openclass;
/*
 * NewRecordWizard.java
 *
 * Created on October 8, 2006, 11:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

import citibob.swing.html.*;
import citibob.wizard.*;
import javax.swing.*;
import java.sql.*;
import offstage.db.*;
import offstage.wizards.*;
import offstage.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.jschema.log.*;
import java.awt.Component;
import offstage.wizards.newrecord.NewPersonWizard;

/**
 *
 * @author citibob
 */
public class NewTeacherWizard extends NewPersonWizard {

	/*
addState(new State("", "", "") {
	public HtmlWiz newWiz(WizState.Context con)
		{ return new }
	public void process(citibob.sql.SqlRun str)
	{
		
	}
});
*/

public NewTeacherWizard(offstage.FrontApp xfapp, Component comp)
{
//	super("New Record", xfapp, comp);
//	addStates(new NewPersonWizard(xfapp, comp));
	super(xfapp, comp);

//// ---------------------------------------------
addStartState(new AbstractWizState("selectteacher", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new SelectTeacherWiz(frame, con.str, fapp); }
	public void process(Wizard.Context con) throws Exception
	{
		String submit = con.v.getString("submit");
		if ("newteacher".equals(submit)) {
			stateName = "person";
		} else {
			Integer entityid = con.v.getInteger("entityid");
			if (entityid == null) stateName = "selectteacher";
			else stateName = "finished";
		}
	}
});
addComputeAction("finished", new ComputeAction() {
public void process(Wizard.Context con) throws Exception {
	Integer entityid = con.v.getInteger("entityid");
	
	// Add the teacher
	String sql = "insert into teachers (entityid) values (" + entityid + ")";
	con.str.execSql(sql);
	
	// Finish for real!
	stateName = null;
}});

// ---------------------------------------------------


}
}
