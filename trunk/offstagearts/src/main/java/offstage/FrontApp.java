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
import citibob.resource.ResData;
import citibob.resource.ResResult;
import citibob.resource.ResSet;
import citibob.resource.UpgradePlan;
import citibob.resource.UpgradePlanSet;
import citibob.swingers.JavaSwingerMap;
import citibob.version.Version;
import java.net.URL;
import java.net.URLClassLoader;
import javax.swing.JOptionPane;
import offstage.config.ConfigChooser;
import offstage.config.UpgradesDialog;
import offstage.resource.OffstageResSet;
import offstage.school.tuition.TuitionScale;

public class FrontApp extends citibob.app.App
{
Version version;


public static final int ACTIONS_SCREEN = 0;
public static final int PEOPLE_SCREEN = 1;
//public static final int SCHOOL_SCREEN = 2;
public static final int MAILINGS_SCREEN = 2;
int screen = PEOPLE_SCREEN;

final File configDir;
final String configName;

/** Connection to our SQL database. */
//Connection db;
Properties props;
ResSet resSet;
int sysVersion;
KeyRing keyRing;
DbChangeModel dbChange;
ConnPool pool;
Stack<SqlBatchSet> batchSets = new Stack();
//SqlBatchSet batchSet;
SwingerMap swingerMap;
//SFormatMap sFormatterMap;
OffstageSchemaSet sset;
EQuerySchema equerySchema;
citibob.reports.Reports reports;
FrameSet frameSet;
ClassLoader siteCode;

//FullEntityDbModel fullEntityDm;
//EQueryModel2 equeries;
//MailingModel2 mailings;
//EntityListTableModel simpleSearchResults;
SwingJobRun guiRunner;		// Run user-initiated actions; when user hits button, etc.
	// This will put on queue, etc.
JobRun appRunner;		// Run secondary events, in response to other events.  Just run immediately
MailSender mailSender;	// Way to send mail (TODO: make this class MVC.)
SqlTypeSet sqlTypeSet;		// Conversion between SQL types and SqlType objects
ExpHandler expHandler;

int loginID;			// entityID of logged-in database application user
TreeSet<String> loginGroups;	// Groups to which logged-in user belongs (by name)
citibob.jschema.log.QueryLogger logger;			// Log all changes to database
SwingPrefs swingPrefs = new SwingPrefs();

/** TODO: This is temporary. */
public static final TimeZone timeZone = TimeZone.getTimeZone("US/Eastern");
public static final SqlDate sqlDate = new SqlDate(timeZone, true);	// Used for on-the-fly Sql creation
public static final SqlTimestamp sqlTimestamp = new SqlTimestamp("GMT", true);
//public static final TimeZone timeZone = TimeZone.getTimeZone("US/Pacific");
//public static final TimeZone timeZone = TimeZone.getTimeZone("Americas/Chicago");
// -------------------------------------------------------
public Version version() { return version; }
public int sysVersion() { return sysVersion; }
public Properties props() { return props; }
public ResSet resSet() { return resSet; }
public KeyRing getKeyRing() { return keyRing; }
public TimeZone timeZone() { return timeZone; }

public SwingPrefs swingPrefs() { return swingPrefs; }
public QueryLogger queryLogger() { return logger; }
public int getLoginID() { return loginID; }
public ConnPool pool() { return pool; }
public SqlBatchSet batchSet() { return batchSets.peek(); }
public void pushBatchSet()
{
	SqlBatchSet bs = new SqlBatchSet(pool);
	batchSets.push(bs);
}
public void popBatchSet() throws Exception
{
	SqlBatchSet bs = batchSets.pop();
	bs.runBatches();
}
public ExpHandler expHandler() { return expHandler; }
public File configDir() { return configDir; }
public String getConfigName() { return configName; }
public void runGui(java.awt.Component c, CBTask r) { guiRunner.doRun(c, r); }
/** Only runs the action if logged-in user is a member of the correct group.
 TODO: This functionality should be maybe in the TaskRunner? */
public void runGui(java.awt.Component c, String group, CBTask r) {
	runGui(c,r);
//	if (loginGroups.contains(group)) {
//		runGui(c,r);
//	} else {
//		javax.swing.JOptionPane.showMessageDialog(c, "You are not authorized for that action.");
//	}
}
public void runGui(java.awt.Component c, String[] groups, CBTask r)
{
	runGui(c,r);
	
//	if (groups == null) {
//		runGui(c, r);
//		return;
//	}
//	for (String g : groups) {
//		if (loginGroups.contains(g)) {
//			runGui(c,r);
//			return;
//		}
//	}
//	javax.swing.JOptionPane.showMessageDialog(c, "You are not authorized for that action.");
}

//public void runGui(CBRunnable r) { guiRunner.doRun(null, r); }
public void runApp(CBTask r) { appRunner.doRun(r); }
public MailSender mailSender() { return mailSender; }
public SqlSchema getSchema(String name) { return sset.get(name); }
public FrameSet frameSet() { return frameSet; }
public ClassLoader getSiteCode() { return siteCode; }
public citibob.sql.SqlTypeSet sqlTypeSet() { return sqlTypeSet; }
public citibob.reports.Reports reports() { return reports; }

public SwingJobRun guiRun() { return guiRunner; }
public JobRun appRun() { return appRunner; }

// ----------------------------------------------------------------------
Preferences pUserRoot;

/** @returns Root user preferences node for this application */
public java.util.prefs.Preferences userRoot()
{
	return pUserRoot;
}

/** Make sure preferences are initialized on first run. */
private void initPrefs()
throws BackingStoreException, IOException, InvalidPreferencesFormatException
{
	Preferences prefs = Preferences.userRoot().node("offstage").node("gui");
	Version[] pvers = Version.getAvailablePrefVersions(prefs);
	
	// Ignore versions greater than our version
	for (int iver = pvers.length - 1; iver >= 0; --iver) {
		if (pvers[iver].size() < 2) continue;	// Ignore
		if (version.compareTo(pvers[iver], 2) == 0) {
			// We have the version we want.  Use it!
			pUserRoot = prefs.node(pvers[iver].toString());
			return;
		}
	}
	
	// Our version does not exist; create it.
	pUserRoot = prefs.node(version.toString(2));
	Preferences.importPreferences(getClass().getClassLoader().getResourceAsStream(
		"offstage/config/prefs.xml"));
}
// ------------------------------------------------------------------








//public Connection createConnection()
//throws SQLException
//{
//	return DBConnection.getConnection();
//}
// -------------------------------------------------------

void loadPropFile(Properties props, String name) throws IOException
{
	InputStream in;
	
	// First: load JAR-based properties
	in = getClass().getClassLoader().getResourceAsStream("offstage/config/" + name);
	props.load(in);
	in.close();

	// Next: Override with anything in user-created overrides
	File f = new File(configDir, name);
	if (f.exists()) {
		in = new FileInputStream(f);
		props.load(in);
		in.close();
	}
}	
	
//	// First: try loading external file
////	File dir = new File(System.getProperty("user.dir"), "config");
//	File f = new File(configDir, name);
//	if (f.exists()) return new FileInputStream(f);
//
//	// File doesn't exist; read from inside JAR file instead.
//	Class klass = offstage.config.OffstageVersion.class;
//	String resourceName = klass.getPackage().getName().replace('.', '/') + "/" + name;
//	return klass.getClassLoader().getResourceAsStream(resourceName);

Properties loadProps() throws IOException
{
	Properties props = new Properties();

	loadPropFile(props, "app.properties");
//	props = new Properties();
//	InputStream in = openPropFile("app.properties");
//	props.load(in);


//	props = new Properties(props);
//	props.load(openPropFile("site.properties"));

//	props = new Properties(props);
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
	version = new Version("1.0.1");
	sysVersion = 17;			// Internal version number

	// Set up Swing GUI preferences, so we can display stuff
	initPrefs();
	
	// Choose the configuration directory, so we can get the rest of
	// the configuration
	Preferences configPrefs = Preferences.userRoot().node("offstage").node("config");
	ConfigChooser dialog = new ConfigChooser(configPrefs,
		new JavaSwingerMap(TimeZone.getDefault()), userRoot(), version);
	dialog.setVisible(true);
	System.out.println(dialog.getConfigFile());
	configDir = dialog.getConfigFile();
	configName = dialog.getConfigName();
	if (configDir == null) System.exit(0);
	
	// Load up properties from the configuration
	props = loadProps();

	// Re-direct (and cache recent) STDOUT
	frameSet = new offstage.gui.OffstageFrameSet(this);
	ConsoleFrame consoleFrame = (ConsoleFrame)frameSet.getFrame("console");

	// Set up exception handler
	expHandler = new MailExpHandler(this, //mailSender,
		new InternetAddress(props.getProperty("mail.bugs.recipient")),
//		new InternetAddress("citibob@citibob.net"),
		"OffstageArts", consoleFrame.getDocument());
	guiRunner = new BusybeeDbJobRun(this, expHandler);
	appRunner = new SimpleDbJobRun(this, expHandler);

	// Use the exception handler
	try {
	
		// Set up database connections, etc.
		this.pool = DB.newConnPool(props);
	//	configDir = new File(System.getProperty("user.dir"), "config");
	//configDir = new File("/export/home/citibob/svn/offstage/config");

		// Load the crypto keys
	//	File userDir = new File(System.getProperty("user.dir"));
	//	File pubDir = new File(userDir, props.getProperty("crypt.pubdir"));
		String pubLeaf = props.getProperty("crypt.pubdir");
		File pubDir = (pubLeaf.charAt(0) == File.separatorChar ?
			new File(pubLeaf) : new File(configDir, pubLeaf)); 

		String privLeaf = props.getProperty("crypt.privdir");
		File privDir = (privLeaf.charAt(0) == File.separatorChar ?
			new File(privLeaf) : new File(configDir, privLeaf)); 
		keyRing = new KeyRing(pubDir, privDir);
		if (!keyRing.pubKeyLoaded()) {
//			javax.swing.JOptionPane.showMessageDialog(null,
//				"The public key failed to load.\n" +
//				"You will be unable to enter credit card details.");
		}

		this.mailSender = new citibob.mail.ConstMailSender(props);
	//	this.swingerMap = new citibob.sql.pgsql.SqlSwingerMap();
		this.sqlTypeSet = new citibob.sql.pgsql.PgsqlTypeSet();
		this.swingerMap = new offstage.types.OffstageSwingerMap(timeZone());
	//	this.sFormatterMap = new offstage.types.OffstageSFormatMap();

		this.pool = pool;
		this.batchSets = new Stack();
		pushBatchSet();
		
		// ================
	} catch(Exception e) {
		expHandler.consume(e);
//		e.printStackTrace();
		System.exit(-1);
	}
}

private void createResSet(SqlBatchSet str)
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
	try {
		final FrontApp app = this;

		dbChange = new DbChangeModel();
		final SqlBatchSet str = app.batchSet();
		createResSet(str);
		ResData rdata = new ResData(str, resSet, sqlTypeSet());
		str.flush();	

		// See if resources need upgrading
		final UpgradePlanSet upset = new UpgradePlanSet(rdata, app.sysVersion());
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
//				public void run(SqlRunner xstr) throws Exception {
					for (UpgradePlan up : upset.uplans) {
						up.applyPlan(str, app.pool());
					}
					createResSet(str);		// We might now have a database!
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
		SqlBatchSet str = new SqlBatchSet(pool);

		//pool = new DBConnPool();
	//	MailSender sender = new GuiMailSender();
		//guiRunner = new SimpleDbTaskRunner(pool);

		final ResResult siteCodeRes = resSet().load(str, "sitecode.jar", 0);
		str.execUpdate(new UpdRunnable() {
		public void run(SqlRunner str) throws IOException {
			if (siteCodeRes.bytes != null) {
				// Save our site code to a temporary jar file
				File outFile = File.createTempFile("sitecode", ".jar");
				outFile.deleteOnExit();
				OutputStream out = new FileOutputStream(outFile);
				out.write(siteCodeRes.bytes);
				out.close();
				
				// Create a classloader on that jar file
				siteCode = new URLClassLoader(new URL[] {new URL(
					"file:///" + outFile.getPath())});
			} else {
				// No site code available!
				// Just use current classloader, hope for the best
				siteCode = getClass().getClassLoader();
			}
		}});
		
		
		// Figure out who we're logged in as
		String sql = "select entityid from dblogins where username = " +
			SqlString.sql(System.getProperty("user.name"));
		str.execSql(sql, new RsRunnable() {
		public void run(SqlRunner str, ResultSet rs) throws SQLException {
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
		str.execSql(sql, new RsRunnable() {
		public void run(SqlRunner str, ResultSet rs) throws SQLException {
			while (rs.next()) loginGroups.add(rs.getString("name"));
			rs.close();
		}});

		this.sset = new OffstageSchemaSet(str, dbChange, timeZone());
		str.runBatches();		// Our SchemaSet must be set up before we go on.
		// ================

		// ================
		str = new SqlBatchSet(pool);
		logger = new OffstageQueryLogger(appRun(), getLoginID());	
	//	fullEntityDm = new FullEntityDbModel(this);
	//	mailings = new MailingModel2(str, this);//, appRunner);

	//	mailings.refreshMailingids();
	//		equeries = new EQueryModel2(st, mailings, sset);
	//	simpleSearchResults = new EntityListTableModel(this.getSqlTypeSet());

		equerySchema = new EQuerySchema(schemaSet());
		str.runBatches();
		// ================

		reports = new offstage.reports.OffstageReports(this);
	} catch(Exception e) {
		expHandler.consume(e);
//		e.printStackTrace();
		System.exit(-1);
	}
}

//public EntityListTableModel getSimpleSearchResults()
//	{ return simpleSearchResults; }
//public Statement createStatement() throws java.sql.SQLException
//	{ return db.createStatement(); }

//public Connection getDb()
//	{ return db; }

// ------------------------------------
//public FullEntityDbModel getFullEntityDm()
//	{ return fullEntityDm; }
//public MailingModel2 getMailingModel()
//	{ return mailings; }
//public EQueryModel2 getEQueryModel2()
//	{ return equeries; }
public DbChangeModel dbChange()
	{ return dbChange; }
public OffstageSchemaSet schemaSet() { return sset; }
public SwingerMap swingerMap() { return swingerMap; }
public SFormatMap sFormatMap() { return (SFormatMap)swingerMap; }
public EQuerySchema getEquerySchema() { return equerySchema;}
// -------------------------------------------------
public int getScreen()
{ return screen; }
public void setScreen(int s)
{
	this.screen = s;
	fireScreenChanged();
}
// -------------------------------------------------
// ===================================================
public static interface Listener
{
	void screenChanged();
}
public static class Adapter implements Listener
{
	public void screenChanged() {}
}
// ===================================================
// ===================================================
// Listener code
public LinkedList listeners = new LinkedList();
public void addListener(Listener l)
{ listeners.add(l); }
public void fireScreenChanged()
{
for (Iterator ii = listeners.iterator(); ii.hasNext(); ) {
	Listener l = (Listener)ii.next();
	l.screenChanged();
}
}
// ===================================================



}
