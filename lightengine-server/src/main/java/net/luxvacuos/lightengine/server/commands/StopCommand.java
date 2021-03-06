/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2019 Lux Vacuos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.luxvacuos.lightengine.server.commands;

import java.io.PrintStream;

import net.luxvacuos.lightengine.server.network.ServerHandler;
import net.luxvacuos.lightengine.universal.commands.SimpleCommand;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.network.packets.Disconnect;

public class StopCommand extends SimpleCommand {

	public StopCommand() {
		super("/stop");
	}

	@Override
	public void execute(PrintStream out, Object... data) {
		ServerHandler.channels.writeAndFlush("Stopping Server, Goodbye!");
		ServerHandler.channels.writeAndFlush(new Disconnect("Stop command"));
		out.println("Stopping");
		StateMachine.stop();
	}

}
