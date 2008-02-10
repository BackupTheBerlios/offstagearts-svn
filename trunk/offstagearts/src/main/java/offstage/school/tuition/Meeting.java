/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.school.tuition;

import citibob.sql.pgsql.SqlDate;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Meeting
{
	public java.util.Date dtstart;
	public java.util.Date dtnext;
	public Meeting(ResultSet rs, SqlDate date) throws SQLException
	{
		dtstart = date.get(rs, "dtstart");
		dtnext = date.get(rs, "dtnext");
	}
}
