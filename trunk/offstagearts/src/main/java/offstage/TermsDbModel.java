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
///*
// * TermsDbModel.java
// *
// * Created on January 23, 2006, 4:00 PM
// *
// * To change this template, choose Tools | Options and locate the template under
// * the Source Creation and Management node. Right-click the template and choose
// * Open. You can then make changes to the template in the Source Editor.
// */
//
//package offstage;
//
//import citibob.sql.*;
//import citibob.jschema.*;
//import java.sql.*;
//import java.io.*;
//import net.sf.jasperreports.engine.*;
//import java.util.*;
//import javax.swing.event.*;
//import offstage.db.DB;
//import offstage.schema.*;
//
///**
// * No setKey() in this class; it just displays terms for past 2 years.
// * @author citibob
// */
//public class TermsDbModel extends SchemaBufDbModel
//{
//
///** Creates a new instance of TermsDbModel */
//public TermsDbModel(Statement st, citibob.sql.DbChangeModel dbChange, citibob.jschema.Schema termids)
//throws java.sql.SQLException
//{
//	super(new SchemaBuf(termids), dbChange);
//	this.setWhereClause("firstdate > now() - interval '2 years'");
//	this.setOrderClause("firstdate");
//}
//
////public void doUpdate(Statement st) throws SQLException
////{
////	super.doUpdate(st);
////	dbChange.fireTableChanged(st, "terms");
////	
////	put this fireTableChanged() as a standard part of DbModel or SchemaModel or something.
////			Also, make it able to fire events based on individual insert/update queries
////			on individual rows (which are stored in the DbModel, making it easy
////			to indicate what happened)
////}
//}
