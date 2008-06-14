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
package offstage.schema;

import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.sql.DbChangeModel;
import java.sql.*;
import citibob.types.*;

public class OcdiscsSchema extends ConstSqlSchema
{

public OcdiscsSchema(citibob.sql.SqlRun str, DbChangeModel change, java.util.TimeZone tz)
throws SQLException
{
	table = "ocdiscs";
	KeyedModel kmodel = new DbKeyedModel(str, change,
		"ocdiscids", "ocdiscid", "name", "name");
	cols = new SqlCol[] {
		new SqlCol(new SqlInteger(false), "serialid", true),
		new SqlCol(new SqlInteger(false), "entityid", false),
		new SqlCol(new SqlEnum(kmodel, false), "ocdiscid", false),
		new SqlCol(new SqlDate(tz, true), "dstart"),
		new SqlCol(new SqlDate(tz, true), "dend")
	};
}
// ------------------------------------------
// Singleton stuff
//private static DonationsSchema instance = new DonationsSchema();
//public static ConstSqlSchema getInstance()
//	{ return instance; }

}
