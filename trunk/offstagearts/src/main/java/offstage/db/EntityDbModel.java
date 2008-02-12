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
/*
 * EntityDbModel.java
 *
 * Created on February 19, 2006, 12:31 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.db;

import java.sql.*;
import citibob.sql.*;
import citibob.jschema.*;
import citibob.swing.table.*;
import citibob.app.*;
import citibob.sql.pgsql.*;

/**
 *
 * @author citibob
 */
public class EntityDbModel extends IntKeyedDbModel
{
	/** Creates a new instance of EntityDbModel */
	public EntityDbModel(SqlSchema schema, App app) {
		super(new EntityBuf(schema), "entityid", null, new IntKeyedDbModel.Params(false));
	}

	/** Override insert stuff */
	public void doInsert(SqlRunner str)
	{
		SchemaBuf sb = this.getSchemaBuf();
		Integer Entityid = (Integer)sb.getValueAt(0, "entityid");
	
		// Set family to self if user hasn't set the family otherwise already.
		int pei = sb.findColumn("primaryentityid");
		if (sb.getValueAt(0, pei) == null) sb.setValueAt(Entityid, 0, pei);
		
		// Now do the insert query!
		super.doInsert(str);
	}

}
