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
