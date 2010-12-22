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

package offstage.datatab;

import citibob.sql.DbChangeModel;
import citibob.sql.SqlRun;
import java.sql.SQLException;
import offstage.schema.InterestsSchema;

/**
 * Kept around for compatibility with old site code.
 * @author citibob
 */
@Deprecated
public class interests_DT extends DataTab
{
public interests_DT(SqlRun str, DbChangeModel change)
throws SQLException
{
	title = "Interests";
	schema = new InterestsSchema(str, change);
	// orderClause = "groupid";
	// idCol = "groupid";
	displayColTitles =
		new String[] {"Interest", "By Person", "Referred By"};
	displayCols = 
		new String[] {"groupid", "byperson", "referredby"};
	equeryAliases = new String[] {
		"interests.groupid", "interests",
	};
	summary_st =
		"<table>\n" +
		"$interests:{it |\n" +
		"<tr><td>$it.groupid$</td></tr>\n" +
		"}$\n" +
		"</table>\n";
	
}
}
