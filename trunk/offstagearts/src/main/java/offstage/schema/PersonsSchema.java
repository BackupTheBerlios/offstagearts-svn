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
import citibob.types.*;
import java.sql.*;


public class PersonsSchema extends EntitiesSchema
{

public static final KeyedModel genderKmodel;
static {
	genderKmodel = new KeyedModel();
		genderKmodel.addItem(null, "<Unknown>");
		genderKmodel.addItem("M", "Male");
		genderKmodel.addItem("F", "Female");
}

public PersonsSchema(citibob.sql.SqlRun str, DbChangeModel change, java.util.TimeZone tz)
throws SQLException
{
	super(str, change);
	table = "persons";
	appendCols(new SqlCol[] {
//		new Column(new SqlString(1), "gender", false),
		new SqlCol(new SqlChar(), "gender", false),
		new SqlCol(new SqlDate(tz, true), "dob", false),
		new SqlCol(new SqlBool(false), "dobapprox", false),
		new SqlCol(new SqlString(100), "email", false),
		new SqlCol(new SqlString(200), "url", false)
	});
}
// ------------------------------------------
//// Singleton stuff
//private static PersonsSchema instance = new PersonsSchema();
//public static ConstSchema getInstance()
//	{ return instance; }

}
