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

package offstage.devel.gui;

import java.text.DateFormat;
import java.util.Date;
import javax.swing.*;
import java.awt.event.*;

import citibob.swing.typed.*;
import citibob.exception.*;
import citibob.jschema.*;
import citibob.types.*;
import citibob.swing.*;

/**
 *
 * @author  citibob
 */
public class IsPrimaryBinder
implements RowModel.ColListener
{

SchemaRowModel bufRow;
int entityidCol, primaryCol;
TypedWidget tw;

// --------------------------------------------------------------
protected void finalize()
{
	bufRow.removeColListener(entityidCol, this);
	bufRow.removeColListener(primaryCol, this);
	bufRow = null;
}
// --------------------------------------------------------------
/** Binds this widget to listen/edit a particular column in a RowModel,
 using the type for that column derived from the associated Schema.
 NOTE: This requires a correspondence in the numbering of columns in
 the Schema and in the RowModel.  No permutions inbetween are allowed!
 This should not be a problem, just make sure the TableRowModel binds DIRECTLY
 to the source SchemaBuf, not to some permutation thereof. */
public void bind(TypedWidget tw, SchemaRowModel bufRow)
{
	this.tw = tw;
	entityidCol = bufRow.getSchema().findCol("entityid");
	primaryCol = bufRow.getSchema().findCol("primaryentityid");

	/** Bind as a listener to the RowModel (which fronts a SchemaBuf)... */
	this.bufRow = bufRow;
	bufRow.addColListener(entityidCol, this);
	bufRow.addColListener(primaryCol, this);
}


// ===============================================================
// Implementation of RowModel.Listener

/** Propagate data from underlying model to widget. */
public void valueChanged(int col)
{
	Integer Entityid = (Integer)bufRow.get(entityidCol);
	Integer Primaryid = (Integer)bufRow.get(primaryCol);
	if (Entityid == null || Primaryid == null) {
		// New record; assume primary for now
		tw.setValue(Boolean.TRUE);
	} else {
		int entityid = Entityid.intValue();
		int primaryid = Primaryid.intValue();
		tw.setValue(entityid == primaryid ? Boolean.TRUE : Boolean.FALSE);
	}
}
public void curRowChanged(int col)
{
	int row = bufRow.getCurRow();
	if (row == MultiRowModel.NOROW) tw.setValue(null);
	else valueChanged(col);
//	setEnabled(row != MultiRowModel.NOROW);
//	setValue(row == MultiRowModel.NOROW ? null : bufRow.get(col));
}

}
