package offstage.reports.acctsummary;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.TreeMap;
import java.util.TreeSet;

/*
categories:

actranstype=1 (tuition): used to delete all tuition records in one blow when recalculating
1	bill
1		tuition
1		scholarship
1		registration fee

2		late fee

4,5,6	payment


transactions


my categories:
	tuition
	scholarship
	registration fee
	late fee
	payment
 *
 *

*/
// ---------------------------------------------------------
/*
Algorithm:

 * 1. Add all bills
 *    sort by date, also add to term collections
 * 2. Match payments to bills
*/

class LineItem {
	int actransid;

	int entityid;
	int actypeid;
	int termid;

	int category;		// application-defined categories
	java.util.Date date;
	String description;

	double amount;
}
class TransPart {
	LineItem trans;
	double partAmount;		// The portion of the transaction we're applying in this case
}

class Bill extends LineItem {
	ArrayList<TransPart> payments;	// Sorted by date
	double amountPaid;
}


class AcctTerm {
	/** The term we're summarizing; or -1 if no term */
//	Term term;
	String students;		// Students for which this person is responsible for this term
	ArrayList<Bill> bills;	// All bills in this segment, sorted by date
	double[] catBilled;		// Amount billed and paid, by user category
	double[] catPaid;		// [0] = total
}

// ----------------------------------
class AcctKey {
	int entityid;
	int actypeid;
}

class Term {
	int termid;
	java.util.Date beginDate;
	java.util.Date endDate;
	TreeMap<AcctKey,AcctTerm> accts;	// Accounts active in this term
}

// ----------------------------------------
// =======================================================
public class TransMatcher {
	String[] catNames;
	TreeSet<Term> terms;	// Sorted by start date
	TreeSet<AcctKey> accts;	// Accounts we've seen

	public void addLineItems(ArrayList<LineItem> items)
	{
		// Sort by amount, so bills are first
		int n = items.size();
		for (int i=0; i<n; ++i) {
			LineItem item = items.get(i);
		}
		ListIterator<LineItem> ii = items.listIterator();
		for (; ii.hasNext(); ) {
			LineItem item = ii.next();
			if (item.amount > 0) {
				ii.
			}
		}

		if (!ii.hasNext()) return;
		ii.
		LineItem item = ii.next();
		for (; item.amount < 0) {


			LineItem item = ii.next();
			if (item.amount >= 0) break;
		}
	}
}

