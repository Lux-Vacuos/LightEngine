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

package net.luxvacuos.lightengine.client.core;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.client.bootstrap.Bootstrap;
import net.luxvacuos.lightengine.client.core.exception.UpdateThreadException;
import net.luxvacuos.lightengine.client.core.states.CrashState;
import net.luxvacuos.lightengine.client.core.subsystems.ClientCoreSubsystem;
import net.luxvacuos.lightengine.client.core.subsystems.GraphicalSubsystem;
import net.luxvacuos.lightengine.client.core.subsystems.NetworkSubsystem;
import net.luxvacuos.lightengine.client.core.subsystems.SoundSubsystem;
import net.luxvacuos.lightengine.client.input.MouseHandler;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.nanovg.Timers;
import net.luxvacuos.lightengine.client.rendering.opengl.GPUProfiler;
import net.luxvacuos.lightengine.universal.core.AbstractEngine;
import net.luxvacuos.lightengine.universal.core.EngineType;
import net.luxvacuos.lightengine.universal.core.Sync;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.states.StateNames;
import net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class LightEngineClient extends AbstractEngine {

	private Thread watchdog;
	private Thread updateThread;

	public LightEngineClient() {
		GLFW.glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
		StateMachine.setEngineType(EngineType.CLIENT);
		init();
	}

	/**
	 * Init function
	 */
	@Override
	public void init() {
		Logger.init();
		Logger.log("Starting Client");

		super.addSubsystem(new ClientCoreSubsystem());
		super.addSubsystem(new GraphicalSubsystem());
		if (!ClientVariables.WSL)
			super.addSubsystem(new SoundSubsystem());
		super.addSubsystem(new NetworkSubsystem());
		
		super.initSubsystems();

		Logger.log("Light Engine Client Version: " + REGISTRY.getRegistryItem(new Key("/Light Engine/version")));
		Logger.log("Light Engine Universal Version: "
				+ REGISTRY.getRegistryItem(new Key("/Light Engine/universalVersion")));
		Logger.log("Running on: " + Bootstrap.getPlatform());
		Logger.log("LWJGL Version: " + REGISTRY.getRegistryItem(new Key("/Light Engine/System/lwjgl")));
		Logger.log("GLFW Version: " + REGISTRY.getRegistryItem(new Key("/Light Engine/System/glfw")));
		Logger.log("OpenGL Version: " + REGISTRY.getRegistryItem(new Key("/Light Engine/System/opengl")));
		Logger.log("GLSL Version: " + REGISTRY.getRegistryItem(new Key("/Light Engine/System/glsl")));
		Logger.log("Assimp: " + REGISTRY.getRegistryItem(new Key("/Light Engine/System/assimp")));
		Logger.log("Vendor: " + REGISTRY.getRegistryItem(new Key("/Light Engine/System/vendor")));
		Logger.log("Renderer: " + REGISTRY.getRegistryItem(new Key("/Light Engine/System/renderer")));

		StateMachine.setCurrentState(StateNames.SPLASH_SCREEN);
		try {
			StateMachine.run();
			update();
			dispose();
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			handleError(t);
		}
	}

	/**
	 * Main Loop
	 */
	@Override
	public void update() {
		int fps = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Core/fps"));
		int ups = (int) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Core/ups"));
		updateThread = new Thread(() -> {
			float delta = 0;
			float accumulator = 0f;
			float interval = 1f / ups;
			Sync sync = new Sync();
			while (StateMachine.isRunning()) {
				Timers.startCPUTimer();
				TaskManager.updateThread();
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
		});
		updateThread.setName("Update Thread");
		updateThread.start();

		watchdog = new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
				if (!updateThread.isAlive()) {
					TaskManager.addTask(() -> {
						throw new UpdateThreadException("Update Thread died");
					});
				}
			}
		});
		watchdog.setDaemon(true);
		watchdog.setName("WatchDog Thread");
		watchdog.start();

		Window window = GraphicalSubsystem.getMainWindow();
		float delta = 0;
		while (StateMachine.isRunning()) {
			TaskManager.update();
			delta = window.getDelta();
			Timers.startGPUTimer();
			GPUProfiler.startFrame();
			GPUProfiler.start("Render");
			super.renderSubsystems(delta);
			StateMachine.render(delta);
			GPUProfiler.end();
			GPUProfiler.endFrame();
			Timers.stopGPUTimer();
			Timers.update();
			window.updateDisplay(fps);
			if (window.isCloseRequested())
				StateMachine.stop();
		}
	}

	/**
	 * Handle all errors
	 * 
	 * @param e
	 *            Throwable
	 */
	@Override
	public void handleError(Throwable e) {
		CrashState.t = e;
		StateMachine.dispose();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
		}
		StateMachine.registerState(new CrashState());
		StateMachine.run();
		StateMachine.setCurrentState(StateNames.CRASH);
		MouseHandler.setGrabbed(GraphicalSubsystem.getMainWindow().getID(), false);
		update();
		dispose();
		GLFW.glfwTerminate();
	}

	@Override
	public void dispose() {
		super.dispose();
		StateMachine.dispose();
	}

}
