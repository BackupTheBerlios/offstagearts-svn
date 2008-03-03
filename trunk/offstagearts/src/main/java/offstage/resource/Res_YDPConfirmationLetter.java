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
public class Res_YDPConfirmationLetter extends DataResource
{

public Res_YDPConfirmationLetter(ResSet rset)
{
	super(rset, "termids", "YDPConfirmationLetter.odt");

	add(new JarInstaller(this, 2));
	add(new JarInstaller(this, 3));
//	add(new JarInstaller(this, 4));
//	add(new JarInstaller(this, 6));
//	add(new JarInstaller(this, 17));
	add(new CopyUpgrader(this, 2,3));
//	add(new CopyUpgrader(this, 3,4));
//	add(new CopyUpgrader(this, 4,6));
}
	
}
