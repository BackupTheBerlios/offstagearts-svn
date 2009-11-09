
import citibob.gui.AppLauncher;
import offstage.cleanse.MergePurge;
import offstage.crypt.PBECrypt;
import offstage.launch.ConfigsFile;
import offstage.launch.Demo;
import offstage.launch.Dialog;
import offstage.licensor.Licensor;
import citibob.licensor.MakeLauncher;
import offstage.gui.RelBrowser;
import offstage.launch.Custom;
import offstage.licensor.WriteJarList;



/**
 *
 * @author citibob
 */
public class Main {
public static void main(String[] args) throws Exception {

	AppLauncher.launch("offstagearts", new Class[] {
		RelBrowser.class,
		Custom.class,
		Dialog.class,
		Demo.class,
		ConfigsFile.class,
		Licensor.class,
		WriteJarList.class,
		MergePurge.class,
		MakeLauncher.class,
		PBECrypt.class,
	});
}
}
