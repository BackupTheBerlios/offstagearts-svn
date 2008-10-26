
import citibob.gui.AppLauncher;
import offstage.cleanse.MergePurge;
import offstage.crypt.PBECrypt;
import offstage.gui.DemoLauncher;
import offstage.gui.MainLauncher;
import offstage.licensor.Licensor;
import offstage.licensor.MakeLauncher;
import offstage.licensor.WriteJarList;



/**
 *
 * @author citibob
 */
public class Main {
public static void main(String[] args) throws Exception {

	AppLauncher.launch("offstagearts", new Class[] {
		MainLauncher.class,
		DemoLauncher.class,
		Licensor.class,
		WriteJarList.class,
		MergePurge.class,
		MakeLauncher.class,
		PBECrypt.class,
	});
}
}
