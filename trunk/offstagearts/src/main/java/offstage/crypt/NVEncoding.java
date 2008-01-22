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
/*
 * NVPairs.java
 *
 * Created on August 3, 2007, 12:16 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.crypt;

import java.util.*;

/**
 *
 * @author citibob
 */
public class NVEncoding {

static String encode(String[] headers, String[] vals)
{
	StringBuffer sb = new StringBuffer();
	int nfield = headers.length;
	for (int i=0; i<nfield; ++i) {
		if (vals[i] != null) {
			sb.append(headers[i]);
			sb.append(":");
			sb.append(vals[i]);
			sb.append(";");
		}
	}
	return sb.toString();
}
static Map decode(String s)
{
	HashMap map = new HashMap();
	String[] pairs = s.split(";");
	if (pairs.length < 1) return map;
	for (int i=0; i<pairs.length; ++i) {
		String p = pairs[i];
		int sc = p.indexOf(':');
		if (sc > 0) map.put(p.substring(0,sc), p.substring(sc+1));
	}
	return map;
}

}
