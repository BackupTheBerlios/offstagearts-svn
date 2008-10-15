/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage;

/**
 * Maintenance stuff
 * @author citibob
 */
public class Maintain {

public static void dailyMaintain(FrontApp app) throws Exception
{
	app.jango().deleteOldGroups();
}
	
}
