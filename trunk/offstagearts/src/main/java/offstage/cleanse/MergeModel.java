/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.cleanse;

import citibob.jschema.MultiDbModel;
import citibob.sql.SqlRun;
import java.io.IOException;
import javax.swing.JOptionPane;
import offstage.FrontApp;
import offstage.devel.gui.DevelModel;

/**
 *
 * @author citibob
 */
public class MergeModel extends MultiDbModel
{


DevelModel[] dm;

static DevelModel[] newDevelModels(FrontApp fapp, int size) {
	DevelModel[] ret = new DevelModel[size];
	for (int i=0; i<ret.length; ++i) ret[i] = new DevelModel(fapp);
	return ret;
}

public MergeModel(FrontApp fapp) {
	super(newDevelModels(fapp, 2));
//	for (int i=0; i<dm.size(); ++I)
}

public DevelModel getDevelModel(int n)
	{ return (DevelModel)super.getModel(n); }


/** @param index 0 or 1 */
public void setEntityID(int index, Integer entityID)
	{ getDevelModel(index).setKey(entityID); }

/** @param index 0 or 1 */
public Integer getEntityID(int index)
	{ return (Integer)getDevelModel(index).getKey(); }



}
