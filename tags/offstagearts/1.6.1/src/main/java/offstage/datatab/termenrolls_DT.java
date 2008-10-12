/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.datatab;

import citibob.app.App;
import citibob.sql.SqlRun;
import citibob.swing.typed.SwingerMap;
import java.sql.SQLException;
import javax.swing.JTabbedPane;
import offstage.devel.gui.DevelModel;
import offstage.gui.GroupPanel;
import offstage.schema.TermenrollsSchema;

/**
 *
 * @author citibob
 */
public class termenrolls_DT extends DataTab
{

public termenrolls_DT(SqlRun str, App app)
throws SQLException
{
	title = "Terms";
	schema = new TermenrollsSchema(str, app.dbChange());
	orderClause = "firstdate desc,name";
	displayColTitles = new String[] {"Term", "Role"};
	displayCols = new String[] {"groupid", "courserole"};
	equeryAliases = new String[] {
		"termenrolls.groupid", "terms",
		"termenrolls.courserole", "termrole",
	};
}

public GroupPanel addToGroupPanels(SqlRun str, DevelModel dm,
JTabbedPane groupPanels, SwingerMap smap)
{
	GroupPanel panel = super.addToGroupPanels(str, dm, groupPanels, smap);
	panel.setEnabled(false);
	return panel;
}
}
