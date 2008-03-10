/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.school.tuition;

/**
 *
 * @author citibob
 */
public class RBPlanSet {

protected RBPlan[] plans;
protected RBPlan defPlan;

/** Get list of plans so we can make a chooser */
public RBPlan[] getPlans()
{ return plans; }
public RBPlan getDefPlan() { return defPlan; }

/** Get one plan by name --- or the default if null. */
public RBPlan getPlan(String name)
{
	if (name == null) return defPlan;
	for (int i=0; i<plans.length; ++i)
		if (name.equals(plans[i].getName())) return plans[i];
	return null;
}
}
