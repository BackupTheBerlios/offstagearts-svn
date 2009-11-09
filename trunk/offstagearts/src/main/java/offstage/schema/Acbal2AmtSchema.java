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
package offstage.schema;

import citibob.sql.*;
import citibob.sql.pgsql.*;
import citibob.jschema.*;
import citibob.sql.DbChangeModel;
import java.sql.*;
import citibob.types.*;

public class Acbal2AmtSchema extends ConstSqlSchema
{

public final KeyedModel assetKmodel;
	
public Acbal2AmtSchema(citibob.sql.SqlRun str, DbChangeModel change)
throws SQLException
{
	super();

	/*
CREATE TABLE acbal2amt
(
  acbalid integer NOT NULL,
  assetid integer NOT NULL,
  bal numeric(9,2),
  CONSTRAINT acbal2amt_pkey PRIMARY KEY (acbalid, assetid),
  CONSTRAINT acbal2amt_acbalid_fkey FOREIGN KEY (acbalid)
      REFERENCES acbal2 (acbalid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (OIDS=FALSE);
ALTER TABLE acbal2amt OWNER TO ballettheatre;

	 */
	table = "acbal2amt";
	assetKmodel = new DbKeyedModel(str, change,
		"assetids", "assetid", "name", "name", null);
	cols = new SqlCol[] {
		new SqlCol(new SqlInteger(false), "acbalid", true),		
		new SqlCol(new SqlEnum(assetKmodel), "assetid", true),
		new SqlCol(new SqlNumeric(9,2), "bal")
	};
}

}
