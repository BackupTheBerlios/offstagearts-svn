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
import java.sql.*;
import citibob.types.*;



public class HolidaysSchema extends ConstSqlSchema
{	
	
public HolidaysSchema(citibob.sql.SqlRun str, DbChangeModel change, java.util.TimeZone tz)
throws SQLException{
	
	table = "holidays";
	cols = new SqlCol[] {
		new SqlCol(new SqlSerial("holidays_holidayid_seq", false), "holidayid", true),
		new SqlCol(new SqlInteger(false), "termid", false),
		new SqlCol(new SqlDate(tz, false), "firstday", false),
		new SqlCol(new SqlDate(tz, true), "lastday", false),
		new SqlCol(new SqlString(false), "description", false)
	};
}
}
