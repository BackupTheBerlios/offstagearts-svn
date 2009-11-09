//package offstage.reports.acctsummary;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Iterator;
//import java.util.TreeMap;
///*
//Algorithm:
//
// * 1. Add all bills
// *    sort by date, also add to term collections
// * 2. Match payments to bills
//*/
//
//class LineItem {
//	int actransid;
//
//	int entityid;
//	int actypeid;
//	int termid;
//
//	int category;		// application-defined categories
//	java.util.Date date;
//	String description;
//
//	double amount;		// <0 for bills, >0 for payments
//
//	// For a bill
//	ArrayList<TransPart> payments;	// Sorted by date
//	double amountPaid;	// >0 for bills, <0 for payments
//
//	double amountRemain() { return amount + amountPaid; }
//
//	void addPayment(LineItem item, double payAmount) {
//		if (payments == null) payments = new ArrayList();
//		amountPaid += payAmount;
//		payments.add(new TransPart(item, payAmount));
//	}
//
//}
//class TransPart {
//	LineItem trans;
//	double partAmount;		// The portion of the transaction we're applying in this case
//	
//	public TransPart(LineItem trans, double partAmount) {
//		this.trans = trans;
//		this.partAmount = partAmount;
//	}
//}
//
//class AcctTerm {
//	/** The term we're summarizing; or -1 if no term */
////	Term term;
//	String students;		// Students for which this person is responsible for this term
//	ArrayList<LineItem> bills;	// All bills in this segment, sorted by date
//	double[] catBilled;		// Amount billed and paid, by user category, billed < 0
//	double[] catPaid;		// [0] = total, paid > 0
//	
//	public AcctTerm(int ncat) {
//		bills = new ArrayList();
//		catBilled = new double[ncat];
//		catPaid = new double[ncat];
//		// Fill in students later...
//	}
//	
//	public void addBill(LineItem bill) {
//		bills.add(bill);
//		catBilled[0] += bill.amount;		// amount < 0
//		if (bill.category > 0)
//			catBilled[bill.category] += bill.amount;
//	}
//}
//
//class Acct {
//	TreeMap<Integer,AcctTerm> terms;	// termid -> data
//	ArrayList<LineItem> bills;		// All bills for this acct, sorted by date
//	
//	Iterator<LineItem> billsIt;
//	LineItem curBill;
//
//	ArrayList<TransPart> overpays;	// Sorted by date; null if no overpays
//	double amountOverpay;
//	
//	public Acct() {
//		terms = new TreeMap();
//	}
//	int ncat() { return -1; }
//	
//	boolean scanToNextUnpaidBill() {
//		for (;;) {
//			// See if the current bill is OK
//			if (curBill != null) {
//				if (curBill.amountRemain() < -.001) {
//					// Haven't yet fully paid this bill
//					return true;
//				}
//				
//				// Bill has been fully paid, scan to the next one
//			}
//			
//			if (!billsIt.hasNext()) return false;	// No more bills
//			curBill = billsIt.next();
//		}
//	}
//	
//	public void sort() {
//		.. sort the bills, terms, etc ...
//		billsIt = bills.iterator();
//		if (billsIt.hasNext()) curBill = billsIt.next();
//	}
//	
//	AcctTerm getAcctTerm(int termid)
//	{
//		// Find the AcctTerm structure
//		AcctTerm aterm = terms.get(termid);
//		if (aterm == null) {
//			aterm = new AcctTerm(ncat());
//			terms.put(termid, aterm);
//		}
//		
//		return aterm;
//	}
//	
//	public void addPayment(LineItem item) {
//		double amountRemain = item.amount;	// >0
//		// Match this payment ot multiple bills
//		while (Math.abs(amountRemain) > .001) {
//			scanToNextUnpaidBill();
//			
//			// Apply this payment to the bill
//			double payAmount = -(curBill.amount + curBill.amountPaid);	// >0
//			payAmount = Math.min(amountRemain, payAmount);	// >0
//			item.amountPaid -= payAmount;	// <0
//			curBill.addPayment(item, payAmount);
//			
//			// Apply payment to the correct AcctTerm
//			AcctTerm aterm = getAcctTerm(item.termid);
//			int cat = item.category;
//			aterm.catPaid[0] += payAmount;
//			if (cat > 0) aterm.catPaid[cat] += payAmount;
//		}
//	}
//}
//
//// ----------------------------------
//class AcctKey {
//	int entityid;
//	int actypeid;
//}
//
//class Term {
//	int termid;
//	java.util.Date beginDate;
//	java.util.Date endDate;
////	TreeMap<AcctKey,AcctTerm> accts;	// Accounts active in this term
//	
//	public Term(int termid) {
//		this.termid = termid;
////		this.accts = new TreeMap();
//	}
//}
//
//// ----------------------------------------
//// =======================================================
//public class TransMatcher {
//	String[] catNames;
//	TreeMap<Integer,Term> terms;	// termid -> term
//	TreeMap<AcctKey,Acct> accts;
////	TreeSet<AcctKey> accts;	// Accounts we've seen
//
//	int ncat() { return catNames.length; }
//	
//	Term getTerm(int termid)
//	{
//		// Make sure the term record is in our data structure
//		Term term = terms.get(termid);
//		if (term == null) {
//			term = new Term(termid);
//				// beginDate and endDate to be set later
//			terms.put(termid, term);
//		}		
//	}
//	
//	Acct getAcct(LineItem item) {
//		AcctKey akey = new AcctKey();
//			akey.entityid = item.entityid;
//			akey.actypeid = item.actypeid;
//		return getAcct(akey);
//	}
//	
//	Acct getAcct(AcctKey akey) {
//		// Make sure the account key is in our structure
//		Acct acct = accts.get(akey);
//		if (acct == null) {
//			acct = new Acct();
//			accts.put(akey, acct);
//		}
//	}
//	
//
//	
//	void addBill(LineItem bill) {
//		Acct acct = getAcct(bill);
//		AcctTerm aterm = acct.getAcctTerm(ncat(), acct, bill.termid);
//		aterm.addBill(bill);
//	}
//	
//	public void addPayment(LineItem item) {
//		Acct acct = getAcct(item);
//		acct.addPayment(item);
//	}
//	
//	public void addLineItems(ArrayList<LineItem> items)
//	{
//		// Sort by amount, so bills are first
//		Collections.sort(items, new Comparator<LineItem>() {
//		public int compare(LineItem o1, LineItem o2) {
//			double v = o1.amount - o2.amount;
//			return (int)Math.signum(v);
//		}});
//		
//		int n = items.size();
//		int itemIx=0;
//		
//		// Add bills
//		if (itemIx >= n) return;		// Nothing to do!
//		while (items.get(itemIx).amount < 0) {
//			// add a bill
//			addBill(items.get(itemIx));
//			if (++itemIx >= n) return;	// Finished
//		}
//
//		// Look up term start dates (to sort by terms)
//		...
//
//		// Look up Account Info...
//				
//		// sort...
//		Collections.sort(terms)
//		
//		// Add payments
//		do {
//			LineItem item = items.get(itemIx);
//			
//			// Figure if this is a payment or a rebate
//			
//			// Add a payment
//			addPayment(item);
//		} while (++itemIx < n);
//	}
//}
//
