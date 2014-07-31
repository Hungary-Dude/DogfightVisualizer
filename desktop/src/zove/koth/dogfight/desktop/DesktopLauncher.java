package zove.koth.dogfight.desktop;

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
	 */
	public static void main(String... args) {
		if (args.length >= 1) {
			try {
				int i = Integer.parseInt(args[0]);
				DogfightVisualizer.sleepStep = i;
			} catch (Exception e) {
			}
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
