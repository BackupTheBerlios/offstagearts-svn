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

public class ClassesSchema extends ConstSqlSchema
{

public ClassesSchema(citibob.sql.SqlRun str, DbChangeModel change)
throws SQLException
{
	super();
	table = "classes";
	KeyedModel kmodel = new DbKeyedModel(str, change,
		"classids", "groupid", "name", "name");
	cols = new SqlCol[] {
		new SqlCol(new SqlEnum(kmodel, false), "groupid", true),
		new SqlCol(new SqlInteger(false), "entityid", true),
		new SqlCol(new SqlString(300,true), "comments", false)
	};
}

}
