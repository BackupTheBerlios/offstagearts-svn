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
 * JMBTConnection.java
 *
 * Created on June 30, 2005, 10:37 PM
 */

package offstage.db;

import java.sql.*;
import citibob.sql.*;


/**
 *
 * @author citibob
 */
public class TestConnPool extends SimpleConnPool
{
static {
	try {
		Class.forName("org.postgresql.Driver");
	} catch(Exception e) {
		e.printStackTrace(System.err);
		System.exit(-1);
	}
}

    /** Creates a new instance of JMBTConnection */
    protected Connection create() throws SQLException
	{
		return DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/jmbt", "jmbt", "fiercecookie");
//		return DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/jmbt2", "jmbt", "fiercecookie");
    }

}
