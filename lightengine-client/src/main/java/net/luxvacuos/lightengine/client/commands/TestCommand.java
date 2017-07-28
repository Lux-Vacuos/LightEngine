package net.luxvacuos.lightengine.client.commands;

import java.io.PrintStream;

import net.luxvacuos.lightengine.universal.commands.SimpleCommand;

public class TestCommand extends SimpleCommand {

	public TestCommand() {
		super("/test");
	}

	@Override
	public void execute(PrintStream out, Object... data) {
		out.println("TEST COMMAND");
		out.println("TEST COMMAND");
		out.println("TEST COMMAND");
		out.println("TEST COMMAND");
	}

}
