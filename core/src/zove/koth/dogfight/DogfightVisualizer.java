package zove.koth.dogfight;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import Planes.Controller;
import Planes.Direction;
import Planes.Plane;
import Planes.PlaneControl;
import Planes.Point3D;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

//@off
/**
 * Visualizer for the Dogfight KOTH challenge. <br/>
 * Credit for the Supermarine Spitfire model goes to jimbob:
 * {@link https://3dwarehouse.sketchup.com/model.html?id=dc03e743739c4f87c27f2d9f006d69eb}
 * 
 * @author Zove Games
 */
//@on
public class DogfightVisualizer extends ApplicationAdapter {
	public static final Object _wait = new Object();

	static boolean started = false;
	static boolean paused = false;
	static boolean step = false;

	ShapeRenderer gridRenderer, guiRenderer;
	ModelBatch modelBatch;
	PerspectiveCamera cam;
	float angleX = 0, angleY = 0;
	float dist = 20f;

	AssetManager manager;
	ModelInstance[] planes = new ModelInstance[4];
	Array<ModelInstance> explosions = new Array<ModelInstance>();
	Array<Vector3[]> shots = new Array<Vector3[]>();

	Vector3 camCenter = new Vector3(0.5f, 0.5f, 0.5f);

	SpriteBatch hudRenderer, tagRenderer;
	BitmapFont font;

	public static String log = "Dogfight Log - Hit start to start or step "
			+ "to simulate one step at a time\nHit 1, 2, or 3 for side views"
			+ "\nLeft click and drag to translate\nMiddle click and drag to rotate\nScroll to zoom"
			+ "\n\nContestants:\n";
	static int totalMatchups = 0;
	static int currentMatchup = 1;
	static DogfightVisualizer INSTANCE;

	static Matrix4[] currentTransform = new Matrix4[] { new Matrix4(),
			new Matrix4(), new Matrix4(), new Matrix4() };
	static Matrix4[] targetTransform = new Matrix4[] { new Matrix4(),
			new Matrix4(), new Matrix4(), new Matrix4() };

	static boolean[] aliveStore = new boolean[] { true, true, true, true };

	static long interpolationStart = System.currentTimeMillis();
	public static int sleepStep = 100;

	static String scoreTable = "";

	static final Object _lock = new Object();

	public static void sleep(int steps) {
		while (paused) {
			synchronized (_wait) {
				try {
					_wait.wait(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (step) {
				step = false;
				break;
			}
		}

		try {
			Thread.sleep(steps * sleepStep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void enterClasses(Class<?>[] classes) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Controller.entries = new PlaneControl[classes.length];
		for (int i = 0; i < classes.length; i++) {
			Constructor<?> c = classes[i].getDeclaredConstructor(new Class[]{int.class, int.class});
			Object o = c.newInstance(Controller.ARENA_SIZE, Controller.ROUNDS);
			Controller.entries[i] = (PlaneControl) o;
		}
	}

	public static void reportEntries(PlaneControl[] entries) {
		int entriesLength = entries.length;
		if (entriesLength <= 1)
			totalMatchups = 1;
		else
			totalMatchups = entriesLength * (entriesLength - 1) / 2;

		for (int i = 0; i < entriesLength; i++) {
			log += entries[i].getClass().getSimpleName() + "\n";
		}
		paused = true;
	}

	public static void beginMatch(String player1, String player2, int matches) {
		sleep(1);
		log = matches + " fight match: " + player1 + "/" + player2
				+ " - Matchup " + currentMatchup + " of " + totalMatchups
				+ "\n";
		currentMatchup++;
		for (int i = 0; i < 4; i++) {
			INSTANCE.planes[i].userData = i < 2 ? player1 : player2;
		}
		sleep(1);
	}

	public static void newFight(int num) {
		log += "Fight #" + num + "\nRounds: ";
		aliveStore = new boolean[] { true, true, true, true };
		Plane[] player1Planes = {
				new Plane(14, 0, new Direction("S"), 0, 14 / 2 - 2, 0),
				new Plane(14, 0, new Direction("S"), 0, 14 / 2 + 1, 0) };
		Plane[] player2Planes = {
				new Plane(14, 0, new Direction("N"), 14 - 1, 14 / 2 + 1, 14 - 1),
				new Plane(14, 0, new Direction("N"), 14 - 1, 14 / 2 - 2, 14 - 1) };
		beginFight(player1Planes[0], player1Planes[1], player2Planes[0],
				player2Planes[1]);
		sleep(1);
	}

	private static void beginFight(Plane player11, Plane player12,
			Plane player21, Plane player22) {
		updatePlane(player11, 0, true);
		updatePlane(player12, 1, true);
		updatePlane(player21, 2, true);
		updatePlane(player22, 3, true);
	}

	public static void newRound(int num) {
		if (num % 10 == 0)
			log += num + " ";
		sleep(1);
	}

	public static void updatePlanes(Plane player11, Plane player12,
			Plane player21, Plane player22) {
		updatePlane(player11, 0, false);
		updatePlane(player12, 1, false);
		updatePlane(player21, 2, false);
		updatePlane(player22, 3, false);
	}

	public static void planeShot(Point3D from, Point3D to) {
		synchronized (_lock) {
			INSTANCE.shots.add(new Vector3[] {
					new Vector3(from.x - 6.5f, from.y - 6.5f, from.z - 6.5f),
					new Vector3(to.x - 6.5f, to.y - 6.5f, to.z - 6.5f),
					new Vector3(0.1f, 0, 0) });
			// third vector3 is the life
		}
	}

	private static void updatePlane(Plane from, int id, boolean instant) {
		synchronized (_lock) {
			if (from.isAlive() != aliveStore[id]) {
				aliveStore[id] = from.isAlive();
				ModelInstance explosion = new ModelInstance(
						(Model) INSTANCE.manager.get("explosion.g3db"));
				explosion.transform.setToTranslation(
						targetTransform[id].getTranslation(new Vector3())).scl(
						0.5f);
				explosion.userData = new Float(0.5);
				// userData is the life
				INSTANCE.explosions.add(explosion);
			}

			currentTransform[id].set(targetTransform[id]);
			interpolationStart = System.currentTimeMillis();
			Point3D point = from.getDirection().getAsPoint3D();
			targetTransform[id]
					.idt()
					.translate(from.getX() - 6.5f, from.getY() - 6.5f,
							from.getZ() - 6.5f)
					.rotate(Vector3.X, new Vector3(point.x, point.y, point.z));
			if (!from.isAlive() || instant)
				currentTransform[id].set(targetTransform[id]);
		}
	}

	public static void fightWinner(String winner) {
		log += "\n\tWinner is: " + winner + "!\n";
		sleep(1);
	}

	public static void matchupEnded(String matchWinner) {
		log += "The winner of the matchup is: " + matchWinner + "!";
		sleep(10);
	}

	public static void overallWinner(String winner, int points) {
		log = "The winner is: " + winner + " with " + points + " points!\n\n"
				+ scoreTable;
	}

	public static void reportFinalScore(String simpleName, int i) {
		scoreTable += simpleName + ": " + i + "\n";
	}

	@Override
	public void create() {
		INSTANCE = this;
		gridRenderer = new ShapeRenderer();
		guiRenderer = new ShapeRenderer();
		modelBatch = new ModelBatch();
		cam = new PerspectiveCamera(45, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		cam.position.set(0, 0, -20);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();

		manager = new AssetManager();
		manager.load("supermarine_spitfire.g3db", Model.class);
		manager.load("explosion.g3db", Model.class);
		// wait for loading
		while (!manager.update(100)) {
			// do nothing! yay
		}
		Model spitfire = manager.get("supermarine_spitfire.g3db");
		for (int i = 0; i < 4; i++) {
			ModelInstance instance = new ModelInstance(spitfire);
			instance.userData = "";
			instance.transform.translate(i + 0.5f, i + 0.5f, i + 0.5f);
			planes[i] = instance;

			for (Material m : instance.materials) {
				m.set(ColorAttribute.createDiffuse(i < 2 ? Color.RED
						: Color.BLUE));
			}
		}

		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean scrolled(int amount) {
				dist += amount;
				return true;
			}

			@Override
			public boolean touchUp(int x, int y, int pointer, int button) {
				if (x > Gdx.graphics.getWidth() - 100
						&& y > Gdx.graphics.getHeight() - 50) {
					if (!started) {
						synchronized (_wait) {
							_wait.notify();
						}
						started = true;
						paused = false;
					} else {
						paused = !paused;
						if (!paused) {
							synchronized (_wait) {
								_wait.notify();
							}
						}
					}
					return true;
				}
				if (x > Gdx.graphics.getWidth() - 200
						&& x < Gdx.graphics.getWidth() - 100
						&& y > Gdx.graphics.getHeight() - 50) {
					if (paused && !step) {
						step = true;
						synchronized (_wait) {
							_wait.notify();
						}
					}
					return true;
				}
				return false;
			}
		});

		hudRenderer = new SpriteBatch();
		tagRenderer = new SpriteBatch();
		font = new BitmapFont();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		// rotate/translate the view based on mouse input
		if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
			Vector3 x = new Vector3();
			x.set(cam.direction).crs(cam.up).y = 0f;
			x.nor();
			Vector3 y = new Vector3().set(cam.up);
			Vector3 translation = new Vector3();
			translation.add(x.scl(Gdx.input.getDeltaX() / -100f)).add(
					y.scl(Gdx.input.getDeltaY() / 100f));
			camCenter.add(translation);
		}
		if (Gdx.input.isButtonPressed(Buttons.MIDDLE)) {
			float deltaY = -Gdx.input.getDeltaY() * 360f
					/ Gdx.graphics.getWidth();
			float deltaX = -Gdx.input.getDeltaX() * 360f
					/ Gdx.graphics.getHeight();
			angleX += deltaX;
			angleX %= 360;
			angleY += deltaY;

			Vector3 v = new Vector3();
			v.set(cam.direction).crs(cam.up).y = 0f;
			cam.rotateAround(cam.position, v.nor(), deltaY);
			cam.rotateAround(cam.position, Vector3.Y, deltaX);
		}

		if (Gdx.input.isKeyPressed(Keys.NUM_1)) {
			cam.direction.set(0, -1, 0);
			cam.up.set(-1, 0, 0);
			camCenter.set(0, 0, 0);
		}
		if (Gdx.input.isKeyPressed(Keys.NUM_2)) {
			cam.direction.set(1, 0, 0);
			cam.up.set(0, 1, 0);
			camCenter.set(0, 0, 0);
		}
		if (Gdx.input.isKeyPressed(Keys.NUM_3)) {
			cam.direction.set(0, 0, 1);
			cam.up.set(0, 1, 0);
			camCenter.set(0, 0, 0);
		}
		cam.position.set(cam.direction).scl(-dist).add(camCenter);
		cam.update();

		float delta = (float) (System.currentTimeMillis() - interpolationStart);
		synchronized (_lock) {
			if (delta < sleepStep) {
				for (int i = 0; i < 4; i++) {
					float w = delta / sleepStep;
					planes[i].transform.set(targetTransform[i]).avg(
							currentTransform[i], w);
				}
			} else {
				for (int i = 0; i < 4; i++) {
					planes[i].transform.idt().set(targetTransform[i]);
				}
			}
		}

		// render the grid
		gridRenderer.setProjectionMatrix(cam.combined);
		gridRenderer.begin(ShapeType.Line);
		// back
		gridRenderer.setColor(Color.GREEN);
		for (int x = -7; x < 8; x++) {
			gridRenderer.line(x, 7, 7, x, -7, 7);
		}
		for (int y = -7; y < 8; y++) {
			gridRenderer.line(-7, y, 7, 7, y, 7);
		}
		// bottom
		gridRenderer.setColor(Color.BLUE);
		for (int x = -7; x < 8; x++) {
			gridRenderer.line(x, -7, 7, x, -7, -7);
		}
		for (int z = -7; z < 8; z++) {
			gridRenderer.line(-7, -7, z, 7, -7, z);
		}
		// left
		gridRenderer.setColor(Color.RED);
		for (int y = -7; y < 8; y++) {
			gridRenderer.line(7, y, -7, 7, y, 7);
		}
		for (int z = -7; z < 8; z++) {
			gridRenderer.line(7, -7, z, 7, 7, z);
		}
		gridRenderer.end();

		modelBatch.begin(cam);
		for (int i = 0; i < 4; i++)
			modelBatch.render(planes[i]);
		synchronized (_lock) {
			modelBatch.render(explosions);
			Iterator<ModelInstance> it = explosions.iterator();
			while (it.hasNext()) {
				ModelInstance e = it.next();
				float time = (Float) e.userData;
				if (time < 0) {
					it.remove();
					continue;
				}
				e.userData = time - Gdx.graphics.getDeltaTime();
				float scale = (0.5f - time);
				e.materials.get(0).set(new BlendingAttribute(true, scale * 2));
			}
		}
		modelBatch.end();

		gridRenderer.begin(ShapeType.Line);
		gridRenderer.setColor(Color.BLACK);
		synchronized (_lock) {
			Iterator<Vector3[]> lines = shots.iterator();
			while (lines.hasNext()) {
				Vector3[] line = lines.next();
				float time = line[2].x;
				line[2].x = time - Gdx.graphics.getDeltaTime();
				if (time < 0) {
					lines.remove();
					continue;
				}
				gridRenderer.line(line[0], line[1]);
			}
		}
		gridRenderer.end();

		tagRenderer.begin();
		for (int i = 0; i < 4; i++) {
			ModelInstance plane = planes[i];
			Vector3 translation = new Vector3();
			plane.transform.getTranslation(translation).add(0, 0.5f, 0);
			cam.project(translation);
			font.setColor(Color.PURPLE);
			font.draw(tagRenderer, plane.userData.toString(), translation.x
					- (font.getBounds(plane.userData.toString()).width / 2),
					translation.y);
		}
		tagRenderer.end();

		hudRenderer.begin();
		font.setColor(Color.BLACK);
		font.drawMultiLine(hudRenderer, log, 0, Gdx.graphics.getHeight());
		hudRenderer.end();

		guiRenderer.begin(ShapeType.Filled);
		guiRenderer.setColor(Color.GRAY);
		if (Gdx.input.getX() > Gdx.graphics.getWidth() - 100
				&& Gdx.input.getY() > Gdx.graphics.getHeight() - 50) {
			guiRenderer.setColor(Color.LIGHT_GRAY);
		}
		guiRenderer.rect(Gdx.graphics.getWidth() - 100, 0, 100, 50);
		guiRenderer.setColor(Color.GRAY);
		if (Gdx.input.getX() > Gdx.graphics.getWidth() - 200
				&& Gdx.input.getX() < Gdx.graphics.getWidth() - 100
				&& Gdx.input.getY() > Gdx.graphics.getHeight() - 50) {
			guiRenderer.setColor(Color.LIGHT_GRAY);
		}
		guiRenderer.rect(Gdx.graphics.getWidth() - 201, 0, 100, 50);
		guiRenderer.end();

		hudRenderer.begin();
		font.setColor(Color.BLACK);
		String text;
		if (!started)
			text = "Start";
		else if (paused)
			text = "Continue";
		else
			text = "Pause";
		font.draw(hudRenderer, text, Gdx.graphics.getWidth() - 70, 25);

		if (paused) {
			font.draw(hudRenderer, "Step", Gdx.graphics.getWidth() - 170, 25);
		}

		hudRenderer.end();
	}

	@Override
	public void dispose() {
		gridRenderer.dispose();
		manager.dispose();
		modelBatch.dispose();
		tagRenderer.dispose();
		guiRenderer.dispose();
	}

	@Override
	public void resize(int w, int h) {
		cam.viewportWidth = w;
		cam.viewportHeight = h;
		hudRenderer.setProjectionMatrix(hudRenderer.getProjectionMatrix().idt()
				.setToOrtho2D(0, 0, w, h));
	}
}
