/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.equery;

import java.util.List;

/**
 * Used on the right hand side of the "in" and "not in" comparators
 * for JEnumMulti.  Specifies that between min and max (inclusive) of the specified
 * elements should match.  Special cases: (min=null ==> at most max).  (max=null ==> at least min).
 * (min=null,max=null => at least 1).
 * @author citibob
 */
public class ListAndRange {

	public List list;
	public Integer min;
	public Integer max;
}
