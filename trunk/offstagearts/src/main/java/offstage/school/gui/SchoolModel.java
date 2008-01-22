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
 * SchoolModel.java
 *
 * Created on December 9, 2007, 5:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.school.gui;

import citibob.app.App;
import citibob.jschema.IntKeyedDbModel;
import citibob.jschema.IntsKeyedDbModel;
import citibob.jschema.SchemaBufRowModel;

/**
 *
 * @author citibob
 */
public class SchoolModel extends SchoolModelMVC
{

int termID;

public StudentDbModel studentDm;
	public SchemaBufRowModel studentRm;
	public SchemaBufRowModel schoolRm;
public IntsKeyedDbModel termregsDm;
	public SchemaBufRowModel termregsRm;
public PayerDbModel payerDm;
//public HouseholdDbModel householdDm;
public ParentDbModel parentDm;
public ParentDbModel parent2Dm;

// -------------------------------------------------------------

public SchoolModel(App fapp)
{
	studentDm = new StudentDbModel(fapp);
	payerDm = new PayerDbModel(fapp);
//	householdDm = new HouseholdDbModel(fapp);
	parentDm = new ParentDbModel(fapp);
	parent2Dm = new ParentDbModel(fapp);
	termregsDm = new IntsKeyedDbModel(fapp.getSchema("termregs"), new String[] {"groupid", "entityid"}, true);

}

public void setTermID(Integer TermID)
	{ setTermID(TermID == null ? -1 : TermID.intValue()); }
public void setTermID(int newTermID)
{
	int oldTermID = termID;
	this.termID = newTermID;
	if (oldTermID != termID) fireTermIDChanged(oldTermID, termID);
}
public int getTermID()
	{ return termID; }

}
