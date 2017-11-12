package net.luxvacuos.lightengine.demo.levels;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import org.lwjgl.glfw.GLFW;

import io.netty.channel.ChannelHandlerContext;
import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.ecs.ClientComponents;
import net.luxvacuos.lightengine.client.ecs.entities.RenderEntity;
import net.luxvacuos.lightengine.client.input.KeyboardHandler;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.network.Client;
import net.luxvacuos.lightengine.client.network.ClientNetworkHandler;
import net.luxvacuos.lightengine.client.rendering.api.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.IWindow.WindowClose;
import net.luxvacuos.lightengine.client.rendering.api.nanovg.WindowMessage;
import net.luxvacuos.lightengine.client.rendering.api.opengl.ParticleDomain;
import net.luxvacuos.lightengine.client.rendering.api.opengl.Renderer;
import net.luxvacuos.lightengine.client.ui.windows.GameWindow;
import net.luxvacuos.lightengine.demo.Global;
import net.luxvacuos.lightengine.demo.ui.LoadWindow;
import net.luxvacuos.lightengine.demo.ui.PauseWindow;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.states.StateNames;
import net.luxvacuos.lightengine.universal.ecs.Components;
import net.luxvacuos.lightengine.universal.network.SharedChannelHandler;
import net.luxvacuos.lightengine.universal.network.packets.ClientConnect;
import net.luxvacuos.lightengine.universal.network.packets.ClientDisconnect;
import net.luxvacuos.lightengine.universal.network.packets.Disconnect;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class Level0 extends AbstractState {

	private GameWindow gameWindow;
	private PauseWindow pauseWindow;
	private LoadWindow loadWindow;

	private Client client;
	private ClientNetworkHandler nh;

	public Level0() {
		super("Level0");
	}

	@Override
	public void start() {
		loadWindow = new LoadWindow();
		GraphicalSubsystem.getWindowManager().addWindow(loadWindow);
		Renderer.init(GraphicalSubsystem.getMainWindow());
		MouseHandler.setGrabbed(GraphicalSubsystem.getMainWindow().getID(), true);
		client = new Client();
		if (!Global.ip.isEmpty())
			client.setHost(Global.ip);
		nh = new ClientNetworkHandler(client, null);
		client.run(nh, new SharedChannelHandler() {

			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				if (msg instanceof Disconnect) {
					handleDisconnect((Disconnect) msg);
				}
				super.channelRead(ctx, msg);
			}

			private void handleDisconnect(Disconnect disconnect) {
				Global.exitWorld = true;
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

		RenderEntity scene = new RenderEntity("", "levels/level0/scene.blend");

		nh.getEngine().addEntity(scene);

		client.sendPacket(new ClientConnect(Components.UUID.get(nh.getPlayer()).getUUID(),
				Components.NAME.get(nh.getPlayer()).getName()));

		gameWindow = new GameWindow(0, (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")),
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")),
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")));
		GraphicalSubsystem.getWindowManager().addWindow(0, gameWindow);
		super.start();
	}

	@Override
	public void end() {
		Global.loaded = false;
		client.sendPacket(new ClientDisconnect(Components.UUID.get(nh.getPlayer()).getUUID(),
				Components.NAME.get(nh.getPlayer()).getName()));
		nh.dispose();
		client.end();
		super.end();
	}

	@Override
	public void update(float delta) {
		Window window = GraphicalSubsystem.getMainWindow();
		if (!Global.loaded) {
			if (window.getAssimpResourceLoader().isDoneLoading()) {
				loadWindow.closeWindow();
				Global.loaded = true;
			}
			return;
		}
		KeyboardHandler kbh = window.getKeyboardHandler();
		if (!Global.paused) {
			nh.update(delta);
			Renderer.getLightRenderer().update(delta);
			ParticleDomain.update(delta, nh.getPlayer());
			if (kbh.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
				kbh.ignoreKeyUntilRelease(GLFW.GLFW_KEY_ESCAPE);
				MouseHandler.setGrabbed(GraphicalSubsystem.getMainWindow().getID(), false);
				GraphicalSubsystem.getWindowManager().toggleShell();
				Global.paused = true;
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
		} else if (Global.exitWorld) {
			gameWindow.closeWindow();
			Global.exitWorld = false;
			Global.paused = false;
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
		if (!Global.loaded)
			return;
		Renderer.render(nh.getEngine().getEntities(), ParticleDomain.getParticles(), null, nh.getPlayer(),
				nh.getWorldSimulation(), nh.getSun(), alpha);
	}

}
