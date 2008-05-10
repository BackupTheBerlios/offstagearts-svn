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
public interface Comp {

/** @param viewName The user's view of this column name */
public String getSql(SqlCol sqlCol, String colName, String viewName, Object value)
throws IOException;

/** Returns the simple name of this comparator that the user sees. */
public String getDisplayName();

/** Returns name of this comparator as used in the XStream file.  Must be unique
 among all comparators. */
public String getSaveName();

}
