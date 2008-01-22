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
 * OffstageSwingerMap.java
 *
 * Created on October 8, 2006, 4:52 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.types;

import citibob.swing.typed.*;
import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.swing.pgsql.*;
import citibob.types.*;
import citibob.swingers.*;
import java.util.*;

/**
 *
 * @author citibob
 */
public class OffstageSwingerMap extends citibob.sql.pgsql.SqlSwingerMap
{
	
/** Creates a new instance of OffstageSwingerMap */
public OffstageSwingerMap(final java.util.TimeZone tz) {
	super(tz);
	
	// SqlPhone
	this.addMaker(SqlPhone.class, new DefaultSwingerMap.Maker() {
	public Swinger newSwinger(JType sqlType) {
		return new PhoneSwinger();
	}});

	// OVERRIDE: SqlTime
	this.addMaker(SqlTime.class, new DefaultSwingerMap.Maker() {
	public Swinger newSwinger(JType sqlType) {
		// Times represented as (timezone-free) offsets from the start
		// of a day (in a particular timezone).  Thus, "GMT" is the timezone.
		return new JDateSwinger((JDateType)sqlType,
			new String[] {"hh:mm a", "HH:mm"},
			"", TimeZone.getTimeZone("GMT"), null);
		//return new SqlTimeSwinger((SqlTime)sqlType, "HH:mm");
	}});
	
}
	
}
