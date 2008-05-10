/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.equery;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.StringConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import offstage.equery.compare.Comp;

/**
 *
 * @author citibob
 */
public class CompConverter extends StringConverter
{

QuerySchema schema;

public CompConverter(QuerySchema schema)
	{ this.schema = schema; }

public boolean canConvert(Class type)
	{ return Comp.class.isAssignableFrom(type); }

public String toString(Object source)
{
	Comp comp = (Comp)source;
	return super.toString(comp.getSaveName());
}
public Object fromString(String str)
{
	String saveName = (String)super.fromString(str);
	return schema.getComp(saveName);
}
		 
		 
}
