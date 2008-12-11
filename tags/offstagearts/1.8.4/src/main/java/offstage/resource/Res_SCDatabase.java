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

package offstage.resource;

import citibob.resource.DbbCreator;
import citibob.resource.DbbResource;
import citibob.resource.DbbUpgrader;
import citibob.resource.ResSet;
import citibob.sql.ConnPool;
import citibob.sql.SqlRun;
import java.sql.Connection;

/**
 *
 * @author citibob
 */
public class Res_SCDatabase extends DbbResource
{

public Res_SCDatabase(ResSet rset)
{
	super(rset, "general", "scdatabase.sql");

	add(new DbbCreator(this, 1));
//	add(new DbbUpgrader(this, 1, 45, true));
}
	
}
