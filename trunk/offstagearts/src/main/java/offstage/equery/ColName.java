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

public class ColName
{
	private String stable;
	private String scol;
	public String getTable() { return stable; }
	public String getSCol() { return scol; }

	public ColName(String table, String col)
	{
		stable = table;
		scol = col;
	}
	public ColName(String fullName)
	{
		int dot = fullName.indexOf('.');
		if (dot < 0) return;
		stable = fullName.substring(0,dot);
		scol = fullName.substring(dot+1);
	}
	public String toString()
		{ return (stable + "." + scol); }
	
	public boolean equals(Object o) {
		if (!(o instanceof ColName)) return false;
		ColName cc = (ColName)o;
		boolean ret = (cc.stable.equals(stable) && cc.scol.equals(scol));
//System.out.println(this + " == " + o + ": " + ret);
		return ret;
	}
	public int hashCode()
	{
		return stable.hashCode() * 31 + scol.hashCode();
	}
}
