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
package offstage;
import java.sql.*;
import java.util.*;
import offstage.equery.*;
import citibob.task.*;
import citibob.sql.*;
import citibob.swing.typed.*;
import offstage.schema.*;
import citibob.mail.*;
import javax.mail.internet.*;
//import offstage.equery.swing.EQueryModel2;
import citibob.jschema.*;
import java.util.prefs.*;
import citibob.text.*;
import citibob.sql.pgsql.*;
import offstage.db.*;
import citibob.jschema.log.*;
import citibob.swing.prefs.*;
import java.io.*;
import offstage.crypt.*;
import citibob.gui.*;
import citibob.reports.ReportsApp;
import citibob.resource.ResData;
import citibob.resource.ResResult;
import citibob.resource.UpgradePlan;
import citibob.resource.UpgradePlanSet;
import citibob.swingers.JavaSwingerMap;
import citibob.version.SvnVersion;
import citibob.version.Version;
import com.sun.java_cup.internal.version;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Policy;
import javax.swing.JOptionPane;
import offstage.config.ConfigChooser;
import offstage.config.UpgradesDialog;
import offstage.resource.OffstageResSet;

public class FrontApp extends ReportsApp
{

// ==========================================================================
KeyRing keyRing;
/** Encryption and decryption keys for CC #s */
public KeyRing keyRing() { return keyRing; }

String configName;
/** Name of Offstage configuration directory used to connect to
 a specific OffstageArts database. */
public String configName() { return configName; }

ClassLoader siteCode;
public ClassLoader siteCode() { return siteCode; }

int loginID;
/** Who is logged in, based on ID gotten out of table from user's
 system login name. */
public int loginID() { return loginID; }

TreeSet<String> loginGroups;	// Groups to which logged-in user belongs (by name)
public TreeSet<String> loginGroups() { return loginGroups; }

EQuerySchema equerySchema;
public EQuerySchema equerySchema() { return equerySchema; }
// ==========================================================================
	
///** Make sure preferences are initialized on first run. */
//private void initPrefs()
//throws BackingStoreException, IOException, InvalidPreferencesFormatException
//{
//	Preferences prefs = Preferences.userRoot().node("offstage").node("gui");
//	Version[] pvers = Version.getAvailablePrefVersions(prefs);
//	
//	// Ignore versions greater than our version
//	for (int iver = pvers.length - 1; iver >= 0; --iver) {
//		if (pvers[iver].size() < 2) continue;	// Ignore
//		if (version.compareTo(pvers[iver], 2) == 0) {
//			// We have the version we want.  Use it!
//			userRoot = prefs.node(pvers[iver].toString());
//			return;
//		}
//	}
//	
//	// Our version does not exist; create it.
//	userRoot = prefs.node(version.toString(2));
//	Preferences.importPreferences(getClass().getClassLoader().getResourceAsStream(
//		"offstage/config/prefs.xml"));
//}

/** Read our base preferences from the JAR file */
public static Map<String,String> readBasePrefs() throws IOException
{
	Map<String,String> map = new TreeMap();
	
	URL url = FrontApp.class.getClassLoader().getResource("offstage/config/prefs.txt");
	BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	String line;
	while ((line = in.readLine()) != null) {
		int equals = line.indexOf('=');
		if (equals < 0) continue;
		
		String key = line.substring(0,equals);
		String value = line.substring(equals+1);
		map.put(key,value);
	}
	in.close();
	return map;
}

// -------------------------------------------------------
void loadPropFile(Properties props, String name) throws IOException
{
	InputStream in;
	
	// First: load JAR-based properties
	in = getClass().getClassLoader().getResourceAsStream("offstage/config/" + name);
	props.load(in);
	in.close();

	// Next: Override with anything in user-created overrides
	if (configDir != null) {
		File f = new File(configDir, name);
		if (f.exists()) {
			in = new FileInputStream(f);
			props.load(in);
			in.close();
		}
	}
}	
Properties loadProps() throws IOException
{
	Properties props = new Properties();

	loadPropFile(props, "app.properties");
	String os = System.getProperty("os.name");
        int space = os.indexOf(' ');
        if (space >= 0) os = os.substring(0,space);
	loadPropFile(props, os + ".properties");
	//if (inn != null) props.load(inn);

	return props;
}
// -------------------------------------------------------
public FrontApp()
throws Exception
//SQLException, java.io.IOException, javax.mail.internet.AddressException,
//java.security.GeneralSecurityException
{
	// Make sure we have the right version
	version = new Version("1.2.0");
	String resourceName = "offstage/version.txt";
	SvnVersion svers = new SvnVersion(getClass().getClassLoader().getResourceAsStream(resourceName));	
	sysVersion = svers.maxVersion;
//	sysVersion = 17;			// Internal version number

	// Set up Swing GUI preferences, so we can display stuff
	// initPrefs();
	userRoot = Preferences.userRoot().node("offstagearts/gui");
	Map<String,String> basePrefs = readBasePrefs();
	swingPrefs = new SwingPrefs(basePrefs);
	
	// Choose the configuration directory, so we can get the rest of
	// the configuration
	Preferences configPrefs = Preferences.userRoot().node("offstagearts").node("config");
	ConfigChooser dialog = new ConfigChooser(configPrefs,
		new JavaSwingerMap(TimeZone.getDefault()), swingPrefs, userRoot(), version.toString());
	dialog.setVisible(true);
	System.out.println(dialog.getConfigFile());
	if (dialog.isDemo()) {
		configDir = null;
		configName = "OffstageArts Demo";
	} else {
		configDir = dialog.getConfigFile();
		configName = dialog.getConfigName();
	}
//	if (configDir == null) System.exit(0);
	
	// Load up properties from the configuration
	props = loadProps();

	// Time Zone should be stored in database!
	timeZone = TimeZone.getDefault();
//	timeZone = TimeZone.getTimeZone(props.getProperty("timezone"));
	
	// Re-direct (and cache recent) STDOUT
	frameSet = new offstage.gui.OffstageFrameSet(this);
	ConsoleFrame consoleFrame = (ConsoleFrame)frameSet.getFrame("console");

	// Set up exception handler
	expHandler = new MailExpHandler(this, //mailSender,
		new InternetAddress(props.getProperty("mail.bugs.recipient")),
		"OffstageArts", consoleFrame.getDocument()) {
	public void consume(Throwable e) {
		if (e instanceof org.postgresql.util.PSQLException &&
			e.getMessage().contains("Connection refused")) {
			super.consume(new FatalAppError(
				"Cannot connect to database.\n" +
				"Please check your network settings.\n" +
				"OffstageArts is now exiting.",
				(Exception)e));
		} else {
			super.consume(e);
		}
	}};
	
//			// Re-interpret PostgreSQL connection problems
//			if (!psqle.getMessage().contains("Connection refused")) throw psqle;
//			throw new FatalAppError
//		}

//	}
	guiRun = new BusybeeDbJobRun(this, expHandler);
	//appRunner = new SimpleDbJobRun(this, expHandler);

	// Use the exception handler
	try {
	
		// Set up database connections, etc.
		this.pool = DB.newConnPool(props);
		this.sqlRun = new BatchSqlRun(pool, expHandler);

		// Load the crypto keys
		if (configDir != null) {
			String pubLeaf = props.getProperty("crypt.pubdir");
			File pubDir = (pubLeaf.charAt(0) == File.separatorChar ?
				new File(pubLeaf) : new File(configDir, pubLeaf)); 

			String privLeaf = props.getProperty("crypt.privdir");
			File privDir = (privLeaf.charAt(0) == File.separatorChar ?
				new File(privLeaf) : new File(configDir, privLeaf)); 
			keyRing = new KeyRing(pubDir, privDir);
			if (!keyRing.pubKeyLoaded()) {
				// Alert user...?
				//			javax.swing.JOptionPane.showMessageDialog(null,
				//				"The public key failed to load.\n" +
				//				"You will be unable to enter credit card details.");
			}
		}
		
		this.mailSender = new citibob.mail.ConstMailSender(props);
		this.sqlTypeSet = new citibob.sql.pgsql.PgsqlTypeSet();
		this.swingerMap = new offstage.types.OffstageSwingerMap(timeZone());
		this.sFormatMap = swingerMap;
		
		// ================
	} catch(Exception e) {
		expHandler.consume(e);
//		e.printStackTrace();
		System.exit(-1);
	}
}

private void createResSet(SqlRun str)
throws Exception
{
		// Set up resource set and read from database
		resSet = new OffstageResSet(str, dbChange);
		str.flush();
		resSet.createAllResourceIDs(str);
		str.flush();
}

public boolean checkResources()  throws Exception
{
	boolean ret = true;
//	try {
		try {
			final FrontApp app = this;

			dbChange = new DbChangeModel();
			final SqlRun str = app.sqlRun();
			createResSet(str);
			resData = new ResData(str, resSet, sqlTypeSet());
			str.flush();	

			// See if resources need upgrading
			final UpgradePlanSet upset = new UpgradePlanSet(resData, app.sysVersion());
			if (upset.reqCannotCreate.size() != 0 || upset.reqNotUpgradeable.size() != 0) {
				upset.print(System.out);
				throw new IOException("Cannot upgrade resources!");
			}
			if (upset.uplans.size() != 0) {
				UpgradesDialog udialog = new UpgradesDialog(null, true);
				udialog.initRuntime(str, app, upset.uplans);
				udialog.setVisible(true);
				if (udialog.doUpgrade) {
	//				app.runApp(new BatchRunnable() {
	//				public void run(SqlRun xstr) throws Exception {
//					try {
						for (UpgradePlan up : upset.uplans) {
							up.applyPlan(str, app.pool());
						}
						createResSet(str);		// We might now have a database!
//					} catch(Exception e) {
//						throw new FatalAppError(
//							"Error encountered while upgrading resources!\n" +
//							"Please consult your system administrator.",
//							e);
//					}
	//				}});

				} else {
					if (udialog.required) {
						JOptionPane.showMessageDialog(null,
							"OffstageArts cannot run without the required upgrades.\n");
						ret = false;
					}
				}
			}
			str.flush();
//		} catch(org.postgresql.util.PSQLException psqle) {
//			// Re-interpret PostgreSQL connection problems
//			if (!psqle.getMessage().contains("Connection refused")) throw psqle;
//			throw new FatalAppError
//		}
	} catch(Exception e) {
		expHandler.consume(e);
		System.exit(-1);
	}
	return ret;
}

/** Finishes initialization, things that require a functional database. */
public void initWithDatabase()
{
	try {
		SqlRun str = sqlRun();

		final ResResult siteCodeRes = resSet().load(str, "sitecode.jar", 0);
		str.execUpdate(new UpdTasklet() {
		public void run() throws IOException {
			if (siteCodeRes.bytes != null) {
				// Save our site code to a temporary jar file
				File outFile = File.createTempFile("sitecode", ".jar");
				System.out.println("Writing sitecode to: " + outFile);
				outFile.deleteOnExit();
				OutputStream out = new FileOutputStream(outFile);
				out.write(siteCodeRes.bytes);
				out.close();

				// Create a classloader on that jar file
				URL siteCodeURL = new URL("file:///" + outFile.getPath());
				siteCode = new URLClassLoader(new URL[] {siteCodeURL}, getClass().getClassLoader());
				
				// Set up security policy to prevent malicious code from sitecode.jar
				Policy.setPolicy(new OffstagePolicy(siteCodeURL));
				System.setSecurityManager(new SecurityManager());
			} else {
				// No site code available!
				// Just use current classloader, hope for the best
				siteCode = getClass().getClassLoader();
			}
		}});
		
		
		// Figure out who we're logged in as
		String sql = "select entityid from dblogins where username = " +
			SqlString.sql(System.getProperty("user.name"));
		str.execSql(sql, new RsTasklet() {
		public void run(ResultSet rs) throws SQLException {
			if (rs.next()) {
				loginID = rs.getInt("entityid");
			} else {
				loginID = -1;
			}
			rs.close();
		}});

		// Figure out what groups we belong to (for action permissions)
		loginGroups = new TreeSet();
		sql = " select distinct name from dblogingroups g, dblogingroupids gid" +
			" where g.entityid=" + SqlInteger.sql(loginID) +
			" and g.groupid = gid.groupid";
		str.execSql(sql, new RsTasklet() {
		public void run(ResultSet rs) throws SQLException {
			while (rs.next()) loginGroups.add(rs.getString("name"));
			rs.close();
		}});

		schemaSet = new OffstageSchemaSet(str, dbChange, timeZone());
		str.flush();		// Our SchemaSet must be set up before we go on.
		// ================

		// ================
		this.queryLogger = new OffstageQueryLogger(sqlRun(), loginID());	
	//	fullEntityDm = new FullEntityDbModel(this);
	//	mailings = new MailingModel2(str, this);//, appRunner);

	//	mailings.refreshMailingids();
	//		equeries = new EQueryModel2(st, mailings, sset);
	//	simpleSearchResults = new EntityListTableModel(this.getSqlTypeSet());

		equerySchema = new EQuerySchema(schemaSet());
		str.flush();
		// ================

		reports = new offstage.reports.OffstageReports(this);
	} catch(Exception e) {
		expHandler.consume(e);
//		e.printStackTrace();
		System.exit(-1);
	}
}
}
