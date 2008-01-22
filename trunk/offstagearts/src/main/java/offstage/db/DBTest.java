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
 * DBTest.java
 *
 * Created on July 24, 2005, 9:19 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.db;

import java.sql.*;
import javax.swing.*;
import offstage.db.TestConnPool;
/**
 *
 * @author citibob
 */
public class DBTest {

/** Creates a new instance of DBImport */
public static void main(String[] args) throws Exception
{
	Connection db = new TestConnPool().checkout();
	Statement st = db.createStatement();
	ResultSet rs = st.executeQuery(
			"select addressto from aapeople where addressto like '%Adelina%'");
	while (rs.next()) {
		System.out.println(rs.getString(1));
	}
}

}
