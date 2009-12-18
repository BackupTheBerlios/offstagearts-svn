/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.cleanse;

import citibob.sql.SqlRun;
import citibob.sql.pgsql.SqlBool;
import citibob.types.KeyedModel;
import java.io.IOException;
import offstage.FrontApp;

/**
 *
 * @author citibob
 */
public class MergeActions {

// Merge codes in the mergelog table
public static final int MC_DUPOK = 0;
public static final int MC_MERGE_TO_0 = 1;		// Merge into entityid0
public static final int MC_MERGE_TO_1 = 2;
public static final int MC_DEL_0 = 3;
public static final int MC_DEL_1 = 4;
public static final int MC_DEL_BOTH = 5;

// Mode our owner is operating in.
public static final int M_REGULAR = 0;
public static final int M_PROVISIONAL = 1;
public static final int M_APPROVE = 2;

static KeyedModel actionKmodel = new KeyedModel();
static {
	actionKmodel.addItem(null, "");
	actionKmodel.addItem(MC_DUPOK, "Do Not Merge");
	actionKmodel.addItem(MC_MERGE_TO_0, "<- Merge");
	actionKmodel.addItem(MC_MERGE_TO_1, "Merge ->");
	actionKmodel.addItem(MC_DEL_0, "Delete New");
	actionKmodel.addItem(MC_DEL_1, "Delete Old");
	actionKmodel.addItem(MC_DEL_BOTH, "Delete Both");
}



/** Perform a particular action
 * @param cleansMode:<ul>
 * <li><b>M_REGULAR</b>: Do the action right away.</li>
 * <li><b>M_PROVISIONAL</b>: Save the action till later</li>
 * @param str
 * @param cleanseMode
 * @param action
 * @param entityid0
 * @param entityid1
 * @throws java.io.IOException
 */
public static void doDbAction(SqlRun str, FrontApp app, int cleanseMode,
int action, int entityid0, int entityid1) throws IOException
{
	switch(action) {
		case MC_DEL_0 :
		case MC_DEL_1 :
		case MC_DEL_BOTH :
			deleteDbAction(str, cleanseMode, action, entityid0, entityid1);
			break;
		case MC_MERGE_TO_0 :
		case MC_MERGE_TO_1 :
			mergeDbAction(str, app, cleanseMode, action, entityid0, entityid1);
			break;
		case MC_DUPOK :
			dupOKDbAction(str, cleanseMode, entityid0, entityid1);
			break;
		default : return;
	}
}

public static void deleteDbAction(SqlRun str, int cleanseMode,
final int action, int entityid0, int entityid1)
{
	if (cleanseMode != M_PROVISIONAL) {
		// Really delete them
		switch(action) {
			case MC_DEL_0 :
				str.execSql("update entities" +
					" set obsolete=true where entityid = " + entityid0);
			break;
			case MC_DEL_1 :
				str.execSql("update entities" +
					" set obsolete=true where entityid = " + entityid1);
			break;
			case MC_DEL_BOTH :
				str.execSql("update entities" +
					" set obsolete=true where entityid in ("
					+ entityid0 + ", " + entityid1 + ")");
			break;
		}
	}

	// Insert into mergelog
	str.execSql(
		" delete from mergelog where entityid0 = " + entityid0 + " and entityid1 = " + entityid1 + ";\n" +
		" delete from mergelog where entityid0 = " + entityid1 + " and entityid1 = " + entityid0 + ";\n" +
		" insert into mergelog (entityid0, entityid1, action, provisional, dtime) values (" +
		entityid0 + "," + entityid1 + "," + action + ", " +
		SqlBool.sql(cleanseMode == M_PROVISIONAL) + ", now());\n");
}

/** @return entityID of resulting merged record (if not M_PROVISIONAL).  Or null. */
public static Integer mergeDbAction(SqlRun str, FrontApp app, int cleanseMode,
final int action, int entityid0, int entityid1)
{
	int entityidFrom, entityidTo;
	if (action == MC_MERGE_TO_0) {
		entityidFrom = entityid1;
		entityidTo = entityid0;
	} else {
		entityidFrom = entityid0;
		entityidTo = entityid1;
	}
	Integer mergedID = null;
	if (cleanseMode != M_PROVISIONAL) {

		MergeSql merge = new MergeSql(app);//.schemaSet());
		mergedID = merge.mergeEntities(entityidFrom, entityidTo);
		str.execSql(merge.toSql());

		// Mark it as coming from the correct database
		str.execSql(
			" update entities set dbid = \n" +
			" (select dbid from entities where entityid = " + entityid1 + ")\n" +
			" where entityid = " + entityidTo);
	}
	str.execSql(
		" delete from mergelog where entityid0 = " + entityid0 + " and entityid1 = " + entityid1 + ";\n" +
		" delete from mergelog where entityid0 = " + entityid1 + " and entityid1 = " + entityid0 + ";\n" +
		" insert into mergelog (entityid0, entityid1, action, provisional, dtime) values (" +
		entityid0 + "," + entityid1 + "," + action + ", " +
		SqlBool.sql(cleanseMode == M_PROVISIONAL) + ", now());\n");
	return mergedID;
}


public static void dupOKDbAction(SqlRun str, int cleanseMode,
int entityid0, int entityid1)
{
	str.execSql(
		" delete from mergelog where entityid0 = " + entityid0 + " and entityid1 = " + entityid1 + ";\n" +
		" delete from mergelog where entityid0 = " + entityid1 + " and entityid1 = " + entityid0 + ";\n" +
		" insert into mergelog (entityid0, entityid1, action, provisional, dtime) values (" +
		entityid0 + "," + entityid1 + "," + MC_DUPOK + "," +
		SqlBool.sql(cleanseMode == M_PROVISIONAL) + ", now());\n");
}



}
