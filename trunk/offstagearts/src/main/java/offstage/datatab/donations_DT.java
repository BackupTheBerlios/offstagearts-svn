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
	summary_st =
		"<hr>\n" +
		"<h3>Donations</h3>\n" +
		"<table>\n" +
		"<tr><th>Type</th><th>Date</th><th>Amount</th></tr>\n" +
		"$donations:{it |\n" +
		"<tr><td><b>$it.groupid$</b></td><td>$it.date$</td><td align=\"right\">$it.amount$</td></tr>\n" +
		"}$\n" +
		"</table>\n";

}
	
}
