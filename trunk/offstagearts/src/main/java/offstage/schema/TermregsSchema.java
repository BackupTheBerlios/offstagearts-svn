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

import citibob.jschema.*;
import citibob.sql.pgsql.*;
import citibob.sql.*;
import java.sql.*;
import citibob.util.*;
import citibob.types.*;

public class TermregsSchema extends ConstSqlSchema
{

public TermregsSchema(citibob.sql.SqlRunner str, DbChangeModel change, java.util.TimeZone tz)
throws SQLException
{
	super();
	table = "termregs";
	KeyedModel kmodel = new DbKeyedModel(str, change, "programids",
		"select programid, name from programids order by name");
	cols = new SqlCol[] {
		new SqlCol(new SqlInteger(false), "groupid", true),	// links to termids; this should really be enum, except that's not needed...
		new SqlCol(new SqlInteger(false), "entityid", true),
		new SqlCol(new SqlNumeric(9,2, true), "tuition"),
		new SqlCol(new SqlNumeric(9,2, true), "defaulttuition"),
		new SqlCol(new SqlNumeric(9,2, false), "scholarship"),
		new SqlCol(new SqlNumeric(9,2, true), "tuitionoverride"),
		new SqlCol(new SqlDate(tz, true), "dtsigned"),
		new SqlCol(new SqlDate(tz, true), "dtregistered"),
		new SqlCol(new SqlString(true), "rbplan"),
		new SqlCol(new SqlEnum(kmodel, "<No Level Selected>"), "programid")		
	};
}

}
