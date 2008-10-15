/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.datatab;

import citibob.app.App;
import citibob.sql.SqlRun;
import java.sql.SQLException;
import offstage.schema.DonationsSchema;

/**
 *
 * @author citibob
 */
public class donations_DT extends DataTab
{

public donations_DT(SqlRun str, App app)
throws SQLException
{
	title = "Donations";
	schema = new DonationsSchema(str, app.dbChange(), app.timeZone());
	orderClause = "date desc";
	displayColTitles = new String[] {"Group", "Type", "Date", "Amount"};
	displayCols = new String[] {"groupid", "donationtypeid", "date", "amount"};
	equeryAliases = new String[] {
		"donations.groupid", "donation",
		"donations.donationtypeid", "donation-type",
		"donations.date", "donation-date",
		"donations.amount", "donation-amount"
	};
}
	
}
