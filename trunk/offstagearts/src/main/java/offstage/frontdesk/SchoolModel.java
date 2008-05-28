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
 * SchoolModel.java
 *
 * Created on December 9, 2007, 5:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.frontdesk;

import offstage.school.gui.*;
import citibob.app.App;
import citibob.jschema.IntKeyedDbModel;
import citibob.jschema.IntsKeyedDbModel;
import citibob.jschema.SchemaBufRowModel;
import citibob.sql.SqlRun;
import offstage.school.tuition.RBPlanSet;

/**
 *
 * @author citibob
 */
public class SchoolModel extends SchoolModelMVC
{

int termID;

public StudentDbModel studentDm;
	public SchemaBufRowModel studentRm;
//	public SchemaBufRowModel schoolRm;
IntKeyedDbModel oneTermDm;
	public SchemaBufRowModel oneTermRm;
//	public RBPlanSet rbPlanSet;
public IntsKeyedDbModel termregsDm;
	public SchemaBufRowModel termregsRm;
public IntsKeyedDbModel payertermregsDm;
	public SchemaBufRowModel payertermregsRm;	
public PayerDbModel payerDm;
	public SchemaBufRowModel payerRm;
//public HouseholdDbModel householdDm;
public ParentDbModel parent1Dm;
	public SchemaBufRowModel parent1Rm;
public ParentDbModel parent2Dm;
	public SchemaBufRowModel parent2Rm;

// -------------------------------------------------------------
App app;
public SchoolModel(App fapp)
{
	this.app = fapp;
	studentDm = new StudentDbModel(fapp);
		studentRm = new SchemaBufRowModel(studentDm.personDb.getSchemaBuf());
	payerDm = new PayerDbModel(fapp);
		payerRm = new SchemaBufRowModel(payerDm.personDb.getSchemaBuf());
	parent1Dm = new ParentDbModel(fapp);
		parent1Rm = new SchemaBufRowModel(parent1Dm.personDb.getSchemaBuf());
	parent2Dm = new ParentDbModel(fapp);
		parent2Rm = new SchemaBufRowModel(parent2Dm.personDb.getSchemaBuf());
	oneTermDm = new IntKeyedDbModel(fapp.getSchema("termids"),
		"groupid", fapp.dbChange());
	oneTermDm.setDoInsertKeys(false);
		oneTermRm = new SchemaBufRowModel(oneTermDm.getSchemaBuf());
	termregsDm = new IntsKeyedDbModel(fapp.getSchema("termregs"),
		new String[] {"groupid", "entityid"}, true);
		termregsRm = new SchemaBufRowModel(termregsDm.getSchemaBuf());

	payertermregsDm = new IntsKeyedDbModel(fapp.getSchema("payertermregs"),
		new String[] {"termid", "entityid"}, true);
		payertermregsRm = new SchemaBufRowModel(payertermregsDm.getSchemaBuf());

}

public void setTermID(Integer TermID)
	{ setTermID(TermID == null ? -1 : TermID.intValue()); }
public void setTermID(int newTermID)
{
	int oldTermID = termID;
	this.termID = newTermID;
	
	SqlRun str = app.sqlRun();
	oneTermDm.doUpdate(str);
	oneTermDm.setKey(termID);
	oneTermDm.doSelect(str);
			
	if (oldTermID != termID) fireTermIDChanged(oldTermID, termID);
}
public int getTermID()
	{ return termID; }

public Integer getStudentID()
	{ return (Integer)studentRm.get("entityid"); }
public Integer getPayerID()
	{ return (Integer)termregsRm.get("payerid"); }
public Integer getParent1ID()
	{ return (Integer)studentRm.get("parent1id"); }
public Integer getParent2ID()
	{ return (Integer)studentRm.get("parent2id"); }

}
