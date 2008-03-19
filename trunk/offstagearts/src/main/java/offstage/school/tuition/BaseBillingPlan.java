/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.school.tuition;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 *
 * @author citibob
 */
public abstract class BaseBillingPlan implements BillingPlan
{

// Used to convert daynumbs in constructor
//protected static DayConv dconv = new DayConv(TimeZone.getTimeZone("GMT"));
protected DateFormat dfmt;

protected BaseBillingPlan()
{
	dfmt = new SimpleDateFormat("yyyyMMdd");
	dfmt.setTimeZone(TimeZone.getTimeZone("GMT"));
}

protected void addRegFee(TuitionCon con, Student student,
int duedateDN, double amount)
{
	con.addTransaction(student, duedateDN, amount,
		con.getData().termName + ": Registration Fee for " + student.getName());
}

protected void addScholarship(TuitionCon con, Student student,
int duedateDN, double amount, String label)
{
	con.addTransaction(student, duedateDN, amount,
		con.getData().termName + ": Scholarship for " + student.getName() +
		(label == null ? "" : " (" + label + ")"));
}

protected void addTuition(TuitionCon con, Student student,
int duedateDN, double amount, String label)
{
	con.addTransaction(student, duedateDN, amount,
		con.getData().termName + ": Tuition for " + student.getName() +
		(label == null ? "" : " (" + label + ")"));
}

}
