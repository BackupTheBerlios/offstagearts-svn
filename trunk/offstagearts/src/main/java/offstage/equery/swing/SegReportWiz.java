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
 * PersonWiz.java
 *
 * Created on October 8, 2006, 6:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.equery.swing;

import citibob.app.App;
import citibob.swing.JTypeColTable;
import java.util.*;
import citibob.swing.html.*;
import citibob.swing.table.DefaultJTypeTableModel;
import citibob.swing.table.JTypeTableModel;
import citibob.types.JType;
import citibob.types.JavaJType;
import java.awt.Dimension;
import javax.swing.*;
import offstage.FrontApp;
import offstage.reports.SegmentationReport;

/**
 *
 * @author citibob
 */
public class SegReportWiz extends HtmlWiz {

JTypeTableModel segmod;
	
/**
 * Creates a new instance of PersonWiz 
 */
public SegReportWiz(java.awt.Frame owner, App app)
throws org.xml.sax.SAXException, java.io.IOException
{
	super(owner);
	setSize(600,400);
//	TypedWidgetMap map = new TypedWidgetMap();
	
	// Set up the Table to select segment types
	String[] avail = SegmentationReport.availSegmentTypes;
	segmod = new DefaultJTypeTableModel(
		new String[] {"selected", "segtype"},
		new JType[] {new JavaJType(Boolean.class), new JavaJType(String.class)},
		avail.length);
	for (int i=0; i < avail.length; ++i) {
		segmod.setValueAt(Boolean.FALSE, i, 0);
		segmod.setValueAt(avail[i],i,1);
	}
	JTypeColTable segtable = new JTypeColTable();
	segtable.setModelU(segmod,
		new String[] {"", "Segment"},
		null, new boolean[] {true, false});
    // Disable auto resizing
//    segtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    
//    // Set the first visible column to a few pixels wide
//    int vColIndex = 0;
//    TableColumn col = segtable.getColumnModel().getColumn(vColIndex);
//    int width = 20;
//    col.setPreferredWidth(width);
	
	
	JScrollPane pane = new JScrollPane();
	pane.setViewportView(segtable);
	pane.setPreferredSize(new Dimension(200,200));
	super.addComponent("segtable", pane);
	
	loadHtml();
}

public void getAllValues(Map map)
{
	// Get value out of segtable
	List<String> segtypes = new ArrayList();
	for (int i=0; i<segmod.getRowCount(); ++i) {
		Boolean sel = (Boolean)segmod.getValueAt(i,0);
		if (sel.booleanValue()) segtypes.add((String)segmod.getValueAt(i,1));
	}
	map.put("segtypes", segtypes);
}


//public static void main(String[] args)
//throws Exception
//{
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	FrontApp fapp = new FrontApp(pool,null);
//
//	JFrame f = new JFrame();
//	f.setVisible(true);
//	SegReportWiz wiz = new SegReportWiz(f, fapp);
//	wiz.setVisible(true);
//	System.out.println(wiz.getSubmitName());
//	
////	wiz = new SegReportWiz(f);
////	wiz.setVisible(true);
////	System.out.println(wiz.getSubmitName());
//	
//	System.exit(0);
//}
}
