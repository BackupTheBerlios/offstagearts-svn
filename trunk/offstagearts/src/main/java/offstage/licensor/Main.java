package offstage.licensor;


import citibob.gui.AppLauncher;
import citibob.licensor.Licensor;

/**
 *
 * @author citibob
 */
public class Main {
public static void main(String[] args) throws Exception {


	AppLauncher.launch("offstageConfig", new Class[] {
		Licensor.class,
		WritePrefs.class,
		WriteJNLP.class,
		WriteJarList.class,
		SignJars.class,
	});
}
}
