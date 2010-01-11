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
public class memberships_DT extends DataTab
{

public memberships_DT(SqlRun str, App app)
throws SQLException
{
	title = "Memberships";
	schema = new MySchema(str, app.dbChange(), app.timeZone());
	orderClause = "startdate desc";
	displayColTitles = new String[] {"Type", "Start", "End"};
	displayCols = new String[] {"groupid", "startdate", "enddate"};
	equeryAliases = new String[] {
		"memberships.groupid", "membership",
		"memberships.startdate", "membership-start",
		"memberships.enddate", "memberships-end"
	};
	summary_st = null;
}


static class MySchema extends ConstSqlSchema
{

public MySchema(SqlRun str, DbChangeModel change, TimeZone tz)
throws SQLException
{
	table = "memberships";
	KeyedModel kmodel = new DbKeyedModel(str, change,
		"membershipids", "groupid", "name", "name", null);
	cols = new SqlCol[] {
		new SqlCol(new SqlInteger(false), "serialid", true),
		new SqlCol(new SqlEnum(kmodel), "groupid", false),
		new SqlCol(new SqlInteger(false), "entityid", false),
		new ColumnDefaultNow(new SqlDate(tz, false), "startdate", false),
		new ColumnDefaultNow(new SqlDate(tz, false), "enddate", false)
	};
}
// ------------------------------------------
// Singleton stuff
//private static DonationsSchema instance = new DonationsSchema();
//public static ConstSqlSchema getInstance()
//	{ return instance; }

}

}
