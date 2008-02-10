/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.school.tuition;

import citibob.sql.pgsql.SqlDate;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

/** One student enrolled in one course */
public class Enrollment
{
	// From enrollments
	public int entityid;			// student
	public int courseid;
	public java.util.Date dstart;	// Custom start date for pro-rating
	public java.util.Date dend;

	// From courseids...
	public int tstartMS;		// Time of day for start and end of course
	public int tnextMS;
	public Double price;		// Price of just this one course
	public int locationid;		// Where the course is held
	
	// Calculated
	private double prorate;		// Pro-rating fraction
	public double getProrate() { return prorate; }
	
	public Enrollment(ResultSet rs, SqlDate date, Map<Integer,Course> courses) throws SQLException
	{
		entityid = rs.getInt("entityid");
		courseid = rs.getInt("courseid");
		dstart = date.get(rs, "dstart");
		dend = date.get(rs, "dend");
		Date TstartMS = (Date)TuitionData.time.get(rs, "tstart");
		tstartMS = (TstartMS == null ? 0 : (int)TstartMS.getTime());
		Date TnextMS = (Date)TuitionData.time.get(rs, "tnext");
		tnextMS = (TnextMS == null ? 0 : (int)TnextMS.getTime());
		price = (Double)TuitionData.money.get(rs, "price");
		locationid = rs.getInt("locationid");
		
		Course course = courses.get(courseid);
		prorate = (course == null ? 1.0D :
			(double)course.numMeetings(dstart,dend) / (double)course.numMeetings());
	}

	public double getPrice()
	{
		if (price == null) return 0;
		return price.doubleValue();
	}
	public int getSec()
	{
		if (price != null) return 0;
		return (tnextMS - tstartMS) / 1000;
	}
}
