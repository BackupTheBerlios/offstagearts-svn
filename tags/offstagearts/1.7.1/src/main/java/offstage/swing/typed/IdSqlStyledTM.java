/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.swing.typed;

import citibob.swing.table.DelegateStyledTM;
import citibob.swing.typed.SwingerMap;

/**
 *
 * @author citibob
 */
public class IdSqlStyledTM extends DelegateStyledTM
{
	public IdSqlStyledTM(SwingerMap smap)
	{
		super(new IdSqlTableModel());
		super.setColumns(smap,
			"name", "Name", false, null);
	}
	public boolean isEditable(int row, int col) { return false; }
//	setValueColU("entityid");
}
