/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.equery;

import com.thoughtworks.xstream.converters.basic.StringConverter;

/**
 *
 * @author citibob
 */
public class ColNameConverter extends StringConverter
{

//QuerySchema schema;
//
//public ColNameConverter(QuerySchema schema)
//	{ this.schema = schema; }

public boolean canConvert(Class type)
	{ return ColName.class.isAssignableFrom(type); }

public String toString(Object source)
{
	ColName ColName = (ColName)source;
	return super.toString(ColName.toString());
}
public Object fromString(String str)
{
	String saveName = (String)super.fromString(str);
	return new ColName(saveName);
}
		 
		 
}
