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

package net.luxvacuos.lightengine.client.core;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.client.core.subsystems.ClientCoreSubsystem;
import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.core.subsystems.NetworkSubsystem;
import net.luxvacuos.lightengine.client.core.subsystems.SoundSubsystem;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.nanovg.Timers;
import net.luxvacuos.lightengine.client.rendering.opengl.GPUProfiler;
import net.luxvacuos.lightengine.universal.core.Engine;
import net.luxvacuos.lightengine.universal.core.EngineType;
import net.luxvacuos.lightengine.universal.core.IEngineLoader;
import net.luxvacuos.lightengine.universal.core.Sync;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.states.StateNames;
import net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem;
import net.luxvacuos.lightengine.universal.core.subsystems.EventSubsystem;
import net.luxvacuos.lightengine.universal.core.subsystems.ISubsystem;
import net.luxvacuos.lightengine.universal.core.subsystems.ResManager;
import net.luxvacuos.lightengine.universal.core.subsystems.ScriptSubsystem;
import net.luxvacuos.lightengine.universal.loader.EngineData;
import net.luxvacuos.lightengine.universal.util.ThreadUtils;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class ClientEngine extends Engine implements IClientEngine {

	private Thread render;

	public ClientEngine(IEngineLoader el, EngineData ed) {
		super(el, ed);
	}

	@Override
	public void init() {
		main.setName("Main");

		Logger.init();
		Logger.log("Starting Client");

		StateMachine.setEngineType(EngineType.CLIENT);

		super.addSubsystem(new ClientCoreSubsystem());
		super.addSubsystem(new GraphicalSubsystem());
		super.addSubsystem(new SoundSubsystem());
		super.addSubsystem(new NetworkSubsystem());
		super.addSubsystem(new ScriptSubsystem());
		super.addSubsystem(new EventSubsystem());
		super.addSubsystem(new ResManager());

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
		watchdog = new Thread(() -> {
			while (true) {
				ThreadUtils.sleep(5000);
			}
		});
		watchdog.setDaemon(true);
		watchdog.setName("WatchDog");
		watchdog.start();

		// Simple wrapper for objects used in the init sync.
		var wSync = new Object() {
			boolean mainReady, renderReady, waitThreads;
		};

		// This thread is used to sync both render and main thread so they start at the
		// same time regardless of init time.
		Thread initThread = new Thread(() -> {
			while (true) {
				if (wSync.mainReady && wSync.renderReady) {
					wSync.waitThreads = true;
					return;
				} else
					ThreadUtils.sleep(1); // Let's sleep meanwhile
			}
		});
		initThread.start();

		render = new Thread(() -> {
			this.initRenderSubsystems();
			Window window = GraphicalSubsystem.getMainWindow();
			ClientTaskManager tm = (ClientTaskManager) TaskManager.tm;
			int fps = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Core/fps"));
			float delta = 0f;

			wSync.renderReady = true; // Thread ready
			while (!wSync.waitThreads) // Wait for main thread
				ThreadUtils.sleep(1);
			wSync.renderReady = false;
			
			window.getDelta(); // Reset delta time
			while (StateMachine.isRunning()) {
				tm.updateRenderThread();
				delta = window.getDelta();
				Timers.startGPUTimer();
				GPUProfiler.startFrame();
				GPUProfiler.start("Render");
				this.renderSubsystems(delta);
				StateMachine.render(delta);
				GPUProfiler.end();
				GPUProfiler.endFrame();
				Timers.stopGPUTimer();
				Timers.update();
				window.updateDisplay(fps);
			}
			this.disposeRenderSubsystems();
			wSync.renderReady = true;
		});
		render.setName("Render");
		render.start();
		GraphicalSubsystem.setRenderThreadID(render.getId());

		int ups = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Core/ups"));
		float delta = 0;
		float accumulator = 0f;
		float interval = 1f / ups;

		wSync.mainReady = true;
		while (!wSync.waitThreads) // Wait for render thread
			ThreadUtils.sleep(1);

		Logger.log("Light Engine Client Version: " + REGISTRY.getRegistryItem(new Key("/Light Engine/version")));
		Logger.log("Light Engine Universal Version: "
				+ REGISTRY.getRegistryItem(new Key("/Light Engine/universalVersion")));
		Logger.log("Running on: " + ed.platform);
		Logger.log("LWJGL Version: " + REGISTRY.getRegistryItem(new Key("/Light Engine/System/lwjgl")));
		Logger.log("GLFW Version: " + REGISTRY.getRegistryItem(new Key("/Light Engine/System/glfw")));
		Logger.log("OpenGL Version: " + REGISTRY.getRegistryItem(new Key("/Light Engine/System/opengl")));
		Logger.log("GLSL Version: " + REGISTRY.getRegistryItem(new Key("/Light Engine/System/glsl")));
		Logger.log("Assimp: " + REGISTRY.getRegistryItem(new Key("/Light Engine/System/assimp")));
		Logger.log("Vendor: " + REGISTRY.getRegistryItem(new Key("/Light Engine/System/vendor")));
		Logger.log("Renderer: " + REGISTRY.getRegistryItem(new Key("/Light Engine/System/renderer")));

		super.runSubsystems();

		StateMachine.setCurrentState(StateNames.SPLASH_SCREEN);

		Sync sync = new Sync();
		while (StateMachine.isRunning()) {
			Timers.startCPUTimer();
			TaskManager.tm.updateMainThread();
			if (sync.timeCount > 1f) {
				CoreSubsystem.ups = CoreSubsystem.upsCount;
				CoreSubsystem.upsCount = 0;
				sync.timeCount--;
			}
			delta = sync.getDelta();
			accumulator += delta;
			while (accumulator >= interval) {
				super.updateSubsystems(interval);
				StateMachine.update(interval);
				CoreSubsystem.upsCount++;
				accumulator -= interval;
			}
			Timers.stopCPUTimer();
			sync.sync(ups);
		}
		while (!wSync.renderReady) // Wait for render thread
			ThreadUtils.sleep(1);
	}

	@Override
	public void handleError(Throwable e) {
		e.printStackTrace();
		StateMachine.stop();
	}

	@Override
	public void initRenderSubsystems() {
		for (ISubsystem subsystem : subsystems)
			subsystem.initRender();
	}

	@Override
	public void renderSubsystems(float delta) {
		for (ISubsystem subsystem : subsystems)
			subsystem.render(delta);
	}

	@Override
	public void disposeRenderSubsystems() {
		for (ISubsystem subsystem : subsystems)
			subsystem.disposeRender();
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
