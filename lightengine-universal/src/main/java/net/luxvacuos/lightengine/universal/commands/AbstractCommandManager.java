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

package net.luxvacuos.lightengine.universal.commands;

import java.io.PrintStream;
import java.util.Arrays;

import com.badlogic.gdx.utils.Array;

public abstract class AbstractCommandManager implements ICommandManager {

	private Array<ICommand> commands;
	private PrintStream out;

	public AbstractCommandManager(PrintStream out) {
		commands = new Array<>();
		this.out = out;
	}

	@Override
	public void command(String command) {
		for (ICommand iCommand : commands) {
			if (command.startsWith(iCommand.getCommand())) {
				Object[] data = command.split(" ");
				iCommand.execute(out, Arrays.copyOfRange(data, 1, data.length));
				return;
			}
		}
		out.println("Command not found: " + command);
	}

	@Override
	public void registerCommand(ICommand command) {
		this.commands.add(command);
	}

}
