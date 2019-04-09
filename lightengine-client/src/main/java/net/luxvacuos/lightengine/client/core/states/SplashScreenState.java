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

package net.luxvacuos.lightengine.client.core.states;

import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.ui.v2.surfaces.SplashSurface;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.AbstractState;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.states.StateNames;

public class SplashScreenState extends AbstractState {

	private boolean ready;
	private float timer;

	private SplashSurface surface;

	public SplashScreenState() {
		super(StateNames.SPLASH_SCREEN);
	}

	@Override
	public void start() {
		GraphicalSubsystem.getSurfaceManager().addSurface(surface = new SplashSurface());
		super.start();
	}

	@Override
	public void end() {
		surface.removeSurfaceFromRoot();
		super.end();
	}

	@Override
	public void update(float delta) {
		if (ready)
			return;
		timer += delta;
		if (timer > 4) {
			if (StateMachine.hasState(StateNames.MAIN)) {
				if (TaskManager.tm.isEmpty()) {
					ready = true;
					StateMachine.setCurrentState(StateNames.MAIN);
				}
			} else {
				ready = true;
				surface.mainNotFound();
			}
		}
	}

}
