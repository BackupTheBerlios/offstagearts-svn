/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.equery.compare;

import citibob.jschema.SqlCol;
import java.io.File;
import java.io.IOException;
import offstage.equery.EQuery;

/**
 *
 * @author citibob
 */
public class InFileComp extends BaseComp
{

boolean positive;

public InFileComp(boolean positive)
{
	super(positive ? "in file" : "not in file");
	this.positive = positive;
}

public String getSql(SqlCol sqlCol, String colName, String viewName, Object value)
throws IOException
{
	String vals = EQuery.readCSVColumn((File)value, viewName, sqlCol.getType());
	if (vals == null || "".equals(vals)) {
		return "false";
	} else {
		String sql = (positive ? "in" : "not in");
		return colName + " " + sql + " (" + vals + ")";
	}
}

	
}
