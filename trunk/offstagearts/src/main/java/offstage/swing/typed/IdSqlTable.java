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
 * SimpleSearchPanel.java
 *
 * Created on June 5, 2005, 5:47 PM
 */

package offstage.swing.typed;
import java.sql.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.swing.table.*;
import offstage.FrontApp;
import offstage.devel.gui.DevelModel;
import offstage.db.*;
import java.awt.event.*;
import citibob.swing.typed.*;
import javax.swing.table.*;
import java.awt.*;

/**
 *
 * @author  citibob
 */
public class IdSqlTable extends JTypedSelectTable {
	
protected IdSqlTableModel searchResults;
citibob.app.App app;

public void initRuntime(citibob.app.App app) //SqlRun str, FullEntityDbModel dm)
{
	this.app = app;
	searchResults = new IdSqlTableModel();
		
	// Add the model (with tooltips)
	setModelU(searchResults,
		new String[] {"Name"},
		new String[] {"name"},
		new String[] {"tooltip"},
		new boolean[] {false},
		app.swingerMap());
	setValueColU("entityid");
}

/** Re-query */
protected void executeQuery(SqlRun str, final String idSql, String orderBy)// throws SQLException
{
	searchResults.executeQuery(str, idSql, orderBy);
}

// ----------------------------------------------------------------------

}
