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
package offstage.school.gui;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import offstage.swing.typed.FamilySelectorTable;

/**
 * Allows users to select other people from within a family.
 * @author  citibob
 */
public class SchoolFamilySelectorTable extends FamilySelectorTable
{
Integer termid;
int primaryEntityID;

public void setTermID(Integer termid) { this.termid = termid; }
	
public void setPrimaryEntityID(SqlRun str, int primaryEntityID)
{
	this.primaryEntityID = primaryEntityID;
	requery(str);
}

public void requery(SqlRun str)
{
	executeQuery(str,
		" select e.entityid from entities e, termregs tr" +
		" where tr.payerid = " + SqlInteger.sql(primaryEntityID) +
		" and e.entityid = tr.entityid and tr.groupid = " + SqlInteger.sql(termid) +
		" and not e.obsolete",
//		" select pe.entityid from entities_school pe, entities ee, entities_school pq" +
//		" where pq.entityid = " + SqlInteger.sql(primaryEntityID) +
//		" and pe.adultid = pq.adultid" +
//		" and pe.entityid = ee.entityid" +
//		" and not ee.obsolete",
		"isprimary desc, name");
}
	
}
