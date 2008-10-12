/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.datatab;

import citibob.app.App;
import citibob.jschema.ColumnDefaultNow;
import citibob.jschema.ConstSqlSchema;
import citibob.jschema.SqlCol;
import citibob.sql.DbChangeModel;
import citibob.sql.DbKeyedModel;
import citibob.sql.SqlEnum;
import citibob.sql.SqlRun;
import citibob.sql.ansi.SqlDate;
import citibob.sql.ansi.SqlInteger;
import citibob.types.KeyedModel;
import java.sql.SQLException;
import java.util.TimeZone;

/**
 *
 * @author citibob
 */
public class constitis_DT extends DataTab
{

public constitis_DT(SqlRun str, App app)
throws SQLException
{
	title = "Constituencies";
	schema = new MySchema(str, app.dbChange(), app.timeZone());
//	orderClause = "startdate desc";
	displayColTitles = new String[] {"Constituency"};
	displayCols = new String[] {"groupid"};
	equeryAliases = new String[] {
		"constits.groupid", "constit",
	};
}


static class MySchema extends ConstSqlSchema
{

public MySchema(SqlRun str, DbChangeModel change, TimeZone tz)
throws SQLException
{
	table = "constits";
	KeyedModel kmodel = new DbKeyedModel(str, change,
		"constitids", "groupid", "name", "name", null);
	cols = new SqlCol[] {
		new SqlCol(new SqlEnum(kmodel), "groupid", true),
		new SqlCol(new SqlInteger(false), "entityid", true)
	};
}

}

}
