/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.datatab;

import citibob.app.App;
import citibob.sql.SqlRun;
import java.sql.SQLException;
import offstage.schema.EventsSchema;

/**
 *
 * @author citibob
 */
public class events_DT extends DataTab
{

public events_DT(SqlRun str, App app)
throws SQLException
{
	title = "Events";
	schema = new EventsSchema(str, app.dbChange());
//	orderClause = "firstdate desc,name";
	displayColTitles = new String[] {"Event"};
	displayCols = new String[] {"groupid"};
	equeryAliases = new String[] {
		"events.groupid", "event-type"
	};
}
}
