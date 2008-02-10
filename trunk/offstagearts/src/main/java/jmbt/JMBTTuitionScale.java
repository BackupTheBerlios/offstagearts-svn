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
 * JMBT.java
 *
 * Created on July 5, 2005, 7:38 PM
 */

package jmbt;

import offstage.school.tuition.*;
import citibob.sql.*;
import java.util.*;
import citibob.sql.pgsql.*;

/**
 * A bunch of "stored procedures" for the JMBT database.  This is because
 * PostgreSQL stored procedures are nearly useless.
 * @author citibob
 */
public class JMBTTuitionScale extends TuitionScale
{
	/** Number of installments we divide payments up into */
	public int numQuarters() { return 4; }
	
	public double getRegistrationFee() { return 25; }

	/** Changes the student records inside payer.students */
	public void calcTuition(Payer payer)
	{
		for (Student ss : payer.students) {
			// Get the tuition...
			ss.defaulttuition = calcTuition(ss);
			if (ss.tuitionoverride != null) {
				// Manual override of tuition --- just set it
				ss.tuition = ss.tuitionoverride;
			} else {
				// No override, use the tuition we calculated.
				ss.tuition = ss.defaulttuition;
			}
			ss.tuitionDesc = data.termName + ": Tuition for " + ss.getName();
		}

		if (payer.isorg) return;	// No sibling discounts for organizational payers
		Collections.sort(payer.students);
		int n = 0;
		for (Student ss : payer.students) {
	System.out.println("student: " + ss + ", tuition=" + ss.tuition);
			if (ss.tuition == 0) continue;		// Don't count non-paying "siblings" (such as payer)
			if (n++ == 0) continue;		// Don't apply to first child
			if (ss.tuitionoverride != null) continue;		// Don't apply if we've manually set tuition

			// Apply discount, we're on a sibling
			ss.defaulttuition *= .9;
			ss.tuition = ss.defaulttuition;
			ss.tuitionDesc += " (w/ sibling discount)";
		}

	}

	/** Returns the (one) tuition number for a particular student. */
	double calcTuition(Student ss)
	{
		ss.setSec();
		double price = ss.getProratedPrice();		// Non-timed items	
		double tuition = getPrice(ss.sec) * ss.secProrate + price;
		return tuition;
	}

	/** @param weeklyS Number of seconds of class per week for this student.  Does
	 not include classes with fixed price. */
	public double getPrice(int weeklyS)
	{
		return JMBTRate_YDP0708.getRateY(weeklyS);
	}


}
