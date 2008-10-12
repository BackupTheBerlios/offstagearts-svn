/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.datatab;

import citibob.sql.DbChangeModel;
import citibob.sql.SqlRun;
import java.sql.SQLException;
import offstage.schema.InterestsSchema;

/**
 *
 * @author citibob
 */
public class interests_DT extends DataTab
{

public interests_DT(SqlRun str, DbChangeModel change)
throws SQLException
{
	title = "Interests";
	schema = new InterestsSchema(str, change);
	// orderClause = "groupid";
	// idCol = "groupid";
	displayColTitles =
		new String[] {"Interest", "By Person", "Referred By"};
	displayCols = 
		new String[] {"groupid", "byperson", "referredby"};
	equeryAliases = new String[] {
		"interests.groupid", "interests",
	};
	
}
}
