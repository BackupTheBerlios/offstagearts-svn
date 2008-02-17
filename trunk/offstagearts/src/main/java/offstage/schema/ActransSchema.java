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

public class ActransSchema extends ConstSqlSchema
{

public static final int AC_SCHOOL = 1;
public static final int AC_TICKET = 2;
public static final int AC_PLEDGE = 3;
public static final int AC_OPENCLASS = 4;
public static final int AC_EXPENSE = 5;
	
public final KeyedModel actranstypeKmodel;
public final KeyedModel actypeKmodel;

public ActransSchema(citibob.sql.SqlRunner str, DbChangeModel change, java.util.TimeZone tz)
throws SQLException
{
	super();
	
	table = "actrans";
	actypeKmodel = new DbKeyedModel(str, change,
		"actypes", "actypeid", "name", "name");
	actranstypeKmodel = new DbKeyedModel(str, change,
		"actranstypes", "actranstypeid", "name", "name");
	cols = new SqlCol[] {
		new SqlCol(new SqlInteger(false), "actransid", true),
		new SqlCol(new SqlEnum(actranstypeKmodel, false), "actranstypeid"),
		new SqlCol(new SqlInteger(false), "entityid"),
		new SqlCol(new SqlEnum(actypeKmodel, false), "actypeid"),
		new SqlCol(new SqlDate(tz, false), "date"),
		new SqlCol(new SqlDate(tz, false), "datecreated"),
		new SqlCol(new SqlNumeric(9,2), "amount"),
		new SqlCol(new SqlString(300,true), "description"),
		new SqlCol(new SqlInteger(true), "studentid"),
		new SqlCol(new SqlInteger(true), "termid"),
		new SqlCol(new SqlString(50), "py_name"),
		new SqlCol(new SqlChar(), "cc_type"),
		new SqlCol(new SqlString(4), "cc_last4"),
		new SqlCol(new SqlString(4), "cc_expdate"),
		new SqlCol(new SqlString(255), "cc_info"),
		new SqlCol(new SqlString(15), "ck_number"),
		new SqlCol(new SqlString(30), "py_phone")
	};
}

}
