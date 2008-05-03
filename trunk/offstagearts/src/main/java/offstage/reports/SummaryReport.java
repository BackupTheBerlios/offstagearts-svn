/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.reports;

import citibob.reports.StringTableModel;
import citibob.text.SFormatMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import offstage.devel.gui.DevelModel;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

/**
 *
 * @author citibob
 */
public class SummaryReport {

public static void setAttribute(StringTemplate st, String var, StringTableModel val)
{
	for (int col=0; col<val.getColumnCount(); ++col) {
		String colName = var + "." + val.getColumnName(col);
		for (int row=0; row<val.getRowCount(); ++row) {
			st.setAttribute(colName, val.getValueAt(row, col));
		}
	}
}

public static String getHtml(DevelModel dmod, SFormatMap sfmap)
throws IOException
{
	// Get the StringTemplate...
	InputStream in = SummaryReport.class.getClassLoader().getResourceAsStream("offstage/reports/summary.stg");
	StringTemplateGroup stg = new StringTemplateGroup(new InputStreamReader(in));
	in.close();
	StringTemplate st = stg.getInstanceOf("summary");
	
	// Format the columns...
	StringTableModel sperson = new StringTableModel(dmod.getPersonSb(), sfmap);
	setAttribute(st, "person", sperson);
	
	StringTableModel sphones = new StringTableModel(dmod.getEntitySb(), sfmap);
	setAttribute(st, "phones", sphones);

	return st.toString();
}


}
