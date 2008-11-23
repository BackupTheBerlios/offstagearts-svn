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

import citibob.util.DayConv;
import java.text.ParseException;


/**
 * Don't do any billing, rely on user to do all custom billing.
 * @author citibob
 */
public class NullBillingPlan extends BaseBillingPlan
{

int regDuedateDN;
double regFee;

public NullBillingPlan(double regFee, String sRegDueDate)
throws ParseException
{
	this.regFee = regFee;
	this.regDuedateDN = DayConv.parse(dfmt, sRegDueDate);
}


/** Adds the tuition billing records for a student.  Should take into account
 the student's tuition, scholarships and any registration fees. */
public void billAccount(TuitionCon con, Student student)
{
	// Registration Fee
	if (regFee != 0) addRegFee(con, student, regDuedateDN, regFee);	
}
}
