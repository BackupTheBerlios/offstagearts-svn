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

package offstage.reports;

import citibob.swing.table.JTypeTableModel;
import citibob.swing.table.StringTableModel;
import citibob.text.SFormatMap;
import java.io.IOException;
import offstage.devel.gui.DevelModel;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

/**
 *
 * @author citibob
 */
public class SummaryReport {

//DevelModel dmod;
//SFormatMap sfmap;
//	
//public SummaryReport(DevelModel dmod, SFormatMap sfmap)
//{
//	this.dmod = dmod;
//	this.sfmap = sfmap;
//}
	
	
public static String getHtml(DevelModel dmod, SFormatMap sfmap)
throws IOException
{
//	// Get the StringTemplate...
//	InputStream in = SummaryReport.class.getClassLoader().getResourceAsStream("offstage/reports/summary.stg");
//	StringTemplateGroup stg = new StringTemplateGroup(new InputStreamReader(in));
//	in.close();
//	StringTemplate st = stg.getInstanceOf("summary");

	// Get the StringTemplate...
	StringTemplateGroup stg = new StringTemplateGroup("summaryGroup");
	StringTemplate st = stg.getInstanceOf("offstage/reports/summary");

	// Format the columns...
	StringTableModel sperson = new StringTableModel(dmod.getPersonSb(), sfmap);
	setAttribute(st, "person", dmod.getPersonSb(), sperson);
	
	StringTableModel sphones = new StringTableModel(dmod.getPhonesSb(), sfmap);
	setAttribute(st, "phones", dmod.getPhonesSb(),sphones);
	
	setAttribute(st, "donations", dmod.getDonationSb(), new StringTableModel(dmod.getDonationSb(), sfmap));
	setAttribute(st, "flags", dmod.getFlagSb(), new StringTableModel(dmod.getFlagSb(), sfmap));
	setAttribute(st, "notes", dmod.getNotesSb(), new StringTableModel(dmod.getNotesSb(), sfmap));
	setAttribute(st, "tickets", dmod.getTicketsSb(), new StringTableModel(dmod.getTicketsSb(), sfmap));
	setAttribute(st, "events", dmod.getEventsSb(), new StringTableModel(dmod.getEventsSb(), sfmap));
	setAttribute(st, "terms", dmod.getTermsSb(), new StringTableModel(dmod.getTermsSb(), sfmap));
	setAttribute(st, "interests", dmod.getInterestsSb(), new StringTableModel(dmod.getInterestsSb(), sfmap));
	
	return st.toString();
}

public static void setAttribute(StringTemplate st, String var, JTypeTableModel _val, StringTableModel val)
{
	
	for (int row=0; row<val.getRowCount(); ++row) {
		StringTemplate.Aggregate map = new StringTemplate.Aggregate();
		for (int col=0; col<val.getColumnCount(); ++col) {
			map.put(val.getColumnName(col), val.getValueAt(row, col));

			// Make an existence flag
			String rawName = "_" + val.getColumnName(col);
			Object rawVal = _val.getValueAt(row, col);
			if (rawVal instanceof Boolean) {
				map.put(rawName, rawVal);
			} else if (rawVal == null) {
				map.put(rawName, Boolean.FALSE);
				// Don't need to add
			} else {
				map.put(rawName, Boolean.TRUE);
			}
//			map.put("_" + val.getColumnName(col), _val.getValueAt(row, col));
		}
		st.setAttribute(var, map);
	}
	
	// Set the count attribute
	st.setAttribute("_" + var, val.getRowCount() > 0);
	
//	HashMap map = new HashMap();
//	for (int col=0; col<val.getColumnCount(); ++col) {
//		List list = new ArrayList(val.getRowCount());
//		for (int row=0; row<val.getRowCount(); ++row) {
//			list.add(val.getValueAt(row, col));
//		}
//		map.put(val.getColumnName(col), list);
//	}
//	st.setAttribute(var, map);
	
//	for (int row=0; row<val.getRowCount(); ++row) {
//		HashMap map = new HashMap();
//		for (int col=0; col<val.getColumnCount(); ++col) {
//			map.put(val.getColumnName(col), val.getValueAt(row, col));
//		}
//		st.setAttribute(var, map);
//	}
	

//	for (int col=0; col<val.getColumnCount(); ++col) {
//		String colName = var + ".{" + val.getColumnName(col) + "}";
//		for (int row=0; row<val.getRowCount(); ++row) {
//			st.setAttribute(colName, val.getValueAt(row, col));
//		}
//	}
}



}
