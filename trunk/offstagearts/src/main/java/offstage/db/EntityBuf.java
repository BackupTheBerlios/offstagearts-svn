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
package offstage.db;

import citibob.jschema.*;
import offstage.schema.*;
import citibob.sql.*;
import offstage.db.*;
import java.sql.*;

/**
 *
 * @author citibob
 */
public class EntityBuf extends SchemaBuf
{
	
/** Creates a new instance of EntitySchemaBuf.  schema is either PersonSchema or OrgSchema */
public EntityBuf(SqlSchema schema) {
	super(schema);
}
	
/** Automatically set lastupdated every time row saved to DB. */
public void getUpdateCols(int row, ConsSqlQuery q, boolean updateUnchanged, SchemaInfo qs)
{
	super.getUpdateCols(row, q, updateUnchanged, qs);
	q.addColumn("lastupdated", "now()");
}

/** Automatically set lastupdated every time row saved to DB. */
public void getInsertCols(int row, ConsSqlQuery q, boolean insertUnchanged, SchemaInfo qs)
{
	super.getInsertCols(row, q, insertUnchanged, qs);
	q.addColumn("lastupdated", "now()");
	q.addColumn("created", "now()");
}

/** Changes primary entity id to be the same as the primaryentityid of someone else. */
public void setPrimaryEntityID(SqlRunner str, int entityid)
throws SQLException
{
	DB.getPrimaryEntityID(str, entityid);
	str.execUpdate(new UpdRunnable() {
	public void run(SqlRunner str) throws Exception {
		setValueAt(str.get("primaryentityid"), 0, findColumn("primaryentityid"));
	}});
}
// ==================================================================
// Custom edits to data in the buffer...
		
public void clearFamily()
{
	setValueAt(getValueAt(0, "entityid"), 0, findColumn("primaryentityid"));
}

}
