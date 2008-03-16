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
package offstage.wizards.modify;
/*
 * NewRecordWizard.java
 *
 * Created on October 8, 2006, 11:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

import citibob.sql.pgsql.SqlInteger;
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

/**
 *
 * @author citibob
 */
public class AmendRecordWizard extends OffstageWizard {

	/*
addState(new State("", "", "") {
	public HtmlWiz newWiz(citibob.sql.SqlRun str)
		{ return new }
	public void process(citibob.sql.SqlRun str)
	{
		
	}
});
*/
	
public AmendRecordWizard(offstage.FrontApp xfapp, java.awt.Component comp)
{
	super("New Person", xfapp, comp);
// ---------------------------------------------
addState(new AbstractWizState("find", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new FindWiz(frame, con.str, fapp); }
	public void process(Wizard.Context con) throws Exception
	{
		FindWiz w = (FindWiz)wiz;
//System.out.println("entityid = " + w.getEntityID());
		v.put("entityid", w.getEntityID());
	}
});
}
// ---------------------------------------------

}

