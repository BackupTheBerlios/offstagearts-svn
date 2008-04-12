/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.equery.compare;

import citibob.jschema.SqlCol;
import citibob.sql.ansi.SqlString;
import java.io.File;
import java.io.IOException;
import offstage.equery.EQuery;

/**
 *
 * @author citibob
 */
public class String_InComp extends BaseComp
{

boolean positive;

public String_InComp(boolean positive)
{
	super(positive ? "in" : "not in");
	this.positive = positive;
}

public String getSaveName() { return "String." + super.getSaveName(); }

public String getSql(SqlCol sqlCol, String colName, String viewName, Object value)
throws IOException
{
	// Handle in lists for strings
	StringBuffer sql = new StringBuffer(colName + " " + comp + " (");
	String[] ll = ((String)(value)).trim().split(",");
	if (ll.length == 0) return "false";
	for (int i=0; ;) {
		sql.append(SqlString.sql(ll[i].trim()));
		if (++i >= ll.length) {
			sql.append(")");
			break;
		}
		sql.append(",");
	}
	return sql.toString();
}

	
}
