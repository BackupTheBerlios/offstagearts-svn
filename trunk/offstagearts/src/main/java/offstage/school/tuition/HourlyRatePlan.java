package offstage.school.tuition;

import java.text.NumberFormat;
import java.util.Collections;

public abstract class HourlyRatePlan implements RatePlan
{

double siblingDiscount;		// % off for second siblings

/** Sets the tuition field(s) in the student records inside payer.students.
 Also calls addTrans() as needed...  Should add the registration fee too, if needed. */
public void setTuition(TuitionCon con, Payer payer)
{
	NumberFormat money = NumberFormat.getCurrencyInstance();
	NumberFormat pct = NumberFormat.getPercentInstance();

	// Set basic tuition (includes pro-rating)
	for (Student ss : payer.students) setTuition(con, ss);

	// Work on sibling discount
	if (payer.isorg) return;	// No sibling discounts for organizational payers
	Collections.sort(payer.students);
	int n = 0;
	for (Student ss : payer.students) {
		double tuition = ss.getTuition();
	System.out.println("student: " + ss + ", tuition=" + ss.getTuition());
		if (tuition == 0) continue;		// Don't count non-paying "siblings" (such as payer)
		if (n++ == 0) continue;		// Don't apply to first child
//		if (ss.tuitionoverride != null) continue;		// Don't apply if we've manually set tuition

		// Apply discount, we're on a sibling
		String desc = ss.tuitionDesc;
		double discount = .01*Math.round(100*(1.0D - siblingDiscount) * ss.defaulttuition);
		desc = desc + "\n - " + money.format(discount) + " (" + pct.format(siblingDiscount) + " sibling discount)";
		con.setCalcTuition(ss, ss.defaulttuition * (1.0D - siblingDiscount), desc);
	}

}

	/** Returns the (one) tuition number for a particular student. */
	void setTuition(TuitionCon con, Student ss)
	{
		NumberFormat money = NumberFormat.getCurrencyInstance();
		String ssec = ss.setSec();
		String sprice = ss.setPrice();
		double tuition = getPrice(ss.sec) * ss.secProrate + ss.priceProrate;
		String desc =
			ssec + "\nTotal Price for Timed Courses: " + money.format(getPrice(ss.sec) * ss.secProrate) + "\n" +
			sprice + "\nTotal Base Tuition: " + money.format(tuition);
		con.setCalcTuition(ss, tuition, desc);
	}

	/** @param weeklyS Number of seconds of class per week for this student.  Does
	 not include classes with fixed price. */
	public abstract double getPrice(int weeklyS);

	
}
