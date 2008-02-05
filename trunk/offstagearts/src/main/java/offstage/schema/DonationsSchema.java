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
package offstage.schema;

import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.sql.DbChangeModel;
import java.sql.*;
import citibob.types.*;

public class DonationsSchema extends ConstSqlSchema
{

public DonationsSchema(citibob.sql.SqlRunner str, DbChangeModel change, java.util.TimeZone tz)
throws SQLException
{
	table = "donations";
	KeyedModel kmodel = new DbKeyedModel(str, change,
		"donationids", "groupid", "name", "name");
	cols = new SqlCol[] {
		new SqlCol(new SqlInteger(false), "serialid", true),
		new SqlCol(new SqlEnum(kmodel, false), "groupid", false),
		new SqlCol(new SqlInteger(false), "entityid", false),
		new ColumnDefaultNow(new SqlDate(tz, false), "date", false),
		new SqlCol(new SqlNumeric(9, 2), "amount", false)
	};
}
// ------------------------------------------
// Singleton stuff
//private static DonationsSchema instance = new DonationsSchema();
//public static ConstSqlSchema getInstance()
//	{ return instance; }

}
