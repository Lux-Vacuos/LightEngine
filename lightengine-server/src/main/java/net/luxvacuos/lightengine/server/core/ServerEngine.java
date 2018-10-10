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

package net.luxvacuos.lightengine.server.core;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.server.core.subsystems.NetworkSubsystem;
import net.luxvacuos.lightengine.server.core.subsystems.ServerCoreSubsystem;
import net.luxvacuos.lightengine.universal.core.Engine;
import net.luxvacuos.lightengine.universal.core.EngineType;
import net.luxvacuos.lightengine.universal.core.IEngineLoader;
import net.luxvacuos.lightengine.universal.core.Sync;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.states.StateNames;
import net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem;
import net.luxvacuos.lightengine.universal.core.subsystems.EventSubsystem;
import net.luxvacuos.lightengine.universal.core.subsystems.ScriptSubsystem;
import net.luxvacuos.lightengine.universal.loader.EngineData;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class ServerEngine extends Engine {

	private float timeCount;

	public ServerEngine(IEngineLoader el, EngineData ed) {
		super(el, ed);
	}

	@Override
	public void init() {
		main.setName("Main");

		Logger.init();
		Logger.log("Starting Server");

		StateMachine.setEngineType(EngineType.SERVER);

		super.addSubsystem(new ServerCoreSubsystem());
		super.addSubsystem(new NetworkSubsystem());
		super.addSubsystem(new ScriptSubsystem());
		super.addSubsystem(new EventSubsystem());

		super.initSubsystems();

		StateMachine.run();
		try {
			run();
		} catch (Throwable t) {
			handleError(t);
		}
		dispose();
	}

	@Override
	public void run() {
		int ups = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Core/ups"));
		float delta = 0;
		float accumulator = 0f;
		float interval = 1f / ups;

		Logger.log("Light Engine Server Version: " + REGISTRY.getRegistryItem(new Key("/Light Engine/version")));
		Logger.log("Light Engine Universal Version: "
				+ REGISTRY.getRegistryItem(new Key("/Light Engine/universalVersion")));
		Logger.log("Running on: " + ed.platform);

		super.runSubsystems();

		StateMachine.setCurrentState(StateNames.SPLASH_SCREEN);

		Sync sync = new Sync();
		while (StateMachine.isRunning()) {
			TaskManager.tm.updateMainThread();
			if (timeCount > 1f) {
				CoreSubsystem.ups = CoreSubsystem.upsCount;
				CoreSubsystem.upsCount = 0;
				timeCount--;
			}
			delta = sync.getDelta();
			accumulator += delta;
			while (accumulator >= interval) {
				StateMachine.update(interval);
				CoreSubsystem.upsCount++;
				accumulator -= interval;
			}
			sync.sync(ups);
		}
	}

	@Override
	public void handleError(Throwable e) {
		e.printStackTrace();
	}

	@Override
	public void restart() {
	}

	@Override
	public void dispose() {
		Logger.log("Cleaning Resources");
		super.disposeSubsystems();
		StateMachine.dispose();
	}

}
