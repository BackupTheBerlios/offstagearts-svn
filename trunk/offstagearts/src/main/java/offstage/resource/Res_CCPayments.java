/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.resource;

import citibob.resource.DataResource;
import citibob.resource.JarInstaller;
import citibob.resource.CopyUpgrader;
import citibob.resource.ResSet;

/**
 *
 * @author citibob
 */
public class Res_CCPayments extends DataResource
{

public Res_CCPayments(ResSet rset)
{
	super(rset, "accounting", "CCPayments.jrxml");

	add(new JarInstaller(this, 1));
//	add(new JarInstaller(this, 3));
//	add(new CopyUpgrader(this, 2,3));
}
	
}
