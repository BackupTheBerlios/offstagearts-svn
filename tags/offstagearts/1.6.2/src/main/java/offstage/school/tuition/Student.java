/*
OffstageArts: Enterprise Database for Arts Organizations
This file Copyright (c) 2005-2008 by Robert Fischer

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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.school.tuition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/** A record of data for one student -- from termregs */
public class Student implements Comparable<Student>
{
	public int entityid;
	public int payerid;
	public String lastname, firstname;
	public double scholarship;
	public double scholarshippct;
	public Double tuitionoverride;			// Manual override tuition
	public List<Enrollment> enrollments;	// Courses we're enrolled in

	// Calculated Stuff
	public double defaulttuition = 0;		// Tuition we calculated
	public double secProrate;				// Pro-rate multiplier for seconds per week of class
	public int sec;						// Seconds of week per class
	public double priceProrate;				// $ of $-priced class
//	public double tuition = 0;				// == 
	public String tuitionDesc;				// Discription of our tuition for account
	public double getTuition()
		{ return (tuitionoverride != null ? tuitionoverride : defaulttuition); }
	
	public Student(ResultSet rs) throws SQLException
	{
		entityid = rs.getInt("entityid");
		payerid = rs.getInt("payerid");
			if (payerid == 0) payerid = entityid;
		lastname = rs.getString("lastname");
		firstname = rs.getString("firstname");
		scholarship = TuitionData.getMoney(rs, "scholarship");
		scholarshippct = rs.getDouble("scholarshippct");
//		Double Tuition = TuitionData.getMoney(rs, "tuition");
//			tuition = (Tuition == null ? 0 : Tuition);
		tuitionoverride = TuitionData.getMoney(rs, "tuitionoverride");
		Double Defaulttuition = TuitionData.getMoney(rs, "defaulttuition");
			defaulttuition = (Defaulttuition == null ? 0 : Defaulttuition);
		enrollments = new ArrayList(1);
	}
	public String toString() { return "Student(" + entityid + ", " + getName() + ")"; }
	public String setPrice()
	{
		StringBuffer desc = new StringBuffer();
		NumberFormat mfmt = NumberFormat.getCurrencyInstance();
		priceProrate = 0;
		for (Enrollment e : enrollments) {
			double p = e.getPrice();
			priceProrate += e.getPrice() * e.getProrate();
			if (p != 0) {
				desc.append(mfmt.format(p));
				if (e.getProrate() != 1.0D) desc.append(" * (" + e.getprorateDesc() +
					") = " + mfmt.format(p * e.getProrate()) + "\n");
			}
System.out.println(entityid + "     : price += " + e.getPrice() + " * " + e.getProrate());
		}
		return desc.toString();
	}
	
	NumberFormat nfmt00 = new DecimalFormat("00");
	NumberFormat nfmt4 = new DecimalFormat("#.0000");
	/** Calculates (prorated) number of seconds.  Returns description */
	public String setSec()
	{
		StringBuffer desc = new StringBuffer();
		sec = 0;
		double psec = 0;		// Prorated # of seconds
		for (Enrollment e : enrollments) {
			int s = e.getSec();
			sec += s;
			psec += s * e.getProrate();
			
			if (s != 0) {
				String ssec = (s / 3600) + ":" + nfmt00.format((s + 59 / 60));
				desc.append(ssec + " * (" + e.getprorateDesc() + ")\n");
			}
System.out.println(entityid + "     : sec += " + e.getSec() + " * " + e.getProrate());
		}
System.out.println(entityid + ": sec = " + sec + " " + psec);
		secProrate = (double)psec / (double)sec;
		if (Double.isNaN(secProrate)) secProrate = 1.0D;	// 0/0 = 1 in this case.
		
		String ssec = (sec / 3600) + ":" + nfmt00.format((sec + 59 / 60));
		desc.append("Total " + ssec);
		if (secProrate != 1.0D) desc.append(" * " + nfmt4.format(secProrate) + " prorate factor");
		return desc.toString();
	}
	public String getName() { return firstname + " " + lastname; }

	public int compareTo(Student o) {
		double d = o.defaulttuition - defaulttuition;		// Sort descending
		if (d > 0) return 1;
		if (d < 0) return -1;
		return 0;
	}
}
