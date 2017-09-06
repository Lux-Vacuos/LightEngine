package net.luxvacuos.lightengine.demo;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
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
import net.luxvacuos.lightengine.universal.network.packets.ClientConnect;
import net.luxvacuos.lightengine.universal.network.packets.ClientDisconnect;
import net.luxvacuos.lightengine.universal.network.packets.Time;
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
		server.run(new ChannelInboundHandlerAdapter() {
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				try {
				if (msg instanceof ClientConnect) {
					handleConnect((ClientConnect) msg);
				} else if (msg instanceof ClientDisconnect) {
					handleDisconnect((ClientDisconnect) msg);
				}
				} finally {
					ReferenceCountUtil.release(msg);
				}
				super.channelRead(ctx, msg);
			}

			private void handleConnect(ClientConnect msg) {
				PlayerEntity p = new PlayerEntity(msg.getName(), msg.getUUID().toString());
				Components.POSITION.get(p).set(0, 2, 0);
				engine.addEntity(p);
				players.put(msg.getUUID(), p);
				ServerHandler.channels.writeAndFlush(msg);
			}

			private void handleDisconnect(ClientDisconnect msg) {
				engine.removeEntity(players.remove(msg.getUUID()));
				ServerHandler.channels.writeAndFlush(msg);
			}
		});
	}

	@Override
	public void dispose() {
		console.stop();
		server.end();
	}

	@Override
	public void update( float delta) {
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
