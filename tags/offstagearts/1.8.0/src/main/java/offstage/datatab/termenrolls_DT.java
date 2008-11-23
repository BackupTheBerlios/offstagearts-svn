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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.datatab;

import citibob.app.App;
import citibob.sql.SqlRun;
import citibob.swing.typed.SwingerMap;
import java.sql.SQLException;
import javax.swing.JTabbedPane;
import offstage.devel.gui.DevelModel;
import offstage.gui.GroupPanel;
import offstage.schema.TermenrollsSchema;

/**
 *
 * @author citibob
 */
public class termenrolls_DT extends DataTab
{

public termenrolls_DT(SqlRun str, App app)
throws SQLException
{
	title = "Terms";
	schema = new TermenrollsSchema(str, app.dbChange());
	orderClause = "firstdate desc,name";
	displayColTitles = new String[] {"Term", "Role"};
	displayCols = new String[] {"groupid", "courserole"};
	equeryAliases = new String[] {
		"termenrolls.groupid", "terms",
		"termenrolls.courserole", "termrole",
	};
}

public GroupPanel addToGroupPanels(SqlRun str, DevelModel dm,
JTabbedPane groupPanels, SwingerMap smap)
{
	GroupPanel panel = super.addToGroupPanels(str, dm, groupPanels, smap);
	panel.setEnabled(false);
	return panel;
}
}
