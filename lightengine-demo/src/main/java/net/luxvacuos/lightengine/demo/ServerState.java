package net.luxvacuos.lightengine.demo;

import net.luxvacuos.lightengine.server.bootstrap.Bootstrap;
import net.luxvacuos.lightengine.server.commands.SayCommand;
import net.luxvacuos.lightengine.server.commands.ServerCommandManager;
import net.luxvacuos.lightengine.server.commands.StopCommand;
import net.luxvacuos.lightengine.server.console.Console;
import net.luxvacuos.lightengine.server.core.ServerWorldSimulation;
import net.luxvacuos.lightengine.universal.commands.ICommandManager;
import net.luxvacuos.lightengine.universal.commands.TimeCommand;
import net.luxvacuos.lightengine.universal.core.GlobalVariables;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;

public class ServerState extends AbstractState {
	private ServerWorldSimulation worldSimulation;
	private Console console;
	private ICommandManager commandManager;

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

		console = new Console();
		console.setCommandManager(commandManager);
		console.start();
	}

	@Override
	public void dispose() {
		console.stop();
	}

	@Override
	public void update( float delta) {
		worldSimulation.update(delta);
	}
	
	public static void main(String[] args) {
		GlobalVariables.PROJECT = "LightEngineDemo";
		TaskManager.addTask(() -> StateMachine.registerState(new ServerState()));
		new Bootstrap(args);
	}

}
