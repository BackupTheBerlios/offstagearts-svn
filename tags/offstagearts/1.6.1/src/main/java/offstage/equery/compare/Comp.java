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
import java.io.IOException;

/**
 *
 * @author citibob
 */
public interface Comp {

/** @param viewName The user's view of this column name */
public String getSql(SqlCol sqlCol, String colName, String viewName, Object value)
throws IOException;

/** Returns the simple name of this comparator that the user sees. */
public String getDisplayName();

/** Returns name of this comparator as used in the XStream file.  Must be unique
 among all comparators. */
public String getSaveName();

}
