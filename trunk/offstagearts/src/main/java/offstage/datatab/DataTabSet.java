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

import citibob.jschema.BaseSchemaSet;
import citibob.sql.SqlRun;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import offstage.FrontApp;

/**
 *
 * @author citibob
 */
public class DataTabSet {

Map<String,DataTab> tabMap = new TreeMap();

public Collection<DataTab> allTabs() { return tabMap.values(); }
public List<DataTab> guiTabs = new ArrayList();
public List<DataTab> equeryTabs = new ArrayList();
public List<DataTab> segmentationReportTabs = new ArrayList();
public List<DataTab> mergeTabs = new ArrayList();

public DataTab get(String tableName)
	{ return tabMap.get(tableName); }

//public Iterator<DataTab> iterator()
//	{ return tabMap.values().iterator(); }
//public Iterator<DataTab> guiIterator()
//	{ return guiTabs.iterator(); }
//public Iterator<DataTab> equeryIterator()
//	{ return equeryTabs.iterator(); }
//public Iterator<DataTab> segmentationReportIterator()
//	{ return segmentationReportTabs.iterator(); }

public void addToSchemaSet(BaseSchemaSet sset)
{
	for (DataTab tab : allTabs()) {
		sset.add(tab.getSchema());
	}
}

protected void addTab(DataTab tab)
{ tabMap.put(tab.getTableName(), tab); }

protected void setList(List<DataTab> list, String... tableNames)
{
	list.clear();
	addList(list, tableNames);
}

protected void addList(List<DataTab> list, String... tableNames)
{
	for (String tn : tableNames) {
		DataTab tab = get(tn);
		if (tab == null) throw
			new NullPointerException("Unknown DataTab named " + tn);
		list.add(tab);
	}
}

protected void addAllLists(String... tableNames)
{
	addList(guiTabs, tableNames);
	addList(equeryTabs, tableNames);
	addList(segmentationReportTabs, tableNames);
	addList(mergeTabs, tableNames);
}

public void init(SqlRun str, FrontApp app)
throws Exception
{
	// First, set up all the tabs individually
	addTab(new donations_DT(str, app));
	addTab(new events_DT(str, app));
	addTab(new flags_DT(str, app));
	addTab(new interests_DT(str, app.dbChange()));
	addTab(new notes_DT(str, app));
	addTab(new termenrolls_DT(str, app));
	addTab(new ticketeventsales_DT(str, app));
	addTab(new memberships_DT(str,app));
	addTab(new constitis_DT(str,app));
	addTab(new activities_DT(str,app));
	
	// Now add them to lists
	addAllLists("donations");
	addAllLists("events");
	addAllLists("notes");
	addAllLists("ticketeventsales");
	addAllLists("interests");
	addAllLists("termenrolls");
	addAllLists("flags");
	addAllLists("memberships");
	addAllLists("activities");
	addAllLists("constits");
}

}
