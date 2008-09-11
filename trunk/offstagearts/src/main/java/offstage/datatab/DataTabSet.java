/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.datatab;

import citibob.sql.SqlRun;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import offstage.FrontApp;

/**
 *
 * @author citibob
 */
public class DataTabSet {

Map<String,DataTab> tabMap;

public Collection<DataTab> allTabs() { return tabMap.values(); }
public List<DataTab> guiTabs = new ArrayList();
public List<DataTab> equeryTabs = new ArrayList();
public List<DataTab> segmentationReportTabs = new ArrayList();

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
}

public void init(SqlRun str, FrontApp app)
throws Exception
{
	// First, set up all the tabs individually
	addTab(new interests_DT(str, app.dbChange()));
	
	// Now add them to lists
	addAllLists("interests");
}

}
