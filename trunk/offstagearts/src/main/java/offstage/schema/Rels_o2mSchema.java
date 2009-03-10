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

public class Rels_o2mSchema extends ConstSqlSchema
{

public Rels_o2mSchema(citibob.sql.SqlRun str, DbChangeModel change)
throws SQLException
{
	super();
	table = "rels";
	DbKeyedModel kmodel = new DbKeyedModel(str, change, "relids",
		"select relid, name, 0 from relids order by name",
		"<No Relationship>");
	cols = new SqlCol[] {
		new SqlCol(new SqlEnum(kmodel), "relid", true),
		new SqlCol(new SqlInteger(false), "temporalid", true),	// links to termids; this should really be enum, except that's not needed...
		new SqlCol(new SqlInteger(false), "entityid0"),
		new SqlCol(new SqlInteger(false), "entityid1", true)
	};
}

}
