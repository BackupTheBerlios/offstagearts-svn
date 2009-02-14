///*
//OffstageArts: Enterprise Database for Arts Organizations
//This file Copyright (c) 2005-2008 by Robert Fischer
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.
//*/
///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package offstage.db;
//
//import citibob.app.App;
//import citibob.io.sslrelay.SSLRelayClient;
//import citibob.sql.ConnFactory;
//import citibob.sql.JDBCConnFactory;
//import citibob.sql.SSLConnFactory;
//import citibob.sql.WrapConnFactory;
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.MalformedURLException;
//import java.net.UnknownHostException;
//import java.sql.Statement;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.Enumeration;
//import java.util.Properties;
//import offstage.FrontApp;
//
///**
// *
// * @author citibob
// */
//public class OffstageConnFactory extends WrapConnFactory
//{
//
////private static URL getResourceOrURL(Properties props, String propName)
////throws MalformedURLException
////{
////	String surl;
////	surl = props.getProperty(propName, "");
////	if (surl.contains("://")) {
////		// Interpret as URL
////		return new URL(surl);
////	} else {
////		// Interpret as the name of a resource
////		return OffstageConnFactory.class.getClassLoader().getResource(surl);
////	}
////	
////}
//	
//private static ConnFactory newSSLSub(FrontApp app)
//throws ClassNotFoundException, UnknownHostException, MalformedURLException, IOException
//{
//	Properties props = app.props();
//	
//	final Properties p2 = new Properties();
//	final String url;
//
//	// Set up the sub-properties
//	p2.setProperty("user", props.getProperty("db.user", null));
//	String pwd = props.getProperty("db.password", null);
//	if (pwd != null) p2.setProperty("password", pwd);
//
//	// Put together as URL
//	Class.forName(props.getProperty("db.driverclass", null));
//	url = "jdbc:" + props.getProperty("db.drivertype", null) + "://" +
//		"127.0.0.1" +
////		props.getProperty("db.host", null) +
//		":%port%/" + props.getProperty("db.database", null);
//	
//	// Set the SSL tunnel parameters
//	String defaultPass = "keyst0re";
//	SSLRelayClient.Params prm = new SSLRelayClient.Params();
////		prm.storeURL = getResourceOrURL(props, "db.store");
////		prm.trustURL = getResourceOrURL(props, "db.trust");
//		String dbUserName = props.getProperty("db.user", null);
//		
//		prm.storeBytes = app.config().getStreamBytes(dbUserName + "-store.jks");
//		prm.trustBytes = app.config().getStreamBytes(dbUserName + "-trust.jks");
////		prm.storeBytes = app.decryptURL(app.getConfigResource(dbUserName + "-store.jks"));
////		prm.trustBytes = app.decryptURL(app.getConfigResource(dbUserName + "-trust.jks"));
//
//		prm.dest = InetAddress.getByName(props.getProperty("db.host", null));
//		prm.destPort = Integer.parseInt(props.getProperty("db.port", null));
//		
//		String storePass = (String)props.getProperty("db.storePass", defaultPass);
//		prm.storePass = storePass.toCharArray();
//		String storeKeyPass = (String)props.getProperty("db.storeKeyPass", storePass);
//		prm.storeKeyPass = storeKeyPass.toCharArray();
//		String trustPass = (String)props.getProperty("db.trustPass", storePass);
//		prm.trustPass = trustPass.toCharArray();
//	return new SSLConnFactory(url, prm, p2, app.expHandler());
//}
//	
//private static ConnFactory newPlainSub(App app)
//throws ClassNotFoundException
////throws java.util.prefs.BackingStoreException, java.sql.SQLException, ClassNotFoundException
//{
//	Properties props = app.props();
//	final Properties p2 = new Properties();
//	final String url;
//
////for (Enumeration en = props.keys(); en.hasMoreElements();) {
////	Object key = en.nextElement();
////	System.out.println(key + " = " + props.get(key));
////}
////System.out.println("db.driverclass = " + props.getProperty("db.driverclass", null));
//	Class.forName(props.getProperty("db.driverclass", null));
//	p2.setProperty("user", props.getProperty("db.user", null));
//
////	// PostgreSQL interprets any setting of the "ssl" property
////	// as a request for SSL.
////	// See: http://archives.free.net.ph/message/20080128.165732.7c127d6b.en.html
////	String sssl = props.getProperty("db.ssl", "false");
////	boolean ssl = (sssl.toLowerCase().equals("true"));
////	if (ssl) p2.setProperty("ssl", "true");
//	
//	String pwd = props.getProperty("db.password", null);
//	if (pwd != null) p2.setProperty("password", pwd);
//
//	url = "jdbc:" + props.getProperty("db.drivertype", null) + "://" +
//		props.getProperty("db.host", null) +
//		":" + props.getProperty("db.port", null) +
//		"/" + props.getProperty("db.database", null);
//	return new JDBCConnFactory(url, p2);
//}
//
//public Connection create() throws SQLException
//{
//	Connection dbb = super.create();
//	Statement st = null;
//	try {
//		st = dbb.createStatement();
//		// All timestamps should be stored in GMT in the database.
//		st.execute("set session time zone 'GMT';");
//	} finally {
//		try { st.close(); } catch(SQLException e2) {}
//	}
//	return dbb;
//}
//
//
//public OffstageConnFactory(FrontApp app)
//throws ClassNotFoundException, UnknownHostException, MalformedURLException, IOException
//{
//	Properties props = app.props();
//	
//	String sssl = props.getProperty("db.ssl", "false");
//	boolean ssl = (sssl.toLowerCase().equals("true"));
//	if (ssl) {
//		init(newSSLSub(app));
//	} else {
//		init(newPlainSub(app));
//	}
//}
//	
//}
