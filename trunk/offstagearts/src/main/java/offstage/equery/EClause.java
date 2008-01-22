/*
OffstageArts: Enterprise Database for Arts Organizations
This file Copyright (c) 2005-2007 by Robert Fischer

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

import java.util.*;


public class EClause
{
// EClause types
public final static int ADD = 1;
public final static int ZERO = 0;
public final static int SUBTRACT = -1;

	public int type = ADD;			// ADD or SUBTRACT; not really public
	protected ArrayList<Element> elements;
	public String name;		// Not really public

	public EClause(String name)
	{
		this.name = name;
		this.elements = new ArrayList();
	}
	public EClause()
		{ this("New Clause"); }
	public Element getElement(int i)
		{ return elements.get(i); }
	public String getName()
		{ return name; }
	public int getType() { return type; }
public ArrayList<Element> getElements() { return elements; }
// ============================================
/** Inserts clause before clause #ix */
public void insertElement(int ix, Element c)
	{ elements.add(ix, c); }
public void removeElement(int ix)
{ elements.remove(ix); }
//public void appendElement(Element c)
//	{ elements.add(c); }

}
