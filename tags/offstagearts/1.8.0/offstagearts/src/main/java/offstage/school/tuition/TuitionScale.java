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

/**
 *
 * @author citibob
 */
public abstract class TuitionScale {

protected TuitionData data;
//protected Map<String, String> duedateCodes;

public void setData(TuitionData data) { this.data = data; }
	
/** Number of installments we divide payments up into */
public abstract int numQuarters();

public abstract double getRegistrationFee();
	
/** Changes the student records inside payer.students */
public abstract void calcTuition(Payer payer);

/** Returns the (one) tuition number for a particular student. */
// abstract double calcTuition(Student ss);

/** @param weeklyS Number of seconds of class per week for this student.  Does
 not include classes with fixed price. */
public abstract double getPrice(int weeklyS);

	
}
