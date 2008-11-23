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

import citibob.jschema.*;
import citibob.sql.pgsql.*;
import citibob.sql.*;
import java.sql.*;
import citibob.util.*;
import citibob.types.*;
import offstage.types.SqlPhone;

public class TermregsSchema extends ConstSqlSchema
{

public TermregsSchema(citibob.sql.SqlRun str, DbChangeModel change, java.util.TimeZone tz)
throws SQLException
{
	super();
	table = "termregs";
	DbKeyedModel kmodel = new DbKeyedModel(str, change, "programids",
		"select programid, name, termid from programids order by name",
		"<No Level Selected>");
	cols = new SqlCol[] {
		new SqlCol(new SqlInteger(false), "groupid", true),	// links to termids; this should really be enum, except that's not needed...
		new SqlCol(new SqlInteger(false), "entityid", true),
		new SqlCol(new SqlInteger(true), "payerid"),
		new SqlCol(new SqlNumeric(9,2, true), "tuition"),
		new SqlCol(new SqlNumeric(9,2, true), "defaulttuition"),
		new SqlCol(new SqlNumeric(9,2, false), "scholarship"),
		new SqlCol(new SqlNumeric(9,2, true), "tuitionoverride"),
		new SqlCol(new SqlDate(tz, true), "dtsigned"),
		new SqlCol(new SqlDate(tz, true), "dtregistered"),
//		new SqlCol(new SqlString(true), "rbplan"),
		new SqlCol(new SqlEnum(kmodel), "programid"),
		new SqlCol(new SqlString(true), "emer_rel"),
		new SqlCol(new SqlString(true), "emer_name"),
		new SqlCol(new SqlString(true), "emer_addr"),
		new SqlCol(new SqlString(true), "emer_city"),
		new SqlCol(new SqlString(true), "emer_state"),
		new SqlCol(new SqlPhone(), "emer_home"),
		new SqlCol(new SqlPhone(), "emer_work"),
		new SqlCol(new SqlPhone(), "emer_cell"),
		new SqlCol(new SqlString(true), "emer_doctorname"),
		new SqlCol(new SqlString(true), "emer_healthins"),
		new SqlCol(new SqlString(true), "emer_healthinsno"),
		new SqlCol(new SqlString(true), "med_pasttreatment"),
		new SqlCol(new SqlString(true), "med_curcondition"),
		new SqlCol(new SqlString(true), "med_allergies"),
		new SqlCol(new SqlString(true), "med_allergymeds"),
		new SqlCol(new SqlString(true), "med_curmeds"),
		new SqlCol(new SqlString(true), "med_tetboosterdate"),
		new SqlCol(new SqlBool(false), "emer_filledout"),
		new SqlCol(new SqlBool(false), "emer_signed")
	};
}

}
