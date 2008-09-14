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
protected SqlSchema schema;
protected String idCol = "groupid";
String orderClause = "groupid";

// EntityPanel...
public String[] displayColTitles;	// Titles to display to user
public String[] displayCols;	// Columns to show to user

//boolean inGui = true;	// Do we display in the devel screen?
//boolean inEQuerySchema = true;
//boolean inSegmentationReport = true;	// True if user can order segmentation by this...

// EQuerySchema...
String[] requiredTables = new String[0];
// Column aliases we add to the user's drop-down; see EQuerySchema.alias
String[] equeryAliases = {};

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
	eschema.addSchema(schema,
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
