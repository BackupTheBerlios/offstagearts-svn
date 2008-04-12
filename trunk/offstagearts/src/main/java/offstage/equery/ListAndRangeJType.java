/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.equery;

import citibob.types.JEnumMulti;
import citibob.types.JavaJType;
import offstage.equery.ListAndRange;

/**
 *
 * @author citibob
 */
public class ListAndRangeJType extends JavaJType
{
	public JEnumMulti subJType;
	
	public ListAndRangeJType(JEnumMulti subJType) {
		super(ListAndRange.class);
		this.subJType = subJType;
	}
}
