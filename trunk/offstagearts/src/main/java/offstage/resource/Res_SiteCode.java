/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.resource;

import citibob.resource.DataResource;
import citibob.resource.JarInstaller;
import citibob.resource.ResSet;

/**
 *
 * @author citibob
 */
public class Res_SiteCode extends DataResource
{

public Res_SiteCode(ResSet rset)
{
	super(rset, null, "sitecode.jar");

	add(new JarInstaller(this, 1));
}
	
}
