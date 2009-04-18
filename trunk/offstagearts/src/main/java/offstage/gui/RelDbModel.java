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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.gui;

import offstage.school.gui.*;
import citibob.app.App;
import citibob.jschema.SchemaBuf;
import citibob.jschema.SqlBufDbModel;
import citibob.sql.SqlRun;
import citibob.sql.SqlSet;
import citibob.sql.pgsql.SqlInteger;
import offstage.db.DB;

/**
 *
 * @author citibob
 */
public class RelDbModel extends SqlBufDbModel
{
private String temporalIdSql;
private String relIdSql;		// Only display these relationships
private Integer entityid;			// Only display rels involving this entityid

public void setTemporalIdSql(String temporalIdSql) {
	if (temporalIdSql == null) temporalIdSql = "select -1 as id";
	this.temporalIdSql = temporalIdSql;
}
public void setRelIdSql(String relIdSql) {
	if (relIdSql == null) relIdSql = "select relid as id from relids";
	this.relIdSql = relIdSql;
}
public void setEntityID(Integer entityid) {
	this.entityid = entityid;
}
public Integer getEntityID()
	{ return entityid; }

public RelDbModel(SqlRun str, App app) {
	super();
	init(str, app,
		new String[] {"rels"},
		null,
		new String[] {"rels"});
}

public SqlSet getSelectSql(boolean proto) {
	return new SqlSet(
		" select r.*,\n" +
		" rclass.relname as tablename,\n" +
		" relids.reltype,req0,req1,\n" +
		" e0.obsolete as obsolete0, e1.obsolete as obsolete1,\n" +
		" case when e0.entityid = " + entityid + " then 'this' else\n" +
		DB.nameColSql("e0") + "end as name0,\n" +
		"  relids.name as relname,\n" +
		" case when e0.entityid = " + entityid + " then 'this' else\n" +
		DB.nameColSql("e1") + "end as name1\n" +
		" from rels r\n" +
		" inner join pg_class rclass on r.tableoid = rclass.oid\n" +
		" inner join relids on r.relid = relids.relid\n" +
		" inner join entities e0 on e0.entityid = r.entityid0\n" +
		" inner join entities e1 on e1.entityid = r.entityid1\n" +
		" where r.temporalid in (" + temporalIdSql + ")\n" +
		" and r.relid in (" + relIdSql + ")\n" +
		" and (r.entityid0 = " + entityid + " or r.entityid1 = " + entityid + ")");
}
// =====================================================================
//public void setRel_o2m(SqlRun str, String srelid, String stemporalid,
//int entityid0, int entityid1)
//{
//	str.execSql(
//		" select w_rels_o2m_set(" +
//		srelid + ", " + stemporalid + ", " + entityid0 + ", " + entityid1 + ");");
//	doSelect(str);
//}


}
