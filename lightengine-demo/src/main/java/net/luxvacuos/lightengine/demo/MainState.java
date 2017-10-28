package net.luxvacuos.lightengine.demo;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import io.netty.channel.ChannelHandlerContext;
import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.ecs.ClientComponents;
import net.luxvacuos.lightengine.client.ecs.entities.RenderEntity;
import net.luxvacuos.lightengine.client.ecs.entities.Sun;
import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.network.Client;
import net.luxvacuos.lightengine.client.network.ClientNetworkHandler;
import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.IWindow.WindowClose;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.WindowMessage;
import net.luxvacuos.lightengine.client.rendering.api.opengl.ParticleDomain;
import net.luxvacuos.lightengine.client.rendering.api.opengl.Renderer;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.CachedAssets;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.CachedTexture;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.Light;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.ParticleTexture;
import net.luxvacuos.lightengine.client.rendering.api.opengl.objects.WaterTile;
import net.luxvacuos.lightengine.client.ui.windows.GameWindow;
import net.luxvacuos.lightengine.client.world.particles.ParticleSystem;
import net.luxvacuos.lightengine.demo.ui.LoadWindow;
import net.luxvacuos.lightengine.demo.ui.PauseWindow;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.states.StateNames;
import net.luxvacuos.lightengine.universal.ecs.Components;
import net.luxvacuos.lightengine.universal.network.SharedChannelHandler;
import net.luxvacuos.lightengine.universal.network.packets.ClientConnect;
import net.luxvacuos.lightengine.universal.network.packets.ClientDisconnect;
import net.luxvacuos.lightengine.universal.network.packets.Disconnect;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class MainState extends AbstractState {

	private Sun sun;
	private ParticleSystem particleSystem;
	private Vector3f particlesPoint;
	private GameWindow gameWindow;
	private PauseWindow pauseWindow;
	private LoadWindow loadWindow;

	private CachedTexture fire;

	private List<WaterTile> waterTiles;

	private Client client;
	private ClientNetworkHandler nh;

	public static boolean paused = false, exitWorld = false, loaded = false;
	public static String ip = "";

	protected MainState() {
		super("mainState");
	}

	@Override
	public void start() {
		loadWindow = new LoadWindow();
		GraphicalSubsystem.getWindowManager().addWindow(loadWindow);
		Renderer.init(GraphicalSubsystem.getMainWindow());
		MouseHandler.setGrabbed(GraphicalSubsystem.getMainWindow().getID(), true);

		waterTiles = new ArrayList<>();
		// for (int x = -128; x <= 128; x++)
		// for (int z = -128; z <= 128; z++)
		// waterTiles.add(new WaterTile(x * WaterTile.TILE_SIZE, -0.5f, z *
		// WaterTile.TILE_SIZE));
		sun = new Sun();

		client = new Client();
		if (!ip.isEmpty())
			client.setHost(ip);
		nh = new ClientNetworkHandler(client);
		client.run(nh, new SharedChannelHandler() {

			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				if (msg instanceof Disconnect) {
					handleDisconnect((Disconnect) msg);
				}
				super.channelRead(ctx, msg);
			}

			private void handleDisconnect(Disconnect disconnect) {
				exitWorld = true;
				if (pauseWindow != null)
					pauseWindow.notifyWindow(WindowMessage.WM_CLOSE, WindowClose.DO_NOTHING);
			}
		});

		Renderer.setOnResize(() -> {
			ClientComponents.PROJECTION_MATRIX.get(nh.getPlayer())
					.setProjectionMatrix(Renderer.createProjectionMatrix(
							(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")),
							(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")),
							(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Core/fov")),
							ClientVariables.NEAR_PLANE, ClientVariables.FAR_PLANE));
		});

		TaskManager.addTask(() -> {

			/*
			 * Light light0 = new Light(new Vector3f(0, 14, 0.05), new Vector3f(50, 50, 50),
			 * new Vector3f(-60, 0, 0), 35, 30); light0.setShadow(true);
			 * Renderer.getLightRenderer().addLight(light0); Light light1 = new Light(new
			 * Vector3f(-0.05, 14, 0), new Vector3f(50, 50, 50), new Vector3f(-60, 90, 0),
			 * 35, 30); light1.setShadow(true);
			 * Renderer.getLightRenderer().addLight(light1); Light light2 = new Light(new
			 * Vector3f(0, 14, -0.05), new Vector3f(50, 50, 50), new Vector3f(-60, 180, 0),
			 * 35, 30); light2.setShadow(true);
			 * Renderer.getLightRenderer().addLight(light2); Light light3 = new Light(new
			 * Vector3f(0.05, 14, 0), new Vector3f(50, 50, 50), new Vector3f(-60, -90, 0),
			 * 35, 30); light3.setShadow(true);
			 * Renderer.getLightRenderer().addLight(light3); Light light4 = new Light(new
			 * Vector3f(0, 18, 0), new Vector3f(1000, 1000, 1000), new Vector3f(-90, 0, 0),
			 * 60, 55); light4.setShadow(true);
			 * Renderer.getLightRenderer().addLight(light4);
			 */

			Renderer.getLightRenderer()
					.addLight(new Light(new Vector3f(-7.25883f, 1.6f, -2.17035f), new Vector3f(0.5f, 0.5f, 0.5f)));
			Renderer.getLightRenderer()
					.addLight(new Light(new Vector3f(-7.25883f, 1.6f, 2.17035f), new Vector3f(0.5f, 0.5f, 0.5f)));
			Renderer.getLightRenderer()
					.addLight(new Light(new Vector3f(5.71191f, 1.6f, -2.17035f), new Vector3f(0.5f, 0.5f, 0.5f)));
			Renderer.getLightRenderer()
					.addLight(new Light(new Vector3f(5.71191f, 1.6f, 2.17035f), new Vector3f(0.5f, 0.5f, 0.5f)));
		});
		RenderEntity plane = new RenderEntity("", "levels/test_state/models/sponza.blend");
		/*
		 * RenderEntity mat2 = new RenderEntity("",
		 * "levels/test_state/models/sphere.blend");
		 * mat2.getComponent(Position.class).set(3, 1, 0);
		 * 
		 * RenderEntity character = new RenderEntity("",
		 * "levels/test_state/models/tigre_sumatra.blend");
		 * character.getComponent(Position.class).set(-3, 0.67335f, 0);
		 * character.getComponent(Scale.class).setScale(2f);
		 * nh.getEngine().addEntity(mat2); nh.getEngine().addEntity(character);
		 */
		nh.getEngine().addEntity(plane);
		fire = CachedAssets.loadTexture("textures/particles/fire0.png");
		particleSystem = new ParticleSystem(new ParticleTexture(fire.getID(), 4), 1000, 1, -1f, 3f, 6f);
		particleSystem.setDirection(new Vector3f(0, -1, 0), 0.4f);
		particlesPoint = new Vector3f(0, 1.7f, -5);

		client.sendPacket(new ClientConnect(Components.UUID.get(nh.getPlayer()).getUUID(),
				Components.NAME.get(nh.getPlayer()).getName()));

		TaskManager.addTask(() -> {
			Renderer.render(nh.getEngine().getEntities(), ParticleDomain.getParticles(), waterTiles, nh.getPlayer(),
					nh.getWorldSimulation(), sun, 0);
			gameWindow = new GameWindow(0, (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")),
					(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")),
					(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")));
			GraphicalSubsystem.getWindowManager().addWindow(0, gameWindow);
		});
		super.start();
	}

	@Override
	public void end() {
		loaded = false;
		Renderer.cleanUp();
		TaskManager.addTask(() -> {
			waterTiles.clear();
			fire.dispose();
			Renderer.getLightRenderer().dispose();
		});
		client.sendPacket(new ClientDisconnect(Components.UUID.get(nh.getPlayer()).getUUID(),
				Components.NAME.get(nh.getPlayer()).getName()));
		nh.dispose();
		client.end();
		super.end();
	}

	@Override
	public void update(float delta) {
		Window window = GraphicalSubsystem.getMainWindow();
		if (!loaded) {
			if (window.getAssimpResourceLoader().isDoneLoading()) {
				loadWindow.closeWindow();
				loaded = true;
			}
			return;
		}
		KeyboardHandler kbh = window.getKeyboardHandler();
		if (!paused) {
			nh.update(delta);
			sun.update(nh.getPlayer().getPosition(), nh.getWorldSimulation().getRotation(), delta);
			Renderer.getLightRenderer().update(delta);
			//particleSystem.generateParticles(particlesPoint, delta);
			ParticleDomain.update(delta, nh.getPlayer());
			if (kbh.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
				kbh.ignoreKeyUntilRelease(GLFW.GLFW_KEY_ESCAPE);
				MouseHandler.setGrabbed(GraphicalSubsystem.getMainWindow().getID(), false);
				GraphicalSubsystem.getWindowManager().toggleShell();
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
			}
		} else if (exitWorld) {
			gameWindow.closeWindow();
			exitWorld = false;
			paused = false;
			StateMachine.setCurrentState(StateNames.MAIN);
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
		Renderer.render(nh.getEngine().getEntities(), ParticleDomain.getParticles(), waterTiles, nh.getPlayer(),
				nh.getWorldSimulation(), sun, alpha);
	}
}