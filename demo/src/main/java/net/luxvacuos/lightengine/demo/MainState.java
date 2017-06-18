package net.luxvacuos.lightengine.demo;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

import java.util.UUID;

import org.lwjgl.glfw.GLFW;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import net.luxvacuos.igl.vector.Matrix4d;
import net.luxvacuos.igl.vector.Vector3d;
import net.luxvacuos.igl.vector.Vector3f;
import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.core.ClientWorldSimulation;
import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.ecs.entities.PlayerCamera;
import net.luxvacuos.lightengine.client.ecs.entities.RenderEntity;
import net.luxvacuos.lightengine.client.ecs.entities.Sun;
import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.Mouse;
import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.api.opengl.ParticleDomain;
import net.luxvacuos.lightengine.client.rendering.api.opengl.Renderer;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.Light;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.Model;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.ParticleTexture;
import net.luxvacuos.lightengine.client.resources.AssimpResourceLoader;
import net.luxvacuos.lightengine.client.resources.ResourceLoader;
import net.luxvacuos.lightengine.client.ui.windows.GameWindow;
import net.luxvacuos.lightengine.client.world.particles.ParticleSystem;
import net.luxvacuos.lightengine.demo.ui.PauseWindow;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.ecs.components.Position;
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

	private RenderEntity mat1, mat2, mat3, mat4, mat5, rocket, plane, character, cerberus;

	private Model sphere, dragon, rocketM, planeM, characterM, cerberusM;
	private ParticleTexture fire;

	public static boolean paused = false, exitWorld = false;

	protected MainState() {
		super("mainState");
	}

	@Override
	public void init() {
		Window window = GraphicalSubsystem.getMainWindow();
		ResourceLoader loader = window.getResourceLoader();

		Matrix4d projectionMatrix = Renderer.createProjectionMatrix(window.getWidth(), window.getHeight(),
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Core/fov")), ClientVariables.NEAR_PLANE,
				ClientVariables.FAR_PLANE);

		camera = new PlayerCamera(projectionMatrix, "player", UUID.randomUUID().toString());
		camera.setPosition(new Vector3d(0, 2, 0));
		sun = new Sun();

		AssimpResourceLoader aLoader = window.getAssimpResourceLoader();

		worldSimulation = new ClientWorldSimulation(10000);
		engine = new Engine();
		physicsSystem = new PhysicsSystem();
		physicsSystem.addBox(new BoundingBox(new Vector3(-50, -1, -50), new Vector3(50, 0, 50)));
		engine.addSystem(physicsSystem);

		sphere = aLoader.loadModel("levels/test_state/models/sphere.blend");

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
		light0 = new Light(new Vector3d(4.5f, 4.2f, 8f), new Vector3f(100, 100, 100), new Vector3d(245, -45, 0), 50,
				40);
		light0.setShadow(true);
		Renderer.getLightRenderer().addLight(light0);
		light1 = new Light(new Vector3d(-4.5f, 4.2f, 8f), new Vector3f(100, 100, 100), new Vector3d(245, 45, 0), 50,
				40);
		light1.setShadow(true);
		Renderer.getLightRenderer().addLight(light1);

		mat1 = new RenderEntity("", sphere);
		mat1.getComponent(Position.class).set(0, 1, 0);

		mat2 = new RenderEntity("", sphere);
		mat2.getComponent(Position.class).set(3, 1, 0);

		mat3 = new RenderEntity("", sphere);
		mat3.getComponent(Position.class).set(6, 1, 0);

		mat4 = new RenderEntity("", sphere);
		mat4.getComponent(Position.class).set(9, 1, 0);
		/*
		 * dragon = aLoader.loadModel("levels/test_state/models/dragon.blend");
		 * 
		 * mat5 = new RenderEntity("", dragon);
		 * 
		 * mat5.getComponent(Position.class).set(-3, 0, 3);
		 * mat5.getComponent(Scale.class).setScale(0.5f);
		 */

		/*
		 * rocketM = aLoader.loadModel("levels/test_state/models/Rocket.obj");
		 * 
		 * rocket = new RenderEntity("", rocketM);
		 * rocket.getComponent(Position.class).set(0, 0, -5);
		 */

		planeM = aLoader.loadModel("levels/test_state/models/plane.blend");

		plane = new RenderEntity("", planeM);

		characterM = aLoader.loadModel("levels/test_state/models/monkey.blend");

		character = new RenderEntity("", characterM);
		character.getComponent(Position.class).set(0, 0, 5);
		// character.getComponent(Scale.class).setScale(0.21f);
		/*
		 * cerberusM =
		 * aLoader.loadModel("levels/test_state/models/cerberus.blend");
		 * 
		 * cerberus = new RenderEntity("", cerberusM);
		 * cerberus.getComponent(Position.class).set(5, 1.25f, 5);
		 * cerberus.getComponent(Scale.class).setScale(0.5f);
		 */

		fire = new ParticleTexture(loader.loadTexture("textures/particles/fire0.png").getID(), 4);

		particleSystem = new ParticleSystem(fire, 1000, 1, -1f, 3f, 6f);
		particleSystem.setDirection(new Vector3d(0, -1, 0), 0.4f);
		particlesPoint = new Vector3d(0, 1.7f, -5);

		// worldSimulation.setTime(22000);
	}

	@Override
	public void dispose() {
		sphere.dispose();
		// dragon.dispose();
		characterM.dispose();
		fire.dispose();
		planeM.dispose();
		// rocketM.dispose();
		// cerberusM.dispose();
	}

	@Override
	public void start() {
		camera.setPosition(new Vector3d(0, 2, 0));
		physicsSystem.getEngine().addEntity(camera);
		physicsSystem.getEngine().addEntity(plane);
		physicsSystem.getEngine().addEntity(mat1);
		physicsSystem.getEngine().addEntity(mat2);
		physicsSystem.getEngine().addEntity(mat3);
		physicsSystem.getEngine().addEntity(mat4);
		// physicsSystem.getEngine().addEntity(mat5);
		/*
		 * physicsSystem.getEngine().addEntity(rocket);
		 */ physicsSystem.getEngine().addEntity(character);
		/*
		 * physicsSystem.getEngine().addEntity(cerberus);
		 */
		Mouse.setGrabbed(true);
		Renderer.render(engine.getEntities(), ParticleDomain.getParticles(), camera, worldSimulation, sun, 0);
		gameWindow = new GameWindow(0, (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")),
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")),
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")));
		GraphicalSubsystem.getWindowManager().addWindow(gameWindow);
	}

	@Override
	public void end() {
		physicsSystem.getEngine().removeAllEntities();
	}

	@Override
	public void update(float delta) {
		GraphicalSubsystem.getWindowManager().update(delta);
		Window window = GraphicalSubsystem.getMainWindow();
		KeyboardHandler kbh = window.getKeyboardHandler();
		if (!paused) {
			Renderer.update(delta);
			engine.update(delta);
			sun.update(camera.getPosition(), worldSimulation.update(delta), delta);
			// particleSystem.generateParticles(particlesPoint, delta);
			ParticleDomain.update(delta, camera);
			light0.getPosition().set(Math.sin(worldSimulation.getGlobalTime() / 8f) * 3f + 4.5f, 8.2f, 8f);
			light1.getPosition().set(Math.sin(worldSimulation.getGlobalTime() / 7f) * 3f - 4.5f, 8.2f, 8f);

			if (kbh.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
				kbh.ignoreKeyUntilRelease(GLFW.GLFW_KEY_ESCAPE);
				Mouse.setGrabbed(false);
				paused = true;
				float borderSize = (float) REGISTRY
						.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/borderSize"));
				float titleBarHeight = (float) REGISTRY
						.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/titleBarHeight"));
				int height = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height"));
				pauseWindow = new PauseWindow(borderSize + 10, height - titleBarHeight - 10,
						(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")) - borderSize * 2f - 20,
						height - titleBarHeight - borderSize - 50);
				GraphicalSubsystem.getWindowManager().addWindow(pauseWindow);
				GraphicalSubsystem.getWindowManager().toggleShell();
			}
		} else if (exitWorld) {
			gameWindow.closeWindow();
			pauseWindow.closeWindow();
			exitWorld = false;
			paused = false;
			StateMachine.setCurrentState("_main");
		} else {
			if (kbh.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
				kbh.ignoreKeyUntilRelease(GLFW.GLFW_KEY_ESCAPE);
				Mouse.setGrabbed(true);
				paused = false;
				pauseWindow.closeWindow();
				GraphicalSubsystem.getWindowManager().toggleShell();
			}
		}

	}

	@Override
	public void render(float alpha) {
		Renderer.render(engine.getEntities(), ParticleDomain.getParticles(), camera, worldSimulation, sun, alpha);
		Renderer.clearBuffer(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		Renderer.clearColors(1, 1, 1, 1);
		GraphicalSubsystem.getWindowManager().render();
	}
}