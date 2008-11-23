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
