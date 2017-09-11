package net.luxvacuos.lightengine.demo;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import net.luxvacuos.lightengine.server.bootstrap.Bootstrap;
import net.luxvacuos.lightengine.server.commands.SayCommand;
import net.luxvacuos.lightengine.server.commands.ServerCommandManager;
import net.luxvacuos.lightengine.server.commands.StopCommand;
import net.luxvacuos.lightengine.server.console.Console;
import net.luxvacuos.lightengine.server.network.Server;
import net.luxvacuos.lightengine.server.network.ServerNetworkHandler;
import net.luxvacuos.lightengine.universal.commands.ICommandManager;
import net.luxvacuos.lightengine.universal.commands.StateCommand;
import net.luxvacuos.lightengine.universal.commands.TimeCommand;
import net.luxvacuos.lightengine.universal.core.GlobalVariables;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.util.registry.Key;
import net.luxvacuos.lightengine.universal.world.PhysicsSystem;

public class ServerState extends AbstractState {
	private Console console;
	private ICommandManager commandManager;
	private Server server;
	private ServerNetworkHandler nh;

	public ServerState() {
		super("_main");
	}

	@Override
	public void init() {
		server = new Server((int) REGISTRY.getRegistryItem(new Key("/Light Engine/Server/port")));
		nh = new ServerNetworkHandler();
		server.run(nh);
		nh.getEngine().getSystem(PhysicsSystem.class)
				.addBox(new BoundingBox(new Vector3(-50, -1, -50), new Vector3(50, 0, 50)));

		commandManager = new ServerCommandManager(System.out);
		commandManager.registerCommand(new StopCommand());
		commandManager.registerCommand(new SayCommand());
		commandManager.registerCommand(new TimeCommand(nh.getWorldSimulation()));
		commandManager.registerCommand(new StateCommand());

		console = new Console();
		console.setCommandManager(commandManager);
		console.start();

	}

	@Override
	public void dispose() {
		nh.dispose();
		console.stop();
		server.end();
	}

	@Override
	public void update(float delta) {
		nh.update(delta);
	}

	public static void main(String[] args) {
		GlobalVariables.PROJECT = "LightEngineDemo";
		TaskManager.addTask(() -> StateMachine.registerState(new ServerState()));
		new Bootstrap(args);
	}

}
