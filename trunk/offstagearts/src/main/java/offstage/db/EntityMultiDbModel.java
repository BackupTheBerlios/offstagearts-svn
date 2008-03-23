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
package offstage.db;

import citibob.jschema.*;
import citibob.sql.*;
import citibob.sql.pgsql.SqlInteger;
import java.util.*;
import java.sql.*;
import offstage.schema.*;
import citibob.jschema.log.*;
import offstage.db.*;

/** A DbModel based on a core entity table, plus a lot of other hanger-oners. */
public class EntityMultiDbModel extends MultiDbModel
{

public final EntityDbModel personDb;

public EntityMultiDbModel(citibob.app.App app)
{
	QueryLogger logger = app.queryLogger();
	logadd(logger, personDb = new EntityDbModel(app.getSchema("persons"), app));
}

///** Sets up the SchemaBufs for a new person,
//which will be inserted into the DB upon doUpdate(). */
//public void newEntity(Statement st, int entityType) throws java.sql.SQLException
//{
//	// Clear all existing data --- including in sub-DBModels.
//	doClear();
//
//	// Get a new entityID for this record.
//	int entityID = DB.r_nextval(st, "entities_entityid_seq");
//	setKey(entityID);
//
//	// Insert a blank record with that entityID
//	try {
//		personDb.getSchemaBuf().insertRow(-1, new String[] {"entityid", "primaryentityid", "isorg"},
//			new Object[] {new Integer(entityID), new Integer(entityID), Boolean.FALSE});
//	} catch(KeyViolationException e) {}	// can't happen, buffer is clear.
//}

//public Integer getPrimaryEntityID()
//{ return (Integer)personDb.getSchemaBuf().getValueAt(0, "primaryentityid"); }

}
