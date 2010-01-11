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

package offstage.datatab;

import offstage.devel.gui.*;
import citibob.jschema.IntKeyedDbModel;
import citibob.jschema.SqlSchema;
import citibob.jschema.log.QueryLogger;
import citibob.sql.SqlRun;
import citibob.swing.typed.SwingerMap;
import javax.swing.JTabbedPane;
import offstage.equery.EQuerySchema;
import offstage.gui.GroupPanel;

/**
 *
 * @author citibob
 */
public class DataTab //implements Comparable<DataTab>
{

protected String title;		// The name to give the tab in the GUI

/** StringTemplate snipped used to format this table in the
 * summary.st main HTML summary screen.
 If NULL, then don't format in HTML.
 eg:
 
 $notes:{it |
<p><b>$it.date$</b>: $it.note$</p>
}$
 
 or
 
 <table>
<tr><th>Type</th><th>Date</th><th>Amount</th></tr>
$donations:{it |
<tr><td><b>$it.groupid$</b></td><td>$it.date$</td><td align="right">$it.amount$</td></tr>
}$
</table>

 */
protected String summary_st = null;

/** StringTemplate snipped used to format this DataTab in the
 * fdSummary.st Front Desk HTML summary screen. */
//protected String fdSummary_st = null;

protected SqlSchema schema;
protected String idCol = "groupid";
protected String orderClause = "groupid";

// EntityPanel...
public String[] displayColTitles;	// Titles to display to user
public String[] displayCols;	// Columns to show to user

//boolean inGui = true;	// Do we display in the devel screen?
//boolean inEQuerySchema = true;
//boolean inSegmentationReport = true;	// True if user can order segmentation by this...

// EQuerySchema...
protected String[] requiredTables = new String[0];
// Column aliases we add to the user's drop-down; see EQuerySchema.alias
protected String[] equeryAliases = {};

public String[] getAliases() { return equeryAliases; }
public SqlSchema getSchema()
	{ return schema; }
public String getOrderClause()
	{ return orderClause; }
// SummaryReport...
//boolean isInSummaryReport;
//int order = 0;		// Ordering of tabs in the summary report

/** Override this for complex/unusual cases */
public void addToEQuerySchema(EQuerySchema eschema)
{
	eschema.addSchema(schema, null,
		getTableName() + ".entityid = main.entityid");
}

public GroupPanel addToGroupPanels(SqlRun str, DevelModel dm,
JTabbedPane groupPanels, SwingerMap smap)
{
	GroupPanel panel = new GroupPanel();
	panel.initRuntime(str, dm.getTabSb(getTableName()),
		displayColTitles, displayCols,
		true, smap);
	groupPanels.insertTab(getTitle(), null, panel, null,
		groupPanels.getTabCount());	
	return panel;
}

public IntKeyedDbModel newDbModel(QueryLogger logger)
{
	IntKeyedDbModel dbModel = new IntKeyedDbModel(schema, "entityid");
	dbModel.setOrderClause(orderClause);
	dbModel.setLogger(logger);
	return dbModel;
}

/** @returns name of underlying database table */
public String getTableName()
	{ return schema.getDefaultTable(); }
public String getIDCol()
	{ return idCol; }
public String getTitle()
	{ return title; }
/** @return Portion of String Template used to display this DataTab
 * in HTML summary.
 * Returns null if it is not to be displayed in HTML summary.
 */
public String getSummary_st()
	{ return summary_st; }
//public DbModel getDbModel()
//	{ return dbModel; }
//
//public SchemaBuf getSchemaBuf()
//	{ return dbModel.getSchemaBuf(); }


/** Returns name of the resource (in classloader)
 * for this tab's portion of SummaryReport.  For
 use in StringTemplateGroup.getInstanceOf(). */
public String getSummaryReportSTPath()
{
	
	return getClass().getPackage().getName().replace('.', '/') + "/" +
		getTableName() + "_SummaryReport.st";
}


//public int compareTo(DataTab b)
//{
//	int cmp = order - b.order;
//	if (cmp != 0) return cmp;
//	return getTitle().compareTo(b.getTitle());
//}
}
