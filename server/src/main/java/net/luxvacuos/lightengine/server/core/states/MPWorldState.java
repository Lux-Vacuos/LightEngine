/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2017 Lux Vacuos
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

package net.luxvacuos.lightengine.server.core.states;

import net.luxvacuos.lightengine.server.commands.SayCommand;
import net.luxvacuos.lightengine.server.commands.ServerCommandManager;
import net.luxvacuos.lightengine.server.commands.StopCommand;
import net.luxvacuos.lightengine.server.commands.TimeCommand;
import net.luxvacuos.lightengine.server.console.Console;
import net.luxvacuos.lightengine.server.core.ServerWorldSimulation;
import net.luxvacuos.lightengine.universal.commands.ICommandManager;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;

public class MPWorldState extends AbstractState {

	private ServerWorldSimulation worldSimulation;
	private Console console;
	private ICommandManager commandManager;

	public MPWorldState() {
		super(StateNames.MP_WORLD);
	}

	@Override
	public void init() {

		worldSimulation = new ServerWorldSimulation();

		commandManager = new ServerCommandManager();
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

}
