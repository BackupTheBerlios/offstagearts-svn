package offstage.school.tuition;

import citibob.sql.pgsql.SqlDate;
import java.sql.ResultSet;
import java.sql.SQLException;

/** When different charges are due throughout the term */
public class DueDate
{
	public String name;
	public String description;
	public java.util.Date duedate;
	public DueDate(ResultSet rs, SqlDate date) throws SQLException
	{
		name = rs.getString("name");
		description = rs.getString("description");
		duedate = date.get(rs, "duedate");
	}
}
