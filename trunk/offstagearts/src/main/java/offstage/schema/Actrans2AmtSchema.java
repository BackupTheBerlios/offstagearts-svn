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

public class Actrans2AmtSchema extends ConstSqlSchema
{

public final KeyedModel assetKmodel;
	
public Actrans2AmtSchema(citibob.sql.SqlRun str, DbChangeModel change, java.util.TimeZone tz)
throws SQLException
{
	super();
	
	table = "actrans2amt";
	assetKmodel = new DbKeyedModel(str, change,
		"assetids", "assetid", "name", "name", null);
	cols = new SqlCol[] {
		new SqlCol(new SqlInteger(false), "actransid", true),		
		new SqlCol(new SqlEnum(assetKmodel), "assetid", true),
		new SqlCol(new SqlNumeric(9,2), "amount")
	};
}

}
