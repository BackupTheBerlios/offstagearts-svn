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
 * CCEncoding.java
 *
 * Created on August 3, 2007, 12:18 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.crypt;

import citibob.wizard.*;
import java.util.*;
import citibob.util.*;

/**
 *
 * @author citibob
 */
public class CCEncoding {

static final String[] headers = {"ct", "cc", "nm", "ex", "cv", "zp"};
static final String[] fields = {"cctype", "ccnumber", "ccname", "expdate", "ccv", "zip"};

public static String encode(TypedHashMap v)
{
	String[] val = new String[fields.length];
	for (int i=0; i<fields.length; ++i) {
		val[i] = v.getString(fields[i]);
	}
	return NVEncoding.encode(headers, val);
}

public static TypedHashMap decode(String s)
{
	TypedHashMap ret = new TypedHashMap();
	Map m = NVEncoding.decode(s);
	for (int i=0; i<fields.length; ++i) {
		Object o = m.get(headers[i]);
		if (o != null) ret.put(fields[i], o);
	}
	return ret;
}
}
