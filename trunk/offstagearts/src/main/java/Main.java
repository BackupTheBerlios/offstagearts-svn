
import citibob.gui.AppLauncher;
import offstage.gui.OffstageLauncher;
import offstage.licensor.Licensor;



/**
 *
 * @author citibob
 */
public class Main {
public static void main(String[] args) throws Exception {

	AppLauncher.launch("holyokefw", new Class[] {
		OffstageLauncher.class,
		Licensor.class
	});
}
}
