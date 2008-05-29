/*
OffstageArts: Enterprise Database for Arts Organizations
This file Copyright (c) 2005-2008 by Robert Fischer

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
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
