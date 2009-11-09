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
import citibob.config.Config;
import citibob.config.ConfigMaker;
import citibob.config.MultiConfig;
import citibob.config.PBEConfig;
import citibob.config.ResourceConfig;
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
import citibob.version.SvnVersion;
import citibob.version.Version;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Policy;
import javax.swing.JOptionPane;
import citibob.config.dialog.UpgradesDialog;
import citibob.crypt.DialogPBEAuth;
import javax.swing.UIManager;
import offstage.datatab.DataTabSet;
import offstage.resource.OffstageResSet;

public class FrontApp extends ReportsApp
{

// ==========================================================================
KeyRing keyRing;
/** Encryption and decryption keys for CC #s */
public KeyRing keyRing() { return keyRing; }

//String configName;
///** Name of Offstage configuration directory used to connect to
// a specific OffstageArts database. */
//public String configName() { return configName; }

ClassLoader siteCode;
public ClassLoader siteCode() { return siteCode; }

public Object newSiteInstance(Class defaultClass) {
	return newSiteInstance("sc." + defaultClass.getName(), defaultClass);
}
public Object newSiteInstance(Class defaultClass, Object... params) {
	return newSiteInstance("sc." + defaultClass.getName(), defaultClass, params);
}
public Object newSiteInstance(String className, Class defaultClass, Object... params) {
	Class klass = defaultClass;
	try {
		klass = siteCode().loadClass(className);
	} catch(Exception e) {}
	try {
		System.out.println("FrontApp.newSiteInstance(" + klass.getName() + ")");
		if (params.length == 0) {
			return klass.newInstance();
		} else {
			Class[] types = new Class[params.length];
			for (int i=0; i<params.length; ++i) types[i] = params[i].getClass();
			
			Constructor[] cons = klass.getConstructors();
			Constructor con = null;
			outer: for (int i=0; i<cons.length; ++i) {
				Class[] paramTypes = cons[i].getParameterTypes();
				if (paramTypes.length != types.length) continue;
				for (int j=0; j<types.length; ++j) {
					Class param = paramTypes[j];
					Class actual = types[j];
					if (!param.isAssignableFrom(actual)) continue outer;
				}
				con = cons[i];
				break;
			}
//			Constructor con = klass.getConstructor(new Class[] {ResSet.class});
//			Constructor con = klass.getConstructor(types);
			return con.newInstance(params);
		}
	} catch(Exception e) {
		return null;
	}
}
//public Object newSiteInstance(String className, Class defaultClass) {
//	Class klass = defaultClass;
//	try {
//		klass = siteCode().loadClass(className);
//	} catch(Exception e) {}
//	try {
//		System.out.println("FrontApp.newSiteInstance(" + klass.getName() + ")");
//		return klass.newInstance();
//	} catch(Exception e) {
//		return null;
//	}
//}

public InputStream getSiteResourceAsStream(String name)
{
	InputStream in = siteCode().getResourceAsStream("sc/" + name);
	if (in != null) return in;
	return siteCode().getResourceAsStream(name);
}


///** Retrieves a file from the config directory --- whether it's on disk
// * or inside a JAR file.
// * @param name
// * @return
// */
//private static URL getConfigResource(String name)
//{
//	new URL(configURL(), name);
//}


int loginID;
/** Who is logged in, based on ID gotten out of table from user's
 system login name. */
public int loginID() { return loginID; }

TreeSet<String> loginGroups;	// Groups to which logged-in user belongs (by name)
public TreeSet<String> loginGroups() { return loginGroups; }

EQuerySchema equerySchema;
public EQuerySchema equerySchema() { return equerySchema; }

Jango jango;
public Jango jango() { return jango; }

DataTabSet dataTabSet;
public DataTabSet dataTabSet() { return dataTabSet; }

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
	
	URL url = FrontApp.class.getClassLoader().getResource("offstage/baseprefs.txt");
//	URL url = getConfigResource("prefs.txt");
//System.out.println("BasePrefsURL = " + url);
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
//PasswordDialog passwordDialog;
//public byte[] decryptURL(URL url) throws IOException
//{
//	// Load the (possibly encrypted) contents of the file
//	byte[] contents;
//	try {
//		InputStream in = url.openStream();
//		contents = StreamUtils.getBytes(in);
//		in.close();
//	} catch(Exception e) {
//		// It didn't exit!
//		return null;
//	}
//	
//	return decryptContents(contents);
//}
//
///** Given the URL of a (possibly encrypted) resource, returns the
// * decrypted version of it.
// * @param url
// */
//public byte[] decryptContents(byte[] contents) throws IOException
//{	
//	// Read the first line of the contents for encryption header
//	BufferedReader reader =
//		new BufferedReader(
//		new InputStreamReader(
//		new ByteArrayInputStream(contents)));
//	String firstLine = reader.readLine();
//	int encrypt = firstLine.indexOf(PBECrypt.BEGIN_ENCRYPTED);
//	
//	// Decrypt if necessary
//	if (encrypt < 0) {
//		// Not encrypted, just read normally
//		return contents;
//	} else {
//		boolean showDialog = true;
//		if (passwordDialog.getHasShown()) showDialog = false;
//		passwordDialog.setAlert("");
//		
//		// It is encrypted; find the password, etc.
//		PBECrypt pbe = new PBECrypt();
//		for (;;) {
//			if (showDialog) {
//				passwordDialog.setVisible(true);
//				if (!passwordDialog.getOK()) System.exit(0);
//			}
//			
//			char[] password = passwordDialog.getPassword();
//			boolean goodPassword = true;
//			try {
//				contents = pbe.decrypt(new String(contents), password);
//				return contents;
//			} catch(Exception e) {
//				goodPassword = false;
//			}
//
//			// Decrypted file didn't load; bad password
//			passwordDialog.setAlert("Bad password; try again!");
//			showDialog = true;
//		}
//	}
//	
//}
//void loadPropFile(Properties props, String name) throws IOException
//{
//	InputStream in;
//	
//	// First: load JAR-based (default) properties
//	URL url = getClass().getClassLoader().getResource("offstage/defaultconfig/" + name);
//System.out.println("loadPropFile: " + url);
////	in = getClass().getClassLoader().getResourceAsStream("offstage/config/" + name);
//	in = url.openStream();
//	props.load(in);
//	in.close();
//
//	
//	// Next: Override with anything in user-created overrides
//	// Get the contents of the file
//	url = getConfigResource(name);
//System.out.println("Loading config resource for " + name + ": " + url);
//	byte[] bcontents = decryptURL(url);
//	if (bcontents == null) return;		// Doesn't exist
//System.out.println("    bcontents = " + new String(bcontents));
//
//	
//	// Interpret the contents
//	try {
//		Properties nprops = new Properties();
//		nprops.load(new ByteArrayInputStream(bcontents));
//
//		// They loaded just fine; now load into real properties
//		props.load(new ByteArrayInputStream(bcontents));
//	} catch(Exception e) {
//		IOException ioe = new IOException("Bad properties file: " + url);
//		ioe.initCause(e);
//		throw ioe;
//	}
//}
//
//Properties loadProps() throws IOException
//{
//	Properties props = new Properties();
//
//	loadPropFile(props, "app.properties");
//	String os = System.getProperty("os.name");
//        int space = os.indexOf(' ');
//        if (space >= 0) os = os.substring(0,space);
//	loadPropFile(props, os + ".properties");
////	dialog.clear();
//	//if (inn != null) props.load(inn);
//
////try {
////	Thread.currentThread().sleep(100000);
////} catch(InterruptedException e) {}
//	
//	return props;
//}
// -------------------------------------------------------
///** Retrieves a resource from the config/ directory, whether it's
// * located on disk or in the classpath
// */
//public URL getConfigResource(String name) throws MalformedURLException
//{
//System.out.println("getConfigResource: " + name);
////System.out.println("    configURL = " + configURL);
////System.out.println("    configResourceFolder = " + configResourceFolder);
////	if (configURL != null) return new URL(configURL, name);
////	return getClass().getClassLoader().getResource(configResourceFolder + name);
//}

// -------------------------------------------------------
public static final int CT_CONFIGCHOOSE = 0;	// Connect via configuration directory
public static final int CT_CONFIGSET = 1;		// Connect to pre-specified configuration directory
public static final int CT_DEMO = 2;	// Use internal demo configuration
public static final int CT_OALAUNCH = 3;	// Use "internal" oalaunch configuration

/**
 * 
 * @param ctType
 * @param mainConfig The user-selected configuration
 * @throws java.lang.Exception
 */
public FrontApp(ConfigMaker configMaker)
//public FrontApp(String xconfigName, int ctType)
throws Exception
//SQLException, java.io.IOException, javax.mail.internet.AddressException,
//java.security.GeneralSecurityException
{	
	name = "OffstageArts";
	
	// Make sure we have the right version
	version = new Version("1.10.8");
//	version = new Version(WriteJNLP.getReleaseVersion3());
	String resourceName = "offstage/version.txt";
	SvnVersion svers = new SvnVersion(getClass().getClassLoader().getResourceAsStream(resourceName));	
	sysVersion = svers.maxVersion;
//	sysVersion = 17;			// Internal version number

	// Set up Swing GUI preferences, so we can display stuff
	// initPrefs();
	userRoot = Preferences.userRoot().node("offstagearts");

	// Load GUI Preferences
	Map<String,String> basePrefs = readBasePrefs();
	swingPrefs = new SwingPrefs(basePrefs);


	// Get the configuration
	Config mainConfig = configMaker.newConfig(this);
	if (mainConfig == null) System.exit(-1);
	config = new MultiConfig(
		new PBEConfig(mainConfig, new DialogPBEAuth(null, "Please enter configuration password.", null)),
		new ResourceConfig("offstage/defaultconfig"));
//	// Choose the configuration directory, so we can get the rest of
//	// the configuration
//	switch(ctType) {
//		case CT_CONFIGSET : {
//			Preferences configPrefs = Preferences.userRoot().node("offstagearts").node("config");
//			String sdir = configPrefs.get(xconfigName, null);
//			configURL = new File(sdir).toURL();
//			configName = xconfigName;
//		} break;
//		case CT_CONFIGCHOOSE : {
//			Preferences configPrefs = Preferences.userRoot().node("offstagearts").node("config");
//			ConfigDialog dialog = new ConfigDialog(configPrefs,
//				new JavaSwingerMap(TimeZone.getDefault()), swingPrefs, guiRoot(), version.toString());
//			dialog.setVisible(true);
//			if (dialog.isDemo()) {
//				configURL = null;
//				configName = "Demo";
//			} else {
//				configURL = dialog.getConfigFile().toURL();
//				configName = dialog.getConfigName();
//			}
//		} break;
//		case CT_DEMO : {
//			configURL = null;
//			configResourceFolder = "offstage/demo/";
////			configURL = getClass().getClassLoader().getResource("offstage/demo/");
//			configName = "Demo";
//		} break;
//		case CT_OALAUNCH : {
//			configURL = null;
//			configResourceFolder = "oalaunch/config/";
////			configURL = getClass().getClassLoader().getResource("oalaunch/config/");
//			
//
//////List<JarURL> jurls = ClassPathUtils.getClassPath();
//////for (JarURL jurl : jurls) System.out.println(jurl.getUrl());
////System.out.println(getClass());
////System.out.println(getClass().getClassLoader());
////System.out.println(getClass().getClassLoader().getResource("oalaunch/config/app.properties"));
////System.out.println(getClass().getClassLoader().getResource("oalaunch/config/"));
////
////System.out.println("in1 = " + getClass().getClassLoader().getResource("oalaunch/config/app.properties").openStream());
////System.out.println("in2 = " + getConfigResource("app.properties").openStream());
//
//			// Load up our OALaunch configuration properties
//			Properties oalaunch = new Properties();
//			ClassLoader clr = getClass().getClassLoader();
//			URL url = clr.getResource("oalaunch/oalaunch.properties");
//			InputStream in = url.openStream();
//			oalaunch.load(in);
//			in.close();
////Thread.currentThread().sleep(20000*1000L);
//
//			configName = oalaunch.getProperty("config.name", "OALaunch");
//if (configName == null) configName = "<null>";
//if ("".equals(configName)) configName = "<blank>";
//			
//			// Strip off slashes and stuff
//			int slash = configName.lastIndexOf('/');
//			if (slash >= 0) configName = configName.substring(slash+1);
//			slash = configName.lastIndexOf('\\');
//			if (slash >= 0) configName = configName.substring(slash+1);
//			slash = configName.lastIndexOf(".jar");
//			if (slash >= 0) configName = configName.substring(0, slash);
//			
//			
//		} break;
//	}

	//	if (configDir == null) System.exit(0);
//configURL = getClass().getClassLoader().getResource("offstage/config/");
	
	// Load up properties from the configuration
//	passwordDialog = new PasswordDialog(null);
	props = config.loadAppProperties();
	
	// Set up connection to JangoMail
	jango = new Jango(props);

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
//		OffstageConnFactory connFactory = new OffstageConnFactory(this); //props, expHandler);
		ConfigConnFactory connFactory = new ConfigConnFactory(config(), props(), expHandler());
		this.pool = new RealConnPool(connFactory);
		connFactory.setConnPool(this.pool);
		this.sqlRun = new BatchSqlRun(pool, expHandler);

//		// Load the crypto keys
//		// These must be loaded from a directory on disk, since they
//		// can be saved as well.  Unlike the stuff packaged in the jar
//		// file, they are NOT read-only.
// ... well, maybe have an external program read and write these.
		// We need a way to specify where the crypto keys
//		if (configURL != null) {
			File pubDir = new File(props.getProperty("crypt.pubdir"));
			File privDir = new File(props.getProperty("crypt.privdir"));
			keyRing = new KeyRing(pubDir, privDir);
			if (!keyRing.pubKeyLoaded()) {
				// Alert user...?
				//			javax.swing.JOptionPane.showMessageDialog(null,
				//				"The public key failed to load.\n" +
				//				"You will be unable to enter credit card details.");
			}
//		}
		
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
		resSet = new OffstageResSet(str, sysVersion(), dbChange);
		str.flush();
		resSet.createAllResourceIDs(str);
		str.flush();
}

/** Upgrade resources if needed */
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

/**
 * Finishes initialization, things that require a functional database.
 @param siteCodeFileName The sitecode.jar file to use.<ul>
 * <li>If null, then look at property name "sitecode.jar" for direction.</li>
 * <li>If "<none>" make sure NO sitecode is used, not even from the database.</li>
 * <li>If still null, then do standard loading of sitecode.jar from database.</li>
 * <li>Otherwise, load the named sitecode.jar file</li>
 * </ul>
 */
public void initWithDatabase(String siteCodeFileName)
{
	try {
		SqlRun str = sqlRun();

		// First look for sitecode.jar described in our configuration files
		if (siteCodeFileName == null) siteCodeFileName = props().getProperty("sitecode.jar");
		if ("<none>".equals(siteCodeFileName)) {
			// Ensure there is NO sitecode.
			siteCode = getClass().getClassLoader();
		} else if (siteCodeFileName != null) {
			// Create a classloader on that jar file
			URL siteCodeURL = new File(siteCodeFileName).toURL();
			siteCode = new URLClassLoader(new URL[] {siteCodeURL}, getClass().getClassLoader());
			
			// Set up security policy to prevent malicious code from sitecode.jar
			Policy.setPolicy(new OffstagePolicy(siteCodeURL));
			System.setSecurityManager(new SecurityManager());
		} else {
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
	//				URL siteCodeURL = new URL("file:" + outFile.getPath());
					URL siteCodeURL = outFile.toURL();
					siteCode = new URLClassLoader(new URL[] {siteCodeURL}, getClass().getClassLoader());

					// Set up security policy to prevent malicious code from sitecode.jar
					Policy.setPolicy(new OffstagePolicy(siteCodeURL));
					System.setSecurityManager(new SecurityManager());
				} else {
					// No site code available!
					// Just use current classloader, hope for the best
					siteCode = getClass().getClassLoader();
	//File siteCodeFile = new File(ClassPathUtils.getMavenProjectRoot(),
	//"../oamisc/sc_yfsc/target/sc_yfsc-1.0-SNAPSHOT.jar");
	//URL siteCodeURL = siteCodeFile.toURL();
	//siteCode = new URLClassLoader(new URL[] {siteCodeURL}, getClass().getClassLoader());
				}
			}});
			str.flush();		// Our site code must be loaded before we go on.
		}

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


//		schemaSet = new OffstageSchemaSet(str, dbChange, timeZone());
		OffstageSchemaSet oschemaSet = (OffstageSchemaSet)newSiteInstance(OffstageSchemaSet.class);
		oschemaSet.init(str, dbChange, timeZone());
		schemaSet = oschemaSet;

		dataTabSet = (DataTabSet)newSiteInstance(DataTabSet.class);
		dataTabSet.init(str, this);
		dataTabSet.addToSchemaSet(oschemaSet);
		
		str.flush();		// Our SchemaSet must be set up before we go on.
		// ================

		// ================
		this.queryLogger = new OffstageQueryLogger(sqlRun(), loginID());	
	//	fullEntityDm = new FullEntityDbModel(this);
	//	mailings = new MailingModel2(str, this);//, appRunner);

	//	mailings.refreshMailingids();
	//		equeries = new EQueryModel2(st, mailings, sset);
	//	simpleSearchResults = new EntityListTableModel(this.getSqlTypeSet());

		equerySchema = (EQuerySchema)newSiteInstance(EQuerySchema.class);
		equerySchema.init(schemaSet(), dataTabSet);
		str.flush();
		// ================

		reports = new offstage.reports.OffstageReports(this);
	} catch(Exception e) {
		expHandler.consume(e);
//		e.printStackTrace();
		System.exit(-1);
	}
}

// =================================================================
public static void launch(ConfigMaker configMaker) throws Exception {
	launch(configMaker, null);
}
/** Creates a new instance of FrameSetX */
public static void launch(ConfigMaker configMaker, String siteCodeFileName) throws Exception {
	/* see:
	http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6604109
	http://www.jasperforge.org/sf/go/artf2423
	*/
	System.setProperty("sun.java2d.print.polling", "false");

//	http://mail-archives.apache.org/mod_mbox/maven-users/200601.mbox/%3C92c218d10601171038kdccb3fbua7f97454adaebace@mail.gmail.com%3E	
	System.setProperty("javax.xml.parsers.SAXParserFactory",
		"com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
	
	System.setProperty("swing.metalTheme", "ocean");
	UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");


	try {
		final FrontApp app = new FrontApp(configMaker); //new File("/export/home/citibob/svn/offstage/config"));
		boolean resGood = app.checkResources();
		app.initWithDatabase(siteCodeFileName);

		if (resGood) {
			app.frameSet().openFrame("maintenance");
		} else {
			app.frameSet().openFrame("config");
		}
		app.sqlRun().flush();
	} catch(Exception e) {
		e.printStackTrace();
		MailExpDialog dialog = new MailExpDialog(null, "OffstageArts", e);
		dialog.setVisible(true);
		System.exit(-1);
	}
}

}
