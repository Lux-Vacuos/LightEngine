/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2018 Lux Vacuos
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

package net.luxvacuos.lightengine.universal.core;

import java.util.ArrayList;
import java.util.List;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.universal.core.subsystems.ISubsystem;
import net.luxvacuos.lightengine.universal.resources.IDisposable;

public abstract class UniversalEngine implements IEngine, IDisposable {

	protected List<ISubsystem> subsystems;

	protected Thread watchdog;

	public UniversalEngine() {
		subsystems = new ArrayList<>();
	}

	@Override
	public void initSubsystems() {
		Logger.log("Initializing Subsystems");
		for (ISubsystem subsystem : subsystems) {
			Logger.log("--- " + subsystem.getClass().getSimpleName());
			subsystem.init();
		}
		Logger.log("--- ");
	}

	@Override
	public void restartSubsystems() {
		Logger.log("Restarting Subsystems");
		for (ISubsystem subsystem : subsystems) {
			Logger.log("--- " + subsystem.getClass().getSimpleName());
			subsystem.restart();
		}
		Logger.log("--- ");
	}

	@Override
	public void updateSubsystems(float delta) {
		for (ISubsystem subsystem : subsystems) {
			subsystem.update(delta);
		}
	}

	@Override
	public void disposeSubsystems() {
		Logger.log("Stopping Subsystems");
		for (ISubsystem subsystem : subsystems) {
			Logger.log("--- " + subsystem.getClass().getSimpleName());
			subsystem.dispose();
		}
		Logger.log("--- ");
	}

	@Override
	public void addSubsystem(ISubsystem subsystem) {
		subsystems.add(subsystem);
	}

}
