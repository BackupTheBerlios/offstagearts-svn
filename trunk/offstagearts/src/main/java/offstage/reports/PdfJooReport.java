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
///*
// * PdfJooReport.java
// *
// * Created on August 7, 2007, 11:33 PM
// *
// * To change this template, choose Tools | Template Manager
// * and open the template in the editor.
// */
//
//package offstage.reports;
//
//import com.artofsolving.jodconverter.*;
//import com.artofsolving.jodconverter.openoffice.connection.*;
//import com.artofsolving.jodconverter.openoffice.converter.*;
//import net.sf.jooreports.templates.*;
//import java.io.*;
//import java.util.*;
//import com.pdfhacks.*;
//
///**
// *
// * @author citibob
// */
//public class PdfJooReport
//{
//
//static final int E_MAIN = 0;
//static final int E_TEMPLATE = 1;
//static final int E_OO = 2;
//static final int E_CONCAT = 3;
//Throwable[] exceptions = new Throwable[4];
////PipedInputStream pin;
////PipedOutputStream pout;
//OpenOfficeConnection connection;
//Process proc;
//InputStream stderr;
//
//void connectOOo() throws Exception //IOException, InterruptedException
//{
//	proc = Runtime.getRuntime().exec(
//		"ooffice -headless -accept=socket,port=8100;urp;StarOffice.ServiceManager");
//	stderr = proc.getErrorStream();
//	
//	// Handle the OOo server we just started.
//	Thread ooThread = new Thread() {
//	public void run() {
//		try {
//			String line = null;
//			int c;
//			while ((c = stderr.read()) > 0) ;
//			proc.destroy();
//		} catch (Throwable t) {
//			exceptions[E_OO] = t;
//			t.printStackTrace();
//		}
//		System.out.println("ooThread exiting");
//	}};
//	ooThread.start();
//
//	// connect to the OpenOffice.org instance we just started or ressucitated
//	for (int i=0; ; ++i) {
//		try {
//			System.out.println("Connection try " + i);
//			connection = new SocketOpenOfficeConnection("127.0.0.1", 8100);
//			connection.connect();
//			break;
//		} catch(Exception e) {
//			if (i < 10) Thread.currentThread().sleep(1000);
//			else throw e;
//		}
//	}
//
//}
//
//void closeOOo() throws IOException
//{
//	// close the connection to OOo
//	connection.disconnect();
//	stderr.close();
//	proc.destroy();
//}
//
//public void exec(final InputStream[] templateIn, final Object[] dataModel, final OutputStream pdfOut) throws Exception
//{
//	DocumentFormatRegistry registry = new XmlDocumentFormatRegistry(); 
//	try {
//		connectOOo();
//		
//		// Set up pipes for concatenating PDF files
//		final ConcatPdfWriter concat = new ConcatPdfWriter(pdfOut);
//		
//			
//		for (int i=0; i<templateIn.length; ++i) {
//			System.out.println("Processing file " + i);
//			
//			// Set up pipes and basic filling-in of the template
//			final PipedInputStream pin = new PipedInputStream();
//			final PipedOutputStream pout = new PipedOutputStream(pin);
//			final int finalI = i;
//			new Thread() { public void run() {
//				try {
//					DocumentTemplate template = new ZippedDocumentTemplate(templateIn[finalI]);
//					template.createDocument(dataModel[finalI], pout);
//				} catch(Throwable e) {
//					exceptions[E_TEMPLATE] = e;
//					e.printStackTrace();
//				}
//			}}.start();
//
//			
//			// Set up pipes to copy to concat
//			final PipedInputStream pin2 = new PipedInputStream();
//			final PipedOutputStream pout2 = new PipedOutputStream(pin2);
//			Thread concatThread = new Thread() { public void run() {
//				try {
//					System.out.println("Concat Running");
//					concat.writePdfDoc(pin2);
//					pin2.close();
//					concat.flush();
//					System.out.println("Concat done running");
//				} catch(Throwable e) {
//					e.printStackTrace();
//					exceptions[E_CONCAT] = e;
//				}
//			}};
//			concatThread.start();
//		
//			// Use our OOo connection to translate the document
//			DocumentConverter converter = new OpenOfficeDocumentConverter(connection, registry); 
//			DocumentFormat odt = registry.getFormatByFileExtension("odt"); 
//			DocumentFormat pdf = registry.getFormatByFileExtension("pdf"); 
//			converter.convert(pin, odt, pout2, pdf);
//			pout2.close();		// Flush out, allows concatThread to finish.
//
//			System.out.println("Done processing file " + i);
//			concatThread.join();	// Wait for concatenation to finish before moving on
//			
//			// Close the pipes
//			pin.close();
//			pout.close();
//		}
//		concat.close();
//	} catch(Exception e) {
//		throw new Exception(
//			exceptions[0].getMessage() + "\n" +
//			exceptions[1].getMessage() + "\n" +
//			exceptions[2].getMessage() + "\n");
//	} finally {
//		try {
//			closeOOo();
//		} catch(Exception e) {
//			System.out.println("Exception closing: ");
//			e.printStackTrace();
//			throw e;
//		}
//	}
//}
//
//
//public static void main(String[] args) throws Exception
//{
//	File dir = new File("reports");
//	final Map data = new HashMap();
//	data.put("name", "Joe");
//	
//	ArrayList items = new ArrayList();
//	Map i1 = new HashMap();
//		i1.put("firstname", "Martha");
//		i1.put("lastname", "Magpie");
//		items.add(i1);
//	i1 = new HashMap();
//		i1.put("firstname", "Joe");
//		i1.put("lastname", "Schmoe");
//		items.add(i1);		
//	data.put("items", items);
//	
//	InputStream[] templateIn = new InputStream[] {
//		new FileInputStream(new File(dir, "test1.odt")),
//		new FileInputStream(new File(dir, "test1.odt")),
//		new FileInputStream(new File(dir, "test1.odt")),
//		new FileInputStream(new File(dir, "test1.odt")),
//		new FileInputStream(new File(dir, "test1.odt")),
//	};
//	Object[] datas = new Object[] {data,data,data,data,data};
//	OutputStream pdfOut = new FileOutputStream(new File(dir, "test1-out.pdf"));
//
//	try {
//		new PdfJooReport().exec(templateIn, datas, pdfOut);
//	} finally {
//		pdfOut.close();
//	}
//}
//}
