package net.luxvacuos.lightengine.demo;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import io.netty.channel.ChannelHandlerContext;
import net.luxvacuos.lightengine.server.bootstrap.Bootstrap;
import net.luxvacuos.lightengine.server.commands.SayCommand;
import net.luxvacuos.lightengine.server.commands.ServerCommandManager;
import net.luxvacuos.lightengine.server.commands.StopCommand;
import net.luxvacuos.lightengine.server.console.Console;
import net.luxvacuos.lightengine.server.core.ServerWorldSimulation;
import net.luxvacuos.lightengine.server.network.Server;
import net.luxvacuos.lightengine.server.network.ServerHandler;
import net.luxvacuos.lightengine.universal.commands.ICommandManager;
import net.luxvacuos.lightengine.universal.commands.StateCommand;
import net.luxvacuos.lightengine.universal.commands.TimeCommand;
import net.luxvacuos.lightengine.universal.core.GlobalVariables;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.ecs.Components;
import net.luxvacuos.lightengine.universal.ecs.entities.PlayerEntity;
import net.luxvacuos.lightengine.universal.network.SharedChannelHandler;
import net.luxvacuos.lightengine.universal.network.packets.ClientConnect;
import net.luxvacuos.lightengine.universal.network.packets.ClientDisconnect;
import net.luxvacuos.lightengine.universal.network.packets.Time;
import net.luxvacuos.lightengine.universal.network.packets.UpdateBasicEntity;
import net.luxvacuos.lightengine.universal.util.registry.Key;
import net.luxvacuos.lightengine.universal.world.PhysicsSystem;

public class ServerState extends AbstractState {
	private ServerWorldSimulation worldSimulation;
	private Console console;
	private ICommandManager commandManager;
	private Server server;
	private PhysicsSystem physicsSystem;
	private Engine engine;

	private Map<UUID, PlayerEntity> players = new HashMap<>();

	public ServerState() {
		super("_main");
	}

	@Override
	public void init() {

		worldSimulation = new ServerWorldSimulation();

		commandManager = new ServerCommandManager(System.out);
		commandManager.registerCommand(new StopCommand());
		commandManager.registerCommand(new SayCommand());
		commandManager.registerCommand(new TimeCommand(worldSimulation));
		commandManager.registerCommand(new StateCommand());

		console = new Console();
		console.setCommandManager(commandManager);
		console.start();

		engine = new Engine();
		physicsSystem = new PhysicsSystem();
		physicsSystem.addBox(new BoundingBox(new Vector3(-50, -1, -50), new Vector3(50, 0, 50)));
		engine.addSystem(physicsSystem);

		server = new Server((int) REGISTRY.getRegistryItem(new Key("/Light Engine/Server/port")));
		server.run(new SharedChannelHandler() {
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
				if (obj instanceof ClientConnect) {
					handleConnect((ClientConnect) obj, ctx);
				} else if (obj instanceof ClientDisconnect) {
					handleDisconnect((ClientDisconnect) obj);
				} else if (obj instanceof UpdateBasicEntity) {
					handleUpdateBasicEntity((UpdateBasicEntity) obj);
				}
				super.channelRead(ctx, obj);
			}

			private void handleConnect(ClientConnect con, ChannelHandlerContext ctx) {
				for(PlayerEntity pl : players.values()) {
					ctx.channel().writeAndFlush(new ClientConnect(Components.UUID.get(pl).getUUID(), Components.NAME.get(pl).getName()));
				}
				PlayerEntity p = new PlayerEntity(con.getName(), con.getUUID().toString());
				Components.POSITION.get(p).set(0, 2, 0);
				engine.addEntity(p);
				players.put(con.getUUID(), p);
				ServerHandler.channels.writeAndFlush(con);
			}

			private void handleDisconnect(ClientDisconnect con) {
				engine.removeEntity(players.remove(con.getUUID()));
				ServerHandler.channels.writeAndFlush(con);
			}

			private void handleUpdateBasicEntity(UpdateBasicEntity ube) {
				PlayerEntity e = players.get(ube.getUUID());
				Components.POSITION.get(e).set(ube.getPosition());
				ServerHandler.channels.writeAndFlush(ube);
			}
		});
	}

	@Override
	public void dispose() {
		console.stop();
		server.end();
	}

	@Override
	public void update(float delta) {
		worldSimulation.update(delta);
		engine.update(delta);
		ServerHandler.channels.writeAndFlush(new Time(worldSimulation.getTime()));
	}

	public static void main(String[] args) {
		GlobalVariables.PROJECT = "LightEngineDemo";
		TaskManager.addTask(() -> StateMachine.registerState(new ServerState()));
		new Bootstrap(args);
	}

}
