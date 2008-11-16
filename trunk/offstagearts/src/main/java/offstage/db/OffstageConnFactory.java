/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.db;

import citibob.sql.ConnFactory;
import citibob.sql.JDBCConnFactory;
import citibob.sql.WrapConnFactory;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author citibob
 */
public class OffstageConnFactory extends WrapConnFactory
{

//private static URL getResourceOrURL(Properties props, String propName)
//{
//	String surl;
//	surl = props.getProperty("db.store", "");
//	if (surl.contains("://")) {
//		// Interpret as URL
//		return new URL(surl);
//	} else {
//		return OffstageConnFactory.class.getClassLoader().getResource(surl);
//	}
//	
//}
	
private static ConnFactory newSSLSub(Properties props)
throws ClassNotFoundException
{
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
//		props.getProperty("db.host", null) +
//		":%port%/" + props.getProperty("db.database", null);
//	
//	// Set the SSL tunnel parameters
//	String defaultPass = "keyst0re";
//	SSLRelayClient.Params prm = new SSLRelayClient.Paramas();
//		prm.storeURL = getResourceOrURL(props, "db.store");
//		prm.trustURL = getResourceOrURL(props, "db.trust");
//		prm.storePass = ((String)props.getProperty("db.storePass", defaultPass)).toCharArray();
//		prm.trustPass
//	return new SSLConnFactory(url, p2);	
	return null;
}
	
private static ConnFactory newSub(Properties props)
throws ClassNotFoundException
//throws java.util.prefs.BackingStoreException, java.sql.SQLException, ClassNotFoundException
{
	final Properties p2 = new Properties();
	final String url;

System.out.println("db.driverclass = " + props.getProperty("db.driverclass", null));
	Class.forName(props.getProperty("db.driverclass", null));
	p2.setProperty("user", props.getProperty("db.user", null));

	// PostgreSQL interprets any setting of the "ssl" property
	// as a request for SSL.
	// See: http://archives.free.net.ph/message/20080128.165732.7c127d6b.en.html
	String sssl = props.getProperty("db.ssl", "false");
	boolean ssl = (sssl.toLowerCase().equals("true"));
	if (ssl) p2.setProperty("ssl", "true");
	
	String pwd = props.getProperty("db.password", null);
	if (pwd != null) p2.setProperty("password", pwd);

	url = "jdbc:" + props.getProperty("db.drivertype", null) + "://" +
		props.getProperty("db.host", null) +
		":" + props.getProperty("db.port", null) +
		"/" + props.getProperty("db.database", null);
	return new JDBCConnFactory(url, p2);
}
	
public Connection create() throws SQLException
{
	Connection dbb = super.create();
	Statement st = null;
	try {
		st = dbb.createStatement();
		// All timestamps should be stored in GMT in the database.
		st.execute("set session time zone 'GMT';");
	} finally {
		try { st.close(); } catch(SQLException e2) {}
	}
	return dbb;
}

public OffstageConnFactory(Properties props)
throws ClassNotFoundException
{
	super(newSub(props));
}
	
}
