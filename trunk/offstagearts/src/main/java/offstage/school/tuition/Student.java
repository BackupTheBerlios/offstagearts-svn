/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.school.tuition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** A record of data for one student -- from termregs */
public class Student implements Comparable<Student>
{
	public int entityid;
	public int payerid;
	public String lastname, firstname;
	public double scholarship;
	public Double tuitionoverride;
	public List<Enrollment> enrollments;	// Courses we're enrolled in

	// Calculated Stuff
	public double defaulttuition = 0;
	public double secProrate;				// Pro-rate multiplier for seconds per week of class
	public int sec;						// Seconds of week per class
	public double tuition = 0;				// Tuition we calculated
	public String tuitionDesc;				// Discription of our tuition for account
	
	public Student(ResultSet rs) throws SQLException
	{
		entityid = rs.getInt("entityid");
		payerid = rs.getInt("payerid");
			if (payerid == 0) payerid = entityid;
		lastname = rs.getString("lastname");
		firstname = rs.getString("firstname");
		scholarship = TuitionData.getMoney(rs, "scholarship");
		Double Tuition = TuitionData.getMoney(rs, "tuition");
			tuition = (Tuition == null ? 0 : Tuition);
		tuitionoverride = TuitionData.getMoney(rs, "tuitionoverride");
		Double Defaulttuition = TuitionData.getMoney(rs, "defaulttuition");
			defaulttuition = (Defaulttuition == null ? 0 : Defaulttuition);
		enrollments = new ArrayList(1);
	}
	public String toString() { return "Student(" + entityid + ", " + getName() + ")"; }
	public double getProratedPrice()
	{
		double price = 0;
		for (Enrollment e : enrollments) {
			price += e.getPrice() * e.getProrate();
System.out.println(entityid + "     : price += " + e.getPrice() + " * " + e.getProrate());
		}
System.out.println(entityid + ": price = " + price);
		return price;
	}
	public void setSec()
	{
		sec = 0;
		double psec = 0;		// Prorated # of seconds
		for (Enrollment e : enrollments) {
			int s = e.getSec();
			sec += s;
			psec += s * e.getProrate();
System.out.println(entityid + "     : sec += " + e.getSec() + " * " + e.getProrate());
		}
System.out.println(entityid + ": sec = " + sec + " " + psec);
		secProrate = (double)psec / (double)sec;
		if (Double.isNaN(secProrate)) secProrate = 1.0D;	// 0/0 = 1 in this case.
	}
	public String getName() { return firstname + " " + lastname; }

	public int compareTo(Student o) {
		double d = o.tuition - tuition;		// Sort descending
		if (d > 0) return 1;
		if (d < 0) return -1;
		return 0;
	}
}
