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
import java.sql.SQLException;
import offstage.schema.NotesSchema;

/**
 *
 * @author citibob
 */
public class notes_DT extends DataTab
{

public notes_DT(SqlRun str, App app)
throws SQLException
{
	title = "Notes";
	schema = new NotesSchema(str, app.dbChange(), app.timeZone());
	orderClause = "date desc";
	displayColTitles = new String[] {"Type", "Date", "Note"};
	displayCols = new String[] {"groupid", "date", "note"};
	equeryAliases = new String[] {
		"notes.groupid", "note-type",
		"notes.date", "note-date",
		"notes.note", "note"
	};
}
	
}
