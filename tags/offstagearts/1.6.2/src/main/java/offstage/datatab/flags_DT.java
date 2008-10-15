/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.datatab;

import citibob.app.App;
import citibob.sql.SqlRun;
import java.sql.SQLException;
import offstage.schema.FlagsSchema;

/**
 *
 * @author citibob
 */
public class flags_DT extends DataTab
{

public flags_DT(SqlRun str, App app)
throws SQLException
{
	title = "Flags";
	schema = new FlagsSchema(str, app.dbChange());
//	orderClause = "date desc";
	displayColTitles = new String[] {"Type"};
	displayCols = new String[]
			{"groupid"};
	equeryAliases = new String[] {
		"flags.groupid", "flags"
	};
}
	
}
