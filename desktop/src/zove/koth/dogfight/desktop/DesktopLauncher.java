package zove.koth.dogfight.desktop;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import zove.koth.dogfight.DogfightVisualizer;
import Planes.Controller;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * Launches the app
 * 
 * @author Zove Games
 */
public class DesktopLauncher {

	/**
	 * String... because I can!
	 * 
	 * @param args
	 *            The args
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	public static void main(String... args) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (args.length > 0) {
			ArrayList<Class<?>> classes = new ArrayList<>();
			for (int i = 0; i < args.length; i++) {
				try {
					Class<?> klazz = Class.forName(args[i]);
					classes.add(klazz);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			DogfightVisualizer.enterClasses(classes.toArray(new Class<?>[classes.size()]));
		}
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 600;
		config.resizable = true;
		config.samples = 4;
		config.title = "Dogfight Visualizer";
		config.width = 800;
		new LwjglApplication(new DogfightVisualizer(), config);
		Controller.main(args);
	}
}
