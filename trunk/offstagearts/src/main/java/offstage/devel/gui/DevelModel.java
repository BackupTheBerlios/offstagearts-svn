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
package offstage.devel.gui;

import citibob.jschema.*;
import citibob.sql.*;
import citibob.sql.pgsql.SqlInteger;
import java.util.*;
import java.sql.*;
import offstage.db.*;
import offstage.schema.*;
import citibob.jschema.log.*;
import offstage.FrontApp;
import offstage.datatab.DataTab;

/** Query one person record and all the stuff related to it.
 Users of this class should use the setKey(Object) method, where the key is
 an entityid. */
public class DevelModel extends MultiDbModel
{


// Key field.
private int entityID;

// -------------------------------------------------------

QueryLogger logger;
EntityDbModel onePerson;
RelO2mDbModel headofDm;				// The person who is head of our household.
IntKeyedDbModel phones;
//IntKeyedDbModel donations;
//IntKeyedDbModel flags;
//IntKeyedDbModel notes;
//IntKeyedDbModel tickets;
//IntKeyedDbModel events;
////IntKeyedDbModel classes;
//IntKeyedDbModel terms;
//IntKeyedDbModel interests;

Map<String,IntKeyedDbModel> tabsDm = new TreeMap();

//public void setKey(int entityID)
//{
//	this.entityID = entityID;
//
//	// First, figure out whether 
//	onePerson.setKey(entityID);
//	phones.setKey(entityID);
//	donations.setKey(entityID);
//	flags.setKey(entityID);
//	notes.setKey(entityID);
//	tickets.setKey(entityID);
//	events.setKey(entityID);
////	classes.setKey(entityID);
//	terms.setKey(entityID);
//	interests.setKey(entityID);
//}
public int getEntityId()
{
	return onePerson.getIntKey();
//	return entityID;
}
// ---------------------------------------------------------
// Return the various SchemaBufs that make up this super-record.

public EntityBuf getPersonSb()
	{ return (EntityBuf)onePerson.getSchemaBuf(); }
public SchemaBuf getHeadofSb()
	{ return headofDm.getSchemaBuf(); }
public EntityDbModel getPersonDb()
	{ return onePerson; }
public EntityBuf getEntitySb()
	{ return (EntityBuf)getEntity().getSchemaBuf(); }
public EntityDbModel getEntity()
	{ return onePerson; }
public SchemaBuf getPhonesSb()
	{ return phones.getSchemaBuf(); }

public IntKeyedDbModel getTabDm(String name)
	{ return tabsDm.get(name); }
public SchemaBuf getTabSb(String name)
	{ return getTabDm(name).getSchemaBuf(); }

void logadd(SchemaBufDbModel m)
{
	add(m);
	m.setLogger(logger);
}
public DevelModel(FrontApp app)
{
	logger = app.queryLogger();
	SchemaSet osset = app.schemaSet();
	logadd(onePerson = new EntityDbModel(osset.get("persons"), app));
	add(headofDm = new RelO2mDbModel(app.sqlRun(), app) {
		public void setKey(Object key) {
			super.entityid1 = (Integer)key;
		}});
	headofDm.setKeys("headof", -1, null);
	
	logadd(phones = new IntKeyedDbModel(osset.get("phones"), "entityid"));
		
	for (DataTab tab : app.dataTabSet().allTabs()) {
		IntKeyedDbModel dm = tab.newDbModel(logger);
		dm.setOrderClause(tab.getOrderClause());
		tabsDm.put(tab.getTableName(), dm);
		logadd(dm);
//		add(dm);
//		dm.setLogger(logger);
	}
		
}

public void insertPhone(int groupTypeID) throws KeyViolationException
{
	getPhonesSb().insertRow(-1, "groupid", new Integer(groupTypeID));
}


/** Override standard delete.  Don't actually delete record, just set obsolete bit. */
public void doDelete(SqlRun str)
//throws java.sql.SQLException
{
	// Delete the immediate record
	SchemaBufDbModel dm = getEntity();
	SchemaBuf sb = dm.getSchemaBuf();
	sb.setValueAt(Boolean.TRUE, 0, sb.findColumn("obsolete"));
	dm.doUpdate(str);

// Do not reassign relationships; this is for a later cleansing step.
//	// Reassign any other family members
//	str.execSql("update entities set primaryentityid=entityid" +
//		" where primaryentityid = " + SqlInteger.sql(this.getEntityId()));	
}


}
