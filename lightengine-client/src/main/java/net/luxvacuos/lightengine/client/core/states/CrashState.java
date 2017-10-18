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

package net.luxvacuos.lightengine.client.core.states;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.ui.windows.CrashWindow;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;
import net.luxvacuos.lightengine.universal.core.states.StateNames;

public class CrashState extends AbstractState {

	public static Throwable t;

	public CrashState() {
		super(StateNames.CRASH);
	}

	@Override
	public void start() {
		GraphicalSubsystem.getWindowManager().closeAllWindows();
		GraphicalSubsystem.getWindowManager().addWindow(new CrashWindow(t));
	}

	@Override
	public void render(float delta) {
	}

	@Override
	public void update(float delta) {
	}

}
