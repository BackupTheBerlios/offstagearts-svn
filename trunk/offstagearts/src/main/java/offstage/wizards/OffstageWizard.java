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
 * OffstageWizard.java
 *
 * Created on October 12, 2006, 9:10 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.wizards;

import citibob.swing.html.*;
import citibob.swing.prefs.*;
import javax.swing.*;
import java.sql.*;
import offstage.db.*;
import offstage.*;
import java.util.prefs.*;
import citibob.wizard.*;
import java.awt.*;

/**
 *
 * @author citibob
 */
public class OffstageWizard extends citibob.swing.SwingWizard
{

protected FrontApp fapp;

/** Creates a new instance of OffstageWizard */
public OffstageWizard(String wizardName, FrontApp fapp, Component component)
{
	super(wizardName, fapp, component);
	this.fapp = fapp;
}
	
/** Creates an INSERT query from the values of the HashMap v, and the schema. */
protected citibob.sql.ConsSqlQuery newInsertQuery(String maintable, TypedHashMap v)
{
	return citibob.sql.SQL.newInsertQuery(maintable, v, fapp.getSchema(maintable));
	
}

}
