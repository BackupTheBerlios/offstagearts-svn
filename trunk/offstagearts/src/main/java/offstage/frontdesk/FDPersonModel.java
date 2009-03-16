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

public class FDPersonModel extends LoggedMultiDbModel
{

EntityDbModel onePerson;
IntKeyedDbModel phones;
// TODO: Use OCDiscModels instead if we want description of discount amounts
public IntKeyedDbModel ocdiscs;
RelO2mDbModel parent1ofDm;
	public SchemaBufRowModel parent1ofRm;
RelO2mDbModel payerofDm;
	public SchemaBufRowModel payerofRm;

public EntityDbModel getPersonDm()
	{ return onePerson; }
public IntKeyedDbModel getPhonesDm()
	{ return phones; }

public FDPersonModel(SqlRun str, citibob.app.App app)
{
	super(app.queryLogger());
	SchemaSet osset = app.schemaSet();
	logadd(onePerson = new EntityDbModel(osset.get("persons"), app));
	logadd(phones = new IntKeyedDbModel(osset.get("phones"), "entityid"));
	
	logadd(payerofDm = new RelO2mDbModel(str, app) {
		public void setKey(Object key) {
			super.entityid1 = (Integer)key;
		}});
	payerofDm.setKeys("payerof", -1, null);

	logadd(parent1ofDm = new RelO2mDbModel(str, app) {
		public void setKey(Object key) {
			super.entityid1 = (Integer)key;
		}});
	parent1ofDm.setKeys("parent1of", -1, null);
	
	ocdiscs = new IntKeyedDbModel(osset.get("ocdiscs"), "entityid");
	logadd(ocdiscs);// = new IntKeyedDbModel(osset.get("ocdiscs"), "entityid"));
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
	str.execSql(
		" delete from rels" +
		" where relid = (select relid from relids where name='headof')\n" +
		" and entityid0 = " + SqlInteger.sql((Integer)this.getKey()));
//	str.execSql("update entities set primaryentityid=entityid" 
//		" where primaryentityid = " + SqlInteger.sql((Integer)this.getKey()));	
}


}
