/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.school.tuition;

/**
 *
 * @author citibob
 */
public class RBPlan {
	String key;		// Short name stored in database, doesn't change
	String name;
	RatePlan ratePlan;
	BillingPlan billingPlan;

	public RBPlan(String key, String name, RatePlan ratePlan, BillingPlan billingPlan)
	{
		this.key = key;
		this.name = name;
		this.ratePlan = ratePlan;
		this.billingPlan = billingPlan;
	}
	
	public String getKey() { return key; }
	public String getName() { return name; }
	public RatePlan getRatePlan() { return ratePlan; }
	public BillingPlan getBillingPlan() { return billingPlan; }
	
	/** For JComboBox */
	public String toString() { return getName(); }
}
