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
import citibob.sql.ansi.SqlInteger;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import offstage.equery.ListAndRange;

/**
 *
 * @author citibob
 */
public class JEnum_InComp extends BaseComp
{

boolean positive;

public JEnum_InComp(boolean positive)
{
	super(positive ? "in" : "not in");
	this.positive = positive;
}

public String getSaveName() { return "JEnum." + super.getSaveName(); }

private void getSqlSum(StringBuffer sql, String colName, List list)
{
	Iterator ii = list.iterator();
	for (;;) {
		Object obj = ii.next();
		Integer ix = (Integer)obj;

		sql.append("case when " + colName + " = " + SqlInteger.sql(ix) + " then 1 else 0 end");
		if (!ii.hasNext()) break;
		sql.append("\n+");
	}
}

public String getSql(SqlCol sqlCol, String colName, String viewName, Object value)
throws IOException
{
	StringBuffer sql = new StringBuffer();
//	ListAndRange lar = (ListAndRange)value;
//	List list = lar.list;
	List list = (List)value;
//	if (lar == null || lar.list == null || list.size() == 0)
	if (list.size() == 0)
		return (positive ? "false" : "true");
	

//	if (lar.min == null && lar.max == null) {
		// Standard SQL "in"
		sql.append(colName + " " + comp + " (");	
		Iterator ii = list.iterator();
		for (;;) {
			Object obj = ii.next();
			Integer ix = (Integer)obj;
			sql.append(SqlInteger.sql(ix));
			if (!ii.hasNext()) break;
			sql.append(",");
		}
		sql.append(")");
//	} else {
//		// Need to make a counted "clause"
//		if (!positive) sql.append("not ");
//		sql.append("(");
//			if (lar.min == null) {
//				getSqlSum(sql, colName, lar.list);
//				sql.append(" <= " + lar.max);
//			} else if (lar.max == null) {
//				getSqlSum(sql, colName, lar.list);
//				sql.append(" >= " + lar.min);
//			} else {
//				getSqlSum(sql, colName, lar.list);
//				sql.append(" >= " + lar.min + " and ");
//				getSqlSum(sql, colName, lar.list);
//				sql.append(" <= " + lar.max);
//
//			}
//		sql.append(")");
//	}
	return sql.toString();
}

	
}
