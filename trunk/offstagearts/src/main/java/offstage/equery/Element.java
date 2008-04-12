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
package offstage.equery;

import citibob.types.JType;
import offstage.equery.compare.Comp;

public class Element
{
	ColName colName;
	private Comp comparator;
	//private String comparator;
	public Object value;
	public transient JType cachedValueType;		// Type we determined for this element, if nothing has changed...
	
	public Element(ColName colName, Comp comparator, Object value)
	{
		this.colName = colName;
		this.comparator = comparator;
		this.value = value;
	}
	public Element()
	{ }

	public Comp getComparator() { return comparator; }
	public void setComparator(Comp comparator)
	{
		this.comparator = comparator;
		cachedValueType = null;
	}
	public ColName getColName() { return colName; }
	public void setColName(ColName colName)
	{
		this.colName = colName;
		cachedValueType = null;
	}
}
