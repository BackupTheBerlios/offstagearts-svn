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

/** Query one person record and all the stuff related to it. */

public class DevelModel extends MultiDbModel
{


// Key field.
private int entityID;

// -------------------------------------------------------

QueryLogger logger;
EntityDbModel onePerson;
IntKeyedDbModel phones;
IntKeyedDbModel donations;
IntKeyedDbModel flags;
IntKeyedDbModel notes;
IntKeyedDbModel tickets;
IntKeyedDbModel events;
//IntKeyedDbModel classes;
IntKeyedDbModel terms;
IntKeyedDbModel interests;


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
public EntityDbModel getPersonDb()
	{ return onePerson; }
public EntityBuf getEntitySb()
	{ return (EntityBuf)getEntity().getSchemaBuf(); }
public EntityDbModel getEntity()
	{ return onePerson; }
public SchemaBuf getPhonesSb()
	{ return phones.getSchemaBuf(); }
public SchemaBuf getDonationSb()
	{ return donations.getSchemaBuf(); }
public SchemaBuf getFlagSb()
	{ return flags.getSchemaBuf(); }
public SchemaBuf getEventsSb()
	{ return events.getSchemaBuf(); }
//public SchemaBuf getClassesSb()
//	{ return classes.getSchemaBuf(); }
public SchemaBuf getTermsSb()
	{ return terms.getSchemaBuf(); }
public SchemaBuf getInterestsSb()
	{ return interests.getSchemaBuf(); }
public SchemaBuf getNotesSb()
	{ return notes.getSchemaBuf(); }
public SchemaBuf getTicketsSb()
	{ return tickets.getSchemaBuf(); }

void logadd(SchemaBufDbModel m)
{
	add(m);
	m.setLogger(logger);
}
public DevelModel(citibob.app.App app)
{
	logger = app.queryLogger();
	SchemaSet osset = app.schemaSet();
	logadd(onePerson = new EntityDbModel(osset.get("persons"), app));
	logadd(phones = new IntKeyedDbModel(osset.get("phones"), "entityid"));
	logadd(donations = new IntKeyedDbModel(osset.get("donations"), "entityid"));
		donations.setOrderClause("date desc");
	logadd(flags = new IntKeyedDbModel(osset.get("flags"), "entityid"));
		flags.setOrderClause("groupid");
	logadd(notes = new IntKeyedDbModel(osset.get("notes"), "entityid"));
		notes.setOrderClause("date desc");
	logadd(tickets = new IntKeyedDbModel(osset.get("tickets"), "entityid"));
		tickets.setOrderClause("date desc");
	logadd(events = new IntKeyedDbModel(osset.get("events"), "entityid"));
		events.setOrderClause("groupid");
//	logadd(classes = new IntKeyedDbModel(osset.classes, "entityid"));
//		classes.setOrderClause("groupid");
	logadd(terms = new IntKeyedDbModel(osset.get("termenrolls"), "entityid"));
		terms.setOrderClause("firstdate desc,name");
	logadd(interests = new IntKeyedDbModel(osset.get("interests"), "entityid"));
		interests.setOrderClause("groupid");
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

	// Reassign any other family members
	str.execSql("update entities set primaryentityid=entityid" +
		" where primaryentityid = " + SqlInteger.sql(this.getEntityId()));	
}


}
