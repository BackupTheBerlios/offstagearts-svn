/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.equery.compare;

import citibob.jschema.SqlCol;
import java.io.IOException;

/**
 *
 * @author citibob
 */
public class BaseComp implements Comp
{

protected String comp;	// sql representation of this comparator

public BaseComp(String sql)
{ this.comp = sql; }

public String getDisplayName() { return comp; }
public String getSaveName() { return comp; }

protected String getSql(SqlCol sqlCol, String colName, Object value)
throws IOException
{
	return colName + " " + comp + " " + " (" + sqlCol.toSql(value) + ")";
}
public String getSql(SqlCol sqlCol, String colName, String viewName, Object value)
throws IOException
{
	return getSql(sqlCol, colName, value);
}

}
