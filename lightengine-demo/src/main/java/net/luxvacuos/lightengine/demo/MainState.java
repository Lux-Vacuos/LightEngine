package net.luxvacuos.lightengine.demo;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.lwjgl.glfw.GLFW;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import net.luxvacuos.igl.vector.Vector3d;
import net.luxvacuos.igl.vector.Vector3f;
import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.core.ClientWorldSimulation;
import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.ecs.ClientComponents;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.ecs.entities.PlayerCamera;
import net.luxvacuos.lightengine.client.ecs.entities.RenderEntity;
import net.luxvacuos.lightengine.client.ecs.entities.Sun;
import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.api.opengl.LightRenderer;
import net.luxvacuos.lightengine.client.rendering.api.opengl.ParticleDomain;
import net.luxvacuos.lightengine.client.rendering.api.opengl.Renderer;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.CachedAssets;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.CachedTexture;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.Light;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.ParticleTexture;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.WaterTile;
import net.luxvacuos.lightengine.client.resources.AssimpResourceLoader;
import net.luxvacuos.lightengine.client.ui.windows.GameWindow;
import net.luxvacuos.lightengine.client.world.ClientPhysicsSystem;
import net.luxvacuos.lightengine.client.world.particles.ParticleSystem;
import net.luxvacuos.lightengine.demo.ui.PauseWindow;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.ecs.components.Position;
import net.luxvacuos.lightengine.universal.ecs.components.Scale;
import net.luxvacuos.lightengine.universal.util.registry.Key;
import net.luxvacuos.lightengine.universal.world.PhysicsSystem;

public class MainState extends AbstractState {

	private PhysicsSystem physicsSystem;
	private Engine engine;
	private Sun sun;
	private ClientWorldSimulation worldSimulation;
	private CameraEntity camera;
	private ParticleSystem particleSystem;
	private Vector3d particlesPoint;
	private GameWindow gameWindow;
	private PauseWindow pauseWindow;
	private Light light0, light1;
	private LightRenderer lightRenderer;

	private RenderEntity mat2, plane, character;

	private CachedTexture fire;

	private List<WaterTile> waterTiles;

	public static boolean paused = false, exitWorld = false, loaded = false;

	protected MainState() {
		super("mainState");
	}

	@Override
	public void start() {
		TaskManager.addTask(() -> Renderer.init(GraphicalSubsystem.getMainWindow()));
		worldSimulation = new ClientWorldSimulation(10000);
		engine = new Engine();
		physicsSystem = new ClientPhysicsSystem();
		physicsSystem.addBox(new BoundingBox(new Vector3(-50, -1, -50), new Vector3(50, 0, 50)));
		engine.addSystem(physicsSystem);
		waterTiles = new ArrayList<>();
		for (int x = -128; x <= 128; x++)
			for (int z = -128; z <= 128; z++)
				waterTiles.add(new WaterTile(x * WaterTile.TILE_SIZE, -0.5f, z * WaterTile.TILE_SIZE));
		sun = new Sun();

		Renderer.setOnResize(() -> {
			ClientComponents.PROJECTION_MATRIX.get(camera)
					.setProjectionMatrix(Renderer.createProjectionMatrix(
							(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")),
							(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")),
							(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Core/fov")),
							ClientVariables.NEAR_PLANE, ClientVariables.FAR_PLANE));
		});
		MouseHandler.setGrabbed(GraphicalSubsystem.getMainWindow().getID(), true);
		TaskManager.addTask(() -> {
			Window window = GraphicalSubsystem.getMainWindow();

			camera = new PlayerCamera("player", UUID.randomUUID().toString());
			camera.setPosition(new Vector3d(0, 2, 0));

			AssimpResourceLoader aLoader = window.getAssimpResourceLoader();

			// Renderer.getLightRenderer().addLight(new Light(new Vector3f(-8, 5,
			// -8), new Vector3f(1, 1, 1)));
			// Renderer.getLightRenderer().addLight(new Light(new Vector3f(-8, 5,
			// 8), new Vector3f(1, 1, 1)));
			// Renderer.getLightRenderer().addLight(new Light(new Vector3f(8, 5,
			// -8), new Vector3f(1, 1, 1)));
			// Renderer.getLightRenderer().addLight(new Light(new Vector3f(8, 5, 8),
			// new Vector3f(1, 1, 1)));
			// Renderer.getLightRenderer().addLight(new Light(new Vector3f(0, 5, 0),
			// new Vector3f(1, 1, 1)));

			lightRenderer = new LightRenderer();

			light0 = new Light(new Vector3d(4.5f, 4.2f, 8f), new Vector3f(50, 50, 50), new Vector3d(245, -45, 0), 50,
					40);
			// light0.setShadow(true);
			// Renderer.getLightRenderer().addLight(light0);
			light1 = new Light(new Vector3d(-4.5f, 4.2f, 8f), new Vector3f(50, 50, 50), new Vector3d(245, 45, 0), 50,
					40);
			// light1.setShadow(true);
			// Renderer.getLightRenderer().addLight(light1);

			mat2 = new RenderEntity("", aLoader.loadModel("levels/test_state/models/sphere.blend"));
			mat2.getComponent(Position.class).set(3, 1, 0);

			plane = new RenderEntity("", aLoader.loadModel("levels/test_state/models/plane.blend"));

			character = new RenderEntity("", aLoader.loadModel("levels/test_state/models/tigre_sumatra.blend"));
			character.getComponent(Position.class).set(0, 0.67335f, 5);
			character.getComponent(Scale.class).setScale(2f);

			fire = CachedAssets.loadTexture("textures/particles/fire0.png");

			particleSystem = new ParticleSystem(new ParticleTexture(fire.getID(), 4), 1000, 1, -1f, 3f, 6f);
			particleSystem.setDirection(new Vector3d(0, -1, 0), 0.4f);
			particlesPoint = new Vector3d(0, 1.7f, -5);

			camera.setPosition(new Vector3d(0, 2, 0));
			physicsSystem.getEngine().addEntity(camera);
			physicsSystem.getEngine().addEntity(plane);
			physicsSystem.getEngine().addEntity(mat2);
			physicsSystem.getEngine().addEntity(character);

			Renderer.render(engine.getEntities(), ParticleDomain.getParticles(), waterTiles, lightRenderer, camera,
					worldSimulation, sun, 0);
			gameWindow = new GameWindow(0, (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")),
					(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")),
					(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")));
			GraphicalSubsystem.getWindowManager().addWindow(gameWindow);
			loaded = true;
		});
		super.start();
	}

	@Override
	public void end() {
		loaded = false;
		physicsSystem.getEngine().removeAllEntities();
		TaskManager.addTask(() -> Renderer.cleanUp());
		TaskManager.addTask(() -> {
			waterTiles.clear();
			fire.dispose();
			lightRenderer.dispose();
		});
		super.end();
	}

	@Override
	public void update(float delta) {
		if (!loaded)
			return;
		Window window = GraphicalSubsystem.getMainWindow();
		KeyboardHandler kbh = window.getKeyboardHandler();
		if (!paused) {
			lightRenderer.update(delta);
			engine.update(delta);
			sun.update(camera.getPosition(), worldSimulation.update(delta), delta);
			particleSystem.generateParticles(particlesPoint, delta);
			ParticleDomain.update(delta, camera);
			// light0.getPosition().set(Math.sin(worldSimulation.getGlobalTime() / 8f) * 3f
			// + 4.5f, 8.2f, 8f);
			// light1.getPosition().set(Math.sin(worldSimulation.getGlobalTime() / 7f) * 3f
			// - 4.5f, 8.2f, 8f);

			if (kbh.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
				kbh.ignoreKeyUntilRelease(GLFW.GLFW_KEY_ESCAPE);
				MouseHandler.setGrabbed(GraphicalSubsystem.getMainWindow().getID(), false);
				paused = true;
				int borderSize = (int) REGISTRY
						.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/borderSize"));
				int titleBarHeight = (int) REGISTRY
						.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight"));
				int height = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height"));
				pauseWindow = new PauseWindow(borderSize + 10, height - titleBarHeight - 10,
						(int) ((int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")) - borderSize * 2f
								- 20),
						(int) (height - titleBarHeight - borderSize - 50));
				GraphicalSubsystem.getWindowManager().addWindow(pauseWindow);
				GraphicalSubsystem.getWindowManager().toggleShell();
			}
		} else if (exitWorld) {
			gameWindow.closeWindow();
			exitWorld = false;
			paused = false;
			StateMachine.setCurrentState("_main");
		} else {
			if (kbh.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
				kbh.ignoreKeyUntilRelease(GLFW.GLFW_KEY_ESCAPE);
				pauseWindow.closeWindow();
			}
		}

	}

	@Override
	public void render(float alpha) {
		if (!loaded)
			return;
		Renderer.render(engine.getEntities(), ParticleDomain.getParticles(), waterTiles, lightRenderer, camera,
				worldSimulation, sun, alpha);
	}
}