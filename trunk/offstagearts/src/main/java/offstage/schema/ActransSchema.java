/*
OffstageArts: Enterprise Database for Arts Organizations
This file Copyright (c) 2005-2007 by Robert Fischer

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
package offstage.schema;

import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.sql.DbChangeModel;
import java.sql.*;
import citibob.types.*;

public class ActransSchema extends ConstSchema
{

public static final int AC_SCHOOL = 1;
public static final int AC_TICKET = 2;
public static final int AC_PLEDGE = 3;
public static final int AC_OPENCLASS = 4;
	
public static final int T_ACTRANS = 0;
public static final int T_INVOICES = 1;
public static final int T_TUITIONTRANS = 2;
public static final int T_ACADJUSTMENTS = 3;
public static final int T_CASHPAYMENTS = 4;
public static final int T_CCPAYMENTS = 5;
public static final int T_CHECKPAYMENTS = 6;

public final KeyedModel tableKmodel;
public final KeyedModel tableoidKmodel;
public final KeyedModel actypeKmodel;

public ActransSchema(citibob.sql.SqlRunner str, DbChangeModel change, java.util.TimeZone tz)
throws SQLException
{
	super();
	
	tableKmodel = new KeyedModel();
	tableKmodel.addItem(new Integer(T_ACTRANS), "transaction");
	tableKmodel.addItem(new Integer(T_INVOICES), "invoice");
	tableKmodel.addItem(new Integer(T_TUITIONTRANS), "tuition invoice");
	tableKmodel.addItem(new Integer(T_ACADJUSTMENTS), "adjustment");
	tableKmodel.addItem(new Integer(T_CASHPAYMENTS), "cash payment");
	tableKmodel.addItem(new Integer(T_CCPAYMENTS), "credit card payment");
	tableKmodel.addItem(new Integer(T_CHECKPAYMENTS), "check payment");
	
	table = "actrans";
	actypeKmodel = new DbKeyedModel(str, change,
		"actypes", "actypeid", "name", "name");
	tableoidKmodel = new KeyedModel();
	tableoidKmodel.addAllItems(str,
			"select oid,relname from pg_class where relname in" +
			" ('invoices', 'tuitiontrans', 'actrans', 'cashpayments'," +
			" 'adjpayments', 'ccpayments', 'checkpayments')",
			"oid", "relname");
	cols = new Column[] {
		new Column(new SqlInteger(false), "actransid", true),
		new Column(new SqlEnum(tableoidKmodel, false), "tableoid"),
		new Column(new SqlInteger(false), "entityid"),
		new Column(new SqlEnum(actypeKmodel, false), "actypeid"),
		new Column(new SqlDate(tz, false), "date"),
		new Column(new SqlDate(tz, false), "datecreated"),
		new Column(new SqlNumeric(9,2), "amount"),
		new Column(new SqlString(300,true), "description")
	};
}

}
