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
 * FamilyTable.java
 *
 * Created on March 19, 2005, 12:00 AM
 */

package offstage.gui;

import citibob.app.App;
import javax.swing.*;
import javax.swing.table.*;
import citibob.swing.*;
import citibob.swing.table.*;
import citibob.swingers.JStringSwinger;

/**
 *
 * @author citibob
 */
public class FamilyTable extends citibob.swing.typed.SingleSelectStyledTable {

public void initRuntime(JTypeTableModel family)
{
	DelegateStyledTM stm = new DelegateStyledTM(family);
	stm.setColumns(null,
		"name", "Name", false, new JStringSwinger());
	super.setStyledTM(stm);
//	
//	super.set
//	super.setModelU(family, )
//	ColPermuteTableModel model = new ColPermuteTableModel(
//		family,
//		new String[] {"Name"},
//		new String[] {"name"}); //, new boolean[] {false});
//	setModel(model);
}

//public Component prepareRenderer(TableCellRenderer renderer,
//								 int rowIndex, int vColIndex) {
//	Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
//	if (c instanceof JComponent) {
//		JComponent jc = (JComponent)c;
//		jc.setToolTipText((String)getValueAt(rowIndex, vColIndex));
//	}
//	return c;
//}

}
