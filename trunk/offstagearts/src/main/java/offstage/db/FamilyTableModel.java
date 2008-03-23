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

package offstage.db;

import citibob.jschema.*;
import citibob.swing.*;
import java.sql.*;
import citibob.sql.*;
import citibob.task.*;
import citibob.app.*;

/**
 *
 * @author citibob
 */
public class FamilyTableModel extends EntityListTableModel
implements RowModel.ColListener //, SchemaRowModelBindable
{

/** Creates a new instance of FamilyTableModel
 @param runner should be a raw runner (for db, not app). */
public FamilyTableModel(App app) {//ActionRunner runner, SqlTypeSet tset) {
	super(app.sqlTypeSet());
	this.app = app;
}

int primaryCol;
SchemaRowModel bufRow;
App app;
// --------------------------------------------------------------
public void setValue(SqlRun str, int primaryEntityID)
//throws SQLException
{
	// Now do the read-only stuff
	String idSql =
		" select pe.entityid from entities pe" +
		" where pe.primaryentityid = " + primaryEntityID +
		" and not obsolete";
	String orderBy = "isprimary desc, relation, name";
	
//System.out.println("*****************\n" + sql);
	// ResultSet rs = st.executeQuery(sql);
	setIdSql(idSql, orderBy);
	executeQuery(str);
}
// ===============================================================
/** Binds this widget to listen/edit a particular column in a RowModel, using the type for that column derived from the associated Schema.  NOTE: This requires a correspondence in the numbering of columns in the Schema and in the RowModel.  No permutions inbetween are allowed!  This should not be a problem, just make sure the TableRowModel binds DIRECTLY to the source SchemaBuf, not to some permutation thereof. */
public void bind(SchemaRowModel bufRow)
{
	primaryCol = bufRow.getSchema().findCol("primaryentityid");

	/** Bind as a listener to the RowModel (which fronts a SchemaBuf)... */
	this.bufRow = bufRow;
	bufRow.addColListener(primaryCol, this);
}
// ===============================================================
// Implementation of RowModel.Listener

/** Propagate data from underlying model to widget. */
public void valueChanged(final int col)
{
		app.sqlRun().pushFlush();
		Integer Primaryid = (Integer)bufRow.get(primaryCol);
		if (Primaryid == null) {
			setRowCount(0);		// Just clear it out...
		} else {
			int primaryid = Primaryid.intValue();
System.out.println("FamilyTableModel: value changed to: " + primaryid);
			setValue(app.sqlRun(), primaryid);
		}
		app.sqlRun().popFlush();
}
public void curRowChanged(int col)
{
	int row = bufRow.getCurRow();
	if (row == MultiRowModel.NOROW) setRowCount(0);
	else valueChanged(col);
}
	
}
