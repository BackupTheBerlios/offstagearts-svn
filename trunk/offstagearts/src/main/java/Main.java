
import citibob.gui.AppLauncher;
import offstage.cleanse.MergePurge;
import offstage.gui.OffstageLauncher;
import offstage.licensor.Licensor;
import offstage.licensor.WriteJarList;



/**
 *
 * @author citibob
 */
public class Main {
public static void main(String[] args) throws Exception {

	AppLauncher.launch("holyokefw", new Class[] {
		OffstageLauncher.class,
		Licensor.class,
		WriteJarList.class,
		MergePurge.class
	});
}
}
