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

package offstage.equery.compare;

import citibob.jschema.SqlCol;
import java.io.File;
import java.io.IOException;
import offstage.equery.EQuery;

/**
 *
 * @author citibob
 */
public class InFileComp extends BaseComp
{

boolean positive;

public InFileComp(boolean positive)
{
	super(positive ? "in file" : "not in file");
	this.positive = positive;
}

public String getSql(SqlCol sqlCol, String colName, String viewName, Object value)
throws IOException
{
	String vals = EQuery.readCSVColumn((File)value, viewName, sqlCol.getType());
	if (vals == null || "".equals(vals)) {
		return "false";
	} else {
		String sql = (positive ? "in" : "not in");
		return colName + " " + sql + " (" + vals + ")";
	}
}

	
}
