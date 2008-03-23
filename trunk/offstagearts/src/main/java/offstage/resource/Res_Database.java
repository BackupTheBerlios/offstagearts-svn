/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.resource;

import citibob.resource.DbbCreator;
import citibob.resource.DbbResource;
import citibob.resource.DbbUpgrader;
import citibob.resource.ResSet;

/**
 *
 * @author citibob
 */
public class Res_Database extends DbbResource
{

public Res_Database(ResSet rset)
{
	super(rset, "general", "database.sql");

	add(new DbbCreator(this, 1));
	add(new DbbUpgrader(this, 1, 2, true));
	add(new DbbUpgrader(this, 2, 3, true));
}
	
}
