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
 * JDate.java
 *
 * Created on May 14, 2003, 8:52 PM
 */

package offstage.swing.typed;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import citibob.exception.*;
import citibob.sql.*;
import citibob.swing.typed.*;
import citibob.app.*;
import citibob.sql.pgsql.*;
import citibob.sql.*;
import citibob.util.IntVal;

public class HouseholdIDEditableLabel extends EntityIDEditableLabel
{

int entityid;		// EntityID of the person having this widget.

void superSetValue(Object o)
{ super.setValue(o); }

/** Called when parent record changes. */
public void setEntityID(int entityid) { this.entityid = entityid; }








/** Called when popup widget's value changes.  Resolve to primary entity id... */
public void propertyChange(java.beans.PropertyChangeEvent evt)
{
	final Object oldval = propertyChangeNoFire(evt);	// Calls our setValue() below.
	if (oldval == null) return;

	// Run final result after we've finished batch set in setValue()
	app.sqlRun().execUpdate(new UpdTasklet2() {
	public void run(SqlRun str) throws Exception {
		Object newval = getValue();
		firePropertyChange("value", oldval, newval);
	}});
}



/** Resolve this to a primary entity id */
public void setValue(Object o)
{
	if (o == null) {
		super.setValue(o);
		return;
	}
	
	// We're trying to emancipate --- allow it always!
	final Integer ID = (Integer)o;
	if (ID.intValue() == entityid) {
		super.setValue(o);
		return;
	}

	// Make sure we're only pointing to a head of household.
//	app.runApp(new BatchRunnable() {
//	public void run(SqlRun str) throws SQLException {
		
	SqlRun str = app.sqlRun();
//	SqlBatchSet str = app.getBatchSet();
	final IntVal ival = offstage.db.DB.getPrimaryEntityID(str, ID);
	str.execUpdate(new UpdTasklet2() {
	public void run(SqlRun str) throws Exception {
		HouseholdIDEditableLabel.super.setValue((Integer)ival.val);
	}});
}
}

