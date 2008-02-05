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

public class PhoneidsSchema extends ConstSqlSchema
{

public PhoneidsSchema()
{
	table = "phoneids";
	cols = new SqlCol[] {
		new SqlCol(new SqlInteger(false), "groupid", true),
		new SqlCol(new SqlString(true), "name", false),
		new SqlCol(new SqlInteger(false), "priority", false),
		new SqlCol(new SqlString(1, false), "letter", false),
		new SqlCol(new SqlString(false), "code", false)
	};
}
// ------------------------------------------
// Singleton stuff
//private static DonationsSchema instance = new DonationsSchema();
//public static ConstSqlSchema getInstance()
//	{ return instance; }

}
