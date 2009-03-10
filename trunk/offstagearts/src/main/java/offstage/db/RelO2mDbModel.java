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

package offstage.db;

import offstage.school.gui.*;
import citibob.app.App;
import citibob.jschema.SchemaBuf;
import citibob.jschema.SqlBufDbModel;
import citibob.jschema.SqlSchemaInfo;
import citibob.sql.SqlRun;
import citibob.sql.SqlSet;
import citibob.sql.pgsql.SqlInteger;
import citibob.sql.pgsql.SqlString;

/**
 * A Model that gives the "parentof" or "headof" relationship.  Just one row.
 * @author citibob
 */
public class RelO2mDbModel extends SqlBufDbModel
{
	protected String relName;
	protected Integer temporalid, entityid1;
	
	public void setKeys(String relName, Integer temporalid, Integer entityid1)
	{
		this.relName = relName;
		this.temporalid = temporalid;
		this.entityid1 = entityid1;
	}

	public static int getCourseRoleID(App app, String name)
	{
		return app.schemaSet().getEnumInt("enrollments", "courserole", name);
	}
	
	public RelO2mDbModel(SqlRun str, App app) {
		super(str, app, (String[])null, null, null);
	}
	static final int COL_ENTITYID0_NOTNULL = 1;
	static final int COL_ENTITYID1 = 2;
	static final int[] editableCols = {COL_ENTITYID0_NOTNULL};
	public SqlSet getSelectSql(boolean proto) {
		return new SqlSet(
			" select\n" +
			" rels.entityid0,\n" +
			" case when rels.entityid0 is null then e.entityid else rels.entityid0 end as entityid0_notnull,\n" +
			" e.entityid as entityid1,\n" +
			" e.obsolete\n" +
			" from entities e\n" +
			" left outer join rels on (e.entityid = rels.entityid1 " +
				" and rels.temporalid=" + temporalid + ")\n" +
			" left outer join relids on (rels.relid = relids.relid " +
				" and relids.name=" + SqlString.sql(relName) + ")\n" +
			" where e.entityid=" + SqlInteger.sql(entityid1));
	}

	protected boolean doSimpleUpdate(int row, SqlRun str, SqlSchemaInfo qs)
	{
		SchemaBuf sb = (SchemaBuf)sbuf;

		if (sbuf.valueChanged(row, editableCols)) {
			Integer Entityid0 = (Integer)sbuf.getValueAt(row, "entityid0_notnull");
			String sql =  "select w_rels_o2m_set(" +
				"(select relid from relids where name = " + SqlString.sql(relName) + "), " +
				temporalid + ", " + SqlInteger.sql(Entityid0) + ", " + entityid1 + ");";
			str.execSql(sql);
			sbuf.setStatus(row, 0);
			return true;
		} else {
			sbuf.setStatus(row, 0);
			return false;
		}
	}

}
