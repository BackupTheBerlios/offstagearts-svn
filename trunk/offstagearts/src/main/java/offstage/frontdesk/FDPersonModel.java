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
package offstage.frontdesk;

import citibob.jschema.*;
import citibob.sql.*;
import citibob.sql.pgsql.SqlInteger;
import java.util.*;
import java.sql.*;
import offstage.db.*;
import offstage.schema.*;
import citibob.jschema.log.*;

/** Query one person record and all the stuff related to it. */

public class FDPersonModel extends MultiDbModel
{


// Key field.
//private int entityID;

// -------------------------------------------------------

QueryLogger logger;
EntityDbModel onePerson;
IntKeyedDbModel phones;

//public void setKey(int entityID)
//{
//	this.entityID = entityID;
//
//	// First, figure out whether 
//	onePerson.setKey(entityID);
//	phones.setKey(entityID);
//}
//public int getEntityId()
//{
//	return entityID;
//}
// ---------------------------------------------------------
// Return the various SchemaBufs that make up this super-record.

public EntityDbModel getPersonDm()
	{ return onePerson; }
public IntKeyedDbModel getPhonesDm()
	{ return phones; }

void logadd(SchemaBufDbModel m)
{
	add(m);
	m.setLogger(logger);
}
public FDPersonModel(citibob.app.App app)
{
	logger = app.queryLogger();
	SchemaSet osset = app.schemaSet();
	logadd(onePerson = new EntityDbModel(osset.get("persons"), app));
	logadd(phones = new IntKeyedDbModel(osset.get("phones"), "entityid"));
}

public void insertPhone(int groupTypeID) throws KeyViolationException
{
	phones.getSchemaBuf().insertRow(-1, "groupid", new Integer(groupTypeID));
}


/** Override standard delete.  Don't actually delete record, just set obsolete bit. */
public void doDelete(SqlRun str)
//throws java.sql.SQLException
{
	// Delete the immediate record
	SchemaBufDbModel dm = getPersonDm();
	SchemaBuf sb = dm.getSchemaBuf();
	sb.setValueAt(Boolean.TRUE, 0, sb.findColumn("obsolete"));
	dm.doUpdate(str);

	// Reassign any other family members
	str.execSql("update entities set primaryentityid=entityid" +
		" where primaryentityid = " + SqlInteger.sql((Integer)this.getKey()));	
}


}
