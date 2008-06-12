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

package offstage.schema;

import citibob.jschema.ConstSqlSchema;
import citibob.jschema.SqlCol;
import citibob.sql.SqlNumeric;
import citibob.sql.pgsql.SqlInteger;
import citibob.sql.pgsql.SqlString;

/**
 *
 * @author citibob
 */
public class TeachersSchema extends ConstSqlSchema
{

public TeachersSchema()
{
	table = "teachers";
	cols = new SqlCol[] {
		new SqlCol(new SqlInteger(false), "entityid", true),
		new SqlCol(new SqlString(30), "displayname"),
		new SqlCol(new SqlNumeric(9,2), "ocpct"),
		new SqlCol(new SqlNumeric(9,2), "hourlyrate"),
		new SqlCol(new SqlNumeric(9,2), "perclassrate")
	};
}
}
